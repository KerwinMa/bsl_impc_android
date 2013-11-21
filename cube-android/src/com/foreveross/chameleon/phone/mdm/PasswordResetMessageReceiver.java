package com.foreveross.chameleon.phone.mdm;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.foreveross.chameleon.push.tmp.MessageListener;

public class PasswordResetMessageReceiver extends CommonMDMReceiver implements
		MessageListener {

	@SuppressLint({ "NewApi", "NewApi" })
	@Override
	public void onActualReceive(Context context, Intent intent) {
		 String password = (String)intent.getExtras().get("content");
			SharedPreferences sharedPrefs = context.getSharedPreferences(
					"config", Context.MODE_PRIVATE);
			sharedPrefs.edit().putString("resetedPassword",password).commit();
			if(TextUtils.isEmpty(password)){
				mDPM.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
				return;
			}
		mDPM.resetPassword(password, 0);
		mDPM.lockNow();
	}

	@Override
	protected Intent assembleIntent(String messageContent) {
		// 解析messageContent，为Intent添加属性
		Intent intent = new Intent();
		intent.putExtra("content", messageContent);
		intent.setAction(MDM.CUBE_ACTION_MDM_PASSWORD_RESET);
		return intent;
	}

	@Override
	protected boolean validateSubMessageType(String messageType) {
		// example:MDM/PASSWORD_QUALITY
		messageType = messageType.toUpperCase();
		if (messageType.contains("MDM/PASSWORD:SET")) {
			return true;
		} else {
			return false;
		}
	}
}
