package com.unovo.carmanager.common.emoji;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import com.unovo.carmanager.ui.chat.input.InputBaseCallback;
import com.unovo.carmanager.utils.SystemTool;
import com.unovo.carmanager.utils.UIUtils;

/**
 * Created by chenchao on 16/1/25.
 */

public class EmojiEditText extends EditText {

  View rootView;
  int rootViewHigh;

  AppCompatActivity mActivity;

  public EmojiEditText(Context context, AttributeSet attrs) {
    super(context, attrs);

    mActivity = (AppCompatActivity) getContext();
    rootView = mActivity.findViewById(android.R.id.content);
    rootView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override public void onGlobalLayout() {
            int lastHigh = rootViewHigh;
            rootViewHigh = rootView.getHeight();
            if (lastHigh > rootViewHigh && callback != null) {
              callback.popSystemInput();
            }
          }
        });
    init();
  }

  private void init() {
    addTextChangedListener(new SimpleTextWatcher() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }
    });
  }

  public void deleteOneChar() {
    KeyEvent event =
        new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
    dispatchKeyEvent(event);
  }

  public void insertEmoji(String s) {
    //int insertPos = getSelectionStart();
    //final String format = ":%s:";
    //String replaced = String.format(format, s);
    //
    //Editable editable = getText();
    //editable.insert(insertPos, String.format(format, s));
    //editable.setSpan(new EmojiconSpan(mActivity, s), insertPos, insertPos + replaced.length(),
    //    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public boolean isPopSystemInput() {
    int rootViewHigh = rootView.getHeight();
    final int bottomHigh = UIUtils.dp2px(100); // 底部虚拟按键高度，nexus5是73dp，以防万一，所以设大一点
    int rootParentHigh = rootView.getRootView().getHeight();
    return rootParentHigh - rootViewHigh > bottomHigh;
  }

  InputBaseCallback callback;

  public void setCallback(InputBaseCallback callback) {
    this.callback = callback;
  }

  public void postAfterSystemInputHide(Runnable run) {
    // 说明键盘已经弹出来了，等键盘消失后再设置 emoji keyboard 可见
    SystemTool.popSoftkeyboard(mActivity, this, false);
    postDelayed(run, 200);
  }
}
