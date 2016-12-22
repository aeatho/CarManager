package com.unovo.carmanager.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.service
 * @Description: TODO
 * @author: loQua.Xee
 * @email: shyscool@163.com
 * @date: 2016/12/22 19:25
 * @version: V1.0
 */
public class ShakeService extends Service implements SensorEventListener {
  private static final int UPTATE_INTERVAL_TIME = 50;

  private SensorManager mSensor = null;
  private Vibrator mVibrator = null;
  private int mSpeedThreshold = 45;// 这个值越大需要越大的力气来摇晃手机

  private float mSensorLastX;
  private float mSensorLastY;
  private float mSensorLastZ;
  private long mSensorLastUpdateTime;

  private ShakeBinder mBinder = new ShakeBinder();

  public class ShakeBinder extends Binder {
    public ShakeService getService() {
      return ShakeService.this;
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override public void onCreate() {
    super.onCreate();
    initShake();
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {

    return START_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    unregisterSensor();
  }

  private void initShake() {
    mSensor = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
    registerSensor();
  }

  @Override public void onSensorChanged(SensorEvent event) {
    long currentUpdateTime = System.currentTimeMillis();
    long timeInterval = currentUpdateTime - mSensorLastUpdateTime;
    if (timeInterval < UPTATE_INTERVAL_TIME) {
      return;
    }
    mSensorLastUpdateTime = currentUpdateTime;

    float x = event.values[0];
    float y = event.values[1];
    float z = event.values[2];

    float deltaX = x - mSensorLastX;
    float deltaY = y - mSensorLastY;
    float deltaZ = z - mSensorLastZ;

    mSensorLastX = x;
    mSensorLastY = y;
    mSensorLastZ = z;

    double speed =
        (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval) * 100;
    if (speed >= mSpeedThreshold) {
      mVibrator.vibrate(300);
      onShake();
    }
  }

  public void onShake() {

  }

  @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  protected boolean mIsRegister;

  public void registerSensor() {
    if (mSensor != null && !mIsRegister) {
      Sensor sensor = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      if (sensor != null) {
        mIsRegister = true;
        mSensor.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
      }
    }
  }

  public void unregisterSensor() {
    if (mSensor != null && mIsRegister) {
      mIsRegister = false;
      mSensor.unregisterListener(this);
    }
  }
}
