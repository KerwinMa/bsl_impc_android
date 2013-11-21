package com.foreveross.chameleon.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Description: 这是一个保存共享的数据工具类
 * @author <a href="mailto:turlet@163.com">turlet@163.com</a>
 * @date 2012-2-16 上午11:46:33
 * @version V1.0
 */
public class SharedPreferencesUtil {
	/**
	 * SharedPreferences文件名
	 */
	private static final String PREFERENCE_NAME = "client_preferences";
	
	public static final String ANNOUNCE_COUNT = "annouce_count";
	
	public static final String MESSAGE_COUNT="message_count";

	/**
	 * 主动预警消息提醒
	 */
	public static final String WARNIG_NOTICE = "warnig_notice";
	
	/**
	 * 即时通讯消息提醒
	 */
	public static final String INSTANT_NOTICE = "instant_notice";
	
	public static final String PUSHABLE = "PUSHABLE";
	public static final String ENABLE_XMPP_SERVICE = "ENABLE_XMPP_SERVICE";
	public static final String ENABLE_MINA_SERVICE = "ENABLE_MINA_SERVICE";
	
	public static final String JID = "jid";
	
	private SharedPreferences mSharedPreferences;

	private static SharedPreferencesUtil instance;

	private SharedPreferencesUtil(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public synchronized static SharedPreferencesUtil getInstance(Context context) {
		if (instance == null) {
			instance = new SharedPreferencesUtil(context);
		}
		return instance;
	}

	/**
	 * 保存字符串
	 * @param key
	 * @param value
	 */
	public void saveString(String key, String value) {
		mSharedPreferences.edit().putString(key, value).commit();
	}

	/**
	 * 获取字符串
	 * @param key
	 * @param defValue
	 * @return bool
	 */
	public String getString(String key, String... defValue) {
		if (defValue.length > 0){
			return mSharedPreferences.getString(key, defValue[0]);
		}else{
			return mSharedPreferences.getString(key, "");
		}
	}

	/**
	 * 保存布尔值
	 * @param key
	 * @param value
	 */
	public void saveBoolean(String key, Boolean value) {
		mSharedPreferences.edit().putBoolean(key, value).commit();
	}

	/**
	 * 获取布尔值
	 * @param key
	 * @param defValue
	 * @return bool
	 */
	public Boolean getBoolean(String key, Boolean... defValue) {
		if (defValue.length > 0){
			return mSharedPreferences.getBoolean(key, defValue[0]);
		}else{
			return mSharedPreferences.getBoolean(key, false);
		}
	}

	/**
	 * 保存整形
	 * @param key
	 * @param value
	 */
	public void saveInteger(String key, Integer value) {
		mSharedPreferences.edit().putInt(key, value).commit();
	}

	/**
	 * 获取整形
	 * @param key
	 * @param defValue
	 * @return bool
	 */
	public Integer getInteger(String key, Integer... defValue) {
		if (defValue.length > 0){
			return mSharedPreferences.getInt(key, defValue[0]);
		}else{
			return mSharedPreferences.getInt(key, 0);
		}
	}

}
