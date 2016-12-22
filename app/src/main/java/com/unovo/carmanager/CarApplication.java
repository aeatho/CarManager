package com.unovo.carmanager;

import com.unovo.carmanager.base.BaseApplication;
import com.unovo.carmanager.bean.UserInfo;
import com.unovo.carmanager.utils.LogUtil;
import com.yuntongxun.ecsdk.ECMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/8 14:43
 * @version: V1.0
 */
public class CarApplication extends BaseApplication {
  private static CarApplication instance;

  public static CarApplication getInstance() {
    if (instance == null) {
      LogUtil.w("[ECApplication] instance is null.");
    }
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
  }

  private static  UserInfo userInfo;
  public static UserInfo getUserInfo() {
    return userInfo;
  }

  public static void setUserInfo(UserInfo temp){
    userInfo = temp;
  }

  private static List<ECMessage> msgs = new ArrayList<>();
  public static void addOffMessages(ECMessage temp) {
    msgs.add(temp);
  }

  public static List<ECMessage> getOffMessages(){
    return msgs;
  }
}
