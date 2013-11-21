package com.foreveross.chameleon.phone.mdm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.foreveross.chameleon.push.tmp.MessageListener;

public class EncryptionMessageReceiver extends CommonMDMReceiver implements
		MessageListener {

	@Override
	public void onActualReceive(Context context, Intent intent) {
		ComponentName componentName=new ComponentName("com.foreveross.cube","MobileMainActivity");
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_ENCRYPTION);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/ENCRYPTION
		messageType = messageType.toUpperCase();
		if (messageType.contains("ENCRYPTION")) {
			return true;
		} else {
			return false;
		}
	}

}
