package com.foreveross.chameleon.push.tmp;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Message {

	public String message;
	public String content;
	public Context context;
	public static Set<MessageListener> listeners = new HashSet<MessageListener>();

	public Message(String message, Context context,String content) {
		this.message = message;
		this.context = context;
		this.content=content;
	}

	public static void addListener(MessageListener listener) {
		Log.d("cube", "add push message listener:" + listener);
		listeners.add(listener);
	}

	public static void removeListener(MessageListener listener) {
		listeners.remove(listener);
	}

	public void broadcast() {
		for (MessageListener listener : listeners) {
			Intent intent = listener.convertMessage2Intent(this);
			if (intent != null) {
				intent.putExtra("content", content);
				Log.d("cube", "broadcast intent:" + intent);
				context.sendBroadcast(intent);
			}
		}
	}

	public String getCommand() {
		// 从message中解析出command
		return message;
	}

	public String getContent() {
		// 从message中解析出content
		return content;
	}
}
