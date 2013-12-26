package com.foreveross.chameleon.phone.activity;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.csair.impc.R;
import com.foreveross.chameleon.URL;

public class DeviceRegisterActivity extends DroidGap {
	public final static int DEVIDE_PAD = 0x01;
	public final static int DEVIDE_PHONE = 0x02;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.v("dd", "CubeAndroid on cretate");
		setFinishOnTouchOutside(false);
		init();
		appView.loadUrl(URL.PAD_REGISTER_URL);
//		super.appView.addJavascriptInterface(this, "android");
//		this.appView.setVisibility(View.VISIBLE);


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
					
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					Log.v("dd", "page finish");
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return super.shouldOverrideUrlLoading(view, url);
				}
			};
		} else {
			webViewClient = new IceCreamCordovaWebViewClient(this, webView) {
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					
				}

				@Override
				public void onPageFinished(WebView arg0, String arg1) {
					super.onPageFinished(arg0, arg1);
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					
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

	

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_in);
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
		
	}

	
}
