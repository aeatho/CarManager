package com.unovo.carmanager.bean;

/**
 * Created by Administrator on 2016/10/19.
 */

public class StatusInfo {
  private int UserID;
  private double Lat;
  private double Lon;
  private boolean Isintruck;

  public int getUserID() {
    return UserID;
  }

  public void setUserID(int UserID) {
    this.UserID = UserID;
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

  public boolean Isintruck() {
    return Isintruck;
  }

  public void setIsintruck(boolean Isintruck) {
    this.Isintruck = Isintruck;
  }
}
