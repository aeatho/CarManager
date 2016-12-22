/**
 * Project Name:Android_Car_Example
 * File Name:RecomandAdapter.java
 * Package Name:com.amap.api.car.example
 * Date:2015年4月3日上午11:29:45
 */

package com.unovo.carmanager.common.lbs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.unovo.carmanager.R;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:RecomandAdapter <br/>
 * Function: 显示的poi列表 <br/>
 * Date: 2015年4月3日 上午11:29:45 <br/>
 *
 * @author yiyi.qi
 * @see
 * @since JDK 1.6
 */
public class RecomandAdapter extends BaseAdapter {
  private List<PositionEntity> mPositionEntities;

  private Context mContext;
  private ListView mListView;

  public RecomandAdapter(ListView listView, Context context) {
    mContext = context;
    mListView = listView;
    mPositionEntities = new ArrayList<>();
  }

  public void setPositionEntities(List<PositionEntity> entities) {
    this.mPositionEntities = entities == null ? new ArrayList<PositionEntity>() : entities;
    //if (mPositionEntities.isEmpty()) {
    //  mListView.setBackgroundResource(R.drawable.popupwindow_graybg_topcenterarrow);
    //}
  }

  @Override public int getCount() {
    return mPositionEntities.size();
  }

  @Override public PositionEntity getItem(int position) {
    return mPositionEntities.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {

    TextView textView = null;
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(mContext);
      textView = (TextView) inflater.inflate(R.layout.view_recommond, null);
    } else {
      textView = (TextView) convertView;
    }
    textView.setText(mPositionEntities.get(position).getAddress());
    return textView;
  }
}
