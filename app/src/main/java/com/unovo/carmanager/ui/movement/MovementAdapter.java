package com.unovo.carmanager.ui.movement;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unovo.carmanager.R;
import com.unovo.carmanager.bean.MovementInfo;
import com.unovo.carmanager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class MovementAdapter extends BaseAdapter {
  private Context mContext;
  private List<MovementInfo> mMovement;

  public MovementAdapter(Context context) {
    this.mContext = context;
    this.mMovement = new ArrayList<>();
  }

  public void setData(List<MovementInfo> movement) {
    if (movement != null) this.mMovement.addAll(movement);
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return mMovement.size();
  }

  @Override public MovementInfo getItem(int position) {
    return mMovement.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LinearLayout.inflate(mContext, R.layout.item_movement, null);
      holder = new ViewHolder();
      holder.title = (TextView) convertView.findViewById(R.id.Movement_Title);
      holder.place = (TextView) convertView.findViewById(R.id.movement_Place);
      holder.mo_time = (TextView) convertView.findViewById(R.id.movement_Time);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    MovementInfo info = mMovement.get(position);

    holder.title.setText(StringUtils.toString(info.getTitle()));
    holder.place.setText(StringUtils.toString(info.getActivityPlace()));
    holder.mo_time.setText(StringUtils.toString(info.getActivityTime()));

    return convertView;
    //ni
  }

  private static class ViewHolder {
    TextView title;
    TextView place;
    TextView mo_time;
  }
}
