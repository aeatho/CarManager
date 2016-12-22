/**
 * Project Name:Android_Car_Example
 * File Name:RouteTask.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月3日下午2:38:10
 */

package com.unovo.carmanager.common.lbs;

import android.content.Context;
import android.content.Intent;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.FromAndTo;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.unovo.carmanager.R;
import com.unovo.carmanager.common.map.AMapUtil;
import com.unovo.carmanager.common.map.ToastUtil;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.utils.UIUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:RouteTask <br/>
 * Function: 封装的驾车路径规划 <br/>
 * Date: 2015年4月3日 下午2:38:10 <br/>
 *
 * @author yiyi.qi
 * @see
 * @since JDK 1.6
 */
public class RouteTask implements OnRouteSearchListener {

  private static RouteTask mRouteTask;

  private RouteSearch mRouteSearch;

  private PositionEntity mFromPoint;

  private PositionEntity mToPoint;

  private List<OnRouteCalculateListener> mListeners = new ArrayList<OnRouteCalculateListener>();

  public interface OnRouteCalculateListener {
    void onRouteCalculate(String distance, String duration);

    void onMarkDriveRoute(DriveRouteResult driveRouteResult);
  }

  public static RouteTask getInstance(Context context) {
    if (mRouteTask == null) {
      mRouteTask = new RouteTask(context);
    }
    return mRouteTask;
  }

  public PositionEntity getStartPoint() {
    return mFromPoint;
  }

  public void setStartPoint(PositionEntity fromPoint) {
    mFromPoint = fromPoint;
  }

  public PositionEntity getEndPoint() {
    return mToPoint;
  }

  public void setEndPoint(PositionEntity toPoint) {
    mToPoint = toPoint;
  }

  private RouteTask(Context context) {
    mRouteSearch = new RouteSearch(context);
    mRouteSearch.setRouteSearchListener(this);
  }

  public void search() {
    if (mFromPoint == null || mToPoint == null) {
      return;
    }

    FromAndTo fromAndTo =
        new FromAndTo(new LatLonPoint(mFromPoint.getLatitue(), mFromPoint.getLongitude()),
            new LatLonPoint(mToPoint.getLatitue(), mToPoint.getLongitude()));
    DriveRouteQuery driveRouteQuery =
        new DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");

    mRouteSearch.calculateDriveRouteAsyn(driveRouteQuery);
  }

  public void search(PositionEntity fromPoint, PositionEntity toPoint) {

    mFromPoint = fromPoint;
    mToPoint = toPoint;
    search();
  }

  public void addRouteCalculateListener(OnRouteCalculateListener listener) {
    synchronized (this) {
      if (mListeners.contains(listener)) return;
      mListeners.add(listener);
    }
  }

  public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
    synchronized (this) {
      mListeners.add(listener);
    }
  }

  @Override public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

  }

  @Override public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int resultCode) {
    if (resultCode == 1000) {
      if (driveRouteResult != null
          && driveRouteResult.getPaths() != null
          && !driveRouteResult.getPaths().isEmpty()) {
        synchronized (this) {
          for (OnRouteCalculateListener listener : mListeners) {
            List<DrivePath> drivepaths = driveRouteResult.getPaths();
            int distance = 0;
            int duration = 0;
            if (drivepaths.size() > 0) {
              DrivePath drivepath = drivepaths.get(0);
              distance = (int) drivepath.getDistance();
              duration = (int) (drivepath.getDuration());
              //float cost = driveRouteResult.getTaxiCost();
            }
            listener.onRouteCalculate(AMapUtil.getFriendlyLength(distance),
                AMapUtil.getFriendlyTime(duration));

            listener.onMarkDriveRoute(driveRouteResult);
          }
        }
      } else {
        UIUtils.getContext().sendBroadcast(new Intent(Constants.ROUTE_DRIVE_FAILED));
        ToastUtil.show(UIUtils.getContext(), R.string.no_result);
      }
    } else {
      UIUtils.getContext().sendBroadcast(new Intent(Constants.ROUTE_DRIVE_FAILED));
      ToastUtil.showerror(UIUtils.getContext(), resultCode);
    }
  }

  @Override public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {
  }

  @Override public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

  }
}
