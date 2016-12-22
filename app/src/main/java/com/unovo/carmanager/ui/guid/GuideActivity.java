package com.unovo.carmanager.ui.guid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.common.lbs.LocationTask;
import com.unovo.carmanager.common.lbs.PoiSearchTask;
import com.unovo.carmanager.common.lbs.PositionEntity;
import com.unovo.carmanager.common.lbs.RegeocodeTask;
import com.unovo.carmanager.common.lbs.RouteTask;
import com.unovo.carmanager.common.map.DriveRouteColorfulOverLay;
import com.unovo.carmanager.common.map.DriveRouteDetailActivity;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.hotel.HotelListActivity;
import com.unovo.carmanager.utils.UIUtils;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.message
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/10 16:56
 * @version: V1.0
 */
public class GuideActivity extends BaseActivity
    implements AMap.OnCameraChangeListener, AMap.OnMapLoadedListener,
    LocationTask.OnLocationGetListener, RegeocodeTask.onRegecodeGetListener, View.OnClickListener,
    RouteTask.OnRouteCalculateListener, PoiSearchTask.OnPoiGetListener {
  private MapView mMapView;
  private AMap mAmap;
  private TextView mAddressTextView;
  private Button mDestinationButton;
  private Marker mPositionMark;
  private LatLng mStartPosition;
  private LatLng mEndPosition;
  private TextView mDesitinationText;
  private ImageView mLocationImage;
  private LinearLayout mFromToContainer, mBottomContainer, mDetailContaner;
  private ImageView mImgLocation, mImgSwitch;
  private TextView mLookforWeather, mLookforHotel, mDetailRoute;

  private LocationTask mLocationTask;
  private RegeocodeTask mRegeocodeTask;
  private PoiSearchTask mPoiSearchTask;
  private int REQUEST_SEARCH_ADDRESS = 999;

  private static final float DEFAULT_ZOOM = 14;

  @Override protected int getActionBarTitle() {
    return R.string.title_guide;
  }

  @Override protected int getLayoutId() {
    return R.layout.activity_routeguide;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mLocationTask = new LocationTask(UIUtils.getContext());
    mLocationTask.setOnLocationGetListener(this);
    mRegeocodeTask = new RegeocodeTask(UIUtils.getContext());
    mRegeocodeTask.setOnRegecodeGetListener(this);
    mPoiSearchTask = new PoiSearchTask(UIUtils.getContext());
    mPoiSearchTask.setOnLocationGetListener(this);
    RouteTask.getInstance(getApplicationContext()).addRouteCalculateListener(this);

    mMapView = (MapView) findViewById(R.id.map);
    mMapView.onCreate(savedInstanceState);
    mAmap = mMapView.getMap();
    mAmap.moveCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

    mAmap.setTrafficEnabled(true);// 显示实时交通状况

    mAmap.getUiSettings().setZoomControlsEnabled(false);
    mAmap.getUiSettings().setScaleControlsEnabled(true);
    mAmap.setOnMapLoadedListener(this);
    mAmap.setOnCameraChangeListener(this);

    initViews();

    registerReceiver(new String[] { Constants.ROUTE_DRIVE_FAILED });
  }

  private void initViews() {
    mAddressTextView = (TextView) findViewById(R.id.address_text);
    mDestinationButton = (Button) findViewById(R.id.destination_button);
    mDestinationButton.setOnClickListener(this);
    mDesitinationText = (TextView) findViewById(R.id.destination_text);
    mDesitinationText.setOnClickListener(this);
    mLocationImage = (ImageView) findViewById(R.id.location_image);
    mLocationImage.setOnClickListener(this);
    mFromToContainer = (LinearLayout) findViewById(R.id.fromto_container);
    mBottomContainer = (LinearLayout) findViewById(R.id.bottom_layout);
    mImgLocation = (ImageView) findViewById(R.id.imgLocation);
    mImgSwitch = (ImageView) findViewById(R.id.imgSwitch);
    mLookforWeather = (TextView) findViewById(R.id.lookforWeather);
    mLookforHotel = (TextView) findViewById(R.id.lookforHotel);
    mDetailRoute = (TextView) findViewById(R.id.detail);
    mDetailContaner = (LinearLayout) findViewById(R.id.detailContent);

    mImgLocation.setOnClickListener(this);
    mImgSwitch.setOnClickListener(this);
    mLookforWeather.setOnClickListener(this);
    mLookforHotel.setOnClickListener(this);
  }

  @Override protected void onResume() {
    super.onResume();
    mMapView.onResume();
    if (mStartPosition == null || (mStartPosition.latitude == 0 && mStartPosition.longitude == 0)) {
      mLocationTask.startSingleLocate();
    } else {
      mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
          new LatLng(mStartPosition.latitude, mStartPosition.longitude),
          currentZoom == 0 ? DEFAULT_ZOOM : currentZoom + 0.000001f));
    }
  }

  @Override protected void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mMapView.onDestroy();
    mLocationTask.onDestroy();
    unregisterReceiver(internalReceiver);
  }

  private void hideView() {
    mFromToContainer.setVisibility(View.GONE);
    mBottomContainer.setVisibility(View.GONE);
  }

  private void showView() {
    mFromToContainer.setVisibility(View.VISIBLE);
    mBottomContainer.setVisibility(View.VISIBLE);
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.destination_button:
        if (mStartPosition == null) {
          showToast("请选择出发地");
          return;
        }
        if (TextUtils.isEmpty(address)) {
          showToast("请输入目的地");
          return;
        }
        showWaitDialog("路径规划中,请稍后...");
        mPoiSearchTask.search(address,
            RouteTask.getInstance(getApplicationContext()).getStartPoint().getCity());
        break;
      case R.id.location_image:
        mLocationTask.startSingleLocate();
        break;
      case R.id.destination_text:
        Intent destinationIntent = new Intent(this, DestinationActivity.class);
        startActivityForResult(destinationIntent, REQUEST_SEARCH_ADDRESS);
        break;
      case R.id.imgLocation:
      case R.id.imgSwitch:
        switchPositonForWeatherAndHotel();
        break;
      case R.id.lookforWeather:
        showWheather();
        break;
      case R.id.lookforHotel:
        showHotel();
        break;
      default:
        break;
    }
  }

  private void showHotel() {
    if (isDestination && RouteTask.getInstance(getApplicationContext()).getEndPoint() == null) {
      showToast("请先输入目的地");
      return;
    }

    startActivity(new Intent(GuideActivity.this, HotelListActivity.class));
  }

  private void showWheather() {
    if (isDestination && RouteTask.getInstance(getApplicationContext()).getEndPoint() == null) {
      showToast("请先输入目的地");
      return;
    }

    String city =
        !isDestination ? RouteTask.getInstance(getApplicationContext()).getStartPoint().getCity()
            : RouteTask.getInstance(getApplicationContext()).getEndPoint().getCity();
    new WeatherDialog(this, city).show();
  }

  private boolean isDestination = false;
  private LatLng selectPostion;

  private void switchPositonForWeatherAndHotel() {
    isDestination = !isDestination;
    mImgLocation.setImageResource(!isDestination ? R.drawable.icon_start : R.drawable.icon_end);
    selectPostion = !isDestination ? mStartPosition : mEndPosition;
  }

  private String address;

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_SEARCH_ADDRESS && resultCode == Activity.RESULT_OK) {
      address = data.getStringExtra(Constants.RESULT_ADDRESS);
      mDesitinationText.setText(address);
      mDestinationButton.setVisibility(View.VISIBLE);
      mDestinationButton.setText(R.string.find_rout);
      mDetailContaner.setVisibility(View.GONE);

      mAmap.clear();

      addDefaultMark();
    }
  }

  @Override public void onCameraChange(CameraPosition cameraPosition) {
    hideView();
  }

  private float currentZoom;

  @Override public void onCameraChangeFinish(CameraPosition cameraPosition) {
    showView();
    if (mDestinationButton.getVisibility() == View.VISIBLE) {
      mStartPosition = cameraPosition.target;
      mRegeocodeTask.search(mStartPosition.latitude, mStartPosition.longitude);
    }
    currentZoom = cameraPosition.zoom;
  }

  @Override public void onMapLoaded() {
    addDefaultMark();

    mLocationTask.startSingleLocate();
  }

  private void addDefaultMark() {
    MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.setFlat(true);
    markerOptions.anchor(0.5f, 0.5f);
    markerOptions.position(new LatLng(0, 0));
    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
        BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_start)));
    mPositionMark = mAmap.addMarker(markerOptions);
    mPositionMark.setPositionByPixels(mMapView.getWidth() / 2, mMapView.getHeight() / 2);
  }

  @Override public void onLocationGet(PositionEntity entity) {
    mAddressTextView.setText(entity.getAddress());
    RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);

    mStartPosition = new LatLng(entity.getLatitue(), entity.getLongitude());
    CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(mStartPosition, DEFAULT_ZOOM);
    mAmap.animateCamera(cameraUpate);
  }

  @Override public void onRegecodeGet(PositionEntity entity) {
    mAddressTextView.setText(entity.getAddress());
    entity.setLatitue(mStartPosition.latitude);
    entity.setLongitude(mStartPosition.longitude);
    RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
  }

  @Override public void onRouteCalculate(String distance, String duration) {
    mDetailContaner.setVisibility(View.VISIBLE);
    mDesitinationText.setText(
        RouteTask.getInstance(getApplicationContext()).getEndPoint().getAddress());
    mDetailRoute.setText(String.format("距离%s,用时%s", distance, duration));
    mDestinationButton.setVisibility(View.VISIBLE);
    mDestinationButton.setText(R.string.find_rout);
  }

  @Override public void onMarkDriveRoute(final DriveRouteResult driveRouteResult) {
    mAmap.clear();// 清理地图上的所有覆盖物

    final DrivePath drivePath = driveRouteResult.getPaths().get(0);
    DriveRouteColorfulOverLay drivingRouteOverlay =
        new DriveRouteColorfulOverLay(mAmap, drivePath, driveRouteResult.getStartPos(),
            driveRouteResult.getTargetPos(), null);
    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
    drivingRouteOverlay.removeFromMap();
    drivingRouteOverlay.addToMap();
    drivingRouteOverlay.zoomToSpan();

    mDestinationButton.setVisibility(View.VISIBLE);
    mDestinationButton.setText(R.string.start_nav);

    hideWaitDialog();

    mDetailContaner.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(GuideActivity.this, DriveRouteDetailActivity.class);
        intent.putExtra("drive_path", drivePath);
        intent.putExtra("drive_result", driveRouteResult);
        startActivity(intent);
      }
    });
  }

  @Override public void onPoiSearchGet(PositionEntity entity) {
    mEndPosition = new LatLng(entity.getLatitue(), entity.getLongitude());
    //开始搜索路径
    RouteTask.getInstance(getApplicationContext()).setEndPoint(entity);
    RouteTask.getInstance(getApplicationContext()).search();
  }

  @Override public void onPoiSearchGetFailed() {
    hideWaitDialog();
  }

  private InternalReceiver internalReceiver;

  /**
   * 注册广播
   */
  protected final void registerReceiver(String[] actionArray) {
    if (actionArray == null) {
      return;
    }
    IntentFilter intentfilter = new IntentFilter();
    for (String action : actionArray) {
      intentfilter.addAction(action);
    }
    if (internalReceiver == null) {
      internalReceiver = new InternalReceiver();
    }
    registerReceiver(internalReceiver, intentfilter);
  }

  private class InternalReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent == null || TextUtils.isEmpty(intent.getAction())) {
        return;
      }
      if (Constants.ROUTE_DRIVE_FAILED.equals(intent.getAction())) {
        hideWaitDialog();
      }
    }
  }
}
