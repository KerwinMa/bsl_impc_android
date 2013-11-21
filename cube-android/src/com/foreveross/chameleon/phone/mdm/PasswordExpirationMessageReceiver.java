package com.foreveross.chameleon.phone.mdm;

import android.content.Context;
import android.content.Intent;

import com.foreveross.chameleon.push.tmp.MessageListener;

public class PasswordExpirationMessageReceiver extends CommonMDMReceiver
		implements MessageListener {

	@Override
	public void onActualReceive(Context context, Intent intent) {
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_PASSWORD_EXPIRATION);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/PASSWORD_EXPIRATION
		messageType = messageType.toUpperCase();
		if (messageType.contains("MDM/STORAGE_SECURITY/ON")) {
			return true;
		} else {
			return false;
		}
	}
}
