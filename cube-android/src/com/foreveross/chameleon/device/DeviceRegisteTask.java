package com.foreveross.chameleon.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.util.DeviceInfoUtil;

/**
 * 设备信息注册
 * 检查设备是否已经注册
 * true:已经注册不做处理
 * false：没有注册弹出注册界面进行注册
 * @author zhoujun
 */
public class DeviceRegisteTask extends AsyncTask<Integer, Void, Boolean> {

	private Context mContext;
	
	public DeviceRegisteTask(Context mContext) {
		this.mContext = mContext;
	}

	
	@Override
	protected Boolean doInBackground(Integer... params) {
		
		boolean isRegisted = checkRegisted();
		return isRegisted;
	}

	/**
	 * 检查是否已经注册
	 * @return
	 */
	private boolean checkRegisted() {
		boolean flag = false;
		String deviceId = DeviceInfoUtil.getDeviceId(mContext);
		String appKey = Application.class.cast(mContext).getCubeApplication().getAppKey();
		String url = URL.BASE_WS +"/csair-extention/deviceRegInfo/check/"+deviceId+"?appKey="+appKey;
		HttpGet request = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		BufferedReader br = null;
		//如果发生异常就默认进到登录界面
		try {
			HttpResponse response = client.execute(request);
			if(response.getStatusLine().getStatusCode() == 200)
			{
				br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line;
				StringBuffer buffer = new StringBuffer();
				while((line = br.readLine()) != null)
				{
					buffer.append(line);
				}
				JSONObject json = new JSONObject(buffer.toString());
				boolean result = json.getBoolean("result");
				if(result)
				{
					return true;
				}
				return false;
				
					
			}
			return true;
		} catch (ClientProtocolException e) {
			flag = true;
		} catch (IOException e) {
			flag = true;
		} catch (JSONException e) {
			flag = true;
		}
		finally
		{
			if(br != null)
			{
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
	}
	

}
