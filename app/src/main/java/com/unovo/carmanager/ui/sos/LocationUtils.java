package com.unovo.carmanager.ui.sos;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.unovo.carmanager.utils.Utils;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.sos
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/24 18:51
 * @version: V1.0
 */
public class LocationUtils {
  private static final long MIN_TIME_UPDATE = 1000 * 60;
  private static final float MIN_DISTANCE_UPDATE = 0;

  private LocationImpl mLocationHelper;
  private LocationManager mLocationManager;
  private String pProvider;
  private MyLocationListener mLocationListener;
  private GpsStatus.Listener mGpsStatusListener;

  private LocationUtils() {
  }

  public LocationUtils(LocationImpl pInterface) {
    this.mLocationHelper = pInterface;
  }

  //初始化监听
  public boolean initLocationListener(Context pContext) {
    try {
      //创建localManager对象
      mLocationManager = (LocationManager) pContext.getSystemService(Context.LOCATION_SERVICE);
      pProvider = mLocationManager.getBestProvider(Utils.createFineCriteria(), true);

      mLocationManager.addGpsStatusListener(mGpsStatusListener = new MyGpsStatusListener());

      mLocationManager.requestLocationUpdates(pProvider, MIN_TIME_UPDATE, MIN_DISTANCE_UPDATE,
          mLocationListener = new MyLocationListener());

      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  //
  //该方法执行的位置需要特别注意，如果是在Activity对象中，根据Activity的生命周期，
  // onPause方法中比较合适，因为onStop、onDestroy两个方法在异常情况下不会被执行。

  public void removeLocationListener() {
    mLocationManager.removeUpdates(mLocationListener);
    mLocationManager.removeGpsStatusListener(mGpsStatusListener);
  }

  public Location getLastKnownLocation() {
    return mLocationManager.getLastKnownLocation(pProvider);
  }

  private class MyLocationListener implements LocationListener {
    @Override public void onLocationChanged(Location location) {
      mLocationHelper.UpdateLocation(location);
    }

    @Override public void onStatusChanged(String provider, int status, Bundle extras) {
      mLocationHelper.UpdateStatus(provider, status, extras);
    }

    @Override public void onProviderEnabled(String provider) {

    }

    @Override public void onProviderDisabled(String provider) {

    }
  }

  //实现动态更新GPS卫星状态信息
  private class MyGpsStatusListener implements GpsStatus.Listener {
    @Override public void onGpsStatusChanged(int event) {
      GpsStatus pGpsStatus = mLocationManager.getGpsStatus(null);

      mLocationHelper.UpdateGPSStatus(pGpsStatus);
    }
  }
}
