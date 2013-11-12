/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.mina.core.session.IoSession;

import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.MessageContent;
import com.foreveross.chameleon.push.mina.library.util.ThreadPool;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * [推送消息处理器（不包括目的地址）]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-6-24]
 */
public class MessageContentHandler extends
		AbstractCommandHandler<MessageContent> {

	private Context context;

	public MessageContentHandler(Context context) {
		this.context = context;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-6-24 下午4:15:42
	 */

	private AtomicInteger atomicInteger = new AtomicInteger(1);

	@Override
	protected void processCmd(final IoSession session, final MessageContent t) {
//		MinaPushService.class.cast(context).runOnUi(new Runnable() {
//
//			@Override
//			public void run() {
//				Toast.makeText(context, t.getTitle() + "--" + t.getContent(),
//						Toast.LENGTH_SHORT).show();
//			}
//		});
		
		
		try {
			//doFeedback(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//发送到事件总线
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(t);
		Log.i("MessageContentHandler", "Message content count is "
				+ atomicInteger.getAndIncrement());
	}

	@Override
	protected MessageContent newInstance(byte[] protoBytes) {
		try {
			return MessageContent.parseFrom(protoBytes);
		} catch (InvalidProtocolBufferException e) {
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		}
		return null;
	}

//	public void doFeedback(final MessageContent t) {
//		ThreadPool.run(new Runnable() {
//			@Override
//			public void run() {
//				HttpClient httpClient = new DefaultHttpClient();
//
//				HttpPut httpPut = new HttpPut();
//				try {
//					// httpPost.addHeader("Accept", "application/json");
//					httpPut.addHeader("Content-Type",
//							"application/x-www-form-urlencoded");
//					// FeedbackVo feedbackVo = new FeedbackVo();
//					// feedbackVo.setDeviceId(DeviceInfoUtil.getDeviceId(context));
//					// feedbackVo.setSendId(t.getId());
//					HttpEntity httpEntity = null;
//					// httpEntity = new
//					// StringEntity(gson.toJson(feedbackVo),"utf-8");
//					String appKey = Application.class.cast(context.getApplicationContext())
//										.getCubeApplication().getAppKey();
//					List<NameValuePair> list = new ArrayList<NameValuePair>();
//					list.add(new BasicNameValuePair("deviceId", DeviceInfoUtil
//							.getDeviceId(context)));
//					list.add(new BasicNameValuePair("sendId", t.getId()));
//					list.add(new BasicNameValuePair("appId", appKey));
//					httpEntity = new UrlEncodedFormEntity(list);
//					httpPut.setEntity(httpEntity);
//					HttpResponse httpResponse = httpClient.execute(httpPut);
//					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//						Log.d("ApplicationEx", "注册成功");
//					}
//				} catch (ClientProtocolException e) {
//					Log.e("MessageContentHandler", "MessageContentHandler", e);
//				} catch (IOException e) {
//					Log.e("MessageContentHandler", "MessageContentHandler", e);
//				}
//			}
//		});
//
//	}

}
