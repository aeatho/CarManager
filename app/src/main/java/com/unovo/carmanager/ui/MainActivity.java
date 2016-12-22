package com.unovo.carmanager.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.common.SDKCoreHelper;
import com.unovo.carmanager.common.lbs.LocationTask;
import com.unovo.carmanager.common.lbs.PositionEntity;
import com.unovo.carmanager.common.network.HttpClient;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.ui.chat.IMChattingHelper;
import com.unovo.carmanager.ui.friend.FriendActivity;
import com.unovo.carmanager.ui.guid.GuideActivity;
import com.unovo.carmanager.ui.message.MessageActivity;
import com.unovo.carmanager.ui.movement.MovementActivity;
import com.unovo.carmanager.ui.sos.SOSActivity;
import com.unovo.carmanager.utils.DialogHelper;
import com.unovo.carmanager.utils.LogUtil;
import com.unovo.carmanager.utils.Settings;
import com.unovo.carmanager.utils.StringUtils;
import com.unovo.carmanager.utils.ToastUtil;
import com.unovo.carmanager.utils.UIUtils;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECInitParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {
  private static final String TAG = "im";
  private TextView mTvStatus;
  private long exitTime = 0;
  private String userid;
  private String userName;
  private boolean isintruck;

  private LocationTask locationTask;
  private TextView mRedPoint;

  @Override protected int getLayoutId() {
    return R.layout.activity_main;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mActionBar.hide();

    SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);

    findViewById(R.id.ly_guid).setOnClickListener(this);
    findViewById(R.id.ly_feedback).setOnClickListener(this);
    findViewById(R.id.ly_friend).setOnClickListener(this);
    findViewById(R.id.ly_active).setOnClickListener(this);
    findViewById(R.id.ly_call).setOnClickListener(this);
    findViewById(R.id.ly_help).setOnClickListener(this);
    findViewById(R.id.ly_gps).setOnClickListener(this);
    mTvStatus = (TextView) findViewById(R.id.showStatus);
    mRedPoint = (TextView) findViewById(R.id.redPoint);

    userid = Settings.getUID(this);
    userName = Settings.getUName(this);

    ((TextView) findViewById(R.id.tvDisplay)).setText(Settings.getUName(this));

    initLBS();
    initStatus();
    initIM();
  }

  private void initIM() {
    // 注册第一次登陆同步消息
    registerReceiver(new String[] {
        IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE, SDKCoreHelper.ACTION_SDK_CONNECT
    });
  }

  private void initStatus() {
    mTvStatus.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (!hasLocation) {
          showToast("正在定位,请稍后！");
          locationTask.startLocate();
          return;
        }

        if (isintruck) {
          isintruck = !isintruck;
          mTvStatus.setText("人车关联(已分离)");
        } else {
          isintruck = !isintruck;
          mTvStatus.setText("人车分离(已关联)");
        }
        requestCurrentPostionStatus(true, currentPostion.getLatitue(),
            currentPostion.getLongitude());
      }
    });
  }

  private PositionEntity currentPostion = new PositionEntity(0, 0);

  private boolean hasLocation = false;

  private void initLBS() {
    locationTask = new LocationTask(UIUtils.getContext());
    locationTask.startLocate();
    locationTask.setOnLocationGetListener(new LocationTask.OnLocationGetListener() {
      @Override public void onLocationGet(PositionEntity entity) {
        if (!StringUtils.isEmpty(entity.getLatitue()) && !StringUtils.isEmpty(
            entity.getLongitude())) {
          hasLocation = true;
          currentPostion = entity;
          requestCurrentPostionStatus(false, entity.getLatitue(), entity.getLongitude());
          locationTask.stopLocate();
        }
      }
    });
  }

  //request data from network
  private void requestCurrentPostionStatus(final boolean isInitiative, double lat, double lon) {
    if (isInitiative) showWaitDialog();
    Call<String> status = HttpClient.getInstance().getAPIs().status(userid, lat, lon, isintruck);
    status.enqueue(new Callback<String>() {
      @Override public void onResponse(Call<String> call, Response<String> response) {
        if (isInitiative) hideWaitDialog();
        if (response.code() == 200) {
          if (isInitiative) {
            showToast("状态获取成功");
          } else {
            mTvStatus.setText("人车关联(已分离)");
          }
        } else {
          if (isInitiative) {
            showToast("状态获取失败");
          }
        }
      }

      @Override public void onFailure(Call<String> call, Throwable t) {
        if (isInitiative) hideWaitDialog();
        showToast(t.getMessage());
      }
    });
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
      if ((System.currentTimeMillis() - exitTime) > 2000) {
        Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
        exitTime = System.currentTimeMillis();
      } else {
        doLogout(userName);
      }
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  private void doLogout(String name) {
    showWaitDialog("注销中,即将退出程序...");
    Call<String> login = HttpClient.getInstance().getAPIs().logout(name);
    login.enqueue(new Callback<String>() {
      @Override public void onResponse(Call<String> call, Response<String> response) {
        hideWaitDialog();
        if (response.code() == 200 && response.body() != null) {
          finish();
          System.exit(0);
        } else {
          DialogHelper.showConfirmDialog(MainActivity.this, "注销失败，是否强制退出?",
              new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  finish();
                  System.exit(0);
                }
              });
        }
      }

      @Override public void onFailure(Call<String> call, Throwable t) {
        hideWaitDialog();
        ToastUtil.showMessage(t.getMessage());
      }
    });
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.ly_guid:
      case R.id.ly_gps:
        openGpsGuide(MainActivity.this);
        break;
      case R.id.ly_feedback:
        startActivity(new Intent(MainActivity.this, MessageActivity.class));
        break;
      case R.id.ly_friend:
        startActivity(new Intent(MainActivity.this, FriendActivity.class));
        break;
      case R.id.ly_active:
        startActivity(new Intent(MainActivity.this, MovementActivity.class));
        break;
      case R.id.ly_call:
        showCallDialog();
        break;
      case R.id.ly_help:
        startActivity(new Intent(MainActivity.this, SOSActivity.class));
      default:
        break;
    }
  }

  private void openGpsGuide(Context context) {
    startActivity(new Intent(context, GuideActivity.class));
  }

  private void showCallDialog() {
    DialogHelper.showConfirmDialog(this, "确认拨打客服电话: 021-58830655",
        new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:021-58830655"));
            startActivity(intent);
          }
        });
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    String userName = intent.getStringExtra("Main_FromUserName");
    String mSession = intent.getStringExtra("Main_Session");

    UIHelper.startChattingAction(this, mSession, userName);
  }

  private InternalReceiver internalReceiver;

  /**
   * 注册广播
   */
  protected final void registerReceiver(String[] actionArray) {
    if (actionArray == null) {
      return;
    }
    IntentFilter intentfilter = new IntentFilter();
    for (String action : actionArray) {
      intentfilter.addAction(action);
    }
    if (internalReceiver == null) {
      internalReceiver = new InternalReceiver();
    }
    registerReceiver(internalReceiver, intentfilter);
  }

  private class InternalReceiver extends BroadcastReceiver {

    @Override public void onReceive(Context context, Intent intent) {
      if (intent == null || TextUtils.isEmpty(intent.getAction())) {
        return;
      }
      LogUtil.d(TAG, "[onReceive] action:" + intent.getAction());
      if (IMChattingHelper.INTENT_ACTION_SYNC_MESSAGE.equals(intent.getAction())) {
        //disPostingLoading();

        mRedPoint.setVisibility(
            CarApplication.getOffMessages().isEmpty() ? View.INVISIBLE : View.VISIBLE);
      } else if (SDKCoreHelper.ACTION_SDK_CONNECT.equals(intent.getAction())) {
        doInitAction();
        // tetstMesge();
        //BaseFragment tabView = getTabView(TAB_CONVERSATION);
        //if (tabView != null && tabView instanceof ConversationListFragment) {
        //  ((ConversationListFragment) tabView).updateConnectState();
        //}
        sendBroadcast(new Intent(Constants.REFRESH_MSG_WHEN_NET_CONNECTED));
      } else if (SDKCoreHelper.ACTION_KICK_OFF.equals(intent.getAction())) {
        String kickoffText = intent.getStringExtra("kickoffText");
        handlerKickOff(kickoffText);
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();
    mRedPoint.setVisibility(
        CarApplication.getOffMessages().isEmpty() ? View.INVISIBLE : View.VISIBLE);
  }

  public void handlerKickOff(String kickoffText) {
    if (isFinishing()) {
      return;
    }

    DialogHelper.showMessageDialog(this, kickoffText, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        finish();
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (internalReceiver != null) {
      unregisterReceiver(internalReceiver);
    }
  }

  private void doInitAction() {
    if (SDKCoreHelper.getConnectState() == ECDevice.ECConnectState.CONNECT_SUCCESS) {

      //String account = getAutoRegistAccount();
      //if (!TextUtils.isEmpty(account)) {
      //  ClientUser user = new ClientUser("").from(account);
      //  CCPAppManager.setClientUser(user);
      //}
      //settingPersonInfo();
      //IMChattingHelper.getInstance().getPersonInfo();
      //// 检测离线消息
      //checkOffineMessage();
      //mInitActionFlag = true;
    }
  }
}