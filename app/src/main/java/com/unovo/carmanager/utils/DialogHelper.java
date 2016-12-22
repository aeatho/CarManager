package com.unovo.carmanager.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import com.unovo.carmanager.common.dialog.UnoAlertDialog;
import com.unovo.carmanager.common.dialog.UnoProgressDialog;

public class DialogHelper {
  /** 左边按钮 */
  public static final int BUTTON_NEGATIVE = 0;
  /** 右边按钮 */
  public static final int BUTTON_POSITIVE = 1;

  /**
   * 创建对话框
   *
   * @param ctx 上下文
   * @param message 对话框内容
   * @param leftBtnText 取消按钮文本
   * @param rightText 确定按钮文本
   */
  public static UnoAlertDialog buildAlert(Context ctx, CharSequence message,
      CharSequence leftBtnText, CharSequence rightText,
      DialogInterface.OnClickListener negativeClickListener,
      DialogInterface.OnClickListener positive) {
    UnoAlertDialog dialog = new UnoAlertDialog(ctx);
    dialog.setMessage(message);
    if (!TextUtils.isEmpty(leftBtnText)) {
      dialog.setButton(BUTTON_POSITIVE, leftBtnText, negativeClickListener);
    }
    if (!TextUtils.isEmpty(rightText)) {
      dialog.setButton(BUTTON_NEGATIVE, rightText, positive);
    }
    return dialog;
  }

  /***
   * 获取一个耗时等待对话框
   */
  public static UnoProgressDialog getWaitDialog(Context context, String message) {
    UnoProgressDialog waitDialog = new UnoProgressDialog(context);
    if (!TextUtils.isEmpty(message)) {
      waitDialog.setPressText(message);
    }
    return waitDialog;
  }

  /***
   * 获取一个信息对话框，注意需要自己手动调用show方法显示
   */
  public static void showMessageDialog(Context context, String message,
      DialogInterface.OnClickListener onClickListener) {
    buildAlert(context, message, "确定", null, onClickListener, null).show();
  }

  public static void showMessageDialog(Context context, String message) {
    showMessageDialog(context, message, null);
  }

  public static void showConfirmDialog(Context context, String message,
      DialogInterface.OnClickListener onClickListener) {
    buildAlert(context, message, "确定", "取消", onClickListener, null).show();
  }

  public static void showConfirmDialog(Context context, String message,
      DialogInterface.OnClickListener onOkClickListener,
      DialogInterface.OnClickListener onCancleClickListener) {
    buildAlert(context, message, "确定", "取消", onOkClickListener, onCancleClickListener).show();
  }

  public static void showConfirmDialog(Context context, String message, String okString,
      String cancleString, DialogInterface.OnClickListener onOkClickListener,
      DialogInterface.OnClickListener onCancleClickListener) {
    showConfirmDialog(context, "", message, okString, cancleString, onOkClickListener,
        onCancleClickListener);
  }

  public static void showConfirmDialog(Context context, String title, String message,
      String okString, String cancleString, DialogInterface.OnClickListener onOkClickListener,
      DialogInterface.OnClickListener onCancleClickListener) {
    UnoAlertDialog buildAlert =
        buildAlert(context, message, okString, cancleString, onOkClickListener,
            onCancleClickListener);
    buildAlert.setTitle(title);
    buildAlert.show();
  }

  /***
   * 获取一个dialog
   */
  public static AlertDialog.Builder getDialog(Context context) {
    return new AlertDialog.Builder(context);
  }

  public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays,
      DialogInterface.OnClickListener onClickListener) {
    AlertDialog.Builder builder = getDialog(context);
    builder.setItems(arrays, onClickListener);
    if (!TextUtils.isEmpty(title)) {
      builder.setTitle(title);
    }
    builder.setPositiveButton("取消", null);
    return builder;
  }

  public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays,
      DialogInterface.OnClickListener onClickListener) {
    return getSelectDialog(context, "", arrays, onClickListener);
  }

  public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title,
      String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
    AlertDialog.Builder builder = getDialog(context);
    builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
    if (!TextUtils.isEmpty(title)) {
      builder.setTitle(title);
    }
    builder.setNegativeButton("取消", null);
    return builder;
  }

  public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays,
      int selectIndex, DialogInterface.OnClickListener onClickListener) {
    return getSingleChoiceDialog(context, "", arrays, selectIndex, onClickListener);
  }
}
