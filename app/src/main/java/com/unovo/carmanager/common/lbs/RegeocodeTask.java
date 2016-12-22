/**
 * Project Name:Android_Car_Example
 * File Name:RegeocodeTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月2日下午6:24:53
 */

package com.unovo.carmanager.common.lbs;

import android.content.Context;
import android.text.TextUtils;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * ClassName:RegeocodeTask <br/>
 * Function: 简单的封装的逆地理编码功能 <br/>
 * Date: 2015年4月2日 下午6:24:53 <br/>
 *
 * @author yiyi.qi
 * @see
 * @since JDK 1.6
 */
public class RegeocodeTask implements OnGeocodeSearchListener {
  private static final float SEARCH_RADIUS = 50;
  private onRegecodeGetListener mOnRegecodeGetListener;

  public interface onRegecodeGetListener {
    void onRegecodeGet(PositionEntity entity);
  }

  private GeocodeSearch mGeocodeSearch;

  public RegeocodeTask(Context context) {
    mGeocodeSearch = new GeocodeSearch(context);
    mGeocodeSearch.setOnGeocodeSearchListener(this);
  }

  public void search(double latitude, double longitude) {
    RegeocodeQuery regecodeQuery =
        new RegeocodeQuery(new LatLonPoint(latitude, longitude), SEARCH_RADIUS, GeocodeSearch.AMAP);
    mGeocodeSearch.getFromLocationAsyn(regecodeQuery);
  }

  public void setOnRegecodeGetListener(onRegecodeGetListener onRegecodeGetListener) {
    mOnRegecodeGetListener = onRegecodeGetListener;
  }

  @Override public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

  }

  @Override public void onRegeocodeSearched(RegeocodeResult regeocodeReult, int resultCode) {
    if (resultCode == 1000) {
      if (regeocodeReult != null
          && regeocodeReult.getRegeocodeAddress() != null
          && mOnRegecodeGetListener != null) {
        String address = regeocodeReult.getRegeocodeAddress().getFormatAddress();
        String city = regeocodeReult.getRegeocodeAddress().getCity();
        if (TextUtils.isEmpty(city)) city = regeocodeReult.getRegeocodeAddress().getProvince();
        PositionEntity entity = new PositionEntity();
        entity.setAddress(address);
        entity.setCity(city);
        mOnRegecodeGetListener.onRegecodeGet(entity);
      }
    }
  }
}
