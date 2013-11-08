package com.foreveross.chameleon.phone.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.BroadcastConstans;
import com.csair.impc.R;
import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.PreferencesUtil;

public class BaseActivity extends Activity {
	protected Application application;
	protected BroadcastReceiver broadcastReceiver;
	protected IntentFilter filter = new IntentFilter();
	protected Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = Application.class.cast(getApplication());
		application.getActivityManager().pushActivity(this);
		handler = new Handler();
		if (!PadUtils.isPad(application)) {
			PreferencesUtil.setValue(this, "DeviceType", "Android Phone");
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} else {
			PreferencesUtil.setValue(this, "DeviceType", "Android Pad");
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
		if (application.getCubeApplication() == null) {
			CubeApplication app = CubeApplication.getInstance(this);
			app.loadApplication();
			application.setCubeApplication(app);
		}

		filter.addAction("com.xmpp.mutipleAccount");
		filter.addAction(BroadcastConstans.MODULE_RESET);
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("com.xmpp.mutipleAccount")) {
					sendBroadcast(new Intent("push.model.change"));
					// 被逼下线
					Dialog dialog = new AlertDialog.Builder(BaseActivity.this)
							.setCancelable(false)
							.setTitle("提示")
							.setMessage("你的账号已在别处被登录，请重启应用")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											application.logOff();
											
										}
									}).create();
					dialog.show();
				}// 重置模块广播
				else if (intent.getAction().equals(
						BroadcastConstans.MODULE_RESET)) {
					String status = intent.getStringExtra("resetStatus");

					if ("isReset".equals(status)) {

					} else if ("start".equals(status)) {
						// DialogUtil.showProgressDialog(progressDialog,
						// "正在重置应用");
					} else if ("finish".equals(status)) {
						// DialogUtil.dismissProgressDialog(progressDialog);
						Toast.makeText(BaseActivity.this, "重置成功",
								Toast.LENGTH_SHORT).show();
						application.logOff();
					} else if ("failed".equals(status)) {
						// DialogUtil.dismissProgressDialog(progressDialog);
						Toast.makeText(BaseActivity.this, "重置失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		};
		registerReceiver(broadcastReceiver, filter);

	}
	
	@Override 
    public void onBackPressed() { 
        super.onBackPressed(); 
        application.getActivityManager().popActivity(this); 
    } 

	public final boolean post(Runnable r) {
		return handler.post(r);
	}

	public final boolean postAtFrontOfQueue(Runnable r) {
		return handler.postAtFrontOfQueue(r);
	}

	public final boolean postAtTime(Runnable r, long uptimeMillis) {
		return handler.postAtTime(r, uptimeMillis);
	}

	public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
		return handler.postAtTime(r, token, uptimeMillis);
	}

	public final boolean postDelayed(Runnable r, long delayMillis) {
		return handler.postDelayed(r, delayMillis);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		application.getActivityManager().popActivity(this);;
		unregisterReceiver(broadcastReceiver);
		cancelDialog();
	}

	public Dialog progressDialog;

	public void showCustomDialog(boolean cancelable) {
		if (progressDialog == null) {
			progressDialog = new Dialog(this, R.style.dialog);
			progressDialog.setContentView(R.layout.dialog_layout);
		}

		if (progressDialog.isShowing()) {
			return;
		}
		progressDialog.setCancelable(cancelable);
		progressDialog.show();
	}

	public void cancelDialog() {
		if (progressDialog == null) {
			return;
		}
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}

}
