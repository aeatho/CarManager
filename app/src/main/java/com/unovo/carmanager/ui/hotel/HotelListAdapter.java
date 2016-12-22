package com.unovo.carmanager.ui.hotel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.unovo.carmanager.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.hotel
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/11 02:03
 * @version: V1.0
 */
public class HotelListAdapter extends BaseAdapter {
  private Context mContext;
  private List<String> mList = new ArrayList<>();

  public HotelListAdapter(Context context, int count) {
    this.mContext = context;
    for (int i = 0; i < count; i++) {
      this.mList.add("");
    }
  }

  @Override public int getCount() {
    return mList.size();
  }

  @Override public String getItem(int position) {
    return mList.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LinearLayout.inflate(mContext, R.layout.item_hotel, null);
      holder = new ViewHolder();
      holder.title = (TextView) convertView.findViewById(R.id.Movement_Title);
      holder.place = (TextView) convertView.findViewById(R.id.movement_Place);
      holder.mo_time = (TextView) convertView.findViewById(R.id.movement_Time);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    if (position % 2 == 0) {
      holder.title.setText("七天酒店（上海静安寺店）");
    } else {
      holder.title.setText("如家上海火车站南广场店");
    }

    holder.mo_time.setText("￥" + getHousePrice() + "元");
    return convertView;
  }

  public static int getHouseCount() {
    Random random = new Random();// 定义随机类
    int result = random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10
    return result + 1;
  }

  public static int getHousePrice() {
    Random random = new Random();// 定义随机类
    int result = random.nextInt(300);// 返回[0,10)集合中的整数，注意不包括10
    return result + 100;
  }

  private static class ViewHolder {
    TextView title;
    TextView place;
    TextView mo_time;
  }
}
