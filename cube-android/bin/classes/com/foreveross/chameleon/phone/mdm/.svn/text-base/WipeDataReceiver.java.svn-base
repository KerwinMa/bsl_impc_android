package com.foreveross.chameleon.phone.mdm;

import android.content.Context;
import android.content.Intent;

public class WipeDataReceiver extends CommonMDMReceiver {

	@Override
	public void onActualReceive(Context context, Intent intent) {
		mDPM.wipeData(0);
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_WIPEDATA);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/LOCK_WIPE
		messageType = messageType.toUpperCase();
		if (messageType.contains("MDM/RESET")) {
			return true;
		} else {
			return false;
		}
	}

}
