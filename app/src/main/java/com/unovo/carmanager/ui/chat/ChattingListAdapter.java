package com.unovo.carmanager.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.utils.MediaPlayTools;
import com.unovo.carmanager.utils.Utils;
import com.yuntongxun.ecsdk.ECMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.chat
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/9 19:41
 * @version: V1.0
 */
public class ChattingListAdapter extends BaseAdapter {
  private ArrayList<ECMessage> mMessages;
  private LayoutInflater mInflater;
  /** 当前语音播放的Item */
  public int mVoicePosition = -1;
  protected View.OnClickListener mOnClickListener;

  public ChattingListAdapter(Context context) {
    mInflater = LayoutInflater.from(context);
    mMessages = new ArrayList<>();
    mOnClickListener = new ChattingListClickListener((ChattingActivity) context);
  }

  public View.OnClickListener getOnClickListener() {
    return mOnClickListener;
  }

  /**
   * 当前语音播放的位置
   */
  public void setVoicePosition(int position) {
    mVoicePosition = position;
  }

  public void onPause() {
    mVoicePosition = -1;
    MediaPlayTools.getInstance().stop();
  }

  public void addMessages(List<ECMessage> msgs) {
    mMessages.addAll(msgs);
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return mMessages.size();
  }

  @Override public ECMessage getItem(int position) {
    return mMessages.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemViewType(int position) {
    ECMessage item = getItem(position);
    if (item.getDirection() == ECMessage.Direction.RECEIVE) {
      return 0;
    } else {
      return 1;
    }
  }

  @Override public int getViewTypeCount() {
    return 2;
  }

  @Override public boolean isEnabled(int position) {
    return false;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      int res = getItemViewType(position) == 0 ? R.layout.message_list_list_item_left
          : R.layout.message_list_list_item_right;
      convertView = mInflater.inflate(res, parent, false);
      holder = new ViewHolder();
      holder.icon = (ImageView) convertView.findViewById(R.id.icon);
      holder.time = (TextView) convertView.findViewById(R.id.time);
      holder.resend = convertView.findViewById(R.id.resend);
      holder.sending = convertView.findViewById(R.id.sending);
      convertView.setTag(holder);
      holder.contentArea = new ContentArea(convertView);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    convertView.setOnClickListener(mOnClickListener);

    holder.position = position;

    ECMessage message = mMessages.get(position);

    if (ECMessage.Type.VOICE == message.getType()) {
      holder.type = ViewHolder.TagType.TAG_VOICE;
    } else if (ECMessage.Type.TXT == message.getType()) {
      holder.type = ViewHolder.TagType.TAG_IM_TEXT;
    } else {
      holder.type = ViewHolder.TagType.TAG_OTHER;
    }

    holder.contentArea.setData(message);

    holder.detail = message;

    // 本条与上一条时间间隔不超过0.5小时就不显示本条时间
    long lastTime = 0;
    if (position > 0) {
      lastTime = getItem(position - 1).getMsgTime();
    }

    long selfTime = message.getMsgTime();
    if (lessThanStandard(selfTime, lastTime)) {
      holder.time.setVisibility(View.GONE);
    } else {
      holder.time.setVisibility(View.VISIBLE);
      holder.time.setText(Utils.dayToNow(selfTime, true));
    }

    if (message.getMsgStatus() == ECMessage.MessageStatus.FAILED) {
      // 显示重试
      holder.resend.setVisibility(View.VISIBLE);
      holder.sending.setVisibility(View.INVISIBLE);
      holder.resend.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          //mContext.doResendMsgRetryTips(iMessage, holder.position);
        }
      });
    } else if (message.getMsgStatus() == ECMessage.MessageStatus.SENDING) {
      // 显示重试
      holder.resend.setVisibility(View.INVISIBLE);
      holder.sending.setVisibility(View.VISIBLE);
    } else if (message.getMsgStatus() == ECMessage.MessageStatus.SUCCESS) {
      // 显示重试
      holder.resend.setVisibility(View.INVISIBLE);
      holder.sending.setVisibility(View.INVISIBLE);
    }
    return convertView;
  }

  private boolean lessThanStandard(long selfTime, long lastTime) {
    return (selfTime - lastTime) < (30 * 60 * 1000);
  }

  public static class ViewHolder {
    TextView time;
    ImageView icon;
    View resend;
    View sending;
    int type;
    ECMessage detail;
    int position;
    ContentArea contentArea;

    public static class TagType {
      static final int TAG_VOICE = 0;
      static final int TAG_IM_TEXT = 1;
      static final int TAG_OTHER = 2;
      static final int TAG_RESEND = 3;
    }
  }
}
