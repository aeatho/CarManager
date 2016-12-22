package com.unovo.carmanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.unovo.carmanager.CarApplication;
import com.unovo.carmanager.R;
import com.unovo.carmanager.base.BaseActivity;
import com.unovo.carmanager.bean.UserInfo;
import com.unovo.carmanager.common.network.HttpClient;
import com.unovo.carmanager.constant.Constants;
import com.unovo.carmanager.utils.DialogHelper;
import com.unovo.carmanager.utils.Settings;
import com.unovo.carmanager.utils.StringUtils;
import com.unovo.carmanager.utils.ToastUtil;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/7/14.
 */

public class LoginActivity extends BaseActivity {

  private EditText mEtName, mEtPwd;

  @Override protected int getLayoutId() {
    return R.layout.activity_login;
  }

  @Override protected void init(Bundle savedInstanceState) {
    mActionBar.hide();

    mEtName = (EditText) findViewById(R.id.et_name);
    mEtPwd = (EditText) findViewById(R.id.et_pwd);
    findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        doLogin(mEtName.getText().toString(), mEtPwd.getText().toString());
      }
    });
  }

  private void doLogin(String name, String pwd) {
    if (StringUtils.isEmpty(name) || StringUtils.isEmpty(pwd)) {
      showToast("用户名或密码不能为空！");
      return;
    }

    showWaitDialog();
    Call<UserInfo> login = HttpClient.getInstance().getAPIs().Login(name, pwd);
    login.enqueue(new Callback<UserInfo>() {
      @Override public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
        hideWaitDialog();
        if (response.code() == 200 && response.body() != null) {
          loginSuccess(response.body());
        } else {
          String result = "";
          try {
            result = response.errorBody().string();
          } catch (IOException e) {
            result = "用户名或密码错误!";
          } finally {
            DialogHelper.showMessageDialog(LoginActivity.this, result);
          }
        }
      }

      @Override public void onFailure(Call<UserInfo> call, Throwable t) {
        hideWaitDialog();
        ToastUtil.showMessage(t.getMessage());
      }
    });
  }

  // 登录成功
  private void loginSuccess(UserInfo userInfo) {
    saveUserInfo(userInfo);
    //userInfo.setUserID("15201922143");
    userInfo.setAppKey(Constants.rongyuntong_appkey);
    userInfo.setAppToken(Constants.rongyuntong_token);
    CarApplication.setUserInfo(userInfo);
    startActivity(new Intent(LoginActivity.this, MainActivity.class));
    LoginActivity.this.finish();
  }

  private void saveUserInfo(UserInfo obj) {
    Settings.saveUID(this, StringUtils.toString(obj.getUserID()));
    Settings.saveUName(this, StringUtils.toString(obj.getDisplayName()));
  }
}
