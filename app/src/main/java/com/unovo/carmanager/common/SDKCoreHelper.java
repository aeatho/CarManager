package com.unovo.carmanager.common;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.R;
import com.unovo.carmanager.bean.UserInfo;
import com.unovo.carmanager.ui.chat.IMChattingHelper;
import com.unovo.carmanager.utils.LogUtil;
import com.unovo.carmanager.utils.UIUtils;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECNotifyOptions;
import com.yuntongxun.ecsdk.SdkErrorCode;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.common
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/8 14:57
 * @version: V1.0
 */
public class SDKCoreHelper implements ECDevice.InitListener, ECDevice.OnECDeviceConnectListener,
    ECDevice.OnLogoutListener {
  public static final String TAG = "IMHelper";
  public static final String ACTION_LOGOUT = "com.yuntongxun.ECDemo_logout";
  public static final String ACTION_SDK_CONNECT = "com.yuntongxun.Intent_Action_SDK_CONNECT";
  public static final String ACTION_KICK_OFF = "com.yuntongxun.Intent_ACTION_KICK_OFF";
  private ECNotifyOptions mOptions;
  private ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
  private ECInitParams.LoginMode mMode = ECInitParams.LoginMode.FORCE_LOGIN;
  private Context mContext;
  private boolean mKickOff = false;
  private ECInitParams mInitParams;
  /** 初始化错误 */
  public static final int ERROR_CODE_INIT = -3;

  private SDKCoreHelper() {
    initNotifyOptions();
  }

  private static SDKCoreHelper sInstance;

  public static SDKCoreHelper getInstance() {
    if (sInstance == null) {
      sInstance = new SDKCoreHelper();
    }
    return sInstance;
  }

  private Handler handler;

  public synchronized void setHandler(final Handler handler) {
    this.handler = handler;
  }

  public static boolean isKickOff() {
    return getInstance().mKickOff;
  }

  public static void init(Context ctx) {
    init(ctx, ECInitParams.LoginMode.AUTO);
  }

  public static void init(Context ctx, ECInitParams.LoginMode mode) {
    getInstance().mKickOff = false;
    LogUtil.d(TAG, "[init] start regist..");
    ctx = UIUtils.getContext();
    getInstance().mMode = mode;
    getInstance().mContext = ctx;
    // 判断SDK是否已经初始化，没有初始化则先初始化SDK
    if (!ECDevice.isInitialized()) {
      getInstance().mConnect = ECDevice.ECConnectState.CONNECTING;
      // ECSDK.setNotifyOptions(getInstance().mOptions);
      ECDevice.initial(ctx, getInstance());

      return;
    }
    LogUtil.d(TAG, " SDK has inited , then regist..");
    // 已经初始化成功，直接进行注册
    getInstance().onInitialized();
  }

  private void initNotifyOptions() {
    if (mOptions == null) {
      mOptions = new ECNotifyOptions();
    }
    // 设置新消息是否提醒
    mOptions.setNewMsgNotify(true);
    // 设置状态栏通知图标
    mOptions.setIcon(R.mipmap.log_logo);
    // 设置是否启用勿扰模式（不会声音/震动提醒）
    mOptions.setSilenceEnable(false);
    // 设置勿扰模式时间段（开始小时/开始分钟-结束小时/结束分钟）
    // 小时采用24小时制
    // 如果设置勿扰模式不启用，则设置勿扰时间段无效
    // 当前设置晚上11点到第二天早上8点之间不提醒
    mOptions.setSilenceTime(23, 0, 8, 0);
    // 设置是否震动提醒(如果处于免打扰模式则设置无效，没有震动)
    mOptions.enableShake(true);
    // 设置是否声音提醒(如果处于免打扰模式则设置无效，没有声音)
    mOptions.enableSound(true);
  }

  /**
   * IM聊天功能接口
   */
  public static ECChatManager getECChatManager() {
    ECChatManager ecChatManager = ECDevice.getECChatManager();
    LogUtil.d(TAG, "ecChatManager :" + ecChatManager);
    return ecChatManager;
  }

  @Override public void onInitialized() {
    // 设置消息提醒
    ECDevice.setNotifyOptions(mOptions);
    IMChattingHelper.getInstance().initManager();
    // 设置SDK注册结果回调通知，当第一次初始化注册成功或者失败会通过该引用回调
    // 通知应用SDK注册状态
    // 当网络断开导致SDK断开连接或者重连成功也会通过该设置回调
    ECDevice.setOnChatReceiveListener(IMChattingHelper.getInstance());
    ECDevice.setOnDeviceConnectListener(this);

    UserInfo clientUser = CarApplication.getUserInfo();
    if (mInitParams == null) {
      mInitParams = ECInitParams.createParams();
    }
    mInitParams.reset();
    // 如：VoIP账号/手机号码/..
    mInitParams.setUserid(clientUser.getVoipAccount());
    // appkey
    mInitParams.setAppKey(clientUser.getAppKey());
    // mInitParams.setAppKey(/*clientUser.getAppKey()*/"ff8080813d823ee6013d856001000029");
    // appToken
    mInitParams.setToken(clientUser.getAppToken());
    // mInitParams.setToken(/*clientUser.getAppToken()*/"d459711cd14b443487c03b8cc072966e");
    // ECInitParams.LoginMode.FORCE_LOGIN
    mInitParams.setMode(getInstance().mMode);

    mInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);

    if (!mInitParams.validate()) {
      UIUtils.showToast("注册参数错误，请检查");
      Intent failIntent = new Intent(ACTION_SDK_CONNECT);
      failIntent.putExtra("error", -1);
      mContext.sendBroadcast(failIntent);
      return;
    }

    ECDevice.login(mInitParams);
  }

  /**
   * 当前SDK注册状态
   */
  public static ECDevice.ECConnectState getConnectState() {
    return getInstance().mConnect;
  }

  @Override public void onError(Exception e) {
    Intent intent = new Intent(ACTION_SDK_CONNECT);
    intent.putExtra("error", ERROR_CODE_INIT);
    mContext.sendBroadcast(intent);
    ECDevice.unInitial();
  }

  @Override public void onConnect() {

  }

  @Override public void onDisconnect(ECError ecError) {

  }

  @Override public void onConnectState(ECDevice.ECConnectState state, ECError error) {
    if (state == ECDevice.ECConnectState.CONNECT_FAILED
        && error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
      return;
    }
  }

  @Override public void onLogout() {
    getInstance().mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
    if (mInitParams != null && mInitParams.getInitParams() != null) {
      mInitParams.getInitParams().clear();
    }
    mInitParams = null;
    mContext.sendBroadcast(new Intent(ACTION_LOGOUT));
  }

  public static void logout(boolean isNotice) {
    ECDevice.NotifyMode notifyMode =
        (isNotice) ? ECDevice.NotifyMode.IN_NOTIFY : ECDevice.NotifyMode.NOT_NOTIFY;
    ECDevice.logout(notifyMode, getInstance());

    release();
  }

  public static void release() {
    getInstance().mKickOff = false;
    IMChattingHelper.getInstance().destroy();
  }

  /**
   * 判断服务是否自动重启
   *
   * @return 是否自动重启
   */
  public static boolean isUIShowing() {
    return ECDevice.isInitialized();
  }
}
