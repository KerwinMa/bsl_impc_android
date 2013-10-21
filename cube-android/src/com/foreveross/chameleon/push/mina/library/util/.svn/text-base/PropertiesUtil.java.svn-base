/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.util;

import java.util.Properties;

import android.content.Context;
import android.util.Log;

/**
 * [读取属性文件]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */
public class PropertiesUtil {
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 */
	private PropertiesUtil() {

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 */
	private Properties props = null;

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param filePath
	 * @return 2013-6-21 下午2:54:48
	 */
	public static PropertiesUtil readProperties(Context context,
			int rawResourceId) {

		PropertiesUtil propertiesUtil = new PropertiesUtil();
		propertiesUtil.props = new Properties();
		try {

			propertiesUtil.props.load(context.getResources().openRawResource(
					rawResourceId));
		} catch (Exception e) {
			Log.e("PropertiesUtil", "Could not find the properties file.", e);
		}
		return propertiesUtil;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param key
	 * @param def
	 * @return 2013-6-21 下午2:54:55
	 */
	public String getString(String key, String def) {
		String value = props.getProperty(key);
		return value == null ? def : value;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param key
	 * @param def
	 * @return 2013-6-21 下午2:54:57
	 */
	public Boolean getBoolean(String key, Boolean def) {
		String value = props.getProperty(key);
		return value == null ? def : Boolean.valueOf(value);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param key
	 * @param def
	 * @return 2013-6-21 下午2:55:00
	 */
	public Byte getByte(String key, Byte def) {
		String value = props.getProperty(key);
		return value == null ? def : Byte.valueOf(value);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param key
	 * @param def
	 * @return 2013-6-21 下午2:55:03
	 */
	public Short getShort(String key, Short def) {
		String value = props.getProperty(key);
		return value == null ? def : Short.valueOf(value);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param key
	 * @param def
	 * @return 2013-6-21 下午2:55:06
	 */
	public Integer getInteger(String key, Integer def) {
		String value = props.getProperty(key);
		return value == null ? def : Integer.valueOf(value);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param key
	 * @param def
	 * @return 2013-6-21 下午2:55:08
	 */
	public Long getLong(String key, Long def) {
		String value = props.getProperty(key);
		return value == null ? def : Long.valueOf(value);
	}

	public boolean containsValue(String key) {
		return props.containsKey(key);
	}

}
