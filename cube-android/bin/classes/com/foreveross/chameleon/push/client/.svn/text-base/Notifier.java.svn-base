/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreveross.chameleon.push.client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-9]
 */
public class Notifier {

	public Notifier(Context context) {
	}

	public static void cancel(Context mContext, int notifyId) {
		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(notifyId);
	}
	
	
	
	@SuppressWarnings("deprecation")
	public static void notifyInfo(final Context mContext, int icon,
			int notifyId, String title, String content, Intent clickIntent) {

		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		clickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		clickIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		clickIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		clickIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				clickIntent, 0);
		Notification notification = new Notification();
		notification.icon = icon;

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.when = System.currentTimeMillis();
		notification.tickerText = title==null?"":title;
		notification.defaults = Notification.DEFAULT_SOUND;
		notification
				.setLatestEventInfo(mContext, title, content, contentIntent);
		notificationManager.notify(notifyId, notification);

	}
}
