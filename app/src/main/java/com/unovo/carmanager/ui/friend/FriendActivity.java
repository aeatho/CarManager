package com.unovo.carmanager.ui.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.bean.FriendInfo;
import com.unovo.carmanager.common.network.HttpClient;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.UIHelper;
import com.unovo.carmanager.ui.chat.IMChattingHelper;
import com.unovo.carmanager.utils.Settings;
import com.unovo.carmanager.widget.EmptyLayout;
import com.unovo.carmanager.widget.LoadNextListView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/7/29.
 */
public class FriendActivity extends BaseActivity
    implements LoadNextListView.OnLoadNextListener, FriendAdapter.OnFriendClickListener {
  private LoadNextListView mListView;
  private FriendAdapter mAdapter;

  private String uid;

  //默认第一页为0
  private int pageIndex = 0;
  private EmptyLayout mEmptyLayout;

  @Override protected int getLayoutId() {
    return R.layout.activity_friend;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_friends;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mListView = (LoadNextListView) findViewById(R.id.listView);
    mListView.setAdapter(mAdapter = new FriendAdapter(this));
    mListView.setOnLoadNextListener(this);
    mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    uid = Settings.getUID(this);

    mAdapter.setOnFriendClickListener(this);

    requestData();

    registerReceiver(new String[] {
        IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE
    });
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
      if (IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE.equals(intent.getAction())) {
        mAdapter.unMarkRed(-1);
      }
    }
  }

  //request data from network
  private void requestData() {
    Call<List<FriendInfo>> getfriends =
        HttpClient.getInstance().getAPIs().getfriends(uid, pageIndex, Constants.PAGE_SIZE);

    getfriends.enqueue(new Callback<List<FriendInfo>>() {
      @Override
      public void onResponse(Call<List<FriendInfo>> call, Response<List<FriendInfo>> response) {
        if (response.code() == 200) {
          List<FriendInfo> tempFriends = response.body();

          if (tempFriends != null && !tempFriends.isEmpty()) {
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mAdapter.setData(response.body());
            if (tempFriends.size() < Constants.PAGE_SIZE) mListView.onComplete(false);
          }
        }
      }

      @Override public void onFailure(Call<List<FriendInfo>> call, Throwable t) {
        t.printStackTrace();
        //如果加载数据失败，PageIndex需要--
        --pageIndex;
      }
    });
  }

  @Override public void onLoadNext() {
    ++pageIndex;
    requestData();
  }

  @Override public void onClickLocation(int postion, FriendInfo info) {
    Intent intent = new Intent(FriendActivity.this, FriendOnMapActivity.class);
    intent.putExtra(Constants.USER_ID, info.getUserID());
    intent.putExtra(Constants.LATITUDE, info.getLat());
    intent.putExtra(Constants.LONGITUDE, info.getLon());
    intent.putExtra(Constants.DISPLAYNAME, info.getDisplayName());
    intent.putExtra(Constants.CARNUMBER, info.getTruckLicense());
    startActivity(intent);
  }

  @Override public void onClickMsg(int postion, FriendInfo info) {
    //info.setUserID("13701720822");
    mAdapter.unMarkRed(postion);
    UIHelper.startChattingAction(FriendActivity.this, info.getVoipAccount(), info.getDisplayName());
  }
}
