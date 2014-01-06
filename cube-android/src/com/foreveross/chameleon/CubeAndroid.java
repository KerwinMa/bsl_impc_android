package com.foreveross.chameleon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.phone.ActivityManager;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.util.FileIntent;
import com.foreveross.chameleon.util.PreferencesUtil;

public class CubeAndroid extends DroidGap {
	public final static int DEVIDE_PAD = 0x01;
	public final static int DEVIDE_PHONE = 0x02;
	public ActivityManager activityManager;
	private String path = null;
	private String identify = null;
	private String from = null;
	private boolean isPageLoadFinish = false;
	private String mainUrl = null;
	private boolean touchable = false;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.v("dd", "CubeAndroid on cretate");

//		Application.class.cast(this.getApplication()).pushWebActivity(this);
		ActivityManager.getScreenManager().pushWeb(this);
		// 为了回调
		init();
		from = getIntent().getStringExtra("from");
		// CookieManager cookieManager = CookieManager.getInstance();
		// cookieManager.setAcceptCookie(true);
		// cookieManager.removeAllCookie();
		if ("main".equals(from)) {

			path = getIntent().getStringExtra("path");
			identify = getIntent().getStringExtra("identify");
			mainUrl = "file:///" + path + "/www/" + identify + "/index.html";
			if (null != getIntent().getStringExtra("recordId")
					&& !"".equals(getIntent().getStringExtra("recordId"))) {
				mainUrl += "?recordId="
						+ getIntent().getStringExtra("recordId");
			}
			super.appView.loadUrl(mainUrl);
			System.out.println("path==" + "file:///" + path + "/www/"
					+ identify + "/index.html");
		} else if ("web".equals(from)) {
			mainUrl = getIntent().getStringExtra("url");
			super.appView.loadUrl(mainUrl);
		} else if ("set".equals(from)) {
			path = getIntent().getStringExtra("path");
			identify = getIntent().getStringExtra("identify");
			mainUrl = "file:///" + path + "/www/" + identify
					+ "/index.html#"+identify+"/settings";
			super.appView.loadUrl(mainUrl);
		} else if ("index".equals(from)) {
			path = getIntent().getStringExtra("path");
			identify = getIntent().getStringExtra("identify");
			String defaultView = getIntent().getStringExtra("defaultView");
			mainUrl = "file:///" + path
					+ "/www/ index.html#com.csair.flightstatus/" + defaultView;
		}
		super.appView.addJavascriptInterface(this, "android");
//		super.appView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//		this.appView.getSettings().setBlockNetworkImage(true);
// 		this.appView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		this.appView.setVisibility(View.VISIBLE);

		RelativeLayout r = new RelativeLayout(this);
		r.setLayoutParams(new LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		View v = LayoutInflater.from(CubeAndroid.this).inflate(
				R.layout.web_close, null);
		final ImageView back = (ImageView) v
				.findViewById(R.id.web_close_button);
		back.setImageDrawable(getResources().getDrawable(R.drawable.web_close));
		back.setOnTouchListener(new TouchListener(appView));
		ViewGroup.LayoutParams x = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		super.appView.addView(v, x);
		super.root.addView(v, x);

	}

	class TouchListener implements OnTouchListener {
		WebView webview;

