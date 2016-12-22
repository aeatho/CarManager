package com.unovo.carmanager.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.location.Criteria;
import android.media.MediaPlayer;
import android.os.Environment;
import com.unovo.carmanager.R;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.utils
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/9 16:41
 * @version: V1.0
 */
public class Utils {
  private static MessageDigest md = null;

  public static String md5(final String c) {
    if (md == null) {
      try {
        md = MessageDigest.getInstance("MD5");
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
    }

    if (md != null) {
      md.update(c.getBytes());
      return byte2hex(md.digest());
    }
    return "";
  }

  public static String byte2hex(byte b[]) {
    String hs = "";
    String stmp = "";
    for (int n = 0; n < b.length; n++) {
      stmp = Integer.toHexString(b[n] & 0xff);
      if (stmp.length() == 1) {
        hs = hs + "0" + stmp;
      } else {
        hs = hs + stmp;
      }
      if (n < b.length - 1) hs = (new StringBuffer(String.valueOf(hs))).toString();
    }

    return hs.toUpperCase();
  }

  /**
   * 外置存储卡的路径
   */
  public static String getExternalStorePath() {
    if (isExistExternalStore()) {
      return Environment.getExternalStorageDirectory().getAbsolutePath();
    }
    return null;
  }

  /**
   * 是否有外存卡
   */
  public static boolean isExistExternalStore() {
    return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
  }

  public static File getVoicePathName() {
    if (!isExistExternalStore()) {
      ToastUtil.showMessage(R.string.media_ejected);
      return null;
    }

    File directory = new File(IMESSAGE_VOICE);
    if (!directory.exists() && !directory.mkdirs()) {
      ToastUtil.showMessage("Path to file could not be created");
      return null;
    }

    return directory;
  }

  /**
   * 计算语音文件的时间长度
   */
  public static int calculateVoiceTime(String file) {
    File _file = new File(file);
    if (!_file.exists()) {
      return 0;
    }
    // 650个字节就是1s
    int duration = (int) Math.ceil(_file.length() / 650);
    if (duration > 60) {
      return 60;
    }
    if (duration < 1) {
      return 1;
    }
    return duration;
  }

  static MediaPlayer mediaPlayer = null;

  public static void playNotifycationMusic(Context context, String voicePath) throws IOException {
    // paly music ...
    AssetFileDescriptor fileDescriptor = context.getAssets().openFd(voicePath);
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

  private static final SimpleDateFormat sFormatToday = new SimpleDateFormat("今天 HH:mm");
  private static final SimpleDateFormat sFormatThisYear = new SimpleDateFormat("MM/dd HH:mm");
  private static final SimpleDateFormat sFormatOtherYear = new SimpleDateFormat("yy/MM/dd HH:mm");
  private static final SimpleDateFormat sFormatMessageToday = new SimpleDateFormat("今天");
  private static final SimpleDateFormat sFormatMessageThisYear = new SimpleDateFormat("MM/dd");
  private static final SimpleDateFormat sFormatMessageOtherYear = new SimpleDateFormat("yy/MM/dd");

  public static String dayToNow(long time, boolean displayHour) {
    long nowMill = System.currentTimeMillis();

    long minute = (nowMill - time) / 60000;
    if (minute < 60) {
      if (minute <= 0) {
        return Math.max((nowMill - time) / 1000, 1) + "秒前"; // 由于手机时间的原因，有时候会为负，这时候显示1秒前
      } else {
        return minute + "分钟前";
      }
    }

    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTimeInMillis(time);
    int year = calendar.get(GregorianCalendar.YEAR);
    int month = calendar.get(GregorianCalendar.MONTH);
    int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

    calendar.setTimeInMillis(nowMill);
    Long timeObject = new Long(time);
    if (calendar.get(GregorianCalendar.YEAR) != year) { // 不是今年
      SimpleDateFormat temp = displayHour ? sFormatOtherYear : sFormatMessageOtherYear;
      return temp.format(timeObject);
    } else if (calendar.get(GregorianCalendar.MONTH) != month
        || calendar.get(GregorianCalendar.DAY_OF_MONTH) != day) { // 今年
      SimpleDateFormat temp = displayHour ? sFormatThisYear : sFormatMessageThisYear;
      return temp.format(timeObject);
    } else { // 今天
      SimpleDateFormat temp = displayHour ? sFormatToday : sFormatMessageToday;
      return temp.format(timeObject);
    }
  }

  public static String EXTERNAL_STOREPATH = getExternalStorePath();
  public static final String APPS_ROOT_DIR = EXTERNAL_STOREPATH + "/ECSDK_Demo";
  public static final String IMESSAGE_VOICE = EXTERNAL_STOREPATH + "/ECSDK_Demo/voice";
  public static final String IMESSAGE_IMAGE = EXTERNAL_STOREPATH + "/ECSDK_Demo/image";
  public static final String IMESSAGE_AVATAR = EXTERNAL_STOREPATH + "/ECSDK_Demo/avatar";
  public static final String IMESSAGE_FILE = EXTERNAL_STOREPATH + "/ECSDK_Demo/file";
  public static final String IMESSAGE_RICH_TEXT = EXTERNAL_STOREPATH + "/ECSDK_Demo/richtext";

  /**
   * 初始化应用文件夹目录
   */
  public static void initFileAccess() {
    File rootDir = new File(APPS_ROOT_DIR);
    if (!rootDir.exists()) {
      rootDir.mkdir();
    }

    File imessageDir = new File(IMESSAGE_VOICE);
    if (!imessageDir.exists()) {
      imessageDir.mkdir();
    }

    File imageDir = new File(IMESSAGE_IMAGE);
    if (!imageDir.exists()) {
      imageDir.mkdir();
    }

    File fileDir = new File(IMESSAGE_FILE);
    if (!fileDir.exists()) {
      fileDir.mkdir();
    }
    File avatarDir = new File(IMESSAGE_AVATAR);
    if (!avatarDir.exists()) {
      avatarDir.mkdir();
    }
    File richTextDir = new File(IMESSAGE_RICH_TEXT);
    if (!richTextDir.exists()) {
      richTextDir.mkdir();
    }
  }

  /** this criteria needs high accuracy, high power, and cost */

  public static Criteria createFineCriteria() {
    Criteria c = new Criteria();
    c.setAccuracy(Criteria.ACCURACY_FINE);//高精度
    c.setAltitudeRequired(true);//包含高度信息
    c.setBearingRequired(true);//包含方位信息
    c.setSpeedRequired(true);//包含速度信息
    c.setCostAllowed(true);//允许付费
    c.setPowerRequirement(Criteria.POWER_HIGH);//高耗电

    return c;
  }
}
