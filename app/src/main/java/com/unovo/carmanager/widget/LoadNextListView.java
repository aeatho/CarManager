package com.unovo.carmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.unovo.carmanager.R;

/**
 * Created by Administrator on 2016/8/2.
 */
public class LoadNextListView extends ListView implements AbsListView.OnScrollListener {

  //总数量
  private int totalItemCount;
  //最后一个可见的数量
  private int lastVisiableCount;
  //底部footer
  private View footer;

  private boolean isLoading = false;

  private OnLoadNextListener onLoadNextListener;

  //加载数据回调接口
  public interface OnLoadNextListener {
    //加载数据回调方法
    public void onLoadNext();
  }

  public void setOnLoadNextListener(OnLoadNextListener onLoadNextListener) {
    this.onLoadNextListener = onLoadNextListener;
  }

  public LoadNextListView(Context context) {
    this(context, null);
  }

  public LoadNextListView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public LoadNextListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    //获取底部footer的对象
    footer = View.inflate(context, R.layout.listview_footer, null);
    //加入底部footer
    addFooterView(footer);
    //隐藏底部footer
    footer.findViewById(R.id.ll_footer).setVisibility(View.GONE);
    setOnScrollListener(this);
  }

  @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    this.lastVisiableCount = firstVisibleItem + visibleItemCount;
    this.totalItemCount = totalItemCount;
  }

  @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
    //第一  当前的lastVisiableCount等于totalItemCount
    //第二  当前listview属于滑动停止状态
    if (hasMoreData && lastVisiableCount == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
      if (!isLoading) {
        isLoading = true;
        //显示底部footer
        footer.findViewById(R.id.ll_footer).setVisibility(View.VISIBLE);
        onLoadNextListener.onLoadNext();
      }
    }
  }

  /**
   * 数据加载完成
   */
  public void onComplete(boolean hasMoreData) {
    this.hasMoreData = hasMoreData;
    isLoading = false;
    //隐藏底部footer
    footer.findViewById(R.id.ll_footer).setVisibility(View.GONE);
  }

  private boolean hasMoreData = true;
}
