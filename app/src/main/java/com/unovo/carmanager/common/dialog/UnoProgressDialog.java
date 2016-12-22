package com.unovo.carmanager.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.unovo.carmanager.R;

/**
 * 网络访问对话框
 *
 * @author Jorstin Chan@容联•云通讯
 * @version 4.0
 * @date 2015-1-4
 */
public class UnoProgressDialog extends Dialog {

  private TextView mTextView;
  private View mImageView;
  AsyncTask mAsyncTask;

  private final OnCancelListener mCancelListener = new OnCancelListener() {

    @Override public void onCancel(DialogInterface dialog) {
      if (mAsyncTask != null) {
        mAsyncTask.cancel(true);
      }
    }
  };

  /**
   * @param context
   */
  public UnoProgressDialog(Context context) {
    super(context, R.style.Theme_Light_CustomDialog_Blue);
    mAsyncTask = null;
    setCancelable(true);
    setContentView(R.layout.common_loading_diloag);
    mTextView = (TextView) findViewById(R.id.textview);
    mTextView.setText(R.string.loading);
    mImageView = findViewById(R.id.imageview);
    setOnCancelListener(mCancelListener);
  }

  /**
   * @param context
   * @param resid
   */
  public UnoProgressDialog(Context context, int resid) {
    this(context);
    mTextView.setText(resid);
  }

  public UnoProgressDialog(Context context, CharSequence text) {
    this(context);
    mTextView.setText(text);
  }

  public UnoProgressDialog(Context context, AsyncTask asyncTask) {
    this(context);
    mAsyncTask = asyncTask;
  }

  public UnoProgressDialog(Context context, CharSequence text, AsyncTask asyncTask) {
    this(context, text);
    mAsyncTask = asyncTask;
  }

  /**
   * 设置对话框显示文本
   */
  public final void setPressText(CharSequence text) {
    mTextView.setText(text);
  }

  public final void dismiss() {
    super.dismiss();
    mImageView.clearAnimation();
  }

  public final void show() {
    super.show();
    Animation loadAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.loading);
    mImageView.startAnimation(loadAnimation);
  }
}
