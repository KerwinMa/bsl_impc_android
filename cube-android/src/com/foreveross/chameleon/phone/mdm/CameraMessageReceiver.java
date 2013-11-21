package com.foreveross.chameleon.phone.mdm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.foreveross.chameleon.push.tmp.MessageListener;

public class CameraMessageReceiver extends CommonMDMReceiver implements
		MessageListener {

	@SuppressLint("NewApi") 
	@Override
	public void onActualReceive(Context context, Intent intent) {

		// Toast.makeText(context, "只有在android4以上才支持",
		// Toast.LENGTH_LONG).show();
		Log.i("chencao", "KKKKKKKKKKK");
		command = intent.getStringExtra("command");
		Log.i("chencao", "CameraMessageReceiver command " + command);
		if (command.toUpperCase().endsWith("ON")) {
			Log.i("chencao", "开启摄像头");
			mDPM.setCameraDisabled(mDeviceAdminSample, false);
		}
		if (command.toUpperCase().endsWith("OFF")) {
			Log.i("chencao", "关闭摄像头");
			mDPM.setCameraDisabled(mDeviceAdminSample, true);
		}
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.setAction(MDM.CUBE_ACTION_MDM_CAMERA);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/CAMERA
		messageType = messageType.toUpperCase();
		if (messageType.contains("MDM/CAMERA")) {
			return true;
		} else {
			return false;
		}
	}

}
