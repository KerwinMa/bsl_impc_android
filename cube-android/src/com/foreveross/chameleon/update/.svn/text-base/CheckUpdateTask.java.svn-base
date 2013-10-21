package com.foreveross.chameleon.update;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.google.gson.Gson;
//import android.content.Context;

public class CheckUpdateTask extends AsyncTask<String, Integer, Void> {
//	private Context context;
	private CubeApplication application;
	private CheckUpdateListener listener;
	private Throwable error = null;
	private CubeApplication newApp = null;

	public CheckUpdateTask(CubeApplication application, CheckUpdateListener listener) {
		this.application = application;
		this.listener = listener;
		
	}
//http://58.215.176.89:9000/m/apps/com.foreveross.cube.android/update
	@Override
	protected void onPreExecute() {
		if(listener != null) listener.onCheckStart();
	}
	
	@Override
	protected void onPostExecute(Void result) {
		if (error != null) {
			if(listener != null) listener.onCheckError(error);
		} else if (newApp != null){
			if(listener != null) listener.onUpdateAvaliable(application, newApp);
		} else {
			if(listener != null) listener.onUpdateUnavailable();
		}
	}
	
	@Override
	protected void onCancelled() {
		if(listener != null) listener.onCancelled();
	}

	@SuppressLint("NewApi")
	@Override
	protected Void doInBackground(String... params) {
		
		CubeApplication currentApplication = application;
		if(currentApplication == null) return null;
		final BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 15 * 1000);
		String updateUrl = URL.UPDATE+"/"+currentApplication.getPackageName()
				+"?appKey="+currentApplication.getAppKey();
		System.out.println("updateUrl:" + updateUrl);
		final HttpClient client = new DefaultHttpClient(httpParameters);
//		final HttpGet bundleget = new HttpGet(URL.BASE+"m/apps/"+currentApplication.getPackageName()+"/update");
		final HttpGet bundleget = new HttpGet(updateUrl);
		bundleget.setHeader("User-Agent", "Deamon");
		try {
			HttpResponse response = client.execute(bundleget);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				//parse result
				final String responseString = EntityUtils.toString(response.getEntity());
				if(!responseString.isEmpty()&&!responseString.equals("{}")) {
//					{"message":"检测应用更新失败！","result":"error"}
					
					JSONObject jb = new JSONObject(responseString);
					if(jb.getString("result").equals("error")){
						throw new RuntimeException("检测更新失败");
					}
					
					
					Gson gson=new Gson();
					CubeApplication nApp=gson.fromJson(responseString, CubeApplication.class);
//				if (nApp.getBuild() != currentApplication.getBuild()&&null!=nApp.getBundle()) {//should greater?
					if (nApp.getBuild() > currentApplication.getBuild()) {//should greater?
						newApp = nApp;
					} else {
						newApp = null;
					}
					
				}
			} else {
				throw new RuntimeException("检测更新失败，服务器连接异常，状态码:" + response.getStatusLine().getStatusCode());
			}
		} catch (Throwable e) {
			Log.e("VersionUpdate", "获取更新失败", e);
			error = new Exception("访问发生错误，请检查网络");
		}
		
		return null;
	}
}
