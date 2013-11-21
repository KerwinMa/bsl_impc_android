package com.foreveross.chameleon.phone.mdm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.foreveross.chameleon.push.tmp.MessageListener;

@SuppressLint("NewApi")
public class StorageSecurityReceiver extends CommonMDMReceiver implements
		MessageListener {

//	@SuppressLint("NewApi") 
	@Override
	public void onActualReceive(Context context, Intent intent) {

//		String path=Environment.getExternalStorageDirectory().getPath()+"/testmd5";
		Log.v("StorageSecurityStatus", String.valueOf(mDPM.getStorageEncryptionStatus()));
		command = intent.getStringExtra("command");
		Log.i("Amberlo", "StorageSecurityReceiver command " + command);
		if (command.toUpperCase().endsWith("ON")) {
			mDPM.setStorageEncryption(mDeviceAdminSample, true);
			Log.v("StorageSecurity", "StorageSecurity：on");
		}
		if (command.toUpperCase().endsWith("OFF")) {
			mDPM.setStorageEncryption(mDeviceAdminSample, false);
			Log.v("StorageSecurity", "StorageSecurity：off");
		}
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_STORAGE_SECURITY);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/CAMERA
		messageType = messageType.toUpperCase();
		if (messageType.contains("MDM/STORAGE_SECURITY")) {
			return true;
		} else {
			return false;
		}
	}

}
