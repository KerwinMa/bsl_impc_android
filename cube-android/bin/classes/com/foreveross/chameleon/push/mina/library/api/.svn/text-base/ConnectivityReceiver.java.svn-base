package com.foreveross.chameleon.push.mina.library.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * [连接状态广播]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [mina_android_lib, 2013-7-8]
 */
public class ConnectivityReceiver extends BroadcastReceiver {
	private final static Logger log = LoggerFactory
			.getLogger(ConnectivityReceiver.class);

	private MinaMobileClient minaMobileClient = null;

	public ConnectivityReceiver(MinaMobileClient minaMobileClient) {
		this.minaMobileClient = minaMobileClient;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		log.debug("mina ConnectivityReceiver.onReceive()...");
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			if (networkInfo.isConnected()) {
				log.info("Network is  connecting ,and then try reconnect....");
				minaMobileClient.reConnect();
			}else{
				log.debug("Network unavailable,begin close mobile client...");
				minaMobileClient.safeClose();
				log.debug("mobile client has been closed");
			}
		} else {
			log.debug("Network unavailable,begin close mobile client...");
			minaMobileClient.safeClose();
			log.debug("mobile client has been closed");
		}
	}

}
