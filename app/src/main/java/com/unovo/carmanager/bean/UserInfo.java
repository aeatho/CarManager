package com.unovo.carmanager.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/20.
 */
public class UserInfo implements Serializable {

  /**
   * UserID : 2
   * UserName : jiang
   * DisplayName : jiang
   * VoipAccount : 8017395600000002
   * VoipPwd : 4LsQFD0Z
   * SubToken : 44a4cd1083e34be75d8e0c5ce8087be3
   * SubAccountSid : f5d150d4a65311e69eaa6c92bf2c165d
   */

  private String UserID;
  private String UserName;
  private String DisplayName;
  private String VoipAccount;
  private String VoipPwd;
  private String SubToken;
  private String SubAccountSid;

  public String getUserID() {
    return UserID;
  }

  public void setUserID(String UserID) {
    this.UserID = UserID;
  }

  public String getUserName() {
    return UserName;
  }

  public void setUserName(String UserName) {
    this.UserName = UserName;
  }

  public String getDisplayName() {
    return DisplayName;
  }

  public void setDisplayName(String DisplayName) {
    this.DisplayName = DisplayName;
  }

  public String getVoipAccount() {
    return VoipAccount;
  }

  public void setVoipAccount(String VoipAccount) {
    this.VoipAccount = VoipAccount;
  }

  public String getVoipPwd() {
    return VoipPwd;
  }

  public void setVoipPwd(String VoipPwd) {
    this.VoipPwd = VoipPwd;
  }

  public String getSubToken() {
    return SubToken;
  }

  public void setSubToken(String SubToken) {
    this.SubToken = SubToken;
  }

  public String getSubAccountSid() {
    return SubAccountSid;
  }

  public void setSubAccountSid(String SubAccountSid) {
    this.SubAccountSid = SubAccountSid;
  }

  private String appKey;

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String temp) {
    appKey = temp;
  }

  private String appToken;

  public String getAppToken() {
    return appToken;
  }

  public void setAppToken(String temp) {
    appToken = temp;
  }
}
