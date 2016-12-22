package com.unovo.carmanager.ui.chat.input;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.common.emoji.EmojiEditText;
import com.unovo.carmanager.common.emoji.SimpleTextWatcher;
import com.unovo.carmanager.ui.chat.ResourceHelper;
import com.unovo.carmanager.utils.DensityUtil;
import com.unovo.carmanager.utils.LogUtil;
import com.unovo.carmanager.utils.SystemTool;
import com.unovo.carmanager.utils.ToastUtil;
import com.unovo.carmanager.utils.UIUtils;
import java.io.File;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.chat
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/9 13:03
 * @version: V1.0
 */
public class InputBar extends FrameLayout
    implements InputAction, KeyboardControl, InputOperate, InputBaseCallback {
  FragmentActivity mActivity;

  private EmojiEditText editText;

  private View editTextLayout;

  private CheckBox popVoice;

  private CheckBox popEmoji;

  private TextView sendText;

  //private Button popEmojiButton;

  KeyboardControl keyboardControl;
  private boolean disableCheckedChange = false;

  private Button showVoiceBtn;
  private View mVoiceHintAnimArea;
  private View mVoiceRcdHitCancelView;
  private TextView mVoiceHintCancelText;
  private ImageView mVoiceHintCancelIcon;
  private View mVoiceHintLoading;
  private View mVoiceHintRcding;
  private View mVoiceHintTooshort;
  private TextView mVoiceNormalWording;

  public InputBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    mActivity = (FragmentActivity) getContext();
    LayoutInflater.from(context).inflate(R.layout.input_view_top_bar, this);
    editText = (EmojiEditText) findViewById(R.id.editText);
    editTextLayout = findViewById(R.id.editTextLayout);
    sendText = (TextView) findViewById(R.id.sendText);
    popVoice = (CheckBox) findViewById(R.id.popVoice);
    popEmoji = (CheckBox) findViewById(R.id.popEmoji);
    showVoiceBtn = (Button) findViewById(R.id.voice_record_bt);
    initViews();
  }

  public void showVoicePanel() {
    editTextLayout.setVisibility(INVISIBLE);
    showVoiceBtn.setVisibility(VISIBLE);
  }

  public void hideVoicePanel() {
    editTextLayout.setVisibility(VISIBLE);
    showVoiceBtn.setVisibility(INVISIBLE);
  }

  private void initViews() {
    editText.setCallback(this);
    if (mActivity instanceof VoiceRecordCompleteCallback) {
      popVoice.setVisibility(VISIBLE);
    } else {
      popVoice.setVisibility(View.GONE);
    }
    editText.addTextChangedListener(new SimpleTextWatcher() {
      @Override public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
          sendText.setEnabled(true);
        } else {
          sendText.setEnabled(false);
        }

        if (mActivity instanceof VoiceRecordCompleteCallback) {
          popVoice.setVisibility(VISIBLE);
        } else {
          popVoice.setVisibility(View.GONE);
        }

        if (s.length() > 0) {
          sendText.setBackgroundResource(R.drawable.edit_send_green);
          sendText.setTextColor(0xffffffff);
        } else {
          sendText.setBackgroundResource(R.drawable.edit_send);
          sendText.setTextColor(0xff999999);
        }
      }
    });
    popEmoji.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
        if (disableCheckedChange) {
          return;
        }

        if (checked) { // 需要弹出 emoji 键盘
          slowlyPop(true);
        } else {
          keyboardControl.showSystemInput(true);
        }
      }
    });

    popVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
        if (disableCheckedChange) {
          return;
        }

        if (checked) {
          slowlyPop(false);
        } else {
          if (popEmoji.isChecked()) {
            keyboardControl.showEmojiInput();
          } else {
            keyboardControl.showSystemInput(true);
          }
        }
      }
    });

    sendText.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (keyboardControl!=null)
          keyboardControl.OnSendTextMessageRequest(editText.getText());

        editText.clearComposingText();
        editText.setText("");
      }
    });

    showVoiceBtn.setOnTouchListener(mOnVoiceRecTouchListener);
  }

  public long getAvailaleSize() {
    File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
    StatFs stat = new StatFs(path.getPath());
    long blockSize = stat.getBlockSize();
    long availableBlocks = stat.getAvailableBlocks();
    return (availableBlocks * blockSize) / 1024 / 1024;//  MIB单位
  }

  public static boolean isExistExternalStore() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  /*
    优化弹出过程, 防止系统键盘和自定义键盘同时出现
    showEmoji 为 true 表示弹出 emoji 键盘, 为 false 弹出 voice 键盘
    */
  private void slowlyPop(final boolean showEmoji) {
    if (editText.isPopSystemInput()) {
      editText.postAfterSystemInputHide(new Runnable() {
        @Override public void run() {
          showCustomKeyboard(showEmoji);
        }
      });
    } else {
      showCustomKeyboard(showEmoji);
    }
  }

  private void showCustomKeyboard(boolean showEmoji) {
    if (showEmoji) {
      keyboardControl.showEmojiInput();
    } else {
      keyboardControl.showVoiceInput();
    }
  }

  public EditText getEditText() {
    return editText;
  }

  public void setKeyboardControl(KeyboardControl keyboardControl) {
    this.keyboardControl = keyboardControl;
  }

  @Override public void deleteOneChar() {
    editText.deleteOneChar();
  }

  @Override public void insertEmoji(String s) {
    editText.insertEmoji(s);
  }

  @Override public void popSystemInput() {
    keyboardControl.hideCustomInput();
  }

  @Override public String getContent() {
    return editText.getText().toString();
  }

  @Override public void clearContent() {
    editText.setText("");
  }

  @Override public void setContent(String s) {
    editText.requestFocus();
    Editable editable = editText.getText();
    editable.clear();

    editable.insert(0, s);
  }

  @Override public void hideKeyboard() {
    SystemTool.popSoftkeyboard(mActivity, editText, false);
  }

  @Override public void insertText(String s) {
    insertText(editText, s);
  }

  public static void insertText(EditText edit, String s) {
    edit.requestFocus();
    int insertPos = edit.getSelectionStart();

    String insertString = s + " ";
    Editable editable = edit.getText();
    editable.insert(insertPos, insertString);
  }

  @Override public boolean isPopCustomKeyboard() {
    return false;
  }

  @Override public void closeCustomKeyboard() {

  }

  @Override public void setClickSend(OnClickListener click) {
    sendText.setOnClickListener(click);
  }

  @Override public void addTextWatcher(TextWatcher textWatcher) {
    editText.addTextChangedListener(textWatcher);
  }

  @Override public void showSystemInput(boolean show) {
    hideCustomInput();
  }

  @Override public void showVoiceInput() {
    disableCheckedChange = true;

    popVoice.setChecked(true);
    editTextLayout.setVisibility(GONE);

    disableCheckedChange = false;
  }

  @Override public void showEmojiInput() {
    disableCheckedChange = true;

    popVoice.setChecked(false);
    editTextLayout.setVisibility(VISIBLE);
    popEmoji.setChecked(true);

    disableCheckedChange = false;
  }

  @Override public void hideCustomInput() {
    disableCheckedChange = true;

    popVoice.setChecked(false);
    editTextLayout.setVisibility(VISIBLE);
    popEmoji.setChecked(false);

    disableCheckedChange = false;
  }

  @Override public void OnVoiceRcdInitReuqest() {

  }

  @Override public void OnVoiceRcdStartRequest() {

  }

  @Override public void OnVoiceRcdCancelRequest() {

  }

  @Override public void OnVoiceRcdStopRequest() {

  }

  @Override public void OnSendTextMessageRequest(CharSequence text) {

  }

  private RecordPopupWindow popupWindow;
  private ImageView mVoiceHintAnim;

  public final void showVoiceRecordWindow(int offsert) {

    int yLocation = 0;
    int maxHeightDensity = ResourceHelper.fromDPToPix(getContext(), 180);
    int density = DensityUtil.getMetricsDensity(getContext(), 50.0F);

    if (offsert + density < maxHeightDensity) {
      yLocation = -1;
    } else {
      yLocation = density + (offsert - maxHeightDensity) / 2;
    }

    if (popupWindow == null) {
      popupWindow =
          new RecordPopupWindow(View.inflate(getContext(), R.layout.voice_rcd_hint_window, null),
              WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
      mVoiceHintAnim =
          ((ImageView) popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_anim));
      mVoiceHintAnimArea = popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_anim_area);
      mVoiceRcdHitCancelView =
          popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_cancel_area);
      mVoiceHintCancelText =
          ((TextView) popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_cancel_text));
      mVoiceHintCancelIcon =
          ((ImageView) popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_cancel_icon));
      mVoiceHintLoading = popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_loading);
      mVoiceHintRcding = popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_rcding);
      mVoiceHintTooshort = popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_tooshort);
      mVoiceNormalWording =
          ((TextView) popupWindow.getContentView().findViewById(R.id.voice_rcd_normal_wording));
    }

    if (yLocation != -1) {
      mVoiceHintTooshort.setVisibility(View.GONE);
      mVoiceHintRcding.setVisibility(View.GONE);
      mVoiceHintLoading.setVisibility(View.VISIBLE);
      popupWindow.showAtLocation(this, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, yLocation);
    }
  }

  long currentTimeMillis = 0;
  public static boolean isRecodering = false;
  private boolean mVoiceButtonTouched;
  // cancel recording sliding distance field.
  private static final int CANCLE_DANSTANCE = 60;

  final private OnTouchListener mOnVoiceRecTouchListener = new OnTouchListener() {
    @Override public boolean onTouch(View v, MotionEvent event) {
      if (getAvailaleSize() < 10) {
        UIUtils.showToast(R.string.media_no_memory);
        return false;
      }
      long time = System.currentTimeMillis() - currentTimeMillis;
      if (time <= 300) {
        currentTimeMillis = System.currentTimeMillis();
        return false;
      }

      if (!isExistExternalStore()) {
        ToastUtil.showMessage(R.string.media_ejected);
        return false;
      }

      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          isRecodering = true;
          mVoiceButtonTouched = true;

          if (keyboardControl != null) {
            keyboardControl.OnVoiceRcdInitReuqest();
          }
          break;

        case MotionEvent.ACTION_MOVE:
          if (popupWindow == null) {
            return false;
          }
          if (event.getX() <= 0.0F || event.getY() <= -CANCLE_DANSTANCE) {
            mVoiceHintCancelText.setText(R.string.chatfooter_cancel_rcd_release);
            mVoiceRcdHitCancelView.setVisibility(View.VISIBLE);
            mVoiceHintAnimArea.setVisibility(View.GONE);
          } else {
            mVoiceHintCancelText.setText(R.string.chatfooter_cancel_rcd);
            mVoiceRcdHitCancelView.setVisibility(View.GONE);
            mVoiceHintAnimArea.setVisibility(View.VISIBLE);
          }
          break;
        case MotionEvent.ACTION_UP:
          isRecodering = false;
          resetVoiceRecordingButton();
          break;
      }

      return false;
    }

    public void resetVoiceRecordingButton() {
      mVoiceButtonTouched = false;


      if (mVoiceRcdHitCancelView != null && mVoiceRcdHitCancelView.getVisibility() == View.VISIBLE) {
        // Start to cancel sending events when recording over
        if (keyboardControl != null) {
          keyboardControl.OnVoiceRcdCancelRequest();
        }
        return;
      }

      if (keyboardControl != null) {
        keyboardControl.OnVoiceRcdStopRequest();
      }
    }
  };

  /**
   *
   */
  public void showVoiceRecording() {
    if (keyboardControl != null) {
      keyboardControl.OnVoiceRcdStartRequest();
    }
    mVoiceHintLoading.setVisibility(View.GONE);
    mVoiceHintRcding.setVisibility(View.VISIBLE);
  }

  /**
   *
   */
  public final void dismissPopuWindow() {
    if (popupWindow != null) {
      popupWindow.dismiss();
      mVoiceHintRcding.setVisibility(View.VISIBLE);
      mVoiceHintLoading.setVisibility(View.GONE);
      mVoiceHintTooshort.setVisibility(View.GONE);
      mVoiceRcdHitCancelView.setVisibility(View.GONE);
      mVoiceHintAnimArea.setVisibility(View.VISIBLE);
    }
    mVoiceButtonTouched = false;
  }

  private static final int ampValue[] = {
      0, 15, 30, 45, 60, 75, 90, 100
  };
  private static final int ampIcon[] = {
      R.drawable.amp1, R.drawable.amp2, R.drawable.amp3, R.drawable.amp4, R.drawable.amp5,
      R.drawable.amp6, R.drawable.amp7
  };

  public void displayAmplitude(double amplitude) {
    for (int i = 0; i < ampIcon.length; i++) {
      if (amplitude < ampValue[i] || amplitude >= ampValue[i + 1]) {
        continue;
      }
      LogUtil.d(LogUtil.getLogUtilsTag(getClass()), "Voice rcd amplitude " + amplitude);
      mVoiceHintAnim.setBackgroundDrawable(
          ResourceHelper.getDrawableById(getContext(), ampIcon[i]));
      if ((amplitude == -1) && (this.popupWindow != null)) {
        popupWindow.dismiss();
        mVoiceHintLoading.setVisibility(View.VISIBLE);
        mVoiceHintRcding.setVisibility(View.GONE);
        mVoiceHintTooshort.setVisibility(View.GONE);
      }
      return;
    }
  }

  private static final int WHAT_ON_DIMISS_DIALOG = 0x1;

  public synchronized void tooShortPopuWindow() {
    showVoiceBtn.setEnabled(false);
    if (popupWindow != null) {
      mVoiceHintTooshort.setVisibility(View.VISIBLE);
      mVoiceHintRcding.setVisibility(View.GONE);
      mVoiceHintLoading.setVisibility(View.GONE);
      //popupWindow.update();
    }
    if (mHandler != null) {
      mHandler.sendEmptyMessageDelayed(WHAT_ON_DIMISS_DIALOG, 500L);
    }
  }

  final private Handler mHandler = new Handler() {

    @Override public void handleMessage(Message msg) {
      super.handleMessage(msg);

      popupWindow.dismiss();
      showVoiceBtn.setEnabled(true);
    }
  };
}
