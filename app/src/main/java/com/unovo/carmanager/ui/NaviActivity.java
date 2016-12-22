package com.unovo.carmanager.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.unovo.carmanager.R;
import com.unovo.carmanager.ui.guid.GuideActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/28.
 */
public class NaviActivity extends Activity implements AMapNaviViewListener, AMapNaviListener {

  private AMapNaviView mAMapNaviView;
  private AMapNavi mAMapNavi;
  private NaviLatLng mEndLatlng = new NaviLatLng(39.925846, 116.432765);
  private NaviLatLng mStartLatlng = new NaviLatLng(39.925041, 116.437901);
  private final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
  private final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.navi);
    init(savedInstanceState);
  }

  private void init(Bundle savedInstanceState) {
    mAMapNaviView = (AMapNaviView) findViewById(R.id.Navimap);
    mAMapNaviView.onCreate(savedInstanceState);
    // 设置导航界面监听
    mAMapNaviView.setAMapNaviViewListener(this);

    //获取AmapNavi实例，并设置监听
    mAMapNavi = AMapNavi.getInstance(getApplicationContext());
    mAMapNavi.addAMapNaviListener(this);

    //起点终点
    sList.add(mStartLatlng);
    eList.add(mEndLatlng);

    // 设置模拟速度
    AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
    // 开启模拟导航
    AMapNavi.getInstance(this).startNavi(NaviType.EMULATOR);
  }

  //导航界面右下角功能设置按钮回调
  @Override public void onNaviSetting() {

  }

  //导航界面左下角返回按钮回调
  @Override public void onNaviCancel() {
    Intent intent = new Intent(NaviActivity.this, GuideActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    startActivity(intent);
    finish();
  }

  @Override public boolean onNaviBackClick() {
    return false;
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      Intent intent = new Intent(NaviActivity.this, GuideActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
      startActivity(intent);
      finish();
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mAMapNaviView.onSaveInstanceState(outState);
  }

  @Override protected void onResume() {
    super.onResume();
    mAMapNaviView.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    mAMapNaviView.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mAMapNaviView.onDestroy();
    mAMapNavi.stopNavi();
    mAMapNavi.destroy();
  }

  @Override public void onNaviMapMode(int i) {

  }

  @Override public void onNaviTurnClick() {

  }

  @Override public void onNextRoadClick() {

  }

  @Override public void onScanViewButtonClick() {

  }

  @Override public void onLockMap(boolean b) {

  }

  @Override public void onNaviViewLoaded() {

  }

  @Override public void onInitNaviFailure() {

  }

  @Override public void onInitNaviSuccess() {
    /**
     * 方法:
     *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
     * 参数:
     * @congestion 躲避拥堵
     * @avoidhightspeed 不走高速
     * @cost 避免收费
     * @hightspeed 高速优先
     * @multipleroute 多路径
     *
     * 说明:
     *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
     * 注意:
     *      不走高速与高速优先不能同时为true
     *      高速优先与避免收费不能同时为true
     */
    int strategy = 0;
    try {
      strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    //驾车路径计算
    mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
  }

  @Override public void onStartNavi(int i) {

  }

  @Override public void onTrafficStatusUpdate() {

  }

  @Override public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

  }

  @Override public void onGetNavigationText(int i, String s) {

  }

  @Override public void onEndEmulatorNavi() {

  }

  @Override public void onArriveDestination() {

  }

  @Override public void onCalculateRouteSuccess() {
    mAMapNavi.startNavi(NaviType.EMULATOR);
  }

  @Override public void onCalculateRouteFailure(int i) {

  }

  @Override public void onReCalculateRouteForYaw() {

  }

  @Override public void onReCalculateRouteForTrafficJam() {

  }

  @Override public void onArrivedWayPoint(int i) {

  }

  @Override public void onGpsOpenStatus(boolean b) {

  }

  @Override public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

  }

  @Override public void onNaviInfoUpdate(NaviInfo naviInfo) {

  }

  @Override
  public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

  }

  @Override public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

  }

  @Override public void showCross(AMapNaviCross aMapNaviCross) {

  }

  @Override public void hideCross() {

  }

  @Override public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

  }

  @Override public void hideLaneInfo() {

  }

  @Override public void onCalculateMultipleRoutesSuccess(int[] ints) {

  }

  @Override public void notifyParallelRoad(int i) {

  }

  @Override
  public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

  }

  @Override public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

  }

  @Override
  public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

  }
}

