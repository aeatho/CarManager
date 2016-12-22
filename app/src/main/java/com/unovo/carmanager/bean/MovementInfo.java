package com.unovo.carmanager.bean;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MovementInfo {

  /**
   * ActivityID : 10002
   * Title : 杭州自驾游
   * Description : 杭州自驾游
   * ActivityTime : 2016/08/23
   * ActivityPlace : 杭州
   * AuditStatus : true
   * CreatedBy : 86738433813
   * CreatedOn : 2016-08-08T09:18:26.293
   */

  private int ActivityID;
  private String Title;
  private String Description;
  private String ActivityTime;
  private String ActivityPlace;
  private boolean AuditStatus;
  private String CreatedBy;
  private String CreatedOn;

  public int getActivityID() {
    return ActivityID;
  }

  public void setActivityID(int ActivityID) {
    this.ActivityID = ActivityID;
  }

  public String getTitle() {
    return Title;
  }

  public void setTitle(String Title) {
    this.Title = Title;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String Description) {
    this.Description = Description;
  }

  public String getActivityTime() {
    return ActivityTime;
  }

  public void setActivityTime(String ActivityTime) {
    this.ActivityTime = ActivityTime;
  }

  public String getActivityPlace() {
    return ActivityPlace;
  }

  public void setActivityPlace(String ActivityPlace) {
    this.ActivityPlace = ActivityPlace;
  }

  public boolean isAuditStatus() {
    return AuditStatus;
  }

  public void setAuditStatus(boolean AuditStatus) {
    this.AuditStatus = AuditStatus;
  }

  public String getCreatedBy() {
    return CreatedBy;
  }

  public void setCreatedBy(String CreatedBy) {
    this.CreatedBy = CreatedBy;
  }

  public String getCreatedOn() {
    return CreatedOn;
  }

  public void setCreatedOn(String CreatedOn) {
    this.CreatedOn = CreatedOn;
  }
}
