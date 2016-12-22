package com.unovo.carmanager.ui.chat;

import android.text.TextUtils;
import android.view.View;
import com.unovo.carmanager.utils.MediaPlayTools;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;

/**
 * 处理聊天消息点击事件响应
 *
 * @author Jorstin Chan@容联•云通讯
 * @version 4.0
 * @date 2014-12-10
 */
public class ChattingListClickListener implements View.OnClickListener {

  /** 聊天界面 */
  private ChattingActivity mContext;

  public ChattingListClickListener(ChattingActivity activity) {
    mContext = activity;
  }

  @Override public void onClick(View v) {
    final ChattingListAdapter.ViewHolder holder = (ChattingListAdapter.ViewHolder) v.getTag();
    ECMessage iMessage = holder.detail;
    switch (holder.type) {
      case ChattingListAdapter.ViewHolder.TagType.TAG_VOICE:
        MediaPlayTools instance = MediaPlayTools.getInstance();
        final ChattingListAdapter adapterForce = mContext.getChattingAdapter();
        if (instance.isPlaying()) {
          instance.stop();
          holder.contentArea.stopPlayVoiceAnim();
        }
        if (adapterForce.mVoicePosition == holder.position) {
          adapterForce.mVoicePosition = -1;
          adapterForce.notifyDataSetChanged();
          return;
        }
        instance.setOnVoicePlayCompletionListener(
            new MediaPlayTools.OnVoicePlayCompletionListener() {

              @Override public void OnVoicePlayCompletion() {
                holder.contentArea.stopPlayVoiceAnim();
                adapterForce.mVoicePosition = -1;
                adapterForce.notifyDataSetChanged();
              }
            });
        ECVoiceMessageBody voiceBody = (ECVoiceMessageBody) holder.detail.getBody();
        String fileLocalPath = voiceBody.getLocalUrl();
        instance.playVoice(fileLocalPath, false);
        holder.contentArea.playVoiceAnim();
        adapterForce.setVoicePosition(holder.position);
        adapterForce.notifyDataSetChanged();
        break;
      case ChattingListAdapter.ViewHolder.TagType.TAG_RESEND:
        mContext.doResendMsgRetryTips(iMessage, holder.position);
        break;
      case ChattingListAdapter.ViewHolder.TagType.TAG_IM_TEXT:
        ECTextMessageBody textBody = (ECTextMessageBody) iMessage.getBody();
        String content = textBody.getMessage();

        if (TextUtils.isEmpty(content)) {
          return;
        }
        content = content.trim();
        if (content.startsWith("www.") || content.startsWith("http://") || content.startsWith(
            "https://")) {
          startWebActivity(content);
        }
        break;
      default:
        break;
    }
  }

  private void startWebActivity(String url) {

    //Intent intent = new Intent(mContext, WebAboutActivity.class);
    //intent.putExtra("url", url);
    //mContext.startActivity(intent);
  }
}
