/**
 * 
 */
package com.foreveross.chameleon.push.cubeparser.type;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.util.Preferences;
import com.foreveross.chameleon.util.PushUtil;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * [模块消息－公告]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-22]
 */
@DatabaseTable(tableName = "NoticeModuleMessageStub")
public class NoticeModuleMessageStub extends ModuleMessage<NoticeModuleMessageStub> {

	private final static Logger log = LoggerFactory
			.getLogger(NoticeModuleMessageStub.class);
	@DatabaseField
	private String noticeId;
	@DatabaseField
	private String attachment;
	
	public NoticeModuleMessageStub() {

	}

	/**
	 * @param sendTime
	 * @param messsageId
	 * @param title
	 * @param content
	 */
	public NoticeModuleMessageStub(long sendTime, String messsageId, String title,
			String content) {
		super(sendTime, messsageId, title, content);
		this.groupBelong = "公告";
	}

	public String getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(String noticeId) {
		this.noticeId = noticeId;
	}

	
	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public Future<Boolean> sync() {

		return PushUtil.getPool().submit(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				HttpGet getMethod = new HttpGet(URL.ANNOUNCE.concat(noticeId));
				getMethod.getParams().setParameter("sessionKey",
						Preferences.getSESSION(Application.sharePref));
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = null;
				boolean success = true;
				try {
					response = httpClient.execute(getMethod);
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String result = null;
						try {
							result = EntityUtils.toString(response.getEntity(),
									"utf-8");
							JSONObject jb = null;
							try {
								jb = new JSONObject(result);
								setContent(jb.getString("content"));
							} catch (JSONException ex) {
								success = false;
								log.error("JSONException", ex);
							}
						} catch (ParseException ex) {
							success = false;
							log.error("ParseException", ex);
						} catch (IOException ex) {
							success = false;
							log.error("IOException", ex);
						}
					}
				} catch (ClientProtocolException e) {
					success = false;
					log.error("ClientProtocolException", e);
				} catch (IOException e) {
					success = false;
					log.error("IOException", e);
				}
				return success;
			}
		});

	}
}
