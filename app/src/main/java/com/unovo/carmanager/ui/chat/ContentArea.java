package com.unovo.carmanager.ui.chat;

import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseApplication;
import com.unovo.carmanager.utils.StringUtils;
import com.unovo.carmanager.utils.UIUtils;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.chat
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/10 01:18
 * @version: V1.0
 */
public class ContentArea {
  protected TextView content;
  protected ImageView voice_play;
  protected LinearLayout voiceLayout;
  protected View linearLayout;//气泡
  protected boolean isRight;
  private AnimationDrawable voicePlayAnim;

  public ContentArea(View convertView) {
    isRight = R.id.message_list_list_item_right == convertView.getId();
    content = (TextView) convertView.findViewById(R.id.content);
    voice_play = (ImageView) convertView.findViewById(R.id.voice_play);
    voiceLayout = (LinearLayout) convertView.findViewById(R.id.voiceLayout);
    linearLayout = convertView.findViewById(R.id.linearLayout);
  }

  public void setData(ECMessage message) {
    LinearLayout.LayoutParams lp_voiceLayout =
        (LinearLayout.LayoutParams) voiceLayout.getLayoutParams();

    if (ECMessage.Type.VOICE == message.getType()) {
      ECVoiceMessageBody voiceBody = (ECVoiceMessageBody) message.getBody();

      voice_play.setVisibility(View.VISIBLE);
      content.setVisibility(View.VISIBLE);
      content.setText("" + voiceBody.getDuration() + " \"");
      content.setBackgroundDrawable(null);
      content.setFocusable(false);
      content.setFocusableInTouchMode(false);

      //让气泡的宽度随着录音长度变化 为什么还要在减去一个32dp?根据布局文件来算不需要的
      int maxWidth = BaseApplication.sWidthPix - (isRight ? UIUtils.dp2px(57 + 53 + 36 + 32)
          : UIUtils.dp2px(57 + 53 + 24 + 32));
      int minWidth = UIUtils.dp2px(60);
      int s = voiceBody.getDuration() >= 60 ? 60 : voiceBody.getDuration();
      int width = minWidth + (maxWidth - minWidth) * s / 60;
      width = width < minWidth ? minWidth : width;
      lp_voiceLayout.width = width;
      if (isRight) {
        lp_voiceLayout.gravity = Gravity.LEFT;
      } else {
        lp_voiceLayout.gravity = Gravity.RIGHT;
      }

      voicePlayAnim = (AnimationDrawable) voice_play.getResources()
          .getDrawable(
              isRight ? R.drawable.anim_play_voice_right : R.drawable.anim_play_voice_left);

      if (Build.VERSION.SDK_INT >= 16) {
        voice_play.setBackground(voicePlayAnim.getFrame(0));
      } else {
        voice_play.setBackgroundDrawable(voicePlayAnim.getFrame(0));
      }
    } else {
      voice_play.setVisibility(View.GONE);
      linearLayout.setOnClickListener(null);
      content.setOnClickListener(null);
      lp_voiceLayout.width = ViewGroup.LayoutParams.WRAP_CONTENT;
      lp_voiceLayout.gravity = Gravity.NO_GRAVITY;

      ECTextMessageBody txtBody = (ECTextMessageBody) message.getBody();
      if (StringUtils.isEmpty(txtBody.getMessage())) {
        content.setVisibility(View.GONE);
        content.setText("");
      } else {
        content.setVisibility(View.VISIBLE);
        content.setText(txtBody.getMessage());
      }
    }
  }

  private Handler mHandler = new Handler();
  int frame = 0;
  private boolean isAnimRuning;

  public void playVoiceAnim() {
    isAnimRuning = true;
    frame++;
    if (frame > 2) {
      frame = 0;
    }

    if (Build.VERSION.SDK_INT >= 16) {
      voice_play.setBackground(voicePlayAnim.getFrame(frame));
    } else {
      voice_play.setBackgroundDrawable(voicePlayAnim.getFrame(frame));
    }
    mHandler.postDelayed(task, 200);
  }

  public void stopPlayVoiceAnim() {
    isAnimRuning = false;
    mHandler.removeCallbacksAndMessages(null);
    frame = 0;
    if (Build.VERSION.SDK_INT >= 16) {
      voice_play.setBackground(voicePlayAnim.getFrame(0));
    } else {
      voice_play.setBackgroundDrawable(voicePlayAnim.getFrame(0));
    }
  }

  private Runnable task = new Runnable() {
    @Override public void run() {
      if (isAnimRuning) {
        playVoiceAnim();
      }
    }
  };
}
