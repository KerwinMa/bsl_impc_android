package com.foreveross.chameleon.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {
	private static final String PREFERENCES_NAME = "System";
	private static final String KEY_RECEIVE = "_receive";
	private static final String KEY_VOICE = "_voice";
	private static final String KEY_TIME = "_time";
	private static final String KEY_INSTRUCTION = "_instruction";
	public static final String VALUE_INSTRUCTION_READ = "1";
	public static final String VALUE_INSTRUCTION_NOREAD = "0";

	public static String getReceive(Context context) {
		String receive = getValue(context, KEY_RECEIVE);
		return receive;
	}

	public static void setReceive(Context context, String receive) {
		setValue(context, KEY_RECEIVE, receive);
	}

	public static String getVoice(Context context) {
		String name = getValue(context, KEY_VOICE);
		return name;
	}

	public static void setVoice(Context context, String token) {
		setValue(context, KEY_VOICE, token);
	}

	public static String getTimes(Context context) {
		String name = getValue(context, KEY_TIME);
		return name;
	}

	public static void setTimes(Context context, String times) {
		setValue(context, KEY_TIME, times);
	}

	public static String getInstruction(Context context) {
		String name = getValue(context, KEY_INSTRUCTION);
		return name;
	}

	public static void setInstruction(Context context, String instruction) {
		setValue(context, KEY_INSTRUCTION, instruction);
	}

	public static String getValue(Context context, String key) {
		return context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_WORLD_READABLE).getString(key, null);
	}

	public static int getIntValue(Context context, String key) {
		return getIntValue(context, key, -1);
	}

	public static int getIntValue(Context context, String key, int defaultValue) {
		return context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_WORLD_READABLE).getInt(key, defaultValue);
	}

	public static void setValue(Context context, String key, String value) {
		final SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void removeValue(Context context, String key) {
		final SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}

	public static void setValue(Context context, String key, int value) {
		final SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void clear(Context context) {
		final SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	public static void setBoolean(Context context, String key, boolean value) {
		final SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_WORLD_WRITEABLE);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static boolean getBoolean(Context context, String key, boolean defaultValue) {
		return context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_WORLD_READABLE).getBoolean(key,defaultValue);
	}
}
