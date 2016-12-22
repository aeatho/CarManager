package com.unovo.carmanager.ui.nav;

import android.os.Bundle;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.model.NaviLatLng;
import com.unovo.carmanager.R;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.nav.base.BaseNaviActivity;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.nav
 * @Description: TODO
 * @author: loQua.Xee
 * @email: shyscool@163.com
 * @date: 2016/12/22 13:52
 * @version: V1.0
 */
public class NaviActivity extends BaseNaviActivity {
  @Override protected int getLayoutId() {
    return R.layout.activity_basic_navi;
  }

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);

    mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
    mAMapNaviView.onCreate(savedInstanceState);
    mAMapNaviView.setAMapNaviViewListener(this);

    Bundle bundle = getIntent().getExtras();

    if (bundle != null) {
      LatLng sLatLng = bundle.getParcelable(Constants.START_POINT);
      if (sLatLng != null) sList.add(new NaviLatLng(sLatLng.latitude, sLatLng.longitude));

      LatLng eLatLng = bundle.getParcelable(Constants.END_POINT);
      if (eLatLng != null) eList.add(new NaviLatLng(eLatLng.latitude, eLatLng.longitude));
    }
  }

  @Override public void onInitNaviSuccess() {
    super.onInitNaviSuccess();
    /**
     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
     *
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
     */
    int strategy = 0;
    try {
      //再次强调，最后一个参数为true时代表多路径，否则代表单路径
      strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
  }

  @Override public void onCalculateRouteSuccess() {
    super.onCalculateRouteSuccess();
    //		mAMapNavi.startNavi(NaviType.EMULATOR);
  }
}
