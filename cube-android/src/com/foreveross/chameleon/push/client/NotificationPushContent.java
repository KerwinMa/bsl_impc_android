package com.foreveross.chameleon.push.client;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.foreveross.chameleon.push.cubeparser.type.CommonModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.MDMMessage;
import com.foreveross.chameleon.push.cubeparser.type.ModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.SystemMessage;
import com.foreveross.chameleon.push.mina.library.Constants;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.MapEntity;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.MessageContent;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.foreveross.chameleon.util.Preferences;

public class NotificationPushContent {
	/**
	 * title:"", content:"", messageType:1, sendId:"", sendTime:"xx", extras:{
	 * moduleIdentifer:"", //模块的identifer，例如，公告模块为com.foss.announcement ...
	 * //根据不同模块自身需求，定义自已需要的键值，例如，公告模块定义announceId表示公告ID announceId:""
	 * //公告模块的公告ID }
	 * */
	 static String getMessageUrl;
	 
	 public static List<Delayed> parseRemoteModel(final Message message,final 	Context context) throws SQLException {
		final List<Delayed> l = new ArrayList<Delayed>();
		
			String deviceId = DeviceInfoUtil.getDeviceId(context);
			String appId = URL.getAppKey();
			String tokenId = Preferences.getToken(Application.sharePref);

					String sendid = "";
					HttpResponse response = null;
					try {
						
						tokenId = URLEncoder.encode(tokenId, "Utf-8");
//						getMessageUrl = URL.GETPUSHMESSAGE+tokenId+"/"+deviceId+"/"+appId;
						getMessageUrl = URL.GETPUSHMESSAGE+deviceId+"/"+appId;
//						getMessageUrl = URL.PUSH_BASE_URL+"receipts/none-receipts/" + tokenId + "/" + deviceId + "/"+ appId;
						HttpGet getMethod = new HttpGet(getMessageUrl);
						HttpClient httpClient = new DefaultHttpClient();
						response = httpClient.execute(getMethod);
						System.out.println("拉取推送信息的URL === "+getMethod.getURI());
					} catch (ClientProtocolException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();	
					} // 发起GET请求
					Log.v("AnnounceMent", "resCode = "+ response.getStatusLine().getStatusCode()); // 获取响应码
					String result = null;
					try {
						result = EntityUtils.toString(response.getEntity(),"utf-8");
						System.out.println("拉取信息为 result== "+result);
						if(result == null||result.equals("[]")) {
							return null;
						}
						String userName = Preferences.getUserName(Application.sharePref);
						try
						{
							JSONArray jsonArray = new JSONArray(result);
							for (int j = 0; j < jsonArray.length(); j++)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(j);
								
								String title = jsonObject.getString("title");
								String content = jsonObject.getString("content");
								String messageType = jsonObject.getString("messageType");
//								String messsageId = jsonObject.getString("sendId");
								
								if(jsonObject.isNull("id"))continue;
								String messsageId = jsonObject.getString("id");
	//							String sendTime =jsonObject.getString ("sendTime")
								long sendTime = System.currentTimeMillis();
								if(messsageId!=null&&!messsageId.equals("")) {
									sendid += messsageId+",";
								}
								;
//								if(String.valueOf(StaticReference.userMf.queryBuilder(NoticeModuleMessage.class).where().eq("messsageId",messsageId).countOf())!=null) {
//									return null;
//								}
								ChanmeleonMessage messageDelay = null;
								if (Constants.MESSAGE_TYPE_SYSTEM.equals(messageType)) {
									messageDelay = new ChanmeleonMessage(new SystemMessage(sendTime,
											messsageId, title, content),userName);
								} else if (Constants.MESSAGE_TYPE_MODULE.equals(messageType)) {
									
									JSONObject jsonObjects = new JSONObject(jsonObject.getString("extras"));
									// 模块类型消息
									String moduleIdentifer =jsonObjects.getString("moduleIdentifer");
									String moduleName = null;
									if(!moduleIdentifer.equals("com.foss.announcement")) {
										if(!jsonObjects.isNull("moduleName")){
											moduleName= jsonObjects.getString("moduleName");
										}
									}
									
									boolean moduleBadgeBool = true;
									boolean busiDetailBool = true;
									
									try {
										String moduleBadge =jsonObjects.getString("moduleBadge") ;
										
										moduleBadgeBool = moduleBadge == null ? false : Boolean
												.valueOf(moduleBadge);
										
										String busiDetail = jsonObjects.getString("busiDetail");
										busiDetailBool = busiDetail == null ? false : Boolean
												.valueOf(busiDetail);
										
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									CubeModule cubeModule = CubeModuleManager.getInstance()
											.getCubeModuleByIdentifier(moduleIdentifer);
									
									
									if (cubeModule != null) {
										cubeModule.setDisplayBadge(moduleBadgeBool);
									}
									if (moduleIdentifer != null	&& moduleIdentifer.equals("com.foss.announcement")) {
										// 公告消息
										String noticeId = jsonObjects.getString("announceId");
										NoticeModuleMessage noticeModuleMessage = new NoticeModuleMessage(
												sendTime, messsageId, title, content);
										noticeModuleMessage.setIdentifier(moduleIdentifer);
										noticeModuleMessage.setNoticeId(noticeId);
										if(jsonObjects.has("noticeFiles"))
										{
											noticeModuleMessage.setAttachment(jsonObjects.getString("noticeFiles"));
										}
										messageDelay = new ChanmeleonMessage(noticeModuleMessage , userName);
									} else if(cubeModule !=null&&cubeModule.getModuleType()!=CubeModule.UNINSTALL){
										// 模块消息
										CommonModuleMessage commonModuleMessage = new CommonModuleMessage(
												sendTime, messsageId, title, content);
										commonModuleMessage.setIdentifier(moduleIdentifer);
										commonModuleMessage
										.setGroupBelong(moduleName == null ? moduleIdentifer
												: moduleName);
										messageDelay = new ChanmeleonMessage(commonModuleMessage,userName);
									}
									ModuleMessage.class.cast(messageDelay.getPackedMessage())
									.setLinkable(busiDetailBool);
									
								}
								
								else if (Constants.MESSAGE_TYPE_MDM.equals(messageType)) {
									// 设备消息
									JSONObject jsonObjectMdm = new JSONObject(jsonObject.getString("extras"));
									messageDelay = new ChanmeleonMessage(new MDMMessage(sendTime,
											messsageId, title, content, jsonObjectMdm.getString("mdm")),userName);
								}
								
								if (messageDelay != null) {
									l.add(messageDelay);
								}
							}
							
							///		do something recode;
							receiptsMessage(context,sendid);
							sendid = "";
							
						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}
						
					} catch (ParseException e2) {
						e2.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
		return l;
		
		
		
		
	}
	 //信息回执
	public static void receiptsMessage(Context context, String t) {
		System.out.println("回执中。。。");
		HttpClient httpClient = new DefaultHttpClient();

		HttpPut httpPut = new HttpPut(URL.FEEDBACK_URL);
		try {
			// httpPost.addHeader("Accept", "application/json");
			httpPut.addHeader("Content-Type",
					"application/x-www-form-urlencoded");
			// FeedbackVo feedbackVo = new FeedbackVo();
			// feedbackVo.setDeviceId(DeviceInfoUtil.getDeviceId(context));
			// feedbackVo.setSendId(t.getId());
			HttpEntity httpEntity = null;
			// httpEntity = new
			// StringEntity(gson.toJson(feedbackVo),"utf-8");
			String appKey = Application.class.cast(context.getApplicationContext())
								.getCubeApplication().getAppKey();
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("deviceId", DeviceInfoUtil.getDeviceId(context)));
//			list.add(new BasicNameValuePair("sendId", t));
			list.add(new BasicNameValuePair("msgId", t));
			list.add(new BasicNameValuePair("appId", appKey));
			httpEntity = new UrlEncodedFormEntity(list);
			httpPut.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPut);
			System.out.println("回执url:"+httpPut.getURI());
	
			
			if (httpResponse.getStatusLine().getStatusCode()<299&&httpResponse.getStatusLine().getStatusCode()>=200) {
				System.out.println("回执状态返回码："+httpResponse.getStatusLine().getStatusCode()+"推送回执成功，sendid =="+t);
			}else {
				System.out.println("回执状态返回码："+httpResponse.getStatusLine().getStatusCode()+"推送回执失败，sendid =="+t);
			}
		} catch (ClientProtocolException e) {
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		} catch (IOException e) {
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		}
	}

	public static <T> T getMapEntityValue(MessageContent messageContent,
			String key, Class<T> zz) {
		List<MapEntity> mapEntitys = messageContent.getExtrasList();
		for (MapEntity map : mapEntitys) {
			if (map.getKey().equals(key)) {
				return zz.cast(map.getValue());
			}
		}
		return null;
	}

}
