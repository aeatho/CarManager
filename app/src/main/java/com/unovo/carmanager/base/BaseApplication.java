package com.unovo.carmanager.base;

import android.app.Application;
import android.os.Handler;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.base
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/8 19:23
 * @version: V1.0
 */
public class BaseApplication extends Application {
  private static BaseApplication _context;

  //获取到主线程的handler
  private static Handler mMainThreadHanler;
  //获取到主线程
  private static Thread mMainThread;
  //获取到主线程的id
  private static int mMainThreadId;

  public static float sScale;
  public static int sWidthDp;
  public static int sWidthPix;
  public static int sHeightPix;

  @Override public void onCreate() {
    super.onCreate();
    _context = this;
    init();
  }

  private void init() {
    mMainThreadHanler = new Handler();
    mMainThread = Thread.currentThread();
    //获取到调用线程的id
    mMainThreadId = android.os.Process.myTid();

    sScale = getResources().getDisplayMetrics().density;
    sWidthPix = getResources().getDisplayMetrics().widthPixels;
    sHeightPix = getResources().getDisplayMetrics().heightPixels;
    sWidthDp = (int) (sWidthPix / sScale);
  }

  public static BaseApplication getInstance() {
    return _context;
  }

  public static Handler getMainThreadHandler() {
    return mMainThreadHanler;
  }

  public static Thread getMainThread() {
    return mMainThread;
  }

  public static int getMainThreadId() {
    return mMainThreadId;
  }
}
