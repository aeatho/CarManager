package com.unovo.carmanager.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.unovo.carmanager.R;
import com.unovo.carmanager.utils.SystemTool;

public class EmptyLayout extends LinearLayout implements View.OnClickListener {

  public static final int HIDE_LAYOUT = 0;
  public static final int NETWORK_ERROR = 1;
  public static final int NETWORK_LOADING = 2;
  public static final int NODATA = 3;

  private View animProgress;
  private boolean clickEnable = true;
  private final Context context;
  public ImageView img;
  private OnClickListener listener;
  private int mErrorState;
  private RelativeLayout mLayout;
  private String strNoDataContent = "";
  private TextView tv;
  private int defaultLoadingMsg = R.string.error_view_loading;

  public EmptyLayout(Context context) {
    super(context);
    this.context = context;
    init();
  }

  public EmptyLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    init();
  }

  public RelativeLayout getRootView() {
    return mLayout;
  }

  public TextView getInfoText() {
    return tv;
  }

  public View getProgressbar() {
    return animProgress;
  }

  private void init() {
    View view = View.inflate(context, R.layout.view_error_layout, null);
    img = (ImageView) view.findViewById(R.id.img_error_layout);
    tv = (TextView) view.findViewById(R.id.tv_error_layout);
    mLayout = (RelativeLayout) view.findViewById(R.id.pageerrLayout);
    animProgress = view.findViewById(R.id.animProgress);
    setBackgroundColor(-1);
    img.setOnClickListener(new OnClickListener() {

      @Override public void onClick(View v) {
        if (clickEnable) {
          if (listener != null) listener.onClick(v);
        }
      }
    });
    addView(view);
  }

  public void dismiss() {
    mErrorState = HIDE_LAYOUT;
    setVisibility(View.GONE);
  }

  public int getErrorState() {
    return mErrorState;
  }

  public boolean isLoadError() {
    return mErrorState == NETWORK_ERROR;
  }

  public boolean isLoading() {
    return mErrorState == NETWORK_LOADING;
  }

  @Override public void onClick(View v) {
    if (clickEnable) {
      // setErrorType(NETWORK_LOADING);
      if (listener != null) listener.onClick(v);
    }
  }

  public void setErrorMessage(String msg) {
    tv.setText(msg);
    tv.setVisibility(VISIBLE);
  }

  /**
   * 新添设置背景
   */
  public void setErrorImag(int imgResource) {
    try {
      img.setImageResource(imgResource);
    } catch (Exception ignored) {
    }
  }

  public void setErrorImag(Bitmap bitmap) {
    try {
      img.setImageBitmap(bitmap);
    } catch (Exception ignored) {
    }
  }

  public void setLoadingMsg(int msgRes) {
    defaultLoadingMsg = msgRes;
  }

  public void setErrorType(int i) {
    setVisibility(View.VISIBLE);
    switch (i) {
      case NETWORK_ERROR:
        mErrorState = NETWORK_ERROR;
        if (SystemTool.hasInternet()) {
            tv.setText(R.string.error_view_load_error_click_to_refresh);
            img.setImageResource(R.mipmap.empty_net_error);
        } else {
          tv.setText(R.string.error_view_network_error_click_to_refresh);
          img.setImageResource(R.mipmap.empty_no_network);
        }
        tv.setVisibility(View.VISIBLE);
        img.setVisibility(View.VISIBLE);
        animProgress.setVisibility(View.GONE);
        clickEnable = true;
        break;
      case NETWORK_LOADING:
        mErrorState = NETWORK_LOADING;
        animProgress.setVisibility(View.VISIBLE);
        img.setVisibility(View.GONE);
        tv.setText(defaultLoadingMsg);
        tv.setVisibility(VISIBLE);
        clickEnable = false;
        break;
      case NODATA:
        mErrorState = NODATA;
        img.setImageResource(R.mipmap.empty_normal);
        img.setVisibility(View.VISIBLE);
        animProgress.setVisibility(View.GONE);
        tv.setVisibility(VISIBLE);
        setTvNoDataContent();
        clickEnable = true;
        break;
      case HIDE_LAYOUT:
        setVisibility(View.GONE);
        break;
      default:
        break;
    }
  }

  public void setNoDataContent(String noDataContent) {
    strNoDataContent = noDataContent;
  }

  public void setOnLayoutClickListener(OnClickListener listener) {
    this.listener = listener;
  }

  public void setTvNoDataContent() {
    if (!strNoDataContent.equals("")) {
      tv.setText(strNoDataContent);
    } else {
      tv.setText(R.string.error_view_no_data);
    }
  }

  @Override public void setVisibility(int visibility) {
    if (visibility == View.GONE) mErrorState = HIDE_LAYOUT;
    super.setVisibility(visibility);
  }
}
