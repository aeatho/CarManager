package com.unovo.carmanager.ui.movement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.bean.MovementInfo;
import com.unovo.carmanager.common.network.HttpClient;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.widget.EmptyLayout;
import com.unovo.carmanager.widget.LoadNextListView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/7/29.
 */
public class MovementActivity extends BaseActivity
    implements LoadNextListView.OnLoadNextListener, AdapterView.OnItemClickListener {
  private LoadNextListView mListView;
  private MovementAdapter mAdapter;
  private EmptyLayout mEmptyLayout;

  public int aid;
  public String title;
  public String desc;
  public String movementTime;
  public String place;
  public boolean status;
  public String bywho;
  public String createTime;

  @Override protected int getLayoutId() {
    return R.layout.activity_movement;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_active;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mListView = (LoadNextListView) findViewById(R.id.listView_movement);
    mListView.setAdapter(mAdapter = new MovementAdapter(this));
    mListView.setOnLoadNextListener(this);
    mListView.setOnItemClickListener(this);
    mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    requestData();
  }

  //request data from network
  private void requestData() {
    Call<List<MovementInfo>> movement = HttpClient.getInstance()
        .getAPIs()
        .movement(aid, title, desc, movementTime, place, status, bywho, createTime);

    movement.enqueue(new Callback<List<MovementInfo>>() {
      @Override
      public void onResponse(Call<List<MovementInfo>> call, Response<List<MovementInfo>> response) {
        if (response.code() == 200) {
          List<MovementInfo> tempMovement = response.body();

          if (tempMovement != null && !tempMovement.isEmpty()) {
            mAdapter.setData(response.body());
            if (tempMovement.size() < Constants.PAGE_SIZE) mListView.onComplete(false);

            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
          }
        }
      }

      @Override public void onFailure(Call<List<MovementInfo>> call, Throwable t) {
        t.printStackTrace();
        //如果加载数据失败，PageIndex需要--
      }
    });
  }

  @Override public void onLoadNext() {
    requestData();
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    MovementInfo item = (MovementInfo) parent.getAdapter().getItem(position);
    Intent intent = new Intent(MovementActivity.this, MovementDetailActivitiy.class);
    intent.putExtra(Constants.TITLE, item.getTitle());
    intent.putExtra(Constants.DESCRIPTION, item.getDescription());
    intent.putExtra(Constants.ACTIVITYPLACE, item.getActivityPlace());
    intent.putExtra(Constants.ACTIVITYTIME, item.getActivityTime());
    intent.putExtra(Constants.CREATOR, item.getCreatedBy());
    startActivity(intent);
  }
}
