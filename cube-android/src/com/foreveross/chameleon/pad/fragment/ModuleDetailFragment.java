package com.foreveross.chameleon.pad.fragment;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.CubeConstants;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.ModuleChangedEvent;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.phone.view.ItemButton;
import com.foreveross.chameleon.phone.view.SlidePageView;
import com.foreveross.chameleon.phone.view.SlidePageView.OnPageChangedListener;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.imageTool.CubeAsyncImage;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class ModuleDetailFragment extends Fragment {
	// titlebar
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private ItemButton app_dealbtn;
	private ImageView icon;
	private ImageView appupData;
	private TextView name;
	private TextView version;
	private TextView releaseNote;
	private CubeModule cubeModule;
	private ProgressBar bar;
	public ProgressDialog progressDialog;
	// 图片滑动
	private SlidePageView pageView;
	private LinearLayout pointlayout;
	private List<SoftReference<Bitmap>> list = new ArrayList<SoftReference<Bitmap>>();

	private String iden;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getTheCubeModule();
		iden =  cubeModule.getIdentifier()+"_"+cubeModule.getVersion()+"_"+cubeModule.getBuild();
		EventBus.getEventBus(TmpConstants.EVENTBUS_MODULE_CHANGED,
				ThreadEnforcer.MAIN).register(this);
		return inflater.inflate(R.layout.app_detail, null);
	}

	@Subscribe
	public void onModuleChnagedEvent(ModuleChangedEvent changedEvent) {
		CubeModule m = CubeModuleManager.getInstance().getCubeModuleByIdentifier(changedEvent
				.getIdentifier());
		if (m == null) {
			return;
		}
		if (cubeModule.getName().equals(m.getName())) {
			name.setText(cubeModule.getName());
			if ((CubeModule.INSTALLING == cubeModule.getModuleType()
					|| CubeModule.UPGRADING == cubeModule.getModuleType() || CubeModule.DELETING == cubeModule
					.getModuleType()) && cubeModule.getProgress() == -1) {
				bar.setVisibility(View.VISIBLE);
				bar.setIndeterminate(true);
				app_dealbtn.initModel(getAssocActivity(), cubeModule);
			} else if (CubeModule.INSTALLING == cubeModule.getModuleType()
					|| CubeModule.UPGRADING == cubeModule.getModuleType()
					|| CubeModule.DELETING == cubeModule.getModuleType()) {
				bar.setVisibility(View.VISIBLE);
				bar.setIndeterminate(false);
				icon.setAlpha(90);
				bar.setProgress(cubeModule.getProgress());
				app_dealbtn.initModel(getAssocActivity(), cubeModule);
			} else {
				bar.setVisibility(View.GONE);
				icon.setAlpha(255);
				app_dealbtn.initModel(getAssocActivity(), cubeModule);
			}
		}
	}

	// TODO Auto-generated method stub
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		progressDialog = new ProgressDialog(getAssocActivity());
		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnTouchListener(onTouchListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("模块详情");
		icon = (ImageView) view.findViewById(R.id.icon);
		bar = (ProgressBar) view.findViewById(R.id.progressBar_download);
		app_dealbtn = (ItemButton) view.findViewById(R.id.app_dealbtn);
		if ((CubeModule.INSTALLING == cubeModule.getModuleType() || CubeModule.UPGRADING == cubeModule
				.getModuleType()) && cubeModule.getProgress() == -1) {
			bar.setVisibility(View.VISIBLE);
			bar.setIndeterminate(true);
			app_dealbtn.initModel(getAssocActivity(), cubeModule);
		} else if (CubeModule.INSTALLING == cubeModule.getModuleType()
				|| CubeModule.UPGRADING == cubeModule.getModuleType()) {
			Log.d("Status", cubeModule.getName() + "module.progress-->"
					+ cubeModule.getProgress());
			bar.setVisibility(View.VISIBLE);
			bar.setIndeterminate(false);
			icon.setAlpha(90);
			bar.setProgress(cubeModule.getProgress());
			app_dealbtn.initModel(getAssocActivity(), cubeModule);
		} else {
			bar.setVisibility(View.GONE);
			icon.setAlpha(255);
			app_dealbtn.initModel(getAssocActivity(), cubeModule);
		}
		appupData = (ImageView) view.findViewById(R.id.imageView_updata);
		if (CubeModule.UPGRADABLE == cubeModule.getModuleType()
				|| CubeModule.UPGRADING == cubeModule.getModuleType()) {
			appupData.setVisibility(View.VISIBLE);
		} else {
			appupData.setVisibility(View.GONE);
		}

		// app_dealbtn.setOnTouchListener(onTouchListener);

		app_dealbtn.initModel(getAssocActivity(), cubeModule);
		name = (TextView) view.findViewById(R.id.name);
		version = (TextView) view.findViewById(R.id.version);
		releaseNote = (TextView) view.findViewById(R.id.releaseNote);
		if(cubeModule.getLocal()!=null) {
			
			PropertiesUtil propertiesUtil = PropertiesUtil
					.readProperties(getAssocActivity(), CubeConstants.CUBE_CONFIG);
			// 判断本地模块是否存在
			String icons = propertiesUtil.getString(
					"icon_"+cubeModule.getIdentifier(), "");
			cubeModule.setInstallIcon(icons);
		}
		
		setImageIcon(cubeModule.getInstallIcon(), icon, (View) icon.getParent());
		name.setText(cubeModule.getName());
		version.setText(cubeModule.getVersion());
		releaseNote.setText(cubeModule.getReleaseNote());

		pageView = (SlidePageView) view.findViewById(R.id.slidePageView);
		pointlayout = (LinearLayout) view.findViewById(R.id.point);
		try {
			drawSnapshot(cubeModule, pageView, pointlayout);
			pageView.setOnPageChangedListener(changedListener);
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(getAssocActivity(), "加载快照异常",
			// Toast.LENGTH_SHORT).show();
		}

	}

	public void getTheCubeModule() {
		if (null == getAssocActivity().getIntent().getStringExtra(
				"FROM_UPGRAGE")) {
			// 从已安装列表跳入或未安装列表跳入
			if (null != CubeModuleManager.getInstance().getIdentifier_old_version_map()
					.get(getAssocActivity().getIntent().getStringExtra(
							"identifier"))) {
				cubeModule = CubeModuleManager.getInstance().getIdentifier_old_version_map()
						.get(getAssocActivity().getIntent().getStringExtra(
								"identifier"));
			} else {
				cubeModule = CubeModuleManager.getInstance()
						.getCubeModuleByIdentifier(getAssocActivity()
								.getIntent().getStringExtra("identifier"));
			}

		} else {
			// 从可更新列表获取
			cubeModule = CubeModuleManager.getInstance().getIdentifier_new_version_map()
					.get(getAssocActivity().getIntent().getStringExtra(
							"identifier"));
		}

		// Log.d("AppDetail",cubeModule.getName()+"--->"+cubeModule.getBuild()+"-->"+cubeModule.getModuleType());
	}

	public void setImageIcon(String url, ImageView icon, final View parent) {
		CubeAsyncImage asyncImageLoader = new CubeAsyncImage(getAssocActivity());
		
		if (url == null) {
			// 没有从服务器中下载到头像，则至为默认头像
			icon.setImageResource(R.drawable.defauit);
		} else if (url.startsWith("file:")) {
			// String urlName = url.substring(6);
			String urlName = url.substring(url.indexOf("www"));
			AssetManager asm = getAssocActivity().getAssets();
			java.io.InputStream inputStream = null;
			try {
				// String url2 =
				// "file:///android_asset/www/res/icon/android/icon-chat.png";
				// inputStream = asm.open("www/res/icon/android/" + urlName);
				inputStream = asm.open(urlName);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap myBitmap = BitmapFactory.decodeStream(inputStream, null, options);
				icon.setImageBitmap(myBitmap);
				list.add(new SoftReference<Bitmap>(myBitmap));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// icon.setImageDrawable(myBitmap);
			icon.setTag(url.toString());

		} else if (url.startsWith("snapshot:")) {
			// String urlName=url.substring(6);
			int index = url.indexOf(":");
			url = url.substring(index + 1);
			AssetManager asm = getAssocActivity().getAssets();
			java.io.InputStream inputStream = null;
			try {
				inputStream = asm.open("image/snapshot/" + url);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap myBitmap = BitmapFactory.decodeStream(inputStream, null, options);
				icon.setImageBitmap(myBitmap);
				list.add(new SoftReference<Bitmap>(myBitmap));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// icon.setImageDrawable(drawable);
		} else {
			
			icon.setTag(url.toString());
			// 异步加载图片，从缓存，内存卡获取图片。
			Bitmap bitmap = asyncImageLoader.loadImage(url.toString(),
					new CubeAsyncImage.ImageCallback() {// 如果无法从缓存，内存卡获取图片，则会调用该方法从网络上下载
						public void imageLoaded(Bitmap bitmap, String imageUrl) {
							ImageView imageViewByTag = (ImageView) parent
									.findViewWithTag(imageUrl);
							if (imageViewByTag == null) {
								return;
							}
							if (null != bitmap) {
								imageViewByTag.setImageBitmap(bitmap);
							} else {
								imageViewByTag
										.setImageResource(R.drawable.defauit);
							}
						}
					});

			if (bitmap != null) {
				// 设置图片显示
				list.add(new SoftReference<Bitmap>(bitmap));
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.defauit);
			}
		}
	}

	OnPageChangedListener changedListener = new com.foreveross.chameleon.phone.view.SlidePageView.OnPageChangedListener() {

		@Override
		public void onPageViewChanged(View view, int currentScreen) {
			for (int i = 0; i < pointlayout.getChildCount(); i++) {
				if (i == currentScreen) {
					pointlayout.getChildAt(i).setBackgroundResource(
							R.drawable.app_detail_selected);
				} else {
					pointlayout.getChildAt(i).setBackgroundResource(
							R.drawable.app_detail_unselected);
				}
			}
		}
	};

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.title_barleft:
				if (getAssocActivity() instanceof FacadeActivity) {
					((FacadeActivity) getAssocActivity()).popRight();
				} else {
					if(getAssocActivity()!=null) {
						getAssocActivity().finish();
					}

				}

				break;
			}
			return false;
		}
	};

	/**
	 * 根据identifier和build获取模块的快照图
	 * 
	 * @param identifier
	 *            ,build
	 */
	private void drawSnapshot(CubeModule module, final SlidePageView pageView,
			final LinearLayout pointlayout) {
		String appKey = Application.class.cast(getAssocActivity().getApplicationContext()).getCubeApplication().getAppKey();
		String identifier = module.getIdentifier();
		String version = module.getVersion();
		int build = module.getBuild();
		View view = LayoutInflater.from(getAssocActivity()).inflate(
				R.layout.app_detail_item, null);
		final ImageView img = (ImageView) view.findViewById(R.id.detail_img);
		final LinearLayout loadinglayout = (LinearLayout) view
				.findViewById(R.id.detail_loadingImglayout);
		final TextView loadingtext = (TextView) view
				.findViewById(R.id.detail_loadtext);
		final ProgressBar loadingbar = (ProgressBar) view
				.findViewById(R.id.detail_loadingBar);
		pageView.addView(view);

		HttpRequestAsynTask task = new HttpRequestAsynTask(getAssocActivity()) {

			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
				img.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
			}
			

			@Override
			protected void doHttpFail(Exception e) {
				// TODO Auto-generated method stub
				super.doHttpFail(e);
				img.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
				loadingbar.setVisibility(View.GONE);
				loadingtext.setText("网络状态不稳定!");
				
			}


			@Override
			protected void doPostExecute(String result) {
				if (result != null) {
					System.out.println(result);
					try {
						JSONArray jsonary = new JSONArray(result);
						int length = jsonary.length();
						List<String> paths = new ArrayList<String>();
						if (length != 0) {
							if (pageView.getChildCount() != 0) {
								pageView.removeAllViews();
							}
							for (int i = 0; i < jsonary.length(); i++) {
								paths.add(URL
										.getDownloadUrl(context,
												jsonary.getJSONObject(i)
														.getString("snapshot")));
							}
							draw(paths, pageView, pointlayout);
						} else {
							img.setVisibility(View.GONE);
							loadinglayout.setVisibility(View.VISIBLE);
							loadingbar.setVisibility(View.GONE);
							loadingtext.setText("没有快照");

						}
					} catch (JSONException e) {
						img.setVisibility(View.GONE);
						loadinglayout.setVisibility(View.VISIBLE);
						loadingbar.setVisibility(View.GONE);
						loadingtext.setText("图片加载出错");
						e.printStackTrace();
					}

				}
			}
		};

		if (module.getLocal() != null) {
			String[] files = Application.class.cast(
					getAssocActivity().getApplication()).getCubeApplication().tool
					.getAssectFilePath(module.getIdentifier());

			if (files.length != 0) {
				if (pageView.getChildCount() != 0) {
					pageView.removeAllViews();
				}
				List<String> paths = new ArrayList<String>();
				for (int i = 0; i < files.length; i++) {
					paths.add("snapshot:" + identifier + "/" + files[i]);
				}
				draw(paths, pageView, pointlayout);
			} else {
				img.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
				loadingbar.setVisibility(View.GONE);
				loadingtext.setText("没有快照");
			}

		} else {
			task.setNeedProgressDialog(false);
			task.execute(URL.SNAPSHOT + identifier + "/" + version + "/snapshot?appKey="+appKey,
					"", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
		}
	}

	private void draw(List<String> files, SlidePageView pageView,
			final LinearLayout pointlayout) {
		if (files.size() != 0) {
			if (pageView.getChildCount() != 0) {
				pageView.removeAllViews();
			}
			for (int i = 0; i < files.size(); i++) {
				if(getAssocActivity()==null) {
					return;
				}

				View view = LayoutInflater.from(getAssocActivity()).inflate(
						R.layout.app_detail_item, null);
				LayoutParams layoutParams = new LayoutParams(400,
						LayoutParams.MATCH_PARENT);
				layoutParams.setMargins(0, 0, 10, 0);
				view.setLayoutParams(layoutParams);
				view.setPadding(20, 10, 20, 0);
				ImageView img2 = (ImageView) view.findViewById(R.id.detail_img);
				setImageIcon(files.get(i), img2, view);
				pageView.addView(view, i);
				View viewPoint = new View(getAssocActivity());
				LayoutParams pointParams = new LayoutParams(files.size(),
						files.size());
				pointParams.setMargins(10, 0, 10, 0);
				viewPoint.setLayoutParams(pointParams);
				if (i == 0) {
					viewPoint
							.setBackgroundResource(R.drawable.app_detail_selected);
				} else {
					viewPoint
							.setBackgroundResource(R.drawable.app_detail_selected);
				}
				pointlayout.addView(viewPoint, i);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroy();
		EventBus.getEventBus(TmpConstants.EVENTBUS_MODULE_CHANGED,
				ThreadEnforcer.MAIN).unregister(this);
		List<SoftReference<Bitmap>> copies = new ArrayList<SoftReference<Bitmap>>();
		for (SoftReference<Bitmap> srf : list) {
			if (srf != null && srf.get() != null && !srf.get().isRecycled()) {
				srf.get().recycle();
				copies.add(srf);
			}
		}
		list.removeAll(copies);
	}

	public String getIdentifier() {
		return cubeModule.getIdentifier();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-9-5 下午11:35:44
	 */
	@Override
	public String toString() {
		return iden==null?this.getClass().getName()+this.hashCode():iden;
	}
}
