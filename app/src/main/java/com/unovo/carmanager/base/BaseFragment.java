package com.unovo.carmanager.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.base
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/9 02:40
 * @version: V1.0
 */
public class BaseFragment extends Fragment {
  /** 广播拦截器 */
  private InternalReceiver internalReceiver;
  /** 当前页面是否可以销毁 */
  private boolean isFinish = false;

  /**
   * 如果子界面需要拦截处理注册的广播
   * 需要实现该方法
   */
  protected void handleReceiver(Context context, Intent intent) {
    // 广播处理
  }

  /**
   * 自定义应用全局广播处理器，方便全局拦截广播并进行分发
   */
  private class InternalReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
      if (intent == null || intent.getAction() == null) {
        return;
      }
      handleReceiver(context, intent);
    }
  }
}
