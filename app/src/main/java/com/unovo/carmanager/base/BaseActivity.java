package com.unovo.carmanager.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import com.unovo.carmanager.AppManager;
import com.unovo.carmanager.R;
import com.unovo.carmanager.common.dialog.UnoProgressDialog;
import com.unovo.carmanager.utils.DialogHelper;
import com.unovo.carmanager.utils.StringUtils;
import com.unovo.carmanager.utils.SystemTool;
import com.unovo.carmanager.utils.ToastUtil;

/**
 * STAY HUNGRY, STAY FOOLISH!
 *
 * @Prject: CarManager
 * @Location: com.unovo.carmanager
 * @Description: TODO
 * @author: Aeatho.Xee
 * @email: aeatho@163.com
 * @date: 2016/11/8 16:09
 * @version: V1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
  private boolean _isVisible;
  private UnoProgressDialog _waitDialog;

  protected LayoutInflater mInflater;
  protected ActionBar mActionBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppManager.getInstance().addActivity(this);
    onBeforeSetContentLayout();
    if (getLayoutId() != 0) {
      setContentView(getLayoutId());
    }
    mActionBar = getSupportActionBar();
    mInflater = getLayoutInflater();
    if (hasActionBar()) {
      initActionBar(mActionBar);
    }

    init(savedInstanceState);
    _isVisible = true;
  }

  protected void onBeforeSetContentLayout() {
  }

  protected boolean hasActionBar() {
    return getSupportActionBar() != null;
  }

  protected abstract int getLayoutId();

  protected View inflateView(int resId) {
    return mInflater.inflate(resId, null);
  }

  protected int getActionBarTitle() {
    return R.string.app_name;
  }

  protected boolean hasBackButton() {
    return true;
  }

  protected void init(Bundle savedInstanceState) {
  }

  protected void initActionBar(ActionBar actionBar) {
    if (actionBar == null) return;
    if (hasBackButton()) {
      mActionBar.setDisplayHomeAsUpEnabled(true);
      mActionBar.setHomeButtonEnabled(true);
    } else {
      actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
      actionBar.setDisplayUseLogoEnabled(false);
    }
    int titleRes = getActionBarTitle();
    if (titleRes != 0) {
      actionBar.setTitle(titleRes);
    }
  }

  public void setActionBarTitle(int resId) {
    if (resId != 0) {
      setActionBarTitle(getString(resId));
    }
  }

  public void setActionBarTitle(String title) {
    if (StringUtils.isEmpty(title)) {
      title = getString(R.string.app_name);
    }
    if (hasActionBar() && mActionBar != null) {
      mActionBar.setTitle(title);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;

      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override protected void onPause() {
    super.onPause();
    if (this.isFinishing()) {
      SystemTool.hideSoftKeyboard(getCurrentFocus());
    }
  }

  public void showToast(int msgResid) {
    ToastUtil.showMessage(msgResid);
  }

  public void showToast(String message) {
    ToastUtil.showMessage(message);
  }

  public UnoProgressDialog showWaitDialog() {
    return showWaitDialog(R.string.loading);
  }

  public UnoProgressDialog showWaitDialog(int resid) {
    return showWaitDialog(getString(resid));
  }

  public UnoProgressDialog showWaitDialog(String message) {
    if (_isVisible) {
      if (_waitDialog == null) {
        _waitDialog = DialogHelper.getWaitDialog(this, message);
        _waitDialog.setCancelable(false);
        _waitDialog.setCanceledOnTouchOutside(false);
      }
      if (_waitDialog != null) {
        _waitDialog.setPressText(message);
        _waitDialog.show();
      }
      return _waitDialog;
    }
    return null;
  }

  public void hideWaitDialog() {
    if (_isVisible && _waitDialog != null) {
      try {
        _waitDialog.dismiss();
        _waitDialog = null;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (_waitDialog != null) {
      _waitDialog.dismiss();
      _waitDialog = null;
    }

    AppManager.getInstance().finishActivity(this);
  }
}

