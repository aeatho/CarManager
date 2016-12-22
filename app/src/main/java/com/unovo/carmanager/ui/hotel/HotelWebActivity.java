package com.unovo.carmanager.ui.hotel;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.widget.EmptyLayout;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager.ui.hotel
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/11 01:46
 * @version: V1.0
 */
public class HotelWebActivity extends BaseActivity {
  private WebView webView;
  private EmptyLayout mEmptyLayout;

  private String url = "http://tuan.ctrip.com/group/4115226.html#ctm_ref=grh_sr_pm_def_b";

  @Override protected int getLayoutId() {
    return R.layout.activity_web;
  }

  @Override protected int getActionBarTitle() {
    return R.string.title_hotel_detail;
  }

  @Override protected void init(Bundle savedInstanceState) {
    webView = (WebView) findViewById(R.id.webview);
    mEmptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

    initWebView(webView);

    webView.loadUrl(url);
  }

  private void initWebView(WebView webView) {
    webView.setWebChromeClient(new WebChromeClient() {
      @Override public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress >= 98) {
          mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
        super.onProgressChanged(view, newProgress);
      }
    });
    webView.setWebViewClient(new WebViewClient());

    WebSettings settings = webView.getSettings();
    //支持屏幕缩放
    settings.setSupportZoom(true);
    settings.setBuiltInZoomControls(true);
    //不显示webview缩放按钮
    settings.setDisplayZoomControls(false);

    settings.setJavaScriptEnabled(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }
    webView.requestFocus();
  }
}
