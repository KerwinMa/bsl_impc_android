package com.foreveross.chameleon.phone.modules;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.util.Preferences;
import com.foreveross.chameleon.util.PushUtil;
import com.foreveross.chameleon.util.imageTool.CubeAsyncImage;

public class CubeGridViewAdapter extends DataSourceAdapter {
	private int showBtn;

	public CubeGridViewAdapter(Context context,List<CubeModule> modules,int showBtn){
		super(context,modules);
		this.showBtn=showBtn;
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
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewItem gridItem=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.app_item,null);
			gridItem = new GridViewItem();
			gridItem.mAppIcon = (ImageView) convertView.findViewById(R.id.ivAppIcon);
			gridItem.mAppName = (TextView) convertView.findViewById(R.id.tvAppName);
			gridItem.mAppbar = (ProgressBar) convertView.findViewById(R.id.progressBar_download);
			gridItem.mAppupdata = (ImageView) convertView.findViewById(R.id.imageView_updata);
			convertView.setTag(gridItem);
		} else {
		    gridItem = (GridViewItem) convertView.getTag();
		}
		CubeModule cubeModule=modules.get(position);
		gridItem.mAppName.setText(cubeModule.getName());
		this.setImageIcon(context, cubeModule.getIcon(), gridItem.mAppIcon, parent);
		if((CubeModule.INSTALLING==cubeModule.getModuleType()||CubeModule.UPGRADING==cubeModule.getModuleType()||CubeModule.DELETING==cubeModule.getModuleType())&&cubeModule.getProgress()==-1){
			gridItem.mAppbar.setVisibility(View.VISIBLE);
			gridItem.mAppbar.setIndeterminate(true);
		}else if(CubeModule.INSTALLING==cubeModule.getModuleType()||CubeModule.UPGRADING==cubeModule.getModuleType()||CubeModule.DELETING==cubeModule.getModuleType()){
			Log.d("Status", cubeModule.getName()+"module.progress-->"+cubeModule.getProgress());
			gridItem.mAppbar.setVisibility(View.VISIBLE);
			gridItem.mAppbar.setIndeterminate(false);
			gridItem.mAppIcon.setAlpha(90);
			gridItem.mAppbar.setProgress(cubeModule.getProgress());
		}else {
			gridItem.mAppbar.setVisibility(View.GONE);
			gridItem.mAppIcon.setAlpha(255);
		}
		if(CubeModuleManager.getInstance().IN_MAIN==showBtn){
			if(cubeModule.getIdentifier().equals("com.foss.chat")){
				cubeModule.setMsgCount(0);
//				int count = Application.class.cast(context.getApplicationContext()).getAllUserIsRead();
//				int count = IMModelManager.instance().getAllUserIsReadCount();
//				gridItem.mAppIcon.setImageBitmap(PushUtil.drawPushCount(context, gridItem.mAppIcon, count));
			}else{
				if(cubeModule.getNoticeCount()!=0){
					gridItem.mAppIcon.setImageBitmap(PushUtil.drawPushCount(context,gridItem.mAppIcon,cubeModule.getNoticeCount()));
				}
				if(cubeModule.getMsgCount()!=0){
					gridItem.mAppIcon.setImageBitmap(PushUtil.drawPushCount(context,gridItem.mAppIcon,cubeModule.getMsgCount()));
				}
			}
		}
		if((CubeModuleManager.getInstance().IN_MAIN==showBtn||CubeModuleManager.getInstance().IN_UPDATABLE==showBtn)&&CubeModule.UPGRADABLE==cubeModule.getModuleType()){
			gridItem.mAppupdata.setVisibility(View.VISIBLE);
		}else if(CubeModuleManager.getInstance().IN_MAIN==showBtn&&cubeModule.isUpdatable()){
			gridItem.mAppupdata.setVisibility(View.VISIBLE);
		}else{
			gridItem.mAppupdata.setVisibility(View.GONE);
		}

		return convertView;
	}
	class GridViewItem{
		public ImageView mAppIcon;
		public TextView mAppName;
		public ProgressBar mAppbar;
		public ImageView mAppupdata;
	}
	public void setImageIcon(Context context,String url,ImageView icon,final View parent){
		CubeAsyncImage asyncImageLoader=new CubeAsyncImage((Activity) context);
		if(url==null){
			//没有从服务器中下载到头像，则至为默认头像
			icon.setImageResource(R.drawable.defauit);
			icon.setTag(null);
		}else if(url.startsWith("file:")){
			String urlName = url.substring(url.indexOf("www"));
//			String urlName=url.substring(6);
			AssetManager asm=context.getAssets();
			java.io.InputStream inputStream=null;
			try {
//				inputStream = asm.open("www/res/icon/android/"+urlName);
				inputStream = asm.open(urlName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Drawable drawable=Drawable.createFromStream(inputStream, null);
			icon.setImageDrawable(drawable);
			icon.setTag(url.toString());
		
		}else{
			url = url + "?sessionKey="+ Preferences.getSESSION(Application.sharePref);
				icon.setTag(url.toString());
				// 异步加载图片，从缓存，内存卡获取图片。
				Bitmap bitmap = asyncImageLoader.loadImage(url.toString(), new CubeAsyncImage.ImageCallback() {// 如果无法从缓存，内存卡获取图片，则会调用该方法从网络上下载
					public void imageLoaded(Bitmap bitmap, String imageUrl) {
						ImageView imageViewByTag = (ImageView) parent.findViewWithTag(imageUrl);
						if (imageViewByTag == null) {
							return;
						}
						if (null != bitmap) {
							imageViewByTag.setImageBitmap(bitmap);
						} else {
							imageViewByTag.setImageResource(R.drawable.defauit);
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
