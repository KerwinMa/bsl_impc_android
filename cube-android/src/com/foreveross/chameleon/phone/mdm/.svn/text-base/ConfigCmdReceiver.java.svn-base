package com.foreveross.chameleon.phone.mdm;

import android.content.Context;
import android.content.Intent;

public class ConfigCmdReceiver extends CommonMDMReceiver {

	@Override
	public void onActualReceive(Context context, Intent intent) {
		String content = (String) intent.getExtras().get("content");
//		DeviceInfo di = ParserConfig.parser(content);
//		int Quality=0;
//		mDPM.setMaximumFailedPasswordsForWipe(mDeviceAdminSample,
//				di.maximumFailedPasswordsForWipe);
//		mDPM.setPasswordMinimumLength(mDeviceAdminSample, di.passwordMinimumLength);
//		if(di.passwordQuality==0){
//			Quality=mDPM.PASSWORD_QUALITY_NUMERIC;//数字
//		}else if(di.passwordQuality==1){
//			Quality=mDPM.PASSWORD_QUALITY_ALPHABETIC;//字符
//		}
//		else if(di.passwordQuality==2){
//			Quality=mDPM.PASSWORD_QUALITY_ALPHANUMERIC;//数字+字母
//		}
//		mDPM.setPasswordQuality(mDeviceAdminSample, Quality);
//		mDPM.getMaximumTimeToLock(mDeviceAdminSample);
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
		if (messageType.contains("MDM/CONFIG")) {
			return true;
		} else {
			return false;
		}
	}

}