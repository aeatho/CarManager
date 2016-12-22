package com.unovo.carmanager.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.amap.api.maps.model.LatLng;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.chat.ChattingActivity;
import com.unovo.carmanager.ui.nav.NaviActivity;

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

  private static void startChattingAction(Context context, String contactid, String username,
      boolean clearTop) {
    Intent intent = new Intent(context, ChattingActivity.class);
    if (clearTop) {
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
    intent.putExtra(ChattingActivity.RECIPIENTS, contactid);
    intent.putExtra(ChattingActivity.CONTACT_USER, username);
    context.startActivity(intent);
  }

  public static void openNaviGuide(Context context, LatLng startPoint, LatLng endPoint) {
    Bundle bundle = new Bundle();
    bundle.putParcelable(Constants.START_POINT, startPoint);
    bundle.putParcelable(Constants.END_POINT, endPoint);

    Intent intent = new Intent(context, NaviActivity.class);
    intent.putExtras(bundle);

    context.startActivity(intent);
  }
}
