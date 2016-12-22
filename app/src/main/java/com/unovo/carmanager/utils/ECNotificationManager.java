/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.unovo.carmanager.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Looper;
import com.unovo.carmanager.R;
import com.unovo.carmanager.ui.MainActivity;
import com.yuntongxun.ecsdk.ECMessage;
import java.io.IOException;

/**
 * 状态栏通知
 *
 * @author Jorstin Chan@容联•云通讯
 * @version 4.0
 * @date 2015-1-4
 */
public class ECNotificationManager {

  public static final int CCP_NOTIFICATOIN_ID_CALLING = 0x1;

  public static final int NOTIFY_ID_PUSHCONTENT = 35;

  private Context mContext;

  private static NotificationManager mNotificationManager;

  public static ECNotificationManager mInstance;

  public static ECNotificationManager getInstance() {
    if (mInstance == null) {
      mInstance = new ECNotificationManager(UIUtils.getContext());
    }

    return mInstance;
  }

  MediaPlayer mediaPlayer = null;

  public void playNotificationMusic(String voicePath) throws IOException {
    //paly music ...
    AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd(voicePath);
    if (mediaPlayer == null) {
      mediaPlayer = new MediaPlayer();
    }
    if (mediaPlayer.isPlaying()) {
      mediaPlayer.stop();
    }
    mediaPlayer.reset();
    mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
        fileDescriptor.getLength());
    mediaPlayer.prepare();
    mediaPlayer.setLooping(false);
    mediaPlayer.start();
  }

  private ECNotificationManager(Context context) {
    mContext = context;
  }

  public final void showCustomNewMessageNotification(Context context, String pushContent,
      String fromUserName, String sessionId, int lastMsgType) {
    LogUtil.w(LogUtil.getLogUtilsTag(ECNotificationManager.class),
        "showCustomNewMessageNotification pushContent： "
            + pushContent
            + ", fromUserName: "
            + fromUserName
            + " ,sessionId: "
            + sessionId
            + " ,msgType: "
            + lastMsgType);

    Intent intent = new Intent(mContext, MainActivity.class);
    intent.putExtra("nofification_type", "pushcontent_notification");
    intent.putExtra("Intro_Is_Muti_Talker", true);
    intent.putExtra("Main_FromUserName", fromUserName);
    intent.putExtra("Main_Session", sessionId);
    intent.putExtra("MainUI_User_Last_Msg_Type", lastMsgType);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(mContext, 35, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    String tickerText = getTickerText(mContext, fromUserName, lastMsgType);
    String contentTitle = getContentTitle(fromUserName);
    String contentText = getContentText(context, pushContent, lastMsgType);

    int defaults = Notification.DEFAULT_ALL;

    Notification notification =
        NotificationUtil.buildNotification(context, R.mipmap.log_logo, defaults, false,
            tickerText, contentTitle, contentText, null, pendingIntent);
    notification.flags = (Notification.FLAG_AUTO_CANCEL | notification.flags);
    ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
        NOTIFY_ID_PUSHCONTENT, notification);
  }

  /**
   *
   * @param contex
   * @param fromUserName
   * @param msgType
   * @return
   */
  public final String getTickerText(Context contex, String fromUserName, int msgType) {
    if (msgType == ECMessage.Type.TXT.ordinal()) {
      return contex.getResources().getString(R.string.notification_fmt_one_txttype, fromUserName);
    } else if (msgType == ECMessage.Type.IMAGE.ordinal()) {
      return contex.getResources().getString(R.string.notification_fmt_one_imgtype, fromUserName);
    } else if (msgType == ECMessage.Type.VOICE.ordinal()) {
      return contex.getResources().getString(R.string.notification_fmt_one_voicetype, fromUserName);
    } else if (msgType == ECMessage.Type.FILE.ordinal()) {
      return contex.getResources().getString(R.string.notification_fmt_one_filetype, fromUserName);
    } else {
      //return contex.getResources().getString(R.string.app_name);
      return contex.getPackageManager().getApplicationLabel(contex.getApplicationInfo()).toString();
    }
  }

  public final String getContentTitle(String fromUserName) {
    return fromUserName;
  }

  /**
   *
   * @param context
   * @return
   */
  public final String getContentText(Context context, String pushContent, int lastMsgType) {

    if (lastMsgType == ECMessage.Type.TXT.ordinal()) {
      return pushContent;
    } else if (lastMsgType == ECMessage.Type.FILE.ordinal()) {
      return context.getResources().getString(R.string.app_file);
    } else if (lastMsgType == ECMessage.Type.VOICE.ordinal()) {
      return context.getResources().getString(R.string.app_voice);
    } else if (lastMsgType == ECMessage.Type.IMAGE.ordinal()) {
      return context.getResources().getString(R.string.app_pic);
    } else {
      return pushContent;
    }
  }

  private void cancel() {
    NotificationManager notificationManager =
        (NotificationManager) UIUtils.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager == null) {
      return;
    }
    notificationManager.cancel(0);
  }

  /**
   * 取消所有的状态栏通知
   */
  public final void forceCancelNotification() {
    cancel();
    NotificationManager notificationManager =
        (NotificationManager) UIUtils.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    if (notificationManager == null) {
      return;
    }
    notificationManager.cancel(NOTIFY_ID_PUSHCONTENT);
  }

  public final Looper getLooper() {
    return Looper.getMainLooper();
  }

  private void checkNotification() {
    if (mNotificationManager == null) {
      mNotificationManager =
          (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }
  }

  public static void cancelCCPNotification(int id) {
    getInstance().checkNotification();
    mNotificationManager.cancel(id);
  }
}
