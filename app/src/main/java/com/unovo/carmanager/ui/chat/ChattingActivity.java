package com.unovo.carmanager.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.ListView;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.common.SDKCoreHelper;
import com.unovo.carmanager.ui.chat.input.InputBar;
import com.unovo.carmanager.ui.chat.input.KeyboardControl;
import com.unovo.carmanager.ui.chat.input.VoiceRecordCompleteCallback;
import com.unovo.carmanager.utils.DialogHelper;
import com.unovo.carmanager.utils.LogUtil;
import com.unovo.carmanager.utils.Settings;
import com.unovo.carmanager.utils.StringUtils;
import com.unovo.carmanager.utils.SystemTool;
import com.unovo.carmanager.utils.ToastUtil;
import com.unovo.carmanager.utils.Utils;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECUserStateMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.chat
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/9 02:34
 * @version: V1.0
 */
public class ChattingActivity extends BaseActivity
    implements VoiceRecordCompleteCallback, IMChattingHelper.OnMessageReportCallback {
  public final static String RECIPIENTS = "recipients";
  public final static String CONTACT_USER = "contact_user";

  /** 会话联系人账号 */
  private String mRecipients;
  /** 联系人名称 */
  private String mUsername;
  /** 语音录制空闲 */
  public static final int RECORD_IDLE = 0;
  /** 语音录制中 */
  public static final int RECORD_ING = 1;
  /** 保存当前的录音状态 */
  public int mRecordState = RECORD_IDLE;
  private long computationTime = -1L;
  /** 按键振动时长 */
  public static final int TONE_LENGTH_MS = 200;
  /** 音量值 */
  private static final float TONE_RELATIVE_VOLUME = 100.0F;
  /** 当前语言录制文件的时间长度 */
  private int mVoiceRecodeTime = 0;
  /** 待发送的语音文件最短时长 */
  private static final int MIX_TIME = 1000;
  /** 待发的ECMessage消息 */
  private ECMessage mPreMessage;

  /** 历史聊天纪录消息显示View */
  private ListView mListView;

  private MyReceiver myReceiver;

  private InputBar mInputBar;
  /** 同步锁 */
  Object mLock = new Object();

  /** IM聊天管理工具 */
  private ECChatManager mChatManager;

  private ChattingListAdapter mChattingAdapter;

  private Handler mVoiceHandler;

  public int getRecordState() {
    synchronized (mLock) {
      return mRecordState;
    }
  }

  public void setRecordState(int state) {
    synchronized (mLock) {
      this.mRecordState = state;
    }
  }

  @Override protected int getLayoutId() {
    return R.layout.chattingui_activity_container;
  }

  protected String mAmrPathName;

  @Override protected void init(Bundle savedInstanceState) {
    Intent intent = getIntent();
    mInputBar = (InputBar) findViewById(R.id.inputBar);
    mListView = (ListView) findViewById(R.id.chatting_history_lv);

    // 初始化IM聊天工具API
    mChatManager = SDKCoreHelper.getECChatManager();
    mVoiceHandler = new Handler();
    mInputBar.setKeyboardControl(listener);

    mRecipients = intent.getStringExtra(RECIPIENTS);
    mUsername = intent.getStringExtra(CONTACT_USER);

    if (TextUtils.isEmpty(mRecipients)) {
      showToast("联系人账号不存在");
      finish();
      return;
    }

    mActionBar.setTitle(mUsername);

    setChattingContactId(mRecipients);

    mListView.setAdapter(mChattingAdapter = new ChattingListAdapter(this));

    if (!CarApplication.getOffMessages().isEmpty()) {
      List<ECMessage> currentMsgs = new ArrayList<>();
      for (ECMessage msg : CarApplication.getOffMessages()) {
        if (mRecipients.equals(msg.getForm())) {
          currentMsgs.add(msg);
        }
      }
      mChattingAdapter.addMessages(currentMsgs);

      mListView.setSelection(mListView.getCount() - 1);

      CarApplication.getOffMessages().removeAll(currentMsgs);
    }
  }

  private KeyboardControl listener = new KeyboardControl() {
    @Override public void showSystemInput(boolean show) {
      SystemTool.popSoftkeyboard(ChattingActivity.this, mInputBar.getEditText(), show);
      mInputBar.hideVoicePanel();
    }

    @Override public void showVoiceInput() {
      SystemTool.popSoftkeyboard(ChattingActivity.this, mInputBar.getEditText(), false);
      mInputBar.showVoicePanel();
    }

    @Override public void showEmojiInput() {
      SystemTool.popSoftkeyboard(ChattingActivity.this, mInputBar.getEditText(), false);
    }

    @Override public void hideCustomInput() {

    }

    @Override public void OnVoiceRcdInitReuqest() {
      mAmrPathName = Utils.md5(String.valueOf(System.currentTimeMillis())) + ".amr";
      if (Utils.getVoicePathName() == null) {
        ToastUtil.showMessage("Path to file could not be created");
        mAmrPathName = null;
        return;
      }
      keepScreenOnState(true);

      if (getRecordState() != RECORD_ING) {
        setRecordState(RECORD_ING);
        // 手指按下按钮，按钮给予振动或者声音反馈
        readyOperation();
        mInputBar.showVoiceRecordWindow(
            findViewById(R.id.ccp_root_view).getHeight() - mInputBar.getHeight());

        final ECChatManager chatManager = SDKCoreHelper.getECChatManager();
        if (chatManager == null) {
          return;
        }
        mVoiceHandler.post(new Runnable() {
          @Override public void run() {
            try {
              ECMessage message = ECMessage.createECMessage(ECMessage.Type.VOICE);
              message.setTo(mRecipients);
              ECVoiceMessageBody messageBody =
                  new ECVoiceMessageBody(new File(Utils.getVoicePathName(), mAmrPathName), 0);
              message.setBody(messageBody);
              mPreMessage = message;
              // 仅录制语音消息，录制完成后需要调用发送接口发送消息
              handleSendUserStateMessage("2");
              chatManager.startVoiceRecording(messageBody,
                  new ECChatManager.OnRecordTimeoutListener() {
                    @Override public void onRecordingTimeOut(long duration) {
                      // 如果语音录制超过最大60s长度,则发送
                      doProcesOperationRecordOver(false, true);
                    }

                    @Override public void onRecordingAmplitude(double amplitude) {
                      // 显示声音振幅
                      if (mInputBar != null && getRecordState() == RECORD_ING) {
                        mInputBar.showVoiceRecording();
                        mInputBar.displayAmplitude(amplitude);
                      }
                    }
                  });
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
      }
    }

    @Override public void OnVoiceRcdStartRequest() {

    }

    @Override public void OnVoiceRcdCancelRequest() {
      handleSendUserStateMessage("0");
      handleMotionEventActionUp(true, false);
    }

    @Override public void OnVoiceRcdStopRequest() {
      handleSendUserStateMessage("0");
      handleMotionEventActionUp(false, true);
    }

    @Override public void OnSendTextMessageRequest(CharSequence text) {
      handleSendTextMessage(text);
    }
  };

  /**
   * 处理Button 按钮按下抬起事件
   *
   * @param doCancle 是否取消或者停止录制
   */
  private void handleMotionEventActionUp(final boolean doCancle, boolean isSend) {
    keepScreenOnState(false);
    if (getRecordState() == RECORD_ING) {
      doVoiceRecordAction(doCancle, isSend);
    }
  }

  /**
   * 处理语音录制结束事件
   *
   * @param doCancle 是否取消或者停止录制
   */
  private void doVoiceRecordAction(boolean doCancle, final boolean isSend) {
    final boolean cancleVoice = doCancle;

    if (mChatManager != null) {
      mVoiceHandler.post(new Runnable() {

        @Override public void run() {
          // 停止或者取消普通模式语音
          mChatManager.stopVoiceRecording(new ECChatManager.OnStopVoiceRecordingListener() {
            @Override public void onRecordingComplete() {
              doProcesOperationRecordOver(cancleVoice, isSend);
            }
          });
        }
      });
    }
  }

  public ChattingListAdapter getChattingAdapter() {
    return mChattingAdapter;
  }

  /**
   * 消息重发
   */
  public void doResendMsgRetryTips(final ECMessage msg, final int position) {
    DialogHelper.showConfirmDialog(ChattingActivity.this, "确认重发该消息？",
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            resendMsg(msg, position);
          }
        });
  }

  protected void resendMsg(ECMessage msg, int position) {
    if (msg == null || position < 0 || mChattingAdapter.getItem(position) == null) {
      return;
    }
    ECMessage message = mChattingAdapter.getItem(position);
    message.setTo(mRecipients);
    long rowid = IMChattingHelper.reSendECMessage(message);
    if (rowid != -1) {
      mChattingAdapter.notifyDataSetChanged();
    }
  }

  /**
   * 给予客户端震动提示
   */
  protected void readyOperation() {
    computationTime = -1L;
    //mRecordTipsToast = null;
    playTone(ToneGenerator.TONE_PROP_BEEP, TONE_LENGTH_MS);
    new Handler().postDelayed(new Runnable() {

      @Override public void run() {
        stopTone();
      }
    }, TONE_LENGTH_MS);
    vibrate(50L);
  }

  /**
   * 手机震动
   */
  public synchronized void vibrate(long milliseconds) {
    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    if (mVibrator == null) {
      return;
    }
    mVibrator.vibrate(milliseconds);
  }

  private ToneGenerator mToneGenerator;

  private Object mToneGeneratorLock = new Object();

  // 初始化
  private void initToneGenerator() {
    AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    if (mToneGenerator == null) {
      try {
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volume = (int) (TONE_RELATIVE_VOLUME * (streamVolume / streamMaxVolume));
        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
      } catch (RuntimeException e) {
        LogUtil.d("Exception caught while creating local tone generator: " + e);
        mToneGenerator = null;
      }
    }
  }

  /**
   * 停止播放声音
   */
  public void stopTone() {
    if (mToneGenerator != null) mToneGenerator.stopTone();
  }

  /**
   * 播放提示音
   */
  public void playTone(int tone, int durationMs) {
    synchronized (mToneGeneratorLock) {
      initToneGenerator();
      if (mToneGenerator == null) {
        LogUtil.d("playTone: mToneGenerator == null, tone: " + tone);
        return;
      }

      // Start the new tone (will stop any playing tone)
      mToneGenerator.startTone(tone, durationMs);
    }
  }

  private void keepScreenOnState(boolean screenOn) {
    if (mListView != null) {
      mListView.setKeepScreenOn(screenOn);
    }
  }

  @Override protected void onResume() {
    super.onResume();

    IMChattingHelper.setOnMessageReportCallback(this);
    IntentFilter filter1 = new IntentFilter();

    filter1.addAction("com.yuntongxun.ecdemo.removemember");
    filter1.addAction(SDKCoreHelper.ACTION_KICK_OFF);

    registerReceiver(myReceiver, filter1);
  }

  @Override protected void onPause() {
    // TODO Auto-generated method stub
    super.onPause();
    //unregisterReceiver(myReceiver);
    setChattingContactId("");
  }

  private void setChattingContactId(String s) {
    Settings.saveHerID(this, s);
  }

  @Override public void recordFinished(long duration, String voicePath) {
    // 发送语音
  }

  @Override public void onMessageReport(ECError error, ECMessage message) {
    if (mChattingAdapter != null) {
      mChattingAdapter.notifyDataSetChanged();
    }
    if (error == null) {
      return;
    }

    if (SdkErrorCode.SDK_TEXT_LENGTH_LIMIT == error.errorCode) {
      // 文本长度超过限制
      showToast("您发送的文本超过最大长度限制");
    }
  }

  @Override public void onPushMessage(String sid, List<ECMessage> msgs) {
    //if (!mRecipients.equals(sid)) {
    //  return;
    //}
    mChattingAdapter.addMessages(msgs);

    // 当前是否正在查看消息
    mListView.setSelection(mListView.getCount() - 1);
  }

  public class MyReceiver extends BroadcastReceiver {
    // 可用Intent的getAction()区分接收到的不同广播
    @Override public void onReceive(Context arg0, Intent intent) {
      if (intent == null) {
        return;
      }
      if (intent.getAction().equals(SDKCoreHelper.ACTION_KICK_OFF) || intent.getAction()
          .equals("com.yuntongxun.ecdemo.removemember")) {
        finish();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mVoiceHandler != null) {
      mVoiceHandler.removeCallbacksAndMessages(null);
      mVoiceHandler = null;
    }
    setChattingContactId("");
    IMChattingHelper.setOnMessageReportCallback(null);
  }

  /**
   * 处理状态消息发送
   */
  private void handleSendUserStateMessage(CharSequence text) {
    if (StringUtils.isEmpty(text)) {
      return;
    }

    if (Settings.getUID(this).equals(mRecipients)) {
      return;
    }

    // 组建一个待发送的ECMessage
    ECMessage msg = ECMessage.createECMessage(ECMessage.Type.STATE);
    // 设置消息接收者
    msg.setTo(mRecipients);
    // 创建一个文本消息体，并添加到消息对象中
    ECUserStateMessageBody msgBody = new ECUserStateMessageBody(text.toString());
    msg.setBody(msgBody);
    ECChatManager ecChatManager = ECDevice.getECChatManager();
    if (ecChatManager == null) {
      return;
    }
    ecChatManager.sendMessage(msg, new ECChatManager.OnSendMessageListener() {
      @Override public void onSendMessageComplete(ECError error, ECMessage message) {

      }

      @Override public void onProgress(String msgId, int totalByte, int progressByte) {

      }
    });
  }

  /**
   * 处理录音结束消息是否发送逻辑
   *
   * @param cancle 是否取消发送
   */
  protected void doProcesOperationRecordOver(boolean cancle, boolean isSend) {
    if (getRecordState() == RECORD_ING) {
      // 当前是否有正在录音的操作
      // 定义标志位判断当前所录制的语音文件是否符合发送条件
      // 只有当录制的语音文件的长度超过1s才进行发送语音
      boolean isVoiceToShort = false;
      File amrPathFile = new File(Utils.getVoicePathName(), mAmrPathName);
      if (amrPathFile.exists()) {
        mVoiceRecodeTime = Utils.calculateVoiceTime(amrPathFile.getAbsolutePath());
        if (mVoiceRecodeTime * 1000 < MIX_TIME) {
          isVoiceToShort = true;
        }
      } else {
        isVoiceToShort = true;
      }
      // 设置录音空闲状态
      setRecordState(RECORD_IDLE);
      if (mInputBar != null) {
        if (isVoiceToShort && !cancle) {
          // 提示语音文件长度太短
          mInputBar.tooShortPopuWindow();
          return;
        }
        // 关闭语音录制对话框
        mInputBar.dismissPopuWindow();
      }

      if (!cancle && mPreMessage != null && isSend) {
        // 如果当前的录音模式为非Chunk模式
        try {
          ECVoiceMessageBody body = (ECVoiceMessageBody) mPreMessage.getBody();
          body.setDuration(mVoiceRecodeTime);
          long rowId = IMChattingHelper.sendECMessage(mPreMessage);
          mPreMessage.setId(rowId);
          notifyIMessageListView(mPreMessage);
        } catch (Exception e) {
          e.printStackTrace();
        }
        return;
      }

      // 删除语音文件
      amrPathFile.deleteOnExit();
      // 重置语音时间长度统计
      mVoiceRecodeTime = 0;
    }
  }

  /**
   * 将发送的消息放入消息列表
   */
  public void notifyIMessageListView(ECMessage message) {
    ArrayList<ECMessage> msgs = new ArrayList<>();
    msgs.add(message);
    mChattingAdapter.addMessages(msgs);
    mListView.setSelection(mListView.getCount() - 1);
  }

  public void showProcessDialog() {
    DialogHelper.showMessageDialog(this, "正在下载中...请稍后");
  }

  public void dismissPostingDialog() {
    DialogHelper.showMessageDialog(this, "下载完成,再次点击即可播放");
  }

  /**
   * 处理文本发送方法事件通知
   */
  private void handleSendTextMessage(CharSequence text) {
    if (text == null) {
      return;
    }
    if (text.toString().trim().length() <= 0) {
      return;
    }
    // 组建一个待发送的ECMessage
    ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
    // 设置消息接收者
    msg.setTo(mRecipients);
    // 创建一个文本消息体，并添加到消息对象中
    ECTextMessageBody msgBody = new ECTextMessageBody(text.toString());
    msg.setBody(msgBody);

    try {
      // 发送消息，该函数见上
      long rowId = IMChattingHelper.sendECMessage(msg);
      // 通知列表刷新
      msg.setId(rowId);
      notifyIMessageListView(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
