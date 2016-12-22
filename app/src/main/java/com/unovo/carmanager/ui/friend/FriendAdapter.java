package com.unovo.carmanager.ui.friend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.R;
import com.unovo.carmanager.bean.FriendInfo;
import com.unovo.carmanager.utils.StringUtils;
import com.yuntongxun.ecsdk.ECMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class FriendAdapter extends BaseAdapter {
  private Context mContext;
  private List<FriendInfo> mFriends;

  public FriendAdapter(Context context) {
    this.mContext = context;
    this.mFriends = new ArrayList<>();
  }

  public void setData(List<FriendInfo> friends) {
    if (friends != null) {
      //FriendInfo friendInfo = friends.get(0);
      //friendInfo.setUserID("13701720822");
      //friendInfo.setDisplayName("测试帐号");
      this.mFriends.addAll(friends);
      notifyDataSetChanged();
    }
  }

  @Override public int getCount() {
    return mFriends.size();
  }

  @Override public FriendInfo getItem(int position) {
    return mFriends.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LinearLayout.inflate(mContext, R.layout.item_friend, null);
      holder = new ViewHolder();
      holder.iv = (ImageView) convertView.findViewById(R.id.imageView);
      holder.name = (TextView) convertView.findViewById(R.id.FriName);
      holder.tel = (TextView) convertView.findViewById(R.id.tel);
      holder.no = (TextView) convertView.findViewById(R.id.trucklicense);
      holder.location = (LinearLayout) convertView.findViewById(R.id.location);
      holder.msg = (RelativeLayout) convertView.findViewById(R.id.msg);
      holder.redPoint = (TextView) convertView.findViewById(R.id.redPoint);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    final FriendInfo info = mFriends.get(position);

    holder.tel.setText(StringUtils.toString(info.getTelePhone()));
    holder.name.setText(StringUtils.toString(info.getDisplayName()));
    holder.no.setText(StringUtils.toString(info.getTruckLicense()));
    holder.redPoint.setVisibility(View.INVISIBLE);
    if (!CarApplication.getOffMessages().isEmpty()) {
      for (ECMessage msg : CarApplication.getOffMessages()) {
        if (info.getUserID().equals(msg.getForm())) {
          holder.redPoint.setVisibility(View.VISIBLE);
          break;
        }
      }
    }

    if (index == position) {
      holder.redPoint.setVisibility(View.INVISIBLE);
    }

    holder.location.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) listener.onClickLocation(position, info);
      }
    });
    holder.msg.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) listener.onClickMsg(position, info);
      }
    });

    return convertView;
  }

  private int index = -1;

  public void unMarkRed(int index) {
    this.index = index;
    notifyDataSetChanged();
  }

  public interface OnFriendClickListener {
    void onClickLocation(int postion, FriendInfo info);

    void onClickMsg(int postion, FriendInfo info);
  }

  private OnFriendClickListener listener;

  public void setOnFriendClickListener(OnFriendClickListener listener) {
    this.listener = listener;
  }

  private static class ViewHolder {
    ImageView iv;
    TextView name;
    TextView tel;
    TextView no;
    LinearLayout location;
    RelativeLayout msg;
    TextView redPoint;
  }
}
