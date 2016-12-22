package com.unovo.carmanager.ui.message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unovo.carmanager.R;
import com.unovo.carmanager.bean.MessageInfo;
import com.unovo.carmanager.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class MessageAdapter extends BaseAdapter {
  private Context mContext;
  private List<MessageInfo> mMessage;

  public MessageAdapter(Context context) {
    this.mContext = context;
    this.mMessage = new ArrayList<>();
  }

  public void setData(List<MessageInfo> messages) {
    if (messages != null) this.mMessage.addAll(messages);
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return mMessage.size();
  }

  @Override public MessageInfo getItem(int position) {
    return mMessage.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LinearLayout.inflate(mContext, R.layout.item_message, null);
      holder = new ViewHolder();

      holder.time = (TextView) convertView.findViewById(R.id.message_Time);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    MessageInfo info = mMessage.get(position);

    holder.time.setText(StringUtils.toString(info.getTime()));

    return convertView;
    //ni
  }

  private static class ViewHolder {
    TextView time;
  }
}
