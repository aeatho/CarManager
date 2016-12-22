package com.unovo.carmanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/1.
 */
public class DateUtils {

  public static String getCurrentTimeStap() {
    SimpleDateFormat sb;
    try {
      sb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    } catch (Exception ignored) {
      return "";
    }

    return sb.format(new Date());
  }
}
