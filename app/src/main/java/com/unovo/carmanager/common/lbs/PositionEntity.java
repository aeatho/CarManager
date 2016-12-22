package com.unovo.carmanager.common.lbs;

import java.io.Serializable;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: apartment_app-V2
 * @Location: com.unovo.apartment.v2.lbs
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 16/4/13 下午11:58
 * @version: V1.0
 */
public class PositionEntity implements Serializable{
  private double latitue;
  private double longitude;
  private String address;
  private String city;

  public double getLatitue() {
    return latitue;
  }

  public void setLatitue(double latitue) {
    this.latitue = latitue;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public PositionEntity() {
  }

  public PositionEntity(double latitude, double longtitude, String address, String city) {
    this.latitue = latitude;
    this.longitude = longtitude;
    this.address = address;
  }

  public PositionEntity(double latitude, double longtitude) {
    this.latitue = latitude;
    this.longitude = longtitude;
  }
}
