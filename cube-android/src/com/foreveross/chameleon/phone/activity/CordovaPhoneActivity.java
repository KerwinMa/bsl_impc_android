package com.foreveross.chameleon.phone.activity;

import java.util.Set;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.BroadcastConstans;
import com.csair.impc.R;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.google.gson.Gson;

public class CordovaPhoneActivity extends DroidGap implements CordovaInterface {
	private String url = null;
	BroadcastReceiver broadcastReceiver;
	public ProgressBar progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		appView.setVisibility(View.VISIBLE);
		// progressDialog = new ProgressBar(this);
		// progressDialog.setLayoutParams(new LayoutParams(40,40));
		// this.root.addView(progressDialog, new LayoutParams(Gravity.CENTER));
		url = getIntent().getStringExtra("value");
		loadUrl(url);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastConstans.PUSH_MODULE_MESSAGE);
		filter.addAction(BroadcastConstans.MODULE_PROGRESS);
		filter.addAction(BroadcastConstans.MODULE_WEB);
		filter.addAction("com.xmpp.mutipleAccount");
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction()
						.equals(BroadcastConstans.MODULE_PROGRESS)) {
					String identifier = intent.getStringExtra("identifier");
					int progress = intent.getIntExtra("progress", 0);
					sendProcessToJs(identifier, progress);
				} else if (intent.getAction().equals(
						BroadcastConstans.PUSH_MODULE_MESSAGE)) {
					String identifier = intent.getStringExtra("identifier");
					int count = intent.getIntExtra("count", 0);
					receiveMessage(identifier, count);
					// sendJavascript("")
				} /*else if (intent.getAction().equals(
						BroadcastConstans.PUSH_CHAT)) {
					Log.i("AAAAA---CordovaPhoneActivity",
							"broadcastConstans.PUSH_CHAT");
				} */else if (intent.getAction().equals(
						BroadcastConstans.MODULE_WEB)) {
					System.out
							.println("AAAAAA-----BroadcastConstans.MODULE_WEB");
					String identifier = intent.getStringExtra("identifier");
					String type = intent.getStringExtra("type");
					CubeModule module = CubeModuleManager.getInstance()
							.getIdentifier_new_version_map().get(identifier);
					if (module == null) {
						module = CubeModuleManager.getInstance()
								.getCubeModuleByIdentifier(identifier);
					}
					refreshModule(identifier, type, module);
				} else if (intent.getAction().equals("com.xmpp.mutipleAccount")) {
					sendBroadcast(new Intent("push.model.change"));
					// 被逼下线
					Dialog dialog = new AlertDialog.Builder(
							CordovaPhoneActivity.this)
							.setCancelable(false)
							.setTitle("提示")
							.setMessage("你的账号已在别处被登录，请重启应用")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// popRightAll();
											findViewById(R.id.child_frame)
													.setVisibility(
															View.INVISIBLE);
											Application.class
													.cast(CordovaPhoneActivity.this
															.getApplicationContext())
													.logOff();
										}
									}).create();
					dialog.show();
				}
			}
		};
		registerReceiver(broadcastReceiver, filter);
	}

	@Override
	public void init() {
		CordovaWebView webView = new CordovaWebView(this);
		CordovaWebViewClient webViewClient;

		if (Build.VERSION.SDK_INT < 11) {
			webViewClient = new CordovaWebViewClient(this, webView) {

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					System.out.println(url);
					if (url.contains("cube-action:pop")) {
						return true;
					} else if (url.contains("cube-action:push")) {
						return true;
					} else {
						return super.shouldOverrideUrlLoading(view, url);
					}
				}
			};
		} else {
			webViewClient = new IceCreamCordovaWebViewClient(this, webView) {
			};
		}
		init(webView, webViewClient, new CubeCordovaChromeWebViewClient(this,
				webView));
	}

	class CubeCordovaChromeWebViewClient extends CordovaChromeClient {

		public CubeCordovaChromeWebViewClient(CordovaInterface cordova,
				CordovaWebView view) {
			super(cordova, view);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("cordova is onDestroy");
		unregisterReceiver(broadcastReceiver);
	}

	public void sendProcessToJs(String identifier, int progress) {
		Log.v("CordovaWebView", "CordovaWebView send Progress" + progress);
		System.out.println("进来sendProcessToJs");
		appView.sendJavascript("updateProgress('" + identifier + "','"
				+ progress + "')");
		System.out.println("出去sendProcessToJs");
		// appView.sendJavascript("alert("+progress+")");
	}

	/*
	 * public void sendCountToJs(String identifier,int count){
	 * Log.v("CordovaWebView", "CordovaWebView send Progress" + count);
	 * appView.sendJavascript("updateProgress('"+identifier+"','"+count+"')");
	 * System.out.println("已经发送updateProgress"); //
	 * appView.sendJavascript("alert("+progress+")"); }
	 */
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			String extraMessage = "";
			Set<CubeModule> allSet = CubeModuleManager.getInstance().getAllSet();
			StringBuilder builder = new StringBuilder();
			for (CubeModule cubeModule : allSet) {
				switch (cubeModule.getModuleType()) {
				case CubeModule.DELETING: {
					builder.append(" 删除");
					break;
				}
				case CubeModule.INSTALLING: {
					builder.append(" 安装");
					break;
				}
				case CubeModule.UPGRADING: {
					builder.append(" 升级");
					break;
				}
				}
			}
			if (builder.length() > 1) {
				// builder.append("正在进行中，退出可能导致操作失败，");
				// extraMessage = builder.toString();
				extraMessage = "有模块正在下载或更新中，退出可能导致操作失败";
			}
			Dialog dialog = new AlertDialog.Builder(this)
					.setTitle("提示")
					.setMessage(extraMessage + " 确定退出?")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									try {
										finish();
										// application.exitApp();
									} catch (Exception e) {
										e.printStackTrace();
										Log.e("exit", "退出应用异常");
									}
								}
							}).create();
			dialog.show();
			return true;
		}
		return false;
	};
	
	

	public void refreshModule(String identifier, String type, CubeModule module) {
		String moduleMessage = new Gson().toJson(module);
		appView.sendJavascript("refreshModule('" + identifier + "','" + type
				+ "','" + moduleMessage + "')");
	}

	public void receiveMessage(String identifier, int count) {
		appView.sendJavascript("receiveMessage('" + identifier + "'," + count
				+ ")");
	}
}
