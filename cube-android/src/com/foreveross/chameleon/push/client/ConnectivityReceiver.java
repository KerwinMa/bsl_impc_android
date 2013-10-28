package com.foreveross.chameleon.push.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.phone.modules.task.ThreadPlatformUtils;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-8]
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private final static Logger log = LoggerFactory
			.getLogger(ConnectivityReceiver.class);

	private NotificationService notificationService;

	public ConnectivityReceiver(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		log.debug("xmpp ConnectivityReceiver.onReceive()...");

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		Application application = (Application) context.getApplicationContext();

		if (networkInfo != null && networkInfo.isConnected()
				&& application.isHasLogined() && notificationService != null) {
			if (!notificationService.isConnected()) {
				log.info("Network connected,begin reconnect to xmpp");
				notificationService.reconnect();
			} else {
				log.info("Network unavailable,begin to close xmpp");
				notificationService.virtualDisconnect();
			}
		} else {

			if (notificationService != null) {
				log.info("Network unavailable,begin to close xmpp");
				notificationService.virtualDisconnect();
			}
			ThreadPlatformUtils.finishAllDownloadTask();
		}
	}

}
