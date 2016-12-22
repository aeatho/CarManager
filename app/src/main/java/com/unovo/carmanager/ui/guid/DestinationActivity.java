package com.unovo.carmanager.ui.guid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.common.emoji.SimpleTextWatcher;
import com.unovo.carmanager.common.lbs.InputTipTask;
import com.unovo.carmanager.common.lbs.PositionEntity;
import com.unovo.carmanager.common.lbs.RecomandAdapter;
import com.unovo.carmanager.common.lbs.RouteTask;
import com.unovo.carmanager.constant.Constants;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.guid
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/10 17:20
 * @version: V1.0
 */
public class DestinationActivity extends BaseActivity implements View.OnClickListener {
  private ListView mRecommendList;
  private TextView mBack_Image;
  private TextView mSearchText;
  private EditText mDestinaionText;
  private RecomandAdapter mRecomandAdapter;

  private RouteTask mRouteTask;

  @Override protected int getLayoutId() {
    return R.layout.activity_destination;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mActionBar.hide();

    mRecommendList = (ListView) findViewById(R.id.recommend_list);
    mBack_Image = (TextView) findViewById(R.id.destination_back);
    mBack_Image.setOnClickListener(this);

    mSearchText = (TextView) findViewById(R.id.destination_search);
    mSearchText.setOnClickListener(this);

    mDestinaionText = (EditText) findViewById(R.id.destination_edittext);
    mDestinaionText.addTextChangedListener(new SimpleTextWatcher() {
      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (RouteTask.getInstance(getApplicationContext()).getStartPoint() == null) {
          showToast("检查网络，Key等问题");
          return;
        }
        if (TextUtils.isEmpty(s.toString())) {
          mRecomandAdapter.setPositionEntities(null);
          return;
        }
        InputTipTask.getInstance(getApplicationContext(), mRecomandAdapter)
            .searchTips(s.toString(),
                RouteTask.getInstance(getApplicationContext()).getStartPoint().getCity());
      }
    });
    mRecomandAdapter = new RecomandAdapter(mRecommendList, getApplicationContext());
    mRecommendList.setAdapter(mRecomandAdapter);
    mRecommendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PositionEntity entity = mRecomandAdapter.getItem(position);
        if (entity.getLatitue() == 0 && entity.getLongitude() == 0) {
          Intent in = new Intent();
          in.putExtra(Constants.RESULT_ADDRESS, entity.getAddress());

          setResult(Activity.RESULT_OK, in);
          finish();
        }
      }
    });

    mRouteTask = RouteTask.getInstance(getApplicationContext());
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.destination_back:
        finish();
        break;
    }
  }
}
