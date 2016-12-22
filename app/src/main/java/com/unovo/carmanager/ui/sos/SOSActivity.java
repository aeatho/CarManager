package com.unovo.carmanager.ui.sos;

import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.bean.SOSType;
import com.unovo.carmanager.common.lbs.LocationTask;
import com.unovo.carmanager.common.lbs.PositionEntity;
import com.unovo.carmanager.common.network.HttpClient;
import com.unovo.carmanager.utils.DateUtils;
import com.unovo.carmanager.utils.DialogHelper;
import com.unovo.carmanager.utils.Settings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.sos
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/24 18:36
 * @version: V1.0
 */
public class SOSActivity extends BaseActivity
    implements LocationImpl, LocationTask.OnLocationGetListener, LocationSource,
    AMapLocationListener {
  private LocationUtils mLocationUtils;
  private boolean mSucceed;

  private MapView mMapView;
  private TextView mEditText;
  private Button mBtn;

  private AMap aMap;

  private String userid;
  private double lat;
  private double lon;
  private double speed;

  @Override protected int getLayoutId() {
    return R.layout.activity_emergency;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_help;
  }

  @Override protected void init(Bundle savedInstanceState) {
    //获取地图控件引用
    mMapView = (MapView) findViewById(R.id.smallmap);
    //在activity执行oncreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
    mMapView.onCreate(savedInstanceState);

    if (aMap == null) {
      aMap = mMapView.getMap();
      setUpMap();
    }

    mBtn = (Button) findViewById(R.id.btn_emer);
    mEditText = (TextView) findViewById(R.id.et_show);

    userid = Settings.getUID(this);

    mBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        sos();
      }
    });
  }

  private void sos() {
    showWaitDialog("上传中...");

    Call<String> sos = HttpClient.getInstance()
        .getAPIs()
        .sos(userid, lat, lon, DateUtils.getCurrentTimeStap(), speed, SOSType.NROMAL.getType());

    sos.enqueue(new Callback<String>() {
      @Override public void onResponse(Call<String> call, Response<String> response) {
        hideWaitDialog();
        if (response.code() == 200 && response.body() != null) {
          DialogHelper.showMessageDialog(SOSActivity.this, "上传GPS信息成功");
        }
      }

      @Override public void onFailure(Call<String> call, Throwable t) {
        hideWaitDialog();
        showToast(t.getMessage());
      }
    });
  }

  private void setUpMap() {
    aMap.setLocationSource(this);//设置定位监听
    aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
    aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示
    aMap.getUiSettings().setZoomControlsEnabled(false);//缩放按钮
    aMap.getUiSettings().setScaleControlsEnabled(true);
    aMap.setMyLocationEnabled(true);//设置为true表示显示定位层可触发定位，fals表示颖仓定位层并不可触发定位，默认是false
    aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
  }

  @Override public void UpdateLocation(Location location) {
    updateView(location);
  }

  @Override public void UpdateStatus(String provider, int status, Bundle extras) {

  }

  @Override public void UpdateGPSStatus(GpsStatus pGpsStatus) {

  }

  @Override protected void onResume() {
    super.onResume();
    mLocationUtils = new LocationUtils(this);//创建工具类对象
    mSucceed = mLocationUtils.initLocationListener(this);//调用工具类中的初始化方法。

    if (mSucceed) {
      Location location = mLocationUtils.getLastKnownLocation();
      //使用location来更新EditText信息
      updateView(location);
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (mSucceed) {
      mLocationUtils.removeLocationListener();//使用完后移除监听
    }
    deactivate();
  }

  @Override public void onLocationGet(PositionEntity entity) {

  }

  //更新ET中内容
  private void updateView(Location newLocation) {
    if (newLocation != null) {
      StringBuffer sb = new StringBuffer();
      sb.append("实时的位置信息：\n");
      sb.append("经度：");
      sb.append(newLocation.getLongitude());
      lon = newLocation.getLongitude();
      lat = newLocation.getLatitude();
      sb.append("\n纬度：");
      sb.append(newLocation.getLatitude());
      sb.append("\n高度：");
      sb.append(newLocation.getAltitude());
      sb.append("  米");
      sb.append("\n速度：");
      sb.append(newLocation.getSpeed() * 3.6);
      speed = newLocation.getSpeed() * 3.6;
      sb.append("  km/h");
      sb.append("\n方向：");
      sb.append(newLocation.getBearing());
      mEditText.setText(sb.toString());

      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          uploadGPS();
        }
      }, 3000);
    } else {
      //如果传入的Location对象为空，则清空EditText
      mEditText.setText("");
    }
  }

  private void uploadGPS() {
    Call<String> uploadGPS = HttpClient.getInstance()
        .getAPIs()
        .uploadGPS(userid, lat, lon, DateUtils.getCurrentTimeStap(), speed);

    uploadGPS.enqueue(new Callback<String>() {
      @Override public void onResponse(Call<String> call, Response<String> response) {
        if (response.code() == 200 && response.body() != null) {

        }
      }

      @Override public void onFailure(Call<String> call, Throwable t) {
      }
    });
  }

  private OnLocationChangedListener mListener;
  private AMapLocationClient mLocationClient;

  @Override public void activate(OnLocationChangedListener listener) {
    mListener = listener;
    if (mLocationClient == null) {
      mLocationClient = new AMapLocationClient(this);
      AMapLocationClientOption mLocationClientOption = new AMapLocationClientOption();
      //设置定位监听
      mLocationClient.setLocationListener(this);
      //设置定位模式为高精度
      mLocationClientOption.setLocationMode(
          AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
      mLocationClient.setLocationOption(mLocationClientOption);
      // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
      // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
      // 在定位结束后，在合适的生命周期调用onDestroy()方法
      // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
      mLocationClient.startLocation();
    }
  }

  @Override public void deactivate() {
    mListener = null;
    if (mLocationClient != null) {
      mLocationClient.stopLocation();
      mLocationClient.onDestroy();
    }
    mLocationClient = null;
  }

  @Override public void onLocationChanged(AMapLocation aMapLocation) {
    //定位回调
    if (mListener != null && aMapLocation != null) {
      if (aMapLocation.getErrorCode() == 0) {
        //错误返回码为0  就是定位成功
        mListener.onLocationChanged(aMapLocation);
      }
    }
  }
}
