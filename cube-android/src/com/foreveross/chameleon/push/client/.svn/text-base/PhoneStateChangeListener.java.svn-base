package com.foreveross.chameleon.push.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.Application;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-9]
 */
public class PhoneStateChangeListener extends PhoneStateListener {

	private final static Logger log = LoggerFactory
			.getLogger(PhoneStateChangeListener.class);

	private  NotificationService notificationService = null;

	public PhoneStateChangeListener(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onDataConnectionStateChanged(int state) {
		super.onDataConnectionStateChanged(state);
		log.debug("Data Connection State = " + getState(state));
		boolean hasLogined=Application.class.cast(notificationService.getApplication()).isHasLogined();
		if (state == TelephonyManager.DATA_CONNECTED&&hasLogined) {
			log.debug("reconnect notification service...");
			notificationService.reconnect();
		} else {
			log.debug("disconnect notification service...");
			notificationService.virtualDisconnect();
		}
	}

	private String getState(int state) {
		switch (state) {
		case 0: // '\0'
			return "DATA_DISCONNECTED";
		case 1: // '\001'
			return "DATA_CONNECTING";
		case 2: // '\002'
			return "DATA_CONNECTED";
		case 3: // '\003'
			return "DATA_SUSPENDED";
		}
		return "DATA_<UNKNOWN>";
	}

}
