package com.foreveross.chameleon.phone.chat.camera;

import java.io.File;
import java.io.FileOutputStream;

import com.csair.impc.R;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.MediaStore;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity {
	private static final int PICTURE_CALLBACK_FAIL = 8;
	Handler mHandler = new Handler() {

	};
	View relativeLayout;
	View linearLayout;
	Preview pv;
	Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		RotateImageView riv = (RotateImageView) findViewById(R.id.iv_scrollbar_background);
		Intent i = this.getIntent();
		if (i != null) {
			Log.d("getStringExtra",
					"" + i.getStringExtra(MediaStore.EXTRA_OUTPUT));
			Log.d("getData", "" + i.getData());
			Log.d("getData", "" + i.getParcelableExtra(MediaStore.EXTRA_OUTPUT));
			uri = i.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
		}
		pv = (Preview) findViewById(R.id.surfaceView);
		relativeLayout = findViewById(R.id.camera_icon_layout);
		linearLayout = findViewById(R.id.camera_button_layout);
		relativeLayout.setVisibility(View.VISIBLE);
		linearLayout.setVisibility(View.GONE);
		Button okbutton = (Button) findViewById(R.id.okbutton);
		Button cancelbutton = (Button) findViewById(R.id.cancelbutton);
		cancelbutton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pv.StartPreview();
				relativeLayout.setVisibility(View.VISIBLE);
				linearLayout.setVisibility(View.GONE);
			}
		});
		okbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		jpgCallback = new JpegPictureCallback();
		riv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("拍照", "");
				pv.takePicture(null, null, jpgCallback);

			}

		});
	}

	boolean mPausing = false;
	JpegPictureCallback jpgCallback;

	// byte[] bufferdata;
	public final class JpegPictureCallback implements PictureCallback {
		// tangzhen 2013-01-14
		// FIXME:照片坐标使用currentProject进行同步，需修改，这里不再传递Location
		/*
		 * Location mLocation;
		 * 
		 * public JpegPictureCallback(Location loc) { mLocation = loc; }
		 */

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			mHandler.removeMessages(PICTURE_CALLBACK_FAIL);

			if (mPausing) {
				Log.d("照片处理中", "");
				return;
			}

			mHandler.removeMessages(PICTURE_CALLBACK_FAIL);
			if (data.length <= 1024) {
				mHandler.sendEmptyMessage(PICTURE_CALLBACK_FAIL);
				Log.e("拍照失败", "照片大小为：" + data.length);
				return;
			} else {
				// mPausing = true;
				Log.d("拍照成功", "照片大小为：" + data.length);
				/*
				 * relativeLayout.setVisibility(View.GONE);
				 * linearLayout.setVisibility(View.VISIBLE);
				 */
				// bufferdata = data;
				relativeLayout.setVisibility(View.GONE);
				if (data != null && data.length > 1024) {
					String path = save(data);
					Log.d("照片存储完成", "路径为设置：" + path);
					if (path == null) {
						return;
					}
					File imageFile = new File(path);
					Log.d("照片存储完成", "imageFile路径为设置：" + imageFile.getPath());
					Log.d("照片存储完成", "imageFile长度：" + imageFile.length());

					if (imageFile != null) {

						Uri uri = Uri.fromFile(imageFile);
						Log.d("照片存储完成", "设置uri:" + uri.getPath());
						setResult(RESULT_OK, (new Intent()).setData(uri));
						finish();
					} else {
						Log.e("照片存储异常", "");
					}
				}

				/*
				 * new Thread(new Runnable(){
				 * 
				 * @Override public void run() { // TODO Auto-generated method
				 * stub int cout = 10; while(cout>0){ cout--; try {
				 * Thread.sleep(1000); } catch (InterruptedException e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); } }
				 * mPausing = false; }
				 * 
				 * }).start();
				 */
			}
		}

	}

	private String save(byte[] data) {
		String path = uri.getPath();
		try {
			// 判断是否装有SD卡
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				// 判断SD卡上是否有足够的空间
				String storage = Environment.getExternalStorageDirectory()
						.toString();
				StatFs fs = new StatFs(storage);
				long available = fs.getAvailableBlocks() * fs.getBlockSize();
				Log.e("获取sd卡大小：", "available=" + available);
				if (available > 0 && available < data.length) {// 小于0则数组越界
					// 空间不足直接返回空
					return null;
				}
				File file = new File(path);
				if (!file.exists())
					// 创建文件
					file.createNewFile();

				FileOutputStream fos = new FileOutputStream(file);
				fos.write(data);
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("保存文件异常：", "Message=" + e.getMessage());

			return null;
		}
		return path;
	}
}
