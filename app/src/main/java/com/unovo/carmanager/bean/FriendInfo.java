package com.unovo.carmanager.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/2.
 */
public class FriendInfo implements Serializable {

  /**
   * UserID : 2
   * UserName : jiang
   * DisplayName : jiang
   * TelePhone :
   * Lat : 31.216118
   * Lon : 111.605153
   * TruckLicense : 鲁S 154K9
   */

  private String UserID;
  private String UserName;
  private String DisplayName;
  private String TelePhone;
  private double Lat;
  private double Lon;
  private String TruckLicense;
  private String VoipAccount;

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

  public String getTelePhone() {
    return TelePhone;
  }

  public void setTelePhone(String TelePhone) {
    this.TelePhone = TelePhone;
  }

  public double getLat() {
    return Lat;
  }

  public void setLat(double Lat) {
    this.Lat = Lat;
  }

  public double getLon() {
    return Lon;
  }

  public void setLon(double Lon) {
    this.Lon = Lon;
  }

  public String getTruckLicense() {
    return TruckLicense;
  }

  public void setTruckLicense(String TruckLicense) {
    this.TruckLicense = TruckLicense;
  }

  public String getVoipAccount() {
    return VoipAccount;
  }

  public void setVoipAccount(String voipAccount) {
    VoipAccount = voipAccount;
  }
}
