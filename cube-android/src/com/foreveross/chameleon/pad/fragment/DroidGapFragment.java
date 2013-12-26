package com.foreveross.chameleon.pad.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.AuthenticationToken;
import org.apache.cordova.Config;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;
import org.apache.cordova.LOG;
import org.apache.cordova.NativeToJsMessageQueue;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.csair.impc.R;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.pad.component.ClosableWindow;

public abstract class DroidGapFragment extends Fragment implements
		CordovaInterface {
	public static String TAG = "CordovaActivity";

	// The webview for our app
	protected CordovaWebView appView;
	protected CordovaWebViewClient webViewClient;

	protected LinearLayout root;
	protected boolean cancelLoadUrl = false;
	protected ProgressDialog spinnerDialog = null;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	private ImageView back;

	// The initial URL for our app
	// ie http://server/path/index.html#abc?query
	// private String url = null;

	private static int ACTIVITY_STARTING = 0;
	private static int ACTIVITY_RUNNING = 1;
	private static int ACTIVITY_EXITING = 2;
	private int activityState = 0; // 0=starting, 1=running (after 1st resume),
									// 2=shutting down

	// Plugin to call when activity result is received
	protected CordovaPlugin activityResultCallback = null;
	protected boolean activityResultKeepRunning;

	// Default background color for activity
	// (this is not the color for the webview, which is set in HTML)
	// private int backgroundColor = Color.WHITE;
	private int backgroundColor = 0x00000000;

	/*
	 * The variables below are used to cache some of the activity properties.
	 */

	// Draw a splash screen using an image located in the drawable resource
	// directory.
	// This is not the same as calling super.loadSplashscreen(url)
	protected int splashscreen = 0x00000000;
	protected int splashscreenTime = 3000;

	// LoadUrl timeout value in msec (default of 20 sec)
	protected int loadUrlTimeoutValue = 20000;

	// Keep app running when pause is received. (default = true)
	// If true, then the JavaScript and native code continue to run in the
	// background
	// when another application (activity) is started.
	protected boolean keepRunning = true;

	private int lastRequestCode;

	private Object responseCode;

	private Intent lastIntent;

	private Object lastResponseCode;

	private String initCallbackClass;

	private Object LOG_TAG;

	protected ClosableWindow closableWindow;

	/**
	 * Sets the authentication token.
	 * 
	 * @param authenticationToken
	 * @param host
	 * @param realm
	 */
	public void setAuthenticationToken(AuthenticationToken authenticationToken,
			String host, String realm) {
		if (this.appView != null && getWebViewClient(this.appView) != null) {
			getWebViewClient(this.appView).setAuthenticationToken(
					authenticationToken, host, realm);
		}

	}

	/**
	 * Removes the authentication token.
	 * 
	 * @param host
	 * @param realm
	 * 
	 * @return the authentication token or null if did not exist
	 */
	public AuthenticationToken removeAuthenticationToken(String host,
			String realm) {
		if (this.appView != null && getWebViewClient(this.appView) != null) {
			return getWebViewClient(this.appView).removeAuthenticationToken(
					host, realm);
		}
		return null;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		if (viewCreateCallBack != null) {
			viewCreateCallBack.viewCreated(appView);
		}

	}

	private ViewCreateCallBack viewCreateCallBack;

	public void setViewCreateCallBack(ViewCreateCallBack viewCreateCallBack) {
		this.viewCreateCallBack = viewCreateCallBack;
	}

	/**
	 * Gets the authentication token.
	 * 
	 * In order it tries: 1- host + realm 2- host 3- realm 4- no host, no realm
	 * 
	 * @param host
	 * @param realm
	 * 
	 * @return the authentication token
	 */
	public AuthenticationToken getAuthenticationToken(String host, String realm) {
		if (this.appView != null && getWebViewClient(this.appView) != null) {
			return getWebViewClient(this.appView).getAuthenticationToken(host,
					realm);
		}
		return null;
	}

	/**
	 * Clear all authentication tokens.
	 */
	public void clearAuthenticationTokens() {
		if (this.appView != null && getWebViewClient(this.appView) != null) {
			getWebViewClient(this.appView).clearAuthenticationTokens();
		}
	}

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Config.init(getAssocActivity());
		LOG.d(TAG, "CordovaActivity.onCreate()");
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			initCallbackClass = savedInstanceState.getString("callbackClass");
		}

		// if (!this.getBooleanProperty("showTitle", false)) {
		//
		// getAssocActivity().getWindow().requestFeature(
		// Window.FEATURE_NO_TITLE);
		// }

		// if (this.getBooleanProperty("setFullscreen", false)) {
		// getAssocActivity().getWindow().setFlags(
		// WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// } else {
		// getAssocActivity().getWindow().setFlags(
		// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// }
		// This builds the view. We could probably get away with NOT having a
		// LinearLayout, but I like having a bucket!
		Display display = getAssocActivity().getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		root = new LinearLayout(getContext()) {
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				View childView = appView.getFocusedChild();
				// Determine if the focus is on the current view or not
				if (childView != null
						&& (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)) {
					return appView.onKeyDown(keyCode, event);
				} else
					return super.onKeyDown(keyCode, event);
			}

			@Override
			public boolean onKeyUp(int keyCode, KeyEvent event) {

				// Get whatever has focus!
				View childView = appView.getFocusedChild();
				if ((appView.isCustomViewShowing() || childView != null)
						&& (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)) {
					return appView.onKeyUp(keyCode, event);
				} else {
					return super.onKeyUp(keyCode, event);
				}
			}

		};
		root.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		// new LinearLayoutSoftKeyboardDetect(getAssocActivity(), width, height)
		// {
		//
		//
		// };
		root.setOrientation(LinearLayout.VERTICAL);
		root.setBackgroundColor(this.backgroundColor);

		root.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));

		// Setup the hardware volume controls to handle volume control
		getAssocActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	/**
	 * Get the Android activity.
	 * 
	 * @return
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup vg = (ViewGroup) root.getParent();
		if (vg != null) {
			vg.removeView(root);
		}
		return root;
	}

	@Override
	public abstract FragmentActivity getActivity();

	/**
	 * Create and initialize web container with default web view objects.
	 */
	@SuppressLint("NewApi")
	public void init() {
		final FrameLayout detailContainer = (FrameLayout) this.getActivity()
				.findViewById(R.id.child_frame);
		CordovaWebView webView = new CordovaWebView(getAssocActivity());
		// webView.setBackgroundColor(Color.parseColor("#000000"));
		// webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		// webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		// webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		CordovaWebViewClient webViewClient;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			webViewClient = new CordovaWebViewClient(this, webView) {
				/**
				 * [一句话功能简述]<BR>
				 * [功能详细描述]
				 * 
				 * @param arg0
				 * @param arg1
				 *            2013-8-24 下午12:09:52
				 */
				@Override
				public void onPageFinished(WebView arg0, String url) {
					super.onPageFinished(arg0, url);
					for (PageFinishListener finishListener : pflList) {
						finishListener.onPageFinished(arg0, url);
					}
				}

				/**
				 * [一句话功能简述]<BR>
				 * [功能详细描述]
				 * 
				 * @param view
				 * @param url
				 * @param favicon
				 *            2013-8-24 下午12:10:05
				 */
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					for (PageStartListener startListener : pslList) {
						startListener.onPageStarted(view, url, favicon);
					}
				}
			};
		} else {
			webViewClient = new IceCreamCordovaWebViewClient(this, webView) {
				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.apache.cordova.CordovaWebViewClient#shouldOverrideUrlLoading
				 * (android.webkit.WebView, java.lang.String)
				 */
				@SuppressLint("NewApi")
				@Override
				public boolean shouldOverrideUrlLoading(WebView webview,
						String url) {
					if (url.contains("cube://exit") || url.contains("cube-action=pop")) {

						if (getAssocActivity() instanceof FacadeActivity) {
							((FacadeActivity) getAssocActivity()).popRight();
							//将右边设置为设置界面
							
						}
						detailContainer.setVisibility(View.GONE);
						// detailContainer.setX(0);
						detailContainer
								.setBackgroundResource(R.drawable.shadow);
						detailContainer.setPadding(21, 0, 0, 0);
						return true;
					}
					
					return super.shouldOverrideUrlLoading(webview, url);
				}

				/**
				 * [一句话功能简述]<BR>
				 * [功能详细描述]
				 * 
				 * @param arg0
				 * @param arg1
				 *            2013-8-24 下午12:10:29
				 */
				@Override
				public void onPageFinished(WebView arg0, String url) {
					super.onPageFinished(arg0, url);

					for (PageFinishListener finishListener : pflList) {
						finishListener.onPageFinished(arg0, url);
					}
				}

				/**
				 * [一句话功能简述]<BR>
				 * [功能详细描述]
				 * 
				 * @param view
				 * @param url
				 * @param favicon
				 *            2013-8-24 下午12:10:34
				 */
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					for (PageStartListener pageStartListener : pslList) {
						pageStartListener.onPageStarted(view, url, favicon);
					}
				}
			};
		}
		this.init(webView, webViewClient,
				new CordovaChromeClient(this, webView) {
					@Override
					public void onReceivedTitle(WebView view, String title) {
						// TODO Auto-generated method stub
						super.onReceivedTitle(view, title);
						// closableWindow.setTitle(appView.getTitle());

					}
				});
	}

	@SuppressLint("ResourceAsColor")
	private void addzoomView(final CordovaWebView view) {
		RelativeLayout r = new RelativeLayout(getActivity());
		r.setLayoutParams(new LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT));
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.web_close,
				null);
		back = (ImageView) v.findViewById(R.id.web_close_button);
		back.setBackgroundColor(R.color.grey);
		back.setImageDrawable(getResources().getDrawable(R.drawable.blowup));
		back.setAlpha(255);
		ViewGroup.LayoutParams x = new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.addView(v, x);
		final FrameLayout detailContainer = (FrameLayout) this.getActivity()
				.findViewById(R.id.child_frame);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				arg0.setEnabled(false);
				scale(detailContainer, arg0);
			}
		});
	}

	public void animscale(final FrameLayout v, final float tx, float ax,
			final View arg0) {

		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation alphaAnimation = new ScaleAnimation(1.0f, ax, 1.0f, 1.0f);
		TranslateAnimation translateAnimation = new TranslateAnimation(0f, tx,
				0f, 0f);// 动画效果
		alphaAnimation.setDuration(1000);
		translateAnimation.setDuration(1000);
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

				if (tx == -256) {
					v.setPadding(0, 0, 0, 0);
					max();// 放大窗口
				} else {
					v.setPadding(21, 0, 0, 0);
					min();// 缩小窗口
				}
				arg0.setEnabled(true);
			}
		});
		v.startAnimation(animationSet);// 调用开始动画
	}

	public void scale(FrameLayout v, View arg0) {
		if (isMax) {
			animscale(v, 980, 0.51f, arg0);
		} else {
			animscale(v, -256, 2.1f, arg0);
		}
		isMax = !isMax;
	}

	// 是否放大
	private boolean isMax = false;

	public void min() {
		final FrameLayout detailContainer = (FrameLayout) this.getActivity()
				.findViewById(R.id.child_frame);
		detailContainer.setBackgroundResource(R.drawable.shadow);
		final View contentView = this.getActivity().findViewById(
				R.id.content_window_id);
		final int contentViewWidth = contentView.getWidth();
		back.setImageDrawable(getResources().getDrawable(R.drawable.blowup));
		contentView.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					public boolean onPreDraw() {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								contentViewWidth / 2 + 24,
								LayoutParams.MATCH_PARENT);
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
								RelativeLayout.TRUE);
						detailContainer.setLayoutParams(layoutParams);
						return true;
					}
				});
	}

	public void max() {
		final FrameLayout detailContainer = (FrameLayout) this.getActivity()
				.findViewById(R.id.child_frame);
		detailContainer.setBackgroundColor(0x00000000);
		final View contentView = this.getActivity().findViewById(
				R.id.content_window_id);
		final int contentViewWidth = contentView.getWidth();
		back.setImageDrawable(getResources()
				.getDrawable(R.drawable.contraction));
		contentView.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					public boolean onPreDraw() {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								contentViewWidth, LayoutParams.MATCH_PARENT);
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
								RelativeLayout.TRUE);
						detailContainer.setLayoutParams(layoutParams);
						detailContainer.setAnimationCacheEnabled(true);
						return true;
					}
				});
	}

	/**
	 * Initialize web container with web view objects.
	 * 
	 * @param webView
	 * @param webViewClient
	 * @param webChromeClient
	 */
	@SuppressLint("NewApi")
	public void init(CordovaWebView webView,
			CordovaWebViewClient webViewClient,
			CordovaChromeClient webChromeClient) {
		LOG.d(TAG, "CordovaActivity.init()");

		// Set up web container
		this.appView = webView;
		this.appView.setId(100);

		this.appView.setWebViewClient(webViewClient);
		this.appView.setWebChromeClient(webChromeClient);
		webViewClient.setWebView(this.appView);
		webChromeClient.setWebView(this.appView);

		this.appView.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));

		if (this.getBooleanProperty("disallowOverscroll", false)) {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
				this.appView
						.setOverScrollMode(CordovaWebView.OVER_SCROLL_NEVER);
			}
		}

		// Add web view but make it invisible while loading URL
		this.appView.setVisibility(View.INVISIBLE);

		if (Build.VERSION.SDK_INT > 11) {
			if (getOpenLayer()) {
				appView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
				setOpenLayer(false);
			} else {
				// appView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
				appView.setLayerType(WebView.LAYER_TYPE_NONE, null);
			}

		}

		if (getIsVisible() == View.VISIBLE) {
//			addzoomView(appView);
		}

		this.root.addView(appView);

		//
		// closableWindow = new
		// ClosableWindow(getAssocActivity(),getAssocActivity().findViewById(R.id.child_frame));
		// closableWindow.addContentView(this.appView);
		// closableWindow.setTitleAreaVisible(this.getIsVisible());
		//
		// RelativeLayout.LayoutParams rl = new
		// RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		// closableWindow.setLayoutParams(rl);
		// this.root.addView(appView);
		//

		// Clear cancel flag
		this.cancelLoadUrl = false;

	}

	public Boolean openLayer = false;
	public int padding;
	public int width;
	private int isVisible;
	public int hight;

	public Boolean getOpenLayer() {
		return openLayer;
	}

	public void setOpenLayer(Boolean openLayer) {
		this.openLayer = openLayer;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHight() {
		return hight;
	}

	public void setHight(int hight) {
		this.hight = hight;
	}

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

	private String nativeUrl = null;

	/**
	 * Load the url into the webview.
	 * 
	 * @param url
	 */
	@SuppressLint("NewApi")
	public void loadUrl(String url) {
		this.nativeUrl = url;
		// Init web view if not already done
		if (this.appView == null) {
			this.init();
		}

		// Set backgroundColor
		this.backgroundColor = this.getIntegerProperty("backgroundColor",
				0x000000);
		this.root.setBackgroundColor(this.backgroundColor);
		// If keepRunning
		this.keepRunning = this.getBooleanProperty("keepRunning", true);

		// Then load the spinner
		// this.loadSpinner();

		this.appView.setVisibility(View.VISIBLE);
		this.appView.loadUrl(url);
	}

	/*
	 * Load the spinner
	 */
	void loadSpinner() {

		// If loadingDialog property, then show the App loading dialog for first
		// page of app
		String loading = null;
		if ((this.appView == null) || !this.appView.canGoBack()) {
			loading = this.getStringProperty("loadingDialog", null);
		} else {
			loading = this.getStringProperty("loadingPageDialog", null);
		}
		if (loading != null) {

			String title = "";
			String message = "Loading Application...";

			if (loading.length() > 0) {
				int comma = loading.indexOf(',');
				if (comma > 0) {
					title = loading.substring(0, comma);
					message = loading.substring(comma + 1);
				} else {
					title = "";
					message = loading;
				}
			}
			this.spinnerStart(title, message);
		}
	}

	/**
	 * Load the url into the webview after waiting for period of time. This is
	 * used to display the splashscreen for certain amount of time.
	 * 
	 * @param url
	 * @param time
	 *            The number of ms to wait before loading webview
	 */
	@SuppressLint("NewApi")
	public void loadUrl(final String url, int time) {
		final View contentView = this.getActivity().findViewById(
				R.id.content_window_id);
		contentView.setBackgroundColor(0x00000000);
		this.nativeUrl = url;
		// Init web view if not already done
		if (this.appView == null) {
			this.init();
		}

		this.splashscreenTime = time;
		this.splashscreen = this.getIntegerProperty("splashscreen", 0);
		this.showSplashScreen(this.splashscreenTime);
		this.appView.loadUrl(url, time);

	}

	/**
	 * Cancel loadUrl before it has been loaded.
	 */
	@Deprecated
	public void cancelLoadUrl() {
		this.cancelLoadUrl = true;
	}

	/**
	 * Clear the resource cache.
	 */
	public void clearCache() {
		if (this.appView == null) {
			this.init();
		}
		this.appView.clearCache(true);
	}

	/**
	 * Clear web history in this web view.
	 */
	public void clearHistory() {
		this.appView.clearHistory();
	}

	/**
	 * Go to previous page in history. (We manage our own history)
	 * 
	 * @return true if we went back, false if we are already at top
	 */
	public boolean backHistory() {
		if (this.appView != null) {
			return appView.backHistory();
		}
		return false;
	}

	@Override
	/**
	 * Called by the system when the device configuration changes while your activity is running.
	 *
	 * @param Configuration newConfig
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		// don't reload the current page when the orientation is changed
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * Get boolean property for activity.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean getBooleanProperty(String name, boolean defaultValue) {
		Bundle bundle = getAssocActivity().getIntent().getExtras();
		if (bundle == null) {
			return defaultValue;
		}
		Boolean p;
		try {
			p = (Boolean) bundle.get(name);
		} catch (ClassCastException e) {
			String s = bundle.get(name).toString();
			if ("true".equals(s)) {
				p = true;
			} else {
				p = false;
			}
		}
		if (p == null) {
			return defaultValue;
		}
		return p.booleanValue();
	}

	/**
	 * Get int property for activity.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getIntegerProperty(String name, int defaultValue) {
		Bundle bundle = getAssocActivity().getIntent().getExtras();
		if (bundle == null) {
			return defaultValue;
		}
		Integer p;
		try {
			p = (Integer) bundle.get(name);
		} catch (ClassCastException e) {
			p = Integer.parseInt(bundle.get(name).toString());
		}
		if (p == null) {
			return defaultValue;
		}
		return p.intValue();
	}

	/**
	 * Get string property for activity.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String getStringProperty(String name, String defaultValue) {
		Bundle bundle = getAssocActivity().getIntent().getExtras();
		if (bundle == null) {
			return defaultValue;
		}
		String p = bundle.getString(name);
		if (p == null) {
			return defaultValue;
		}
		return p;
	}

	/**
	 * Get double property for activity.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public double getDoubleProperty(String name, double defaultValue) {
		Bundle bundle = getAssocActivity().getIntent().getExtras();
		if (bundle == null) {
			return defaultValue;
		}
		Double p;
		try {
			p = (Double) bundle.get(name);
		} catch (ClassCastException e) {
			p = Double.parseDouble(bundle.get(name).toString());
		}
		if (p == null) {
			return defaultValue;
		}
		return p.doubleValue();
	}

	/**
	 * Set boolean property on activity.
	 * 
	 * @param name
	 * @param value
	 */
	public void setBooleanProperty(String name, boolean value) {
		Log.d(TAG,
				"Setting boolean properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml");
		getAssocActivity().getIntent().putExtra(name, value);
	}

	/**
	 * Set int property on activity.
	 * 
	 * @param name
	 * @param value
	 */
	public void setIntegerProperty(String name, int value) {
		Log.d(TAG,
				"Setting integer properties in CordovaActivity will be deprecated in 3.1 on August 2013, please use config.xml");
		getAssocActivity().getIntent().putExtra(name, value);
	}

	/**
	 * Set string property on activity.
	 * 
	 * @param name
	 * @param value
	 */
	public void setStringProperty(String name, String value) {
		Log.d(TAG,
				"Setting string properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml");
		getAssocActivity().getIntent().putExtra(name, value);
	}

	/**
	 * Set double property on activity.
	 * 
	 * @param name
	 * @param value
	 */
	public void setDoubleProperty(String name, double value) {
		Log.d(TAG,
				"Setting double properties in CordovaActivity will be deprecated in 3.0 on July 2013, please use config.xml");
		getAssocActivity().getIntent().putExtra(name, value);
	}

	@Override
	/**
	 * Called when the system is about to start resuming a previous activity.
	 */
	public void onPause() {
		super.onPause();

		LOG.d(TAG, "Paused the application!");

		// Don't process pause if shutting down, since onDestroy() will be
		// called
		if (this.activityState == ACTIVITY_EXITING) {
			return;
		}

		if (this.appView == null) {
			return;
		} else {
			this.appView.handlePause(this.keepRunning);
		}

		// hide the splash screen to avoid leaking a window
		this.removeSplashScreen();
	}

	/**
	 * Called when the activity receives a new intent
	 **/
	protected void onNewIntent(Intent intent) {
		// Forward to plugins
		if (this.appView != null)
			this.appView.onNewIntent(intent);
	}

	@Override
	/**
	 * Called when the activity will start interacting with the user.
	 */
	public void onResume() {
		super.onResume();
		// Reload the configuration
		Config.init(getAssocActivity());

		LOG.d(TAG, "Resuming the App");

		// Code to test CB-3064
		String errorUrl = this.getStringProperty("errorUrl", null);
		LOG.d(TAG, "CB-3064: The errorUrl is " + errorUrl);

		if (this.activityState == ACTIVITY_STARTING) {
			this.activityState = ACTIVITY_RUNNING;
			return;
		}

		if (this.appView == null) {
			return;
		}

		this.appView.handleResume(this.keepRunning,
				this.activityResultKeepRunning);

		// If app doesn't want to run in background
		if (!this.keepRunning || this.activityResultKeepRunning) {

			// Restore multitasking state
			if (this.activityResultKeepRunning) {
				this.keepRunning = this.activityResultKeepRunning;
				this.activityResultKeepRunning = false;
			}
		}
	}

	@Override
	/**
	 * The final call you receive before your activity is destroyed.
	 */
	public void onDestroy() {
		LOG.d(TAG, "CordovaActivity.onDestroy()");
		super.onDestroy();

		// hide the splash screen to avoid leaking a window
		this.removeSplashScreen();

		if (this.appView != null) {
			appView.handleDestroy();
		} else {
			this.activityState = ACTIVITY_EXITING;
		}
	}

	/**
	 * Send a message to all plugins.
	 * 
	 * @param id
	 *            The message id
	 * @param data
	 *            The message data
	 */
	public void postMessage(String id, Object data) {
		if (this.appView != null) {
			this.appView.postMessage(id, data);
		}
	}

	/**
	 * @deprecated Add services to res/xml/plugins.xml instead.
	 * 
	 *             Add a class that implements a service.
	 * 
	 * @param serviceType
	 * @param className
	 */
	public void addService(String serviceType, String className) {
		if (this.appView != null && this.appView.pluginManager != null) {
			this.appView.pluginManager.addService(serviceType, className);
		}
	}

	/**
	 * Send JavaScript statement back to JavaScript. (This is a convenience
	 * method)
	 * 
	 * @param message
	 */
	public void sendJavascript(String statement) {
		if (this.appView != null) {
			getNativeToJsMessageQueue(this.appView).addJavaScript(statement);
		}
	}

	public CordovaWebViewClient getWebViewClient(CordovaWebView appView) {
		try {
			Field jsField = CordovaWebView.class.getDeclaredField("viewClient");
			jsField.setAccessible(true);
			return (CordovaWebViewClient) jsField.get(appView);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		//
		return null;
	}

	public NativeToJsMessageQueue getNativeToJsMessageQueue(
			CordovaWebView appView) {
		try {
			Field jsField = CordovaWebView.class
					.getDeclaredField("jsMessageQueue");
			jsField.setAccessible(true);
			return (NativeToJsMessageQueue) jsField.get(appView);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		//
		return null;
	}

	/**
	 * Show the spinner. Must be called from the UI thread.
	 * 
	 * @param title
	 *            Title of the dialog
	 * @param message
	 *            The message of the dialog
	 */
	public void spinnerStart(final String title, final String message) {
		if (this.spinnerDialog != null) {
			this.spinnerDialog.dismiss();
			this.spinnerDialog = null;
		}
		this.spinnerDialog = ProgressDialog.show(getAssocActivity(), title,
				message, true, true, new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						spinnerDialog = null;
					}
				});
	}

	/**
	 * Stop spinner - Must be called from UI thread
	 */
	public void spinnerStop() {
		if (this.spinnerDialog != null && this.spinnerDialog.isShowing()) {
			this.spinnerDialog.dismiss();
			this.spinnerDialog = null;
		}
	}

	/**
	 * End this activity by calling finish for activity
	 */
	public void endActivity() {
		this.activityState = ACTIVITY_EXITING;
		// super.finish();
	}

	/**
	 * Launch an activity for which you would like a result when it finished.
	 * When this activity exits, your onActivityResult() method will be called.
	 * 
	 * @param command
	 *            The command object
	 * @param intent
	 *            The intent to start
	 * @param requestCode
	 *            The request code that is passed to callback to identify the
	 *            activity
	 */
	public void startActivityForResult(CordovaPlugin command, Intent intent,
			int requestCode) {
		this.activityResultCallback = command;
		this.activityResultKeepRunning = this.keepRunning;

		// If multitasking turned on, then disable it for activities that return
		// results
		if (command != null) {
			this.keepRunning = false;
		}

		// Start activity
		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * Called when an activity you launched exits, giving you the requestCode
	 * you started it with, the resultCode it returned, and any additional data
	 * from it.
	 * 
	 * @param requestCode
	 *            The request code originally supplied to
	 *            startActivityForResult(), allowing you to identify who this
	 *            result came from.
	 * @param resultCode
	 *            The integer result code returned by the child activity through
	 *            its setResult().
	 * @param data
	 *            An Intent, which can return result data to the caller (various
	 *            data can be attached to Intent "extras").
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		LOG.d(TAG, "Incoming Result");
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d(TAG, "Request code = " + requestCode);
		ValueCallback<Uri> mUploadMessage = this.appView.getWebChromeClient()
				.getValueCallback();
		if (requestCode == CordovaChromeClient.FILECHOOSER_RESULTCODE) {
			Log.d(TAG, "did we get here?");
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
					: intent.getData();
			Log.d(TAG, "result = " + result);
			// Uri filepath = Uri.parse("file://" +
			// FileUtils.getRealPathFromURI(result, this));
			// Log.d(TAG, "result = " + filepath);
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
		CordovaPlugin callback = this.activityResultCallback;
		if (callback == null && initCallbackClass != null) {
			// The application was restarted, but had defined an initial
			// callback
			// before being shut down.
			this.activityResultCallback = appView.pluginManager
					.getPlugin(initCallbackClass);
			callback = this.activityResultCallback;
		}
		if (callback != null) {
			LOG.d(TAG, "We have a callback to send this result to");
			callback.onActivityResult(requestCode, resultCode, intent);
		}
	}

	public void setActivityResultCallback(CordovaPlugin plugin) {
		this.activityResultCallback = plugin;
	}

	/**
	 * Report an error to the host application. These errors are unrecoverable
	 * (i.e. the main resource is unavailable). The errorCode parameter
	 * corresponds to one of the ERROR_* constants.
	 * 
	 * @param errorCode
	 *            The error code corresponding to an ERROR_* value.
	 * @param description
	 *            A String describing the error.
	 * @param failingUrl
	 *            The url that failed to load.
	 */
	public void onReceivedError(final int errorCode, final String description,
			final String failingUrl) {
		final Activity me = getAssocActivity();

		// If errorUrl specified, then load it
		final String errorUrl = getStringProperty("errorUrl", null);
		if ((errorUrl != null)
				&& (errorUrl.startsWith("file://") || Config
						.isUrlWhiteListed(errorUrl))
				&& (!failingUrl.equals(errorUrl))) {

			// Load URL on UI thread
			me.runOnUiThread(new Runnable() {
				public void run() {
					// Stop "app loading" spinner if showing
					spinnerStop();
					appView.showWebPage(errorUrl, false, true, null);
				}
			});
		}
		// If not, then display error dialog
		else {
			final boolean exit = !(errorCode == WebViewClient.ERROR_HOST_LOOKUP);
			me.runOnUiThread(new Runnable() {
				public void run() {
					if (exit) {
						appView.setVisibility(View.GONE);
						displayError("Application Error", description + " ("
								+ failingUrl + ")", "OK", exit);
					}
				}
			});
		}
	}

	/**
	 * Display an error dialog and optionally exit application.
	 * 
	 * @param title
	 * @param message
	 * @param button
	 * @param exit
	 */
	public void displayError(final String title, final String message,
			final String button, final boolean exit) {
		getAssocActivity().runOnUiThread(new Runnable() {
			public void run() {
				try {
					AlertDialog.Builder dlg = new AlertDialog.Builder(
							getAssocActivity());
					dlg.setMessage(message);
					dlg.setTitle(title);
					dlg.setCancelable(false);
					dlg.setPositiveButton(button,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (exit) {
										endActivity();
									}
								}
							});
					dlg.create();
					dlg.show();
				} catch (Exception e) {
					// finish();
				}
			}
		});
	}

	/**
	 * Determine if URL is in approved list of URLs to load.
	 * 
	 * @param url
	 * @return
	 */
	public boolean isUrlWhiteListed(String url) {
		return Config.isUrlWhiteListed(url);
	}

	/*
	 * Hook in Cordova for menu plugins
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.postMessage("onOptionsItemSelected", item);
		return true;
	}

	/**
	 * Get Activity context.
	 * 
	 * @return
	 */
	public Context getContext() {
		LOG.d(TAG, "This will be deprecated December 2012");
		return getAssocActivity();
	}

	/**
	 * Load the specified URL in the Cordova webview or a new browser instance.
	 * 
	 * NOTE: If openExternal is false, only URLs listed in whitelist can be
	 * loaded.
	 * 
	 * @param url
	 *            The url to load.
	 * @param openExternal
	 *            Load url in browser instead of Cordova webview.
	 * @param clearHistory
	 *            Clear the history stack, so new page becomes top of history
	 * @param params
	 *            Parameters for new app
	 */
	public void showWebPage(String url, boolean openExternal,
			boolean clearHistory, HashMap<String, Object> params) {
		if (this.appView != null) {
			appView.showWebPage(url, openExternal, clearHistory, params);
		}
	}

	protected Dialog splashDialog;

	/**
	 * Removes the Dialog that displays the splash screen
	 */
	public void removeSplashScreen() {
		if (splashDialog != null && splashDialog.isShowing()) {
			splashDialog.dismiss();
			splashDialog = null;
		}
	}

	/**
	 * Shows the splash screen over the full Activity
	 */
	@SuppressWarnings("deprecation")
	protected void showSplashScreen(final int time) {

		Runnable runnable = new Runnable() {
			public void run() {
				// Get reference to display
				Display display = getAssocActivity().getWindowManager()
						.getDefaultDisplay();

				// Create the layout for the dialog
				LinearLayout root = new LinearLayout(getAssocActivity());
				root.setMinimumHeight(display.getHeight());
				root.setMinimumWidth(display.getWidth());
				root.setOrientation(LinearLayout.VERTICAL);
				root.setBackgroundColor(getIntegerProperty("backgroundColor",
						0x000000));
				root.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT, 0.0F));
				root.setBackgroundResource(splashscreen);

				// Create and show the dialog
				splashDialog = new Dialog(getAssocActivity(),
						android.R.style.Theme_Translucent_NoTitleBar);
				// check to see if the splash screen should be full screen
				// if ((getAssocActivity().getWindow().getAttributes().flags &
				// WindowManager.LayoutParams.FLAG_FULLSCREEN) ==
				// WindowManager.LayoutParams.FLAG_FULLSCREEN) {
				// splashDialog.getWindow().setFlags(
				// WindowManager.LayoutParams.FLAG_FULLSCREEN,
				// WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// }
				splashDialog.setContentView(root);
				splashDialog.setCancelable(false);
				splashDialog.show();

				// Set Runnable to remove splash screen just in case
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						removeSplashScreen();
					}
				}, time);
			}
		};
		getAssocActivity().runOnUiThread(runnable);
	}

	/**
	 * Called when a message is sent to plugin.
	 * 
	 * @param id
	 *            The message id
	 * @param data
	 *            The message data
	 * @return Object or null
	 */
	public Object onMessage(String id, Object data) {
		LOG.d(TAG, "onMessage(" + id + "," + data + ")");
		if ("splashscreen".equals(id)) {
			if ("hide".equals(data.toString())) {
				this.removeSplashScreen();
			} else {
				// If the splash dialog is showing don't try to show it again
				if (this.splashDialog == null || !this.splashDialog.isShowing()) {
					this.splashscreen = this.getIntegerProperty("splashscreen",
							0);
					this.showSplashScreen(this.splashscreenTime);
				}
			}
		} else if ("spinner".equals(id)) {
			if ("stop".equals(data.toString())) {
				this.spinnerStop();
				this.appView.setVisibility(View.VISIBLE);
			}
		} else if ("onReceivedError".equals(id)) {
			JSONObject d = (JSONObject) data;
			try {
				this.onReceivedError(d.getInt("errorCode"),
						d.getString("description"), d.getString("url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if ("exit".equals(id)) {
			this.endActivity();
		}
		return null;
	}

	public ExecutorService getThreadPool() {
		return threadPool;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.activityResultCallback != null) {
			String cClass = this.activityResultCallback.getClass().getName();
			outState.putString("callbackClass", cClass);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		this.postMessage("onCreateOptionsMenu", menu);
		this.postMessage("onPrepareOptionsMenu", menu);

	}

	public String getNatvieUrl() {
		return nativeUrl;
	}

	private List<PageStartListener> pslList = new ArrayList<PageStartListener>();

	public void addPageStartListener(PageStartListener pageStartListener) {
		pslList.add(pageStartListener);
	}

	public void removePageStartListener(PageStartListener pageStartListener) {
		pslList.remove(pageStartListener);
	}

	private List<PageFinishListener> pflList = new ArrayList<PageFinishListener>();

	public void addPageFinishListener(PageFinishListener pageFinishListener) {
		pflList.add(pageFinishListener);
	}

	public void removePageFinishListener(PageFinishListener pageFinishListener) {
		pflList.remove(pageFinishListener);
	}

	public void refreshDrawable() {
		appView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// appView.refreshDrawableState();
				// appView.invalidate();
				appView.requestFocus();
			}
		}, 100);
	}

	@SuppressLint("NewApi")
	public void enableAccelerate() {
		appView.setBackgroundColor(Color.TRANSPARENT);
		appView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
	}

	@SuppressLint("NewApi")
	public void disableAccelerate() {
		appView.setBackgroundColor(Color.parseColor("#00FFFFFF"));
		appView.setLayerType(View.LAYER_TYPE_NONE, null);

	}

}
