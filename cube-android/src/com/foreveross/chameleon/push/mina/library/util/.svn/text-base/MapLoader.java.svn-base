/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.push.mina.library.handler.AbstractCommandHandler;

/**
 * [消息处理映射器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */
public class MapLoader {

	public static String handlerPropsPath = null;
	/**
	 * key:protobuf类型名称<BR>
	 * value:对应处理器
	 */

	private static Map<String, AbstractCommandHandler<?>> map = new HashMap<String, AbstractCommandHandler<?>>();

	public static void loadMap(Context context, int resourceId) {
		Properties properties = new Properties();
		try {
			map.clear();
			properties.load(context.getResources().openRawResource(resourceId));
			Set<String> handlerNames = properties.stringPropertyNames();
			for (String handlerName : handlerNames) {
				try {
					Class<?> clazz = Class.forName(properties
							.getProperty(handlerName));
					Constructor<?> constructor = null;
					try {
						constructor = clazz.getConstructor(Context.class);
						map.put(handlerName,
								(AbstractCommandHandler<?>) constructor
										.newInstance(context));
						continue;
					} catch (NoSuchMethodException e) {
						map.put(handlerName,
								(AbstractCommandHandler<?>) clazz.newInstance());
					} catch (IllegalArgumentException e) {
						Log.e("MapLoader", "MapLoader", e);
					} catch (InvocationTargetException e) {
						Log.e("MapLoader", "MapLoader", e);
					}

					
				} catch (ClassNotFoundException e) {
					Log.e("MapLoader", "MapLoader", e);
				} catch (InstantiationException e) {
					Log.e("MapLoader", "MapLoader", e);
				} catch (IllegalAccessException e) {
					Log.e("MapLoader", "MapLoader", e);
				}
			}
		} catch (IOException e) {
			Log.e("MapLoader", "MapLoader", e);
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-6-21 下午1:06:36
	 */
	public static AbstractCommandHandler<?> getAbstractCommandHandler(
			String name) {
		return map.get(name);
	}

}
