package com.unovo.carmanager.ui.chat;

import android.content.Intent;
import android.text.TextUtils;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.common.SDKCoreHelper;
import com.unovo.carmanager.utils.ECNotificationManager;
import com.unovo.carmanager.utils.LogUtil;
import com.unovo.carmanager.utils.Settings;
import com.unovo.carmanager.utils.UIUtils;
import com.unovo.carmanager.utils.Utils;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.chat
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/8 14:42
 * @version: V1.0
 */
public class IMChattingHelper
    implements OnChatReceiveListener, ECChatManager.OnDownloadMessageListener {
  private static final java.lang.String TAG = "im";
  public static final String INTENT_ACTION_SYNC_MESSAGE = "com.yuntongxun.ecdemo_sync_message";

  private static IMChattingHelper sInstance;
  /** 云通讯SDK聊天功能接口 */
  private ECChatManager mChatManager;

  /** 全局处理所有的IM消息发送回调 */
  private ChatManagerListener mListener;

  private static HashMap<String, SyncMsgEntry> syncMessage = new HashMap<String, SyncMsgEntry>();

  public static IMChattingHelper getInstance() {
    if (sInstance == null) {
      sInstance = new IMChattingHelper();
    }
    return sInstance;
  }

  private IMChattingHelper() {
    initManager();
    mListener = new ChatManagerListener();
  }

  public void initManager() {
    mChatManager = SDKCoreHelper.getECChatManager();
  }

  /**
   * 消息发送报告
   */
  private OnMessageReportCallback mOnMessageReportCallback;

  public void destroy() {
    if (syncMessage != null) {
      syncMessage.clear();
    }
    mListener = null;
    mChatManager = null;
    isFirstSync = false;
    sInstance = null;
  }

  public interface OnMessageReportCallback {
    void onMessageReport(ECError error, ECMessage message);

    void onPushMessage(String sessionId, List<ECMessage> msgs);
  }

  public static void setOnMessageReportCallback(OnMessageReportCallback callback) {
    getInstance().mOnMessageReportCallback = callback;
  }

  /**
   * 发送ECMessage 消息
   */
  public static long sendECMessage(ECMessage msg) {
    getInstance().initManager();
    // 获取一个聊天管理器
    ECChatManager manager = getInstance().mChatManager;
    if (manager != null) {
      msg.setMsgTime(System.currentTimeMillis());

      manager.sendMessage(msg, getInstance().mListener);

      // 保存发送的消息到数据库
      if (msg.getType() == ECMessage.Type.FILE && msg.getBody() instanceof ECFileMessageBody) {
        ECFileMessageBody fileMessageBody = (ECFileMessageBody) msg.getBody();
        msg.setUserData("fileName=" + fileMessageBody.getFileName());
      }
    } else {
      msg.setMsgStatus(ECMessage.MessageStatus.FAILED);
    }
    return 0;
  }

  @Override public void onDownloadMessageComplete(ECError e, ECMessage message) {
    if (e.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
      if (message == null) return;
      // 处理发送文件IM消息的时候进度回调
      LogUtil.d(TAG, "[onDownloadMessageComplete] msgId：" + message.getMsgId());
      postDowloadMessageResult(message);

      if (message.getType() == ECMessage.Type.VIDEO
          && mOnMessageReportCallback != null
          && message.getDirection() == ECMessage.Direction.RECEIVE
          && mOnMessageReportCallback instanceof ChattingActivity) {

        ((ChattingActivity) mOnMessageReportCallback).dismissPostingDialog();
      }
    } else {
      // 重试下载3次
      SyncMsgEntry remove = syncMessage.remove(message.getMsgId());
      if (remove == null) {
        return;
      }
      LogUtil.d(TAG, "[onDownloadMessageComplete] download fail , retry ：" + remove.retryCount);
      retryDownload(remove);
    }
  }

  private void retryDownload(SyncMsgEntry entry) {
    if (entry == null || entry.msg == null || entry.isRetryLimit()) {
      return;
    }
    entry.increase();
    // download ..
    if (mChatManager != null) {
      if (entry.thumbnail) {
        mChatManager.downloadThumbnailMessage(entry.msg, this);
      } else {
        mChatManager.downloadMediaMessage(entry.msg, this);
      }
    }
    syncMessage.put(entry.msg.getMsgId(), entry);
  }

  private synchronized void postDowloadMessageResult(ECMessage message) {
    if (message == null) {
      return;
    }
    if (message.getType() == ECMessage.Type.VOICE) {
      ECVoiceMessageBody voiceBody = (ECVoiceMessageBody) message.getBody();
      voiceBody.setDuration(Utils.calculateVoiceTime(voiceBody.getLocalUrl()));
    }
    if (mOnMessageReportCallback != null) {
      mOnMessageReportCallback.onMessageReport(null, message);
    }
    boolean showNotice = true;
    SyncMsgEntry remove = syncMessage.remove(message.getMsgId());
    if (remove != null) {
      showNotice = remove.showNotice;
      if (mOnMessageReportCallback != null && remove.msg != null) {
        ArrayList<ECMessage> msgs = new ArrayList<ECMessage>();
        msgs.add(remove.msg);
        mOnMessageReportCallback.onPushMessage(remove.msg.getSessionId(), msgs);
      }
    }
    CarApplication.addOffMessages(message);
    UIUtils.getContext().sendBroadcast(new Intent(IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE));

    if (showNotice) showNotification(message);
  }

  @Override public void onProgress(String s, int i, int i1) {

  }

  @Override public void OnReceivedMessage(ECMessage msg) {
    if (msg == null) {
      return;
    }
    postReceiveMessage(msg, !msg.isNotify());
  }

  private void postReceiveMessage(ECMessage msg, boolean showNotice) {

    if (msg.isMultimediaBody()) {
      if (msg.getType() != ECMessage.Type.CALL) {
        ECFileMessageBody body = (ECFileMessageBody) msg.getBody();
        Utils.initFileAccess();
        if (!TextUtils.isEmpty(body.getRemoteUrl())) {
          if (msg.getType() == ECMessage.Type.VOICE) {
            body.setLocalUrl(new File(Utils.getVoicePathName(),
                Utils.md5(String.valueOf(System.currentTimeMillis())) + ".amr").getAbsolutePath());
          }
          if (syncMessage != null) {
            syncMessage.put(msg.getMsgId(), new SyncMsgEntry(showNotice, false, msg));
          }

          if (mChatManager != null) {
            mChatManager.downloadMediaMessage(msg, this);
          }
        }
      }
    }
    if (msg.getType() == ECMessage.Type.TXT) {
      if (mOnMessageReportCallback != null) {
        ArrayList<ECMessage> msgs = new ArrayList<ECMessage>();
        msgs.add(msg);
        mOnMessageReportCallback.onPushMessage(msg.getSessionId(), msgs);
      }

      CarApplication.addOffMessages(msg);
      UIUtils.getContext().sendBroadcast(new Intent(IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE));

      // 是否状态栏提示
      if (showNotice) {
        showNotification(msg);
      }
    }
  }

  private static void showNotification(ECMessage msg) {
    if (checkNeedNotification(msg.getSessionId())) {
      ECNotificationManager.getInstance().forceCancelNotification();
      String lastMsg = "";
      if (msg.getType() == ECMessage.Type.TXT) {
        lastMsg = ((ECTextMessageBody) msg.getBody()).getMessage();
      }

      ECNotificationManager.getInstance()
          .showCustomNewMessageNotification(UIUtils.getContext(), lastMsg, msg.getNickName(),
              msg.getSessionId(), msg.getType().ordinal());
    }
  }

  /**
   * 是否需要状态栏通知
   */
  public static boolean checkNeedNotification(String contactId) {
    String currentChattingContactId = Settings.getHerID(UIUtils.getContext());
    if (contactId == null) {
      return true;
    }
    // 当前聊天
    if (contactId.equals(currentChattingContactId)) {
      return false;
    }

    return true;
  }

  @Override public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {

  }

  @Override public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {

  }

  private int mHistoryMsgCount = 0;

  @Override public void onOfflineMessageCount(int count) {
    mHistoryMsgCount = count;
  }

  @Override public int onGetOfflineMessage() {
    // 获取全部的离线历史消息
    return ECDevice.SYNC_OFFLINE_MSG_ALL;
  }

  /** 是否同步离线消息 */
  private boolean isSyncOffline = true;
  /** 是否是同步消息 */
  private boolean isFirstSync = false;
  private ECMessage mOfflineMsg = null;

  @Override public void onReceiveOfflineMessage(List<ECMessage> msgs) {
    UIUtils.getContext().sendBroadcast(new Intent(INTENT_ACTION_SYNC_MESSAGE));

    if (msgs != null && !msgs.isEmpty() && !isFirstSync) isFirstSync = true;
    for (ECMessage msg : msgs) {
      mOfflineMsg = msg;
      postReceiveMessage(msg, false);
    }
  }

  @Override public void onReceiveOfflineMessageCompletion() {
    if (mOfflineMsg == null) {
      return;
    }
    // SDK离线消息拉取完成之后会通过该接口通知应用
    // 应用可以在此做类似于Loading框的关闭，Notification通知等等
    // 如果已经没有需要同步消息的请求时候，则状态栏开始提醒
    ECMessage lastECMessage = mOfflineMsg;
    try {
      if (mHistoryMsgCount > 0 && isFirstSync) {
        showNotification(lastECMessage);
        // lastECMessage.setSessionId(lastECMessage.getTo().startsWith("G")?lastECMessage.getTo():lastECMessage.getForm());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    isFirstSync = isSyncOffline = false;
    // 无需要同步的消息
    UIUtils.getContext().sendBroadcast(new Intent(INTENT_ACTION_SYNC_MESSAGE));
    mOfflineMsg = null;
  }

  public static boolean isSyncOffline() {
    return getInstance().isSyncOffline;
  }

  @Override public void onServicePersonVersion(int i) {

  }

  @Override public void onReceiveDeskMessage(ECMessage ecMessage) {

  }

  @Override public void onSoftVersion(String s, int i) {

  }

  private class ChatManagerListener implements ECChatManager.OnSendMessageListener {

    @Override public void onSendMessageComplete(ECError error, ECMessage message) {
      if (message == null) {
        return;
      }
      // 处理ECMessage的发送状态
      if (message.getType() == ECMessage.Type.VOICE) {
        try {
          Utils.playNotifycationMusic(UIUtils.getContext(), "sound/voice_message_sent.mp3");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (mOnMessageReportCallback != null) {
        mOnMessageReportCallback.onMessageReport(error, message);
      }
    }

    @Override public void onProgress(String msgId, int total, int progress) {
      // 处理发送文件IM消息的时候进度回调
      LogUtil.d(TAG, "[IMChattingHelper - onProgress] msgId："
          + msgId
          + " ,total："
          + total
          + " ,progress:"
          + progress);
    }
  }

  /**
   * 消息重发
   */
  public static long reSendECMessage(ECMessage msg) {
    ECChatManager manager = getInstance().mChatManager;
    if (manager != null) {
      manager.sendMessage(msg, getInstance().mListener);
    }
    return -1;
  }

  public class SyncMsgEntry {
    // 是否是第一次初始化同步消息
    boolean showNotice = false;
    boolean thumbnail = false;

    // 重试下载次数
    private int retryCount = 1;
    ECMessage msg;

    public SyncMsgEntry(boolean showNotice, boolean thumbnail, ECMessage message) {
      this.showNotice = showNotice;
      this.msg = message;
      this.thumbnail = thumbnail;
    }

    public void increase() {
      retryCount++;
    }

    public boolean isRetryLimit() {
      return retryCount >= 3;
    }
  }
}
