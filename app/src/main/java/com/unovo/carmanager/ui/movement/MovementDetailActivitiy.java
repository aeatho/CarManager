package com.unovo.carmanager.ui.movement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.utils.DialogHelper;

import static com.unovo.carmanager.R.id.btnJoin;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MovementDetailActivitiy extends BaseActivity {

  private String title;
  private String desc;
  private String place;
  private String movementTime;

  private ImageView mGoMap1, mGoMap2, mGoMap3;

  private TextView mTvTitle, mTvDesc, mTvPlace, mTvMoTime;

  @Override protected int getLayoutId() {
    return R.layout.activity_active_detail;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_active_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mTvTitle = (TextView) findViewById(R.id.tvTitle);
    mTvDesc = (TextView) findViewById(R.id.tvDesc);
    mTvPlace = (TextView) findViewById(R.id.tvPlace);
    mTvMoTime = (TextView) findViewById(R.id.tvMovementTime);

    mGoMap1 = (ImageView) findViewById(R.id.GoMap1);
    mGoMap2 = (ImageView) findViewById(R.id.GoMap2);
    mGoMap3 = (ImageView) findViewById(R.id.GoMap3);

    findViewById(btnJoin).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        joinGroup();
      }
    });

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      title = bundle.getString(Constants.TITLE);
      desc = bundle.getString(Constants.DESCRIPTION);
      place = bundle.getString(Constants.ACTIVITYPLACE);
      movementTime = bundle.getString(Constants.ACTIVITYTIME);
    }

    mTvTitle.setText(title);
    mTvDesc.setText(desc);
    mTvPlace.setText(place);
    mTvMoTime.setText(movementTime);

    mGoMap1.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(MovementDetailActivitiy.this, FriendOnMapActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("Name", "张强");
        bundle.putDouble("Lat", 31.2107720000);
        bundle.putDouble("Lon", 121.5982830000);
        intent.putExtras(bundle);

        startActivity(intent);
      }
    });

    mGoMap2.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(MovementDetailActivitiy.this, FriendOnMapActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("Name", "刘庆宇");
        bundle.putDouble("Lat", 31.2115750000);
        bundle.putDouble("Lon", 121.6298950000);
        intent.putExtras(bundle);

        startActivity(intent);
      }
    });

    mGoMap3.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(MovementDetailActivitiy.this, FriendOnMapActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("Name", "余家");
        bundle.putDouble("Lat", 31.2122870000);
        bundle.putDouble("Lon", 121.4893000000);
        intent.putExtras(bundle);

        startActivity(intent);
      }
    });
  }

  private void joinGroup() {
    showWaitDialog();

    new Handler().postDelayed(new Runnable() {
      public void run() {
        hideWaitDialog();
        DialogHelper.showMessageDialog(MovementDetailActivitiy.this, "加入活动成功！");
      }
    }, 700);
  }
}
