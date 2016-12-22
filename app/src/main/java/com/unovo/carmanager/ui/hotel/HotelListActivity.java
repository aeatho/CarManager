package com.unovo.carmanager.ui.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.hotel
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/11 01:58
 * @version: V1.0
 */
public class HotelListActivity extends BaseActivity {
  private ListView mListView;

  @Override protected int getLayoutId() {
    return R.layout.activity_hotel_list;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_hotel_list;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mListView = (ListView) findViewById(R.id.listview);
    mListView.setAdapter(new HotelListAdapter(this, HotelListAdapter.getHouseCount()));
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(HotelListActivity.this, HotelWebActivity.class));
      }
    });
  }
}
