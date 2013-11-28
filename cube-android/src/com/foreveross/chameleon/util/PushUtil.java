package com.foreveross.chameleon.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.push.mina.library.inf.vo.DeviceCheckinVo;
import com.foreveross.chameleon.push.mina.library.inf.vo.TagEntryVo;
import com.foreveross.chameleon.push.mina.library.util.ThreadPool;
import com.google.gson.Gson;

public class PushUtil {
	
	private static ExecutorService pool ;
	
	public static ExecutorService getPool(){
		if(pool==null){
			pool = Executors.newCachedThreadPool();
		}
		return pool;
	}

	public static void registerPush(final Application application){
		ThreadPool.run(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(URL.CHECKIN_URL);
//				HttpPut httpPut = new HttpPut("http://10.108.1.36:18860/push/api/checkinservice/checkins");
				try {
					String token = 
							createMD5Token(application)
//							DeviceInfoUtil.getDeviceId(application) 
							+ "@" + application.getPushManager().getConnection().getServiceName();
					httpPut.addHeader("Accept", "application/json");
					httpPut.addHeader("Content-Type", "application/json");
					DeviceCheckinVo checkinVo = new DeviceCheckinVo();
					checkinVo.setDeviceId(DeviceInfoUtil.getDeviceId(application));
					// checkinVo.setAppId("51f787228314bd3f4de8f98e");
					checkinVo.setAppId(application.getCubeApplication().getAppKey());
					// checkinVo.setAlias(alias);
					checkinVo.setChannelId("openfire");
					checkinVo.setDeviceName(android.os.Build.MODEL);
					// checkinVo.setGis("128,68");
					checkinVo.setOsName("android");
					checkinVo.setOsVersion(android.os.Build.VERSION.RELEASE);
					checkinVo.setPushToken(token);
					checkinVo.setTags(new TagEntryVo[] { new TagEntryVo("platform", "Android") });

					httpPut.setEntity(new StringEntity(new Gson().toJson(checkinVo), "utf-8"));

					HttpResponse httpResponse = httpClient.execute(httpPut);
					int statusCode = httpResponse.getStatusLine()
							.getStatusCode();
					Log.d("openfire Client", "签到 code == " + statusCode);
					if (statusCode == HttpStatus.SC_OK) {
						Log.d("openfire Client", "openfire 签到成功");
					}
				} catch (ClientProtocolException e) {
					Log.e("openfire Handler", "MessageContentHandler", e);
				} catch (IOException e) {
					Log.e("openfire Handler", "MessageContentHandler", e);
				}
			}
		});
	}
	
	
	public  static String createMD5Token(Application application){
		String token = DeviceInfoUtil.getDeviceId(application)
				+ "_" + URL.getAppKey()
				;
//				+ "@" + application.getPushManager().getConnection().getServiceName();
		return MD5Util.toMD5(token);
	}


	
