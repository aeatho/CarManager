package com.unovo.carmanager.ui.sos;

import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.sos
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/24 18:35
 * @version: V1.0
 */
public interface LocationImpl {

  void UpdateLocation(Location location);//位置信息发生改变

  void UpdateStatus(String provider, int status, Bundle extras);//位置状态发生改变

  void UpdateGPSStatus(GpsStatus pGpsStatus);//GPS状态发生改变
}
