package com.unovo.carmanager.ui.guid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.unovo.carmanager.R;
import com.unovo.carmanager.common.map.ToastUtil;
import java.util.List;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.guid
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/11 00:30
 * @version: V1.0
 */
public class WeatherDialog extends Dialog implements WeatherSearch.OnWeatherSearchListener {
  private Context context;
  private TextView city;
  private TextView forecasttv;
  private TextView reporttime1;
  private TextView reporttime2;
  private TextView weather;
  private TextView Temperature;
  private TextView wind;
  private TextView humidity;
  private WeatherSearchQuery mquery;
  private WeatherSearch mweathersearch;
  private LocalWeatherLive weatherlive;
  private LocalWeatherForecast weatherforecast;
  private List<LocalDayWeatherForecast> forecastlist = null;
  private String cityname;

  public WeatherDialog(Context context, String cityname) {
    super(context, R.style.loadingDialog);
    this.context = context;
    this.cityname = TextUtils.isEmpty(cityname) ? "上海市" : cityname;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    init();
    searchliveweather();
    searchforcastsweather();
  }

  private void init() {
    setContentView(getLoadingView());// 设置内容视图

    Window window = this.getWindow();
    //        window.setWindowAnimations(com.unovo.library.R.style.dialog_anim);
    WindowManager.LayoutParams wl = window.getAttributes();
    wl.width = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
    window.setGravity(Gravity.CENTER);// 布局位置为底端

    onWindowAttributesChanged(wl);
    setCanceledOnTouchOutside(false);// 点击对话框其他地方是否使对话框消失~
  }

  private View getLoadingView() {
    View view = LayoutInflater.from(context).inflate(R.layout.dialog_weather, null);
    city = (TextView) view.findViewById(R.id.city);
    city.setText(cityname);
    forecasttv = (TextView) view.findViewById(R.id.forecast);
    reporttime1 = (TextView) view.findViewById(R.id.reporttime1);
    reporttime2 = (TextView) view.findViewById(R.id.reporttime2);
    weather = (TextView) view.findViewById(R.id.weather);
    Temperature = (TextView) view.findViewById(R.id.temp);
    wind = (TextView) view.findViewById(R.id.wind);
    humidity = (TextView) view.findViewById(R.id.humidity);

    return view;
  }

  private void searchforcastsweather() {
    mquery = new WeatherSearchQuery(cityname,
        WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
    mweathersearch = new WeatherSearch(context);
    mweathersearch.setOnWeatherSearchListener(this);
    mweathersearch.setQuery(mquery);
    mweathersearch.searchWeatherAsyn(); //异步搜索
  }

  private void searchliveweather() {
    mquery = new WeatherSearchQuery(cityname,
        WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
    mweathersearch = new WeatherSearch(context);
    mweathersearch.setOnWeatherSearchListener(this);
    mweathersearch.setQuery(mquery);
    mweathersearch.searchWeatherAsyn(); //异步搜索
  }

  /**
   * 实时天气查询回调
   */
  @Override public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
    if (rCode == 1000) {
      if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
        weatherlive = weatherLiveResult.getLiveResult();
        reporttime1.setText(weatherlive.getReportTime() + "发布");
        weather.setText(weatherlive.getWeather());
        Temperature.setText(weatherlive.getTemperature() + "°");
        wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
        humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
      } else {
        ToastUtil.show(context, R.string.no_result);
      }
    } else {
      ToastUtil.showerror(context, rCode);
    }
  }

  /**
   * 天气预报查询结果回调
   */
  @Override public void onWeatherForecastSearched(LocalWeatherForecastResult weatherForecastResult,
      int rCode) {
    if (rCode == 1000) {
      if (weatherForecastResult != null
          && weatherForecastResult.getForecastResult() != null
          && weatherForecastResult.getForecastResult().getWeatherForecast() != null
          && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
        weatherforecast = weatherForecastResult.getForecastResult();
        forecastlist = weatherforecast.getWeatherForecast();
        fillforecast();
      } else {
        ToastUtil.show(context, R.string.no_result);
      }
    } else {
      ToastUtil.showerror(context, rCode);
    }
  }

  private void fillforecast() {
    reporttime2.setText(weatherforecast.getReportTime() + "发布");
    String forecast = "";
    for (int i = 0; i < forecastlist.size(); i++) {
      LocalDayWeatherForecast localdayweatherforecast = forecastlist.get(i);
      String week = null;
      switch (Integer.valueOf(localdayweatherforecast.getWeek())) {
        case 1:
          week = "周一";
          break;
        case 2:
          week = "周二";
          break;
        case 3:
          week = "周三";
          break;
        case 4:
          week = "周四";
          break;
        case 5:
          week = "周五";
          break;
        case 6:
          week = "周六";
          break;
        case 7:
          week = "周日";
          break;
        default:
          break;
      }
      String temp = String.format("%-3s/%3s", localdayweatherforecast.getDayTemp() + "°",
          localdayweatherforecast.getNightTemp() + "°");
      String date = localdayweatherforecast.getDate();
      forecast += date + "  " + week + "                       " + temp + "\n\n";
    }
    forecasttv.setText(forecast);
  }
}
