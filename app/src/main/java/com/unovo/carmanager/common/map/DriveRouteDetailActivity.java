package com.unovo.carmanager.common.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.UIHelper;

public class DriveRouteDetailActivity extends BaseActivity {
  private DrivePath mDrivePath;
  private DriveRouteResult mDriveRouteResult;
  private TextView mTitleDriveRoute, mDesDriveRoute;
  private ListView mDriveSegmentList;
  private DriveSegmentListAdapter mDriveSegmentListAdapter;
  private ImageView mGuideBtn;

  private LatLng sLatLng, eLatLng;

  @Override public void init(Bundle savedInstanceState) {
    getIntentData();
    init();
  }

  @Override protected int getLayoutId() {
    return R.layout.activity_route_detail;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_route_detail;
  }

  private void init() {
    mTitleDriveRoute = (TextView) findViewById(R.id.firstline);
    mDesDriveRoute = (TextView) findViewById(R.id.secondline);
    mGuideBtn = (ImageView) findViewById(R.id.guide);
    String dur = AMapUtil.getFriendlyTime((int) mDrivePath.getDuration());
    String dis = AMapUtil.getFriendlyLength((int) mDrivePath.getDistance());
    mTitleDriveRoute.setText(dur + "(" + dis + ")");
    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
    mDesDriveRoute.setText("打车约" + taxiCost + "元");
    mDesDriveRoute.setVisibility(View.VISIBLE);
    configureListView();

    Bundle bundle = getIntent().getExtras();

    if (bundle != null) {
      sLatLng = bundle.getParcelable(Constants.START_POINT);
      eLatLng = bundle.getParcelable(Constants.END_POINT);
    }

    mGuideBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        UIHelper.openNaviGuide(DriveRouteDetailActivity.this, sLatLng, eLatLng);
      }
    });
  }

  private void configureListView() {
    mDriveSegmentList = (ListView) findViewById(R.id.bus_segment_list);
    mDriveSegmentListAdapter =
        new DriveSegmentListAdapter(this.getApplicationContext(), mDrivePath.getSteps());
    mDriveSegmentList.setAdapter(mDriveSegmentListAdapter);
  }

  private void getIntentData() {
    Intent intent = getIntent();
    if (intent == null) {
      return;
    }
    mDrivePath = intent.getParcelableExtra("drive_path");
    mDriveRouteResult = intent.getParcelableExtra("drive_result");
  }
}
