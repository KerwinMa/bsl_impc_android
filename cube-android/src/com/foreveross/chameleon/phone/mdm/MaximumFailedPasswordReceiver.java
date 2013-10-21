package com.foreveross.chameleon.phone.mdm;

import android.content.Context;
import android.content.Intent;

public class MaximumFailedPasswordReceiver extends CommonMDMReceiver {

	@Override
	public void onActualReceive(Context context, Intent intent) {
		mDPM.setMaximumFailedPasswordsForWipe(mDeviceAdminSample, 5);
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_MAXXIMUMFAILEDPASW);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/LOCK_WIPE
		messageType = messageType.toUpperCase();
		if (messageType.contains("sdas")) {
			return true;
		} else {
			return false;
		}
	}

}
