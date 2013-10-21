package com.foreveross.chameleon.phone.modules;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.phone.view.ItemButton;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.util.Preferences;
import com.foreveross.chameleon.util.PushUtil;
import com.foreveross.chameleon.util.imageTool.CubeAsyncImage;

public class CubeListViewAdapter extends DataSourceAdapter {

	public List<CubeModule> getModules() {
		return modules;
	}

	private int showBtn;

	public CubeListViewAdapter(Context context, List<CubeModule> modules,
			int showBtn) {
		super(context, modules);
		this.showBtn = showBtn;
	}

	@Override
	public int getCount() {
		return modules.size();
	}

	@Override
	public Object getItem(int position) {
		return modules.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListViewItem listItem = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.app_list_item, null);
			listItem = new ListViewItem();
			listItem.mAppIcon = (ImageView) convertView
					.findViewById(R.id.ivAppIcon);
			listItem.mAppName = (TextView) convertView
					.findViewById(R.id.tvAppName);
			listItem.mAppbar = (ProgressBar) convertView
					.findViewById(R.id.progressBar_download);
			listItem.mAppupdata = (ImageView) convertView
					.findViewById(R.id.imageView_updata);
			listItem.mAppVersion = (TextView) convertView
					.findViewById(R.id.tvAppversion);
			listItem.mAppReleaseNote = (TextView) convertView
					.findViewById(R.id.tvAppreleaseNote);
			listItem.mAppInstallBtn = (ItemButton) convertView
					.findViewById(R.id.tvAppbtn);
			listItem.mAppNext = (ImageView) convertView
					.findViewById(R.id.app_next);
			convertView.setTag(listItem);
		} else {
			listItem = (ListViewItem) convertView.getTag();
		}
		// 只有一行数据，用全白色圆角
		if (modules.size() == 1) {
			convertView
					.setBackgroundResource(R.drawable.listview_select_corner);
		} else {
			if (position == 0) {
				convertView
						.setBackgroundResource(R.drawable.listview_select_white_top);
			} else if (position == modules.size() - 1) {
				if (position % 2 == 0) {
					// 双数，显示白色背景
					convertView
							.setBackgroundResource(R.drawable.listview_select_white_bottom);
				} else {
					// 单数，显示灰色背景
					convertView
							.setBackgroundResource(R.drawable.listview_select_gray_bottom);
				}
			} else {
				if (position % 2 == 0) {
					// 双数，显示白色背景
					convertView
							.setBackgroundResource(R.drawable.listview_select_white_cell);
				} else {
					// 单数，显示灰色背景
					convertView
							.setBackgroundResource(R.drawable.listview_select_gray_cell);
				}
			}

		}

		CubeModule cubeModule = modules.get(position);

		listItem.mAppName.setText(cubeModule.getName());
		listItem.mAppReleaseNote.setText(cubeModule.getReleaseNote());
		listItem.mAppVersion.setText(cubeModule.getVersion());
		this.setImageIcon(context, cubeModule.getIcon(), listItem.mAppIcon,
				parent);
		if ((CubeModule.INSTALLING == cubeModule.getModuleType()
				|| CubeModule.UPGRADING == cubeModule.getModuleType() || CubeModule.DELETING == cubeModule
				.getModuleType()) && cubeModule.getProgress() == -1) {
			listItem.mAppbar.setVisibility(View.VISIBLE);
			listItem.mAppbar.setIndeterminate(true);
		} else if (CubeModule.INSTALLING == cubeModule.getModuleType()
				|| CubeModule.UPGRADING == cubeModule.getModuleType()
				|| CubeModule.DELETING == cubeModule.getModuleType()) {
			listItem.mAppbar.setVisibility(View.VISIBLE);
			listItem.mAppbar.setIndeterminate(false);
			listItem.mAppIcon.setAlpha(90);
			listItem.mAppbar.setProgress(cubeModule.getProgress());
		} else {
			listItem.mAppbar.setVisibility(View.GONE);
			listItem.mAppIcon.setAlpha(255);
		}

		if (CubeModuleManager.getInstance().IN_MAIN == showBtn) {
			listItem.mAppName.setTextSize(17);
			listItem.mAppNext.setVisibility(View.VISIBLE);
			listItem.mAppInstallBtn.setVisibility(View.GONE);
			listItem.mAppVersion.setVisibility(View.GONE);
			listItem.mAppReleaseNote.setVisibility(View.GONE);
			if (CubeModule.UPGRADABLE == cubeModule.getModuleType()
					|| (CubeModule.INSTALLED == cubeModule.getModuleType() && cubeModule
							.isUpdatable())) {
				listItem.mAppupdata.setVisibility(View.VISIBLE);
			} else {
				listItem.mAppupdata.setVisibility(View.GONE);
			}
			if (cubeModule.getIdentifier().equals("com.foss.chat")) {
				cubeModule.setMsgCount(0);
				// int count =
				// Application.class.cast(context.getApplicationContext()).getAllUserIsRead();
				// int count =
				// IMModelManager.instance().getAllUserIsReadCount();
				// listItem.mAppIcon.setImageBitmap(PushUtil.drawPushCount(
				// context, listItem.mAppIcon, count));
			} else {
				if (cubeModule.getNoticeCount() != 0) {
					listItem.mAppIcon.setImageBitmap(PushUtil.drawPushCount(
							context, listItem.mAppIcon,
							cubeModule.getNoticeCount()));
				}
				if (cubeModule.getMsgCount() != 0) {
					listItem.mAppIcon.setImageBitmap(PushUtil.drawPushCount(
							context, listItem.mAppIcon,
							cubeModule.getMsgCount()));
				}
			}
		} else if (CubeModuleManager.getInstance().IN_UPDATABLE == showBtn
				&& CubeModule.UPGRADABLE == cubeModule.getModuleType()) {
			listItem.mAppInstallBtn.setVisibility(View.VISIBLE);
			listItem.mAppInstallBtn.initModel(context, cubeModule);
			listItem.mAppupdata.setVisibility(View.VISIBLE);
		}
		// else if(CubeModuleManager .IN_TASKLIST == showBtn ){
		//
		// }
		else {
			if (CubeModule.UPGRADABLE == cubeModule.getModuleType()
					|| (CubeModule.INSTALLED == cubeModule.getModuleType() && cubeModule
							.isUpdatable())) {
				listItem.mAppupdata.setVisibility(View.VISIBLE);
			} else {
				listItem.mAppupdata.setVisibility(View.GONE);
			}
			listItem.mAppInstallBtn.setVisibility(View.VISIBLE);
			listItem.mAppInstallBtn.initModel(context, cubeModule);
		}
		return convertView;
	}

	class ListViewItem {
		public ImageView mAppIcon;
		public ImageView mAppNext;
		public TextView mAppName;
		public ProgressBar mAppbar;
		public ImageView mAppupdata;
		public ItemButton mAppInstallBtn;
		public TextView mAppVersion;
		public TextView mAppReleaseNote;
	}

	public void setImageIcon(Context context, String url, ImageView icon,
			final View parent) {
		CubeAsyncImage asyncImageLoader = new CubeAsyncImage((Activity) context);
		if (null == url) {
			// 没有从服务器中下载到头像，则至为默认头像
			icon.setImageResource(R.drawable.defauit);
			icon.setTag(null);
		} else if (url.startsWith("file:")) {
			// String urlName = url.substring(6);
			String urlName = url.substring(url.indexOf("www"));
			AssetManager asm = context.getAssets();
			java.io.InputStream inputStream = null;
			try {
				// String url2 =
				// "file:///android_asset/www/res/icon/android/icon-chat.png";
				// inputStream = asm.open("www/res/icon/android/" + urlName);
				inputStream = asm.open(urlName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Drawable drawable = Drawable.createFromStream(inputStream, null);
			icon.setImageDrawable(drawable);
			icon.setTag(url.toString());

		} else {
			url = url + "?sessionKey="
					+ Preferences.getSESSION(Application.sharePref);
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
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.defauit);
			}
		}
	}

}
