package com.unovo.carmanager.ui.movement;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.UIHelper;

/**
 * Created by Administrator on 2016/8/5.
 */
public class FriendOnMapActivity extends BaseActivity {
  private MapView mapView;
  private AMap aMap;
  private LocationManager locationManager;
  private Button mBtnSendMessage;

  private double dLon, dLat;

  private String Fri_name;
  private String Fri_id;

  @Override protected int getLayoutId() {
    return R.layout.friendonmap;
  }

  @Override protected void init(Bundle savedInstanceState) {
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    mapView = (MapView) findViewById(R.id.FriOnMapView);
    //回调Mapview的onCreate方法
    mapView.onCreate(savedInstanceState);
    init();

    Intent intent = this.getIntent();
    Bundle bundle = intent.getExtras();
    Fri_name = bundle.getString("Name");
    Fri_id = bundle.getString(Constants.VOIP_ID);
    dLat = bundle.getDouble("Lat");
    dLon = bundle.getDouble("Lon");

    mActionBar.setTitle(Fri_name);

    //将经纬度封装LatLng
    LatLng pos = new LatLng(dLat, dLon);
    //创建一个设置经纬度的CameraUpdate
    CameraUpdate cameraUpdate = CameraUpdateFactory.changeLatLng(pos);
    //更新地图的显示区域
    aMap.moveCamera(cameraUpdate);
    //创建MarkerOption对象
    MarkerOptions markerOptions = new MarkerOptions();
    //设置MO位置
    markerOptions.position(pos);
    //设置好友名字
    markerOptions.title(Fri_name);
    //图标
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    markerOptions.draggable(true);
    //添加MK
    Marker marker = aMap.addMarker(markerOptions);
    marker.showInfoWindow();//设置默认显示信息窗

    mBtnSendMessage.setOnClickListener(new View.OnClickListener() {//发消息
      @Override public void onClick(View v) {
        UIHelper.startChattingAction(FriendOnMapActivity.this, Fri_id, Fri_name);
      }
    });
  }

  private void updatePosition(Location location) {
    LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
    //创建一个设置经纬度的CameraUpdate
    CameraUpdate cu = CameraUpdateFactory.changeLatLng(pos);
    //更新地图的显示区域
    aMap.moveCamera(cu);
    //清除所有覆盖物
    aMap.clear();
    //创建一个MO对象
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(pos);
    //图标
    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    markerOptions.draggable(true);
    //添加Mo
    Marker marker = aMap.addMarker(markerOptions);
  }

  //初始化AMap
  private void init() {
    if (aMap == null) {
      aMap = mapView.getMap();
      //创建一个设置放大级别的CameraUpdate
      CameraUpdate cu = CameraUpdateFactory.zoomTo(14);
      //设置默认放大级别
      aMap.moveCamera(cu);
      //创建一个更改倾斜度的CU
      CameraUpdate tiltUpdate = CameraUpdateFactory.changeTilt(30);
      //更改倾斜度
      aMap.moveCamera(tiltUpdate);
    }

    mBtnSendMessage = (Button) findViewById(R.id.btn_sendMessages);
  }

  @Override protected void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }
}
