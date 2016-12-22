package com.unovo.carmanager.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.bean.MessageInfo;
import com.unovo.carmanager.common.network.HttpClient;
import com.unovo.carmanager.constant.Constants;
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
public class MessageActivity extends BaseActivity
    implements LoadNextListView.OnLoadNextListener, AdapterView.OnItemClickListener {
  private LoadNextListView mListView;
  private MessageAdapter mAdapter;

  private String uid;
  private String time;
  private String path;

  //默认第一页为0
  private int pageIndex = 0;
  private EmptyLayout mEmptyLayout;

  @Override protected int getLayoutId() {
    return R.layout.activity_message;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_feedback;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mListView = (LoadNextListView) findViewById(R.id.listView_message);
    mListView.setAdapter(mAdapter = new MessageAdapter(this));
    mListView.setOnLoadNextListener(this);
    mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
    uid = Settings.getUID(this);

    mListView.setOnItemClickListener(this);

    requestData();
  }

  //request data from network
  private void requestData() {
    Call<List<MessageInfo>> message =
        HttpClient.getInstance().getAPIs().message(uid, pageIndex, Constants.PAGE_SIZE, time, path);

    message.enqueue(new Callback<List<MessageInfo>>() {
      @Override
      public void onResponse(Call<List<MessageInfo>> call, Response<List<MessageInfo>> response) {
        if (response.code() == 200) {
          List<MessageInfo> tempMessage = response.body();

          if (tempMessage != null && !tempMessage.isEmpty()) {
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mAdapter.setData(response.body());
            if (tempMessage.size() < Constants.PAGE_SIZE) mListView.onComplete(false);
          }
        }
      }

      @Override public void onFailure(Call<List<MessageInfo>> call, Throwable t) {
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

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    MessageInfo item = (MessageInfo) parent.getAdapter().getItem(position);
    Intent intent = new Intent(MessageActivity.this, MessagePlayActivity.class);
    intent.putExtra(Constants.CREATETIME, item.getTime());
    intent.putExtra(Constants.PATH, item.getPath());
    startActivity(intent);
  }
}
