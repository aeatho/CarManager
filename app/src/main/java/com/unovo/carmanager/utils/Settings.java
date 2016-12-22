package com.unovo.carmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.unovo.carmanager.constant.Constants;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Settings {

  private static SharedPreferences getSP(Context context) {
    return context.getSharedPreferences(Constants.SETTING_FILE_NAME, Context.MODE_PRIVATE);
  }

  public static void saveUID(Context context, String uid) {
    getSP(context).edit().putString(Constants.USER_ID, uid).apply();
  }

  public static String getUID(Context context) {
    return getSP(context).getString(Constants.USER_ID, "");
  }

  public static void saveUName(Context context, String name) {
    getSP(context).edit().putString(Constants.USER_NAME, name).apply();
  }

  public static String getUName(Context context) {
    return getSP(context).getString(Constants.USER_NAME, "");
  }

  public static void saveHerID(Context context, String herID) {
    getSP(context).edit().putString(Constants.current_conversion_id, herID).apply();
  }

  public static String getHerID(Context context) {
    return getSP(context).getString(Constants.current_conversion_id, "");
  }
}
