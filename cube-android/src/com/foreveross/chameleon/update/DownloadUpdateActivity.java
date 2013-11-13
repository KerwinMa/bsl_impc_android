package com.foreveross.chameleon.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.phone.modules.CubeApplication;

/**
 * 下载进度Activity
 * @author Justin
 *
 */
public class DownloadUpdateActivity extends Activity {

	public static final String INTENT_CURRENT_APPLICATION = "com.foreveross.cube.appnew";
	public static final String INTENT_NEW_APPLICATION = "com.foreveross.cube.appcurrent";
	
	DownloadTask downloadTask;
	ProgressDialog progressDialog;
	WakeLock wakeLock;
	
	int newBuild = 0;
	String appKey="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//透明activity
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(new View(this));
		
		//创建进度对话框
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(100);
		progressDialog.setTitle("南航移动应用平台版本更新");
		progressDialog.setMessage("下载中，请稍候...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setButton(Dialog.BUTTON_NEGATIVE, "取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (downloadTask != null) {
					downloadTask.cancel(true);
				}
				Toast.makeText(DownloadUpdateActivity.this, "更新已取消", 3 * 1000).show();
				
				DownloadUpdateActivity.this.finish();
			}
		});
		progressDialog.setOnKeyListener(new OnKeyListener() {
		    @Override
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
		            return true; // Pretend we processed it
		        }
		        return false; // Any other keys are still processed as normal
		    }
		});
		
		CubeApplication newApp = (CubeApplication) getIntent().getExtras().getSerializable(INTENT_NEW_APPLICATION);
		//开始下载
		newBuild = newApp.getBuild();
		appKey = CubeApplication.getInstance(getApplicationContext()).getAppKey();
		
		downloadTask = new DownloadTask();
		downloadTask.execute(newApp);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		//下载期间，暗屏，不睡眠
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass().getName());
		wakeLock.acquire();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		if (wakeLock !=null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock =null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		downloadTask.cancel(true);
	}

	/*
	 * 获取存放更新安装包的路径
	 */
	public File getAppDataDir() {
		File appBasePath = null;
		File downloadCacheDirectory = Environment.getExternalStorageDirectory();
		if (downloadCacheDirectory != null) {
			appBasePath = new File(downloadCacheDirectory.getAbsolutePath()
							+ File.separator + "Android"
							+ File.separator + "data"
							+ File.separator + this.getPackageName()
							+ File.separator + "files");
			
			if(!appBasePath.exists()) {
				appBasePath.mkdirs();
			}
		}
		return appBasePath;
	}
	
	/*
	 * 根据版本获取安装包文件
	 */
	public File getAPKFile(CubeApplication app) {
		String apkPath = String.format(getAppDataDir().getAbsolutePath() + File.separator + "amp-%s.apk", app.getBuild());
		File apkFile = new File(apkPath);
		return apkFile;
	}
	
	public void install(CubeApplication app) {
		Log.d("VersionUpdate", "安装更新");
		
		//uninstall
//		Uri packageURI = Uri.parse("package:com.csair.cs");         
//		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);   
//		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		startActivity(uninstallIntent);
		
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setAction(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(getAPKFile(app)), "application/vnd.android.package-archive");  
	    startActivity(intent);
	    
	    //关闭自身，否则取消安装后，跳回此activity，会重新下载
	    this.finish();
	}
	
	void showDownloadError(String error){
		Log.d("VersionUpdate", "显示更新下载错误");
		
		//关闭进度框
		progressDialog.dismiss();
		
		//提示更新失败
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("版本更新出错")
		.setMessage(error)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				DownloadUpdateActivity.this.finish();
			}
		})
		.create();
		dialog.show();
	}
	
	class DownloadTask extends AsyncTask<CubeApplication, Integer, Void> {

		private CubeApplication newApp = null;
		private Throwable error = null;
		
		@Override
		protected void onPreExecute() {
			Log.d("VersionUpdate", "开始下载更新包");
			
			progressDialog.show();
			progressDialog.setProgress(0);
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//Log.d("VersionUpdate", "进度:" + progress[0]);
			progressDialog.setProgress(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Void v) {
			Log.d("VersionUpdate", "下载完毕");
			
			progressDialog.dismiss();
			
			if (error != null) {
				showDownloadError(error.getMessage());
			} else {
				//安装
				//更新服务端计数
				
				
				  new UpateRecordTask().execute();
				
				install(newApp);
			}
		}
		
		@Override
		protected void onCancelled() {
			progressDialog.dismiss();
		}

		@Override
		protected Void doInBackground(CubeApplication... params) {
			System.out.println(params[0]);
			final CubeApplication app = params[0];
			newApp = app;
//			Log.d("VersionUpdate", String.format("应用版本:%s|%d,url:%s", app.getVersion(), app.getBuild(), app.getDownloadUrl()));
			
			final BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 15 * 1000);
			final HttpClient client = new DefaultHttpClient(httpParameters);
//			http://58.215.176.89:9001/storage/attachments/ff8080813c95f60b013c962573560000
			String updateUrl=URL.getUpdateAppplicationUrl(DownloadUpdateActivity.this, newApp.getBundle());
			final HttpGet get = new HttpGet(updateUrl);
			System.out.println("更新包地址为:url ="+get.getURI());	
			get.setHeader("User-Agent", "Deamon");
			
			InputStream is=null;
			FileOutputStream fos = null;
			
			File apkFile = getAPKFile(app);
			try {
				HttpResponse response = client.execute(get);
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
					
					final HttpEntity entity = response.getEntity();
					final long length = entity.getContentLength();
					Log.d("VersionUpdate", "安装包大小:" + length / 1000 + "kb");
					is = entity.getContent();

					if(!apkFile.exists()) apkFile.createNewFile();
					fos = new FileOutputStream(apkFile);
					
					byte[] buffer = new byte[1024 * 100];
					int count = 0;
					long total = 0;
					while( (count =is.read(buffer, 0, buffer.length)) != -1) {
						fos.write(buffer, 0, count);
						total += count;
						long progress = total * 100 / length; 
						publishProgress((int)progress);//通知进度更新
					}
					Log.d("VersionUpdate", "实际大小:" + total / 1000 + "kb");
					if (total != length) {
						throw new RuntimeException("安装包大小与服务器不符，可能由于网络不稳定导致，请重新更新");
					}
				} else {
					throw new RuntimeException("安装包下载失败，服务器连接异常，状态码:" + response.getStatusLine().getStatusCode());
				}
			} catch (Throwable e) {
				Log.e("VersionUpdate", "获取更新失败", e);
				error = new Exception("访问发生错误，请检查网络");
			} finally {
				try {
					if (fos != null) {
						fos.flush();
						fos.close();
					}
					if (is != null) {
						is.close();
					}
				} catch (IOException e) {
				}
			}
			
			return null;
		}
		
	}
		
	//下载完成更新服务端计数
		class UpateRecordTask extends AsyncTask<Integer, Void, Void>
		{

			@Override
			protected Void doInBackground(Integer... params) {
				
				final BasicHttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, 15 * 1000);
				String updateUrl = URL.UPDATE_RECORD+newBuild
						+"?appKey="+appKey;
				System.out.println("updateUrl:" + updateUrl);
				final HttpClient client = new DefaultHttpClient(httpParameters);
//				final HttpGet bundleget = new HttpGet(URL.BASE+"m/apps/"+currentApplication.getPackageName()+"/update");
				final HttpGet bundleget = new HttpGet(updateUrl);
				bundleget.setHeader("User-Agent", "Deamon");
				try {
					HttpResponse response = client.execute(bundleget);
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						Log.i("UPATE_RECORD_Tag", "更新计数成功");
					} 
					else {
						throw new RuntimeException("更新计数失败，服务器连接异常，状态码:" + response.getStatusLine().getStatusCode());
					}
				} catch (Throwable e) {
					Log.e("VersionUpdate", "获取更新失败", e);
				}
				
				return null;
			}
			
		}
}
