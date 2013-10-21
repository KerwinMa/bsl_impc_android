package com.foreveross.chameleon.phone.mdm;

import android.content.Context;
import android.content.Intent;

public class LockWipeMessageReceiver extends CommonMDMReceiver {

	@Override
	public void onActualReceive(Context context, Intent intent) {
		// "锁屏。。command=" + command + " isActive:" + active,
		// Toast.LENGTH_LONG).show();
		if (active) {
			mDPM.lockNow();
		}

	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_LOCK_WIPE);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/LOCK_WIPE
		messageType = messageType.toUpperCase();
		if (messageType.contains("MDM/LOCK_WIPE")) {
			return true;
		} else {
			return false;
		}
	}

}
