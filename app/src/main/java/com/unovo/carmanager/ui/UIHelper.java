package com.unovo.carmanager.ui;

import android.content.Context;
import android.content.Intent;
import com.unovo.carmanager.ui.chat.ChattingActivity;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/9 12:06
 * @version: V1.0
 */
public class UIHelper {

  /**
   * 聊天界面
   */
  public static void startChattingAction(Context context, String contactid, String username) {
    startChattingAction(context, contactid, username, false);
  }

  public static void startChattingAction(Context context, String contactid, String username,
      boolean clearTop) {
    Intent intent = new Intent(context, ChattingActivity.class);
    if (clearTop) {
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
    intent.putExtra(ChattingActivity.RECIPIENTS, contactid);
    intent.putExtra(ChattingActivity.CONTACT_USER, username);
    context.startActivity(intent);
  }
}
