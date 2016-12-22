package com.unovo.carmanager.bean;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.bean
 * @Description: TODO
 * @author: loQua.Xee
 * @email: shyscool@163.com
 * @date: 2016/12/22 12:18
 * @version: V1.0
 */
public enum SOSType {
  AUTO(1), NROMAL(2);

  private int type;

  SOSType(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }
}
