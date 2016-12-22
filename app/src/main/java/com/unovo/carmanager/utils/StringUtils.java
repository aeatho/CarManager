package com.unovo.carmanager.utils;

/**
 * Created by Administrator on 2016/8/2.
 */
public class StringUtils {

  /**
   * 判断是否为空
   */
  public static boolean isEmpty(Object obj) {
    if (obj == null) return true;
    String input = String.valueOf(obj);
    if (input.length() == 0) return true;

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
        return false;
      }
    }
    return true;
  }

  /**
   * 格式化字符串
   */
  public static String toString(Object... inputs) {
    StringBuilder sb = new StringBuilder();

    for (Object each : inputs) {
      sb.append(isEmpty(each) ? "" : each);
    }
    return sb.toString();
  }
}
