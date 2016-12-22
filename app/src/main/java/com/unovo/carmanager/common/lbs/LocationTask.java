package com.unovo.carmanager.common.lbs;

import android.content.Context;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: apartment_app-V2
 * @Location: com.unovo.apartment.v2.lbs
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 16/4/14 上午12:04
 * @version: V1.0
 */
public class LocationTask implements AMapLocationListener {
  //声明AMapLocationClient类对象
  private AMapLocationClient mLocationClient = null;
  //声明mLocationOption对象
  private AMapLocationClientOption mLocationOption = null;

  private OnLocationGetListener mOnLocationGetlisGetListener;

  public interface OnLocationGetListener {
    void onLocationGet(PositionEntity entity);
  }

  public LocationTask(Context context) {
    mLocationClient = new AMapLocationClient(context);
    mLocationOption = new AMapLocationClientOption();
    //设置定位回调监听
    mLocationClient.setLocationListener(this);

    initLocationOption();
  }

  public void setOnLocationGetListener(OnLocationGetListener onGetLocationListener) {
    mOnLocationGetlisGetListener = onGetLocationListener;
  }

  private void initLocationOption() {
    //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    //设置是否返回地址信息,默认返回地址信息
    mLocationOption.setNeedAddress(true);
    //设置是否强制刷新WIFI，默认为强制刷新
    mLocationOption.setWifiActiveScan(true);
    //设置是否允许模拟位置,默认为false，不允许模拟位置
    mLocationOption.setMockEnable(false);
  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    PositionEntity entity = null;
    System.out.println(aMapLocation);
    if (mOnLocationGetlisGetListener != null
        && aMapLocation != null
        && aMapLocation.getErrorCode() == 0) {
      entity = new PositionEntity();
      entity.setLatitue(aMapLocation.getLatitude());
      entity.setLongitude(aMapLocation.getLongitude());
      entity.setAddress(aMapLocation.getAddress());
      entity.setCity(aMapLocation.getCity());
    }
    if (mOnLocationGetlisGetListener != null && entity != null) {
      mOnLocationGetlisGetListener.onLocationGet(entity);
    }
  }

  /**
   * 开启单次定位
   *
   * 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
   */
  public void startSingleLocate() {
    //设置是否只定位一次,默认为false
    mLocationOption.setOnceLocation(true);
    //给定位客户端对象设置定位参数
    mLocationClient.setLocationOption(mLocationOption);
    //开启定位
    mLocationClient.startLocation();
  }

  /**
   * 开启多次定位
   */
  public void startLocate() {
    //设置是否只定位一次,默认为false
    mLocationOption.setOnceLocation(false);
    //设置定位间隔,单位毫秒,默认为2000ms
    mLocationOption.setInterval(2000);
    //给定位客户端对象设置定位参数
    mLocationClient.setLocationOption(mLocationOption);
    //开启定位
    mLocationClient.startLocation();
  }

  /**
   * 停止定位
   */
  public void stopLocate() {
    mLocationClient.stopLocation();
  }

  /**
   * 销毁定位资源
   */
  public void onDestroy() {
    mOnLocationGetlisGetListener = null;
    if (mLocationClient != null) {
      mLocationClient.stopLocation();
      mLocationClient.onDestroy();
    }
    mLocationClient = null;
  }
}