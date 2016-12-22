package com.unovo.carmanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.base.BaseApplication;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.utils
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/8 16:18
 * @version: V1.0
 */
public class SystemTool {

  public static void hideSoftKeyboard(View view) {
    if (view == null) return;
    ((InputMethodManager) CarApplication.getInstance()
        .getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
        view.getWindowToken(), 0);
  }

  public static boolean hasInternet() {
    ConnectivityManager mConnectivityManager = (ConnectivityManager) BaseApplication.getInstance()
        .getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
    if (mNetworkInfo != null) {
      return mNetworkInfo.isAvailable();
    }
    return false;
  }

  public static void popSoftkeyboard(Context ctx, View view, boolean wantPop) {
    InputMethodManager imm =
        (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (wantPop) {
      view.requestFocus();
      imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    } else {
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }
}