//	public static void regisrerPush(Context context ,String tokenId) {
//		HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
//			
//			@Override
//			protected void doPostExecute(String result) {
//				super.doPostExecute(result);
//				if(result!=null){
//					Log.v("registerPush", "registe openfire success");
//							
//					//{"tokenId":"chenshaomou@chens-macbook-pro.local","applicationId":"cube_app",
//					//"deviceChannel":"Openfire","deviceId":"chenshaomou-openfire-tester"}
//					PreferencesUtil.setBoolean(context,"isRegisterPush", true);
//					
//				}
//			}
//			
//		};
//		task.setShowProgressDialog(false);
//		task.setNeedProgressDialog(false);
//		StringBuilder sb = new StringBuilder();
//		String applicationId = context.getPackageName();
//		if(applicationId.endsWith(".android")){
//			int strIndex = applicationId.lastIndexOf(".");
//			applicationId = applicationId.substring(0,strIndex);
//		}
////		int strIndex = applicationId.length()-8;
////		applicationId = applicationId.substring(0,strIndex);
//		sb=sb.append("{\"tokenId\":").append("\""+tokenId+"\",")
//			 .append("\"applicationId\":").append("\"" +applicationId +"\",")
////			 .append("\"applicationId\":").append("\"com.foreveross.cube\",")
//			 .append("\"deviceChannel\":").append("\"Openfire\",")
//			 .append("\"deviceId\":").append("\""+DeviceInfoUtil.getDeviceId(context)+"\"}");
//		String s = sb.toString();
//		String url=URL.BASE_WEB+"push/token";
//		
//		task.execute(url,s, HttpUtil.UTF8_ENCODING,HttpUtil.HTTP_POST);
//	}
	
	
	/**
	  * @author Amberlo
	  * @param ImageView 绘制所在的图片 count为消息条数，0则不显示
	  *  例：img.setImageBitmap(drawPushCount(img,1))；
	  **/   
	    public static Bitmap drawPushCount(Context context,ImageView img,int count){  
	    	
	    	
	    	Bitmap icon = ((BitmapDrawable)img.getDrawable()).getBitmap(); 
	    	if(count==0){
	    		return icon;
	    	}
	        //初始化画布  
	        int iconSize=(int)context.getResources().getDimension(android.R.dimen.app_icon_size);  
	        Bitmap contactIcon=Bitmap.createBitmap(iconSize, iconSize, Config.ARGB_8888);  
	        Canvas canvas=new Canvas(contactIcon);  
	          
	        //拷贝图片  
	        Paint iconPaint=new Paint();  
	        iconPaint.setDither(true);//防抖动  
	        iconPaint.setFilterBitmap(true);//用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果  
	        Rect src=new Rect(0, 0, icon.getWidth(), icon.getHeight());  
	        Rect dst=new Rect(0, 0, iconSize, iconSize);  
	        canvas.drawBitmap(icon, src, dst, iconPaint);  
	        int drawCount=count;
	      //头像的宽度
	        float x = contactIcon.getWidth();
	        //文字所在位置
	        float textSite=x-20;
	        //浮标基本长度
	        int length=28;
	        //根据数字位数动态扩展浮标宽度
	        while(count>=10){
	        	length=length+8;
	        	if(count!=0)count/=10;
	        	textSite=textSite-10;
	        }
	        
	        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
	    //画最外层黑边    
	        RectF outerRect = new RectF(x-length, 0,x,28);
	        paint.setARGB(255, 204, 204, 204);
	        canvas.drawRoundRect(outerRect, 10f, 10f, paint); 
	     //画灰白色圆环   
	        outerRect=new RectF(x-length+0.5f,0.5f,x-0.5f,27.5f);
	        paint.setColor(Color.WHITE);
	        canvas.drawRoundRect(outerRect, 10f, 10f, paint); 
	     //里层红色底   
	        RectF innerRect = new RectF(x-length+3, 3, x-3, 25); 
	        paint.setColor(Color.RED); 
	        canvas.drawRoundRect(innerRect,10f,10f,paint);
	      //启用抗锯齿和使用设备的文本字距  
	        Paint countPaint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DEV_KERN_TEXT_FLAG);  
	        countPaint.setColor(Color.WHITE);  
	        countPaint.setTextSize(20f);  
	        countPaint.setTypeface(Typeface.DEFAULT_BOLD);  
	        canvas.drawText(String.valueOf(drawCount), textSite, 20, countPaint);  
	        
	        return contactIcon;  
	    } 
	    
	    /** 注册后台推送服务 */
		public static void registerDevide(Context context,String appIdentifier, String devideId,
				String deviceName, String os, String osVersion) {
			HttpRequestAsynTask task = new HttpRequestAsynTask(context) {

				@Override
				protected void doPostExecute(String result) {
					super.doPostExecute(result);
					if (result != null) {
						try {
							JSONObject jb = new JSONObject(result);
							if ("success".equals(jb.getString("status"))) {

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}

			};
			task.setShowProgressDialog(false);
			task.setNeedProgressDialog(false);
			StringBuilder sb = new StringBuilder();
			sb = sb.append("POST:appIdentifier=").append(appIdentifier)
					.append(";deviceId=").append(devideId).append(";deviceName=")
					.append(deviceName).append(";os=").append(os)
					.append(";osVersion=").append(osVersion);
			String s = sb.toString();
			String url = URL.BASE_WEB + "devices/register";
			task.execute(url, s, HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);

		}
	 
	    public static void drawCountWithImg(Context context,LinearLayout layout,int count){ 
	    	layout.removeAllViews();
	    	View v = LayoutInflater.from(context).inflate(R.layout.count, null);
			
			if(count==0){
				v.setVisibility(View.GONE);
			}else{
				ImageView countView= (ImageView) v.findViewById(R.id.count_img);
				TextView text = (TextView) v.findViewById(R.id.count_text);
				text.setText(String.valueOf(count));
				int tmpCount=1;
				while(count>=10){
					count=count/10;
					tmpCount++;
				}
				switch (tmpCount) {
				case 1:
					countView.setBackgroundResource(R.drawable.push_count_1);
					break;
				case 2:
					countView.setBackgroundResource(R.drawable.push_count_10);
					break;
				case 3:
					countView.setBackgroundResource(R.drawable.push_count_100);
					break;
				default:
					break;
				}
				layout.addView(v);
			}
	    }
}