		public TouchListener(WebView webview) {
			this.webview = webview;
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (touchable) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
//					 view.setAlpha(50);
					break;
				case MotionEvent.ACTION_UP:
//					 view.setAlpha(255);
					webview.stopLoading();
					
//					finish();
					ActivityManager.getScreenManager().popAllWeb();
					Application.isSettingOn = true;
				}

			}
			return true;
		}

	}

	@Override
	public void init() {
		CordovaWebView webView = new CordovaWebView(this);
		CordovaWebViewClient webViewClient;

		if (Build.VERSION.SDK_INT < 11) {
			webViewClient = new CordovaWebViewClient(this, webView) {
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					touchable = false;
					// 防止网络差情况下重复加载页面

					// if (url.endsWith("push")&& !mainUrl.equals(url)) {
					if (url.contains("cube-action=push")
							&& !mainUrl.equals(url)) {
						view.stopLoading();
						url = subUrl(url);
						Intent intent = new Intent(CubeAndroid.this,
								CubeAndroid.class);
						intent.putExtra("from", "web");
						intent.putExtra("url", url);
						startActivity(intent);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_left_out);
						return;
					}
					// else if (url.endsWith("pop")) {
					// view.stopLoading();
					// Application.getApp().popWebActivity(CubeAndroid.this);
					// CubeAndroid.this.finish();
					// }
					else {
						// 防止重复加载
						if (isPageLoadFinish == true) {
							Log.v("dd", "page stopLoading");
							view.stopLoading();
							return;
						} else {
							Log.v("dd", "page start");
							super.onPageStarted(view, url, favicon);
						}

					}
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					touchable = true;
					isPageLoadFinish = true;
					Log.v("dd", "page finish");
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					
					if (url.contains("cube-action=push")
							&& !mainUrl.equals(url)) {
						view.stopLoading();
						url = subUrl(url);
						Intent intent = new Intent(CubeAndroid.this,
								CubeAndroid.class);
						intent.putExtra("from", "web");
						intent.putExtra("url", url);
						startActivity(intent);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_left_out);
						return true;
					}
					if (url.contains("cube-action=pop")) {
//						finish();
						ActivityManager.getScreenManager().popWeb(CubeAndroid.this);
						return true;
					} else if (url.endsWith("cube://exit")) {
//						finish();
						ActivityManager.getScreenManager().popWeb(CubeAndroid.this);
						return true;
					}else{
						return super.shouldOverrideUrlLoading(view, url);
					}

				}
			};
		} else {
			webViewClient = new IceCreamCordovaWebViewClient(this, webView) {
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					touchable = false;
					//效能监控点击模块保存数据

					
					String zhengze = "www/[^/]+/";
					
					Pattern sinaPatten = Pattern.compile(zhengze,
							Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
					Matcher matcher = sinaPatten.matcher(url);
					String id = null;
					while (!matcher.hitEnd()) {
						if (matcher.find()) {
							
							id = matcher.group(0);
							id=id.substring(4, id.length()-1);
						}
					}
					if(null != id)
					{
						 CubeModule module = CubeModuleManager.getInstance().getCubeModuleByIdentifier(id);
						if(module == null) {
							module = CubeModuleManager.getInstance().getIdentifier_new_version_map().get(id);
							 if(module!=null)
								 Application.class.cast(getActivity().getApplication()).saveModulerRecord(module);
						}
					}
		//			
					
				}

				@Override
				public void onPageFinished(WebView arg0, String arg1) {
					super.onPageFinished(arg0, arg1);
					touchable = true;
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					// if (url.endsWith("push")) {
					if (url.contains("cube-action=push")) {
						url = subUrl(url);
						Intent intent = new Intent(CubeAndroid.this,
								CubeAndroid.class);
						intent.putExtra("from", "web");
						intent.putExtra("url", url);
						startActivity(intent);
						overridePendingTransition(R.anim.push_right_in,
								R.anim.push_left_out);
						return true;
					} else if (url.contains("cube-action=pop")) {
//						finish();
						ActivityManager.getScreenManager().popWeb(CubeAndroid.this);
						return true;

					} else if (url.endsWith("cube://exit")) {
//						finish();
						ActivityManager.getScreenManager().popWeb(CubeAndroid.this);
						return true;
					}
					
					return  super.shouldOverrideUrlLoading(view, url);
				}

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

	public void openfile(String fileType, String path) {
		// 获取id，跳转activity。
		Log.i("chencao", "openFile type=" + fileType + " path=" + path);

		Intent intent = null;
		if (FileIntent.FILE_PDF.equals(fileType)) {
			intent = FileIntent.getPdfFileIntent(path);
		} else if (FileIntent.FILE_WORD.equals(fileType)) {
			intent = FileIntent.getWordFileIntent(path);
		} else if (FileIntent.FILE_EXCEL.equals(fileType)) {
			intent = FileIntent.getExcelFileIntent(path);
		} else if (FileIntent.FILE_PPT.equals(fileType)) {
			intent = FileIntent.getPptFileIntent(path);
		} else if (FileIntent.FILE_CHM.equals(fileType)) {
			intent = FileIntent.getChmFileIntent(path);
		} else {
			// do nothing...
		}

		if (intent != null) {
			try {
				CubeAndroid.this.startActivity(intent);
			} catch (Exception ex) {
				Log.w("chencao", "打开文件出错，没有合适的程序。");
				Toast.makeText(CubeAndroid.this, "打开文件出错，没有合适的程序。",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void finish() {
		super.finish();
		
		overridePendingTransition(R.anim.push_left_in, R.anim.push_right_out);
	}

	/**
	 * @author Amberlo
	 * @return int DEVIDE_PHONE or DEVIDE_PAD 6.5寸以上，按照平板界面，6.5寸以下按手机界面
	 **/
	public int getDevideType() {
		if (checkSceenSize() < 6.4) {
			return DEVIDE_PHONE;
		} else
			return DEVIDE_PAD;
	}

	public double checkSceenSize() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		// int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		double diagonalPixels = Math.sqrt(Math.pow(screenWidth, 2)
				+ Math.pow(screenHeight, 2));
		double screenSize = diagonalPixels / (160 * density);
		return screenSize;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		int i = getDevideType();
		if (i == DEVIDE_PHONE) {
			PreferencesUtil.setValue(this, "DeviceType", "Android Phone");
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		} else if (i == DEVIDE_PAD) {
			PreferencesUtil.setValue(this, "DeviceType", "Android Pad");
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}
	}

	public String subUrl(String url) {
		// int start=url.indexOf("cube-action=push");
		// int end = start+16;
		// // System.out.println(url.substring(start, end));
		// String newUrl = url.substring(0, start)+url.subSequence(end,
		// url.length());
		// // System.out.println(newUrl);
		int start = url.indexOf("cube-action=push");
		int end = start + 16;
		String newUrl = "";
		if (url.indexOf("&") == -1 && start != -1) {
			newUrl = url.substring(0, (url.indexOf("cube-action=push") - 1))
					+ url.subSequence(end, url.length());
		} else if (url.indexOf("&") > -1 && start < url.indexOf("&")) {
			newUrl = url.substring(0, (url.indexOf("cube-action=push")))
					+ url.subSequence(end + 1, url.length());
		} else if (url.indexOf("&") > -1 && start > url.indexOf("&")) {
			newUrl = url.substring(0, url.indexOf("&"))
					+ url.subSequence(end, url.length());
		}
		return newUrl;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cordova.DroidGap#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("CubeAndroid_destory_Tag", "destroy");
		// appView.clearCache(true);
		// appView.clearFormData();
		// appView.clearHistory();
		// appView.clearMatches();
		// appView.clearView();
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}
	
	
	
}
