package com.unovo.carmanager.ui.message;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.utils.UIUtils;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MessagePlayActivity extends BaseActivity
    implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {
  private String time;
  private String path;
  private MediaPlayer mediaPlayer;
  private ImageView mBtnPlay;
  private TextView mTvTime;

  @Override protected int getLayoutId() {
    return R.layout.activity_play_message;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_feedback_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    Intent in = getIntent();
    if (in != null) {
      Bundle bundle = in.getExtras();
      if (bundle != null) {
        time = bundle.getString(Constants.CREATETIME);
        path = bundle.getString(Constants.PATH);
      }
    }

    init();
  }

  private void init() {
    mBtnPlay = (ImageView) findViewById(R.id.img_play);
    mTvTime = (TextView) findViewById(R.id.tvCreateTime);

    mTvTime.setText(time);

    mediaPlayer = new MediaPlayer();
    mediaPlayer.setOnCompletionListener(this);
    mediaPlayer.setOnErrorListener(this);
    mediaPlayer.setOnBufferingUpdateListener(this);
    mediaPlayer.setOnPreparedListener(this);

    mediaPlayer = MediaPlayer.create(this, R.raw.test);

    mBtnPlay.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (mediaPlayer.isPlaying()) {
          // 暂定
          mediaPlayer.pause();
          mBtnPlay.setImageResource(R.mipmap.ic_pause);
          mActionBar.setTitle(UIUtils.getString(R.string.title_feedback_detail) + " ( 已暂停 )");
        } else {
          // 播放
          mediaPlayer.start();
          mBtnPlay.setImageResource(R.mipmap.ic_play);
          mActionBar.setTitle(UIUtils.getString(R.string.title_feedback_detail) + " ( 播放中 )");
        }
      }
    });
  }

  //当MediaPlayer缓冲的时候，调用OnbufferingUpdate方法

  @Override public void onBufferingUpdate(MediaPlayer mp, int percent) {
    mBtnPlay.setEnabled(false);
  }

  //当完成prepareAsync方法时，将调用OnPrepared方法，表明音频准备播放
  @Override public void onPrepared(MediaPlayer mp) {
    mBtnPlay.setEnabled(true);
  }

  //当MediaPlayer完成播放音频文件时，将调用Oncompletion方法
  //此时设置“播放”按钮可以点击，“暂停”不可点击
  @Override public void onCompletion(MediaPlayer mp) {
    showToast("播放结束");
    mBtnPlay.setEnabled(true);
    mBtnPlay.setImageResource(R.mipmap.ic_pause);
  }

  @Override protected void onDestroy() {
    mediaPlayer.stop();
    super.onDestroy();
  }

  @Override public boolean onError(MediaPlayer mp, int what, int extra) {
    switch (what) {
      case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
        Toast.makeText(this, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK" + extra,
            Toast.LENGTH_SHORT).show();
        break;
      case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
        Toast.makeText(this, "MEDIA_ERROR_SERVER_DIED" + extra, Toast.LENGTH_SHORT).show();
        break;
      case MediaPlayer.MEDIA_ERROR_UNKNOWN:
        Toast.makeText(this, "MEDIA_ERROR_UNKNOWN", Toast.LENGTH_SHORT).show();
        break;
      default:
        break;
    }
    return false;
  }
}
