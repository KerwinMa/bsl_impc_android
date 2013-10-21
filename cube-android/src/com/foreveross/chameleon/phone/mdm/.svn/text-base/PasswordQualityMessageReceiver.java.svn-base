package com.foreveross.chameleon.phone.mdm;

import android.content.Context;
import android.content.Intent;

import com.foreveross.chameleon.push.tmp.MessageListener;

public class PasswordQualityMessageReceiver extends CommonMDMReceiver implements
		MessageListener {

	@Override
	public void onActualReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_PASSWORD_QUALITY);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/PASSWORD_QUALITY
		messageType = messageType.toUpperCase();
		if (messageType.contains("PASSWORD_QUALITY")) {
			return true;
		} else {
			return false;
		}
	}
}
