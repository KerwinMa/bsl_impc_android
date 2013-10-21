/**
 * 
 */
package com.foreveross.chameleon.phone.modules;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.chameleon.CubeAndroid;
import com.csair.impc.R;
import com.foreveross.chameleon.util.imageTool.CubeAsyncImage;

/**
 * @author zhoujun
 *
 */
public class CubeModelItemView extends RelativeLayout
{
	private ImageView img_update;
	
	private TextView item_title;
	
	private Context mContext;
	

	public CubeModelItemView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	public CubeModelItemView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public CubeModelItemView(Context context)
	{
		super(context);
		initView(context);
	}
	
	private void initView(Context context)
	{
		this.mContext = context;
		View viewItem = LayoutInflater.from(context).inflate(R.layout.cube_module_item, null);
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,70);
//		viewItem.setLayoutParams(params);
		img_update = (ImageView) viewItem.findViewById(R.id.img_update);
		item_title = (TextView) viewItem.findViewById(R.id.item_title);
		addView(viewItem);
	}
	
	public void updateView(final CubeModule module)
	{
		item_title.setText(module.getName());
		this.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View paramView)
			{
				String path = Environment.getExternalStorageDirectory()
						.getPath() + "/" + mContext.getPackageName();
				Intent intent = new Intent();
				intent.putExtra("from", "set");
				intent.putExtra("identify", module.getIdentifier());
				intent.putExtra("path", path);
				intent.setClass(mContext, CubeAndroid.class);
				mContext.startActivity(intent);
			}
		});
		setImageIcon(mContext,module.getInstallIcon(),img_update,this);
	}
	public void setImageIcon(Context context, String url, ImageView icon,
			final View parent) {
		CubeAsyncImage asyncImageLoader = new CubeAsyncImage((Activity) context);
		if (null == url) {
			// 没有从服务器中下载到头像，则至为默认头像
			icon.setImageResource(R.drawable.defauit);
			icon.setTag(null);
		} else if (url.startsWith("local:")) {
			String urlName = url.substring(6);
			AssetManager asm = context.getAssets();
			java.io.InputStream inputStream = null;
			try {
				inputStream = asm.open("www/res/icon/android/" + urlName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Drawable drawable = Drawable.createFromStream(inputStream, null);
			icon.setImageDrawable(drawable);
			icon.setTag(url.toString());

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
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.defauit);
			}
		}
	}

}
