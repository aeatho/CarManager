package com.unovo.carmanager.common.network;

import com.unovo.carmanager.R;
import com.unovo.carmanager.utils.UIUtils;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Administrator on 2016/7/20.
 * logging-interceptor是记录网络请求日志的
 */
public class HttpClient {
  public static final String BASE_URL = UIUtils.getString(R.string.base_url);
  ;
  //超时时间15秒
  public static final int BASE_TIMEOUT = 15;
  private static HttpClient mInstance;
  private static OkHttpClient mOkHttpClient;
  private final ApiService mHttpService;

  public static synchronized HttpClient getInstance() {
    if (mInstance == null) {
      mInstance = new HttpClient();
    }
    return mInstance;
  }

  private HttpClient() {
    initOkHttpClient();
    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
        .client(mOkHttpClient)
        // 增加对返回值为string的支持
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    mHttpService = retrofit.create(ApiService.class);
  }

  private void initOkHttpClient() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    if (mOkHttpClient == null) {
      synchronized (HttpClient.class) {
        if (mOkHttpClient == null) {
          mOkHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
              .retryOnConnectionFailure(true)
              .connectTimeout(BASE_TIMEOUT, TimeUnit.SECONDS)
              .build();
        }
      }
    }
  }

  public ApiService getAPIs() {
    return mHttpService;
  }
}
