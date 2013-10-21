/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.push.mina.library.api.ConnectivityReceiver;
import com.foreveross.chameleon.push.mina.library.api.MinaMobileClient;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [mina_android_lib, 2013-7-8]
 */
public class MinaPushService extends Service {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param intent
	 *            2013-8-5 上午10:39:39
	 */
	private MinaMobileClient minaMobileClient = null;
	private final static Logger log = LoggerFactory.getLogger(MinaPushService.class);
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-8-5 上午11:18:51
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		log.debug("onCreate()...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				minaMobileClient = new MinaMobileClient(MinaPushService.this);
				minaMobileClient.prepairReqConnect();
				log.debug("minaMobileClient prepairReqConnect...");
				minaMobileClient.start();
				log.debug("minaMobileClient start...");
				registerConnectivityReceiver();
			}
		}).start();

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param arg0
	 * @return 2013-7-8 下午4:06:33
	 */
	@Override
	public IBinder onBind(Intent intent) {
		log.debug("onBind()...");
		return new MinaPushServiceBinder();
	}

	public class MinaPushServiceBinder extends Binder {
		public MinaPushService getService() {
			return MinaPushService.this;
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param intent
	 *            2013-7-9 下午3:09:11
	 */

	public boolean isOnline() {
		return minaMobileClient.online();
	}

	public void runOnUi(Runnable runnable) {
		Application.class.cast(this.getApplication()).getUIHandler()
				.post(runnable);
	}

	public boolean isConnected(){
		return minaMobileClient.isConnected();
	}
	public void disConnected(){
		minaMobileClient.safeClose();
	}
	public void reconnect(){
		minaMobileClient.reConnect();
	}
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-8-5 上午11:19:25
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		log.debug("onDestroy()...");
		minaMobileClient.safeClose();
		log.debug("safeClose()...");
		unregisterConnectivityReceiver();
	}

	private ConnectivityReceiver connectivityReceiver;

	private void registerConnectivityReceiver() {
		log.debug("registerConnectivityReceiver()...");
		connectivityReceiver = new ConnectivityReceiver(minaMobileClient);
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		log.debug("unregisterConnectivityReceiver()...");
		unregisterReceiver(connectivityReceiver);
	}
}
