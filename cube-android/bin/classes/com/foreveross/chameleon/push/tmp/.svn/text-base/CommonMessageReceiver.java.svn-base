package com.foreveross.chameleon.push.tmp;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.foreveross.chameleon.phone.mdm.DeviceAdminSampleReceiver;

public abstract class CommonMessageReceiver extends BroadcastReceiver implements
		MessageListener {

	protected DevicePolicyManager mDPM;
	protected ComponentName mDeviceAdminSample;
	protected boolean active;
	protected String command;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("cube", "received intent:" + intent);

		mDPM = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mDeviceAdminSample = new ComponentName(context,
				DeviceAdminSampleReceiver.class);
		active = mDPM.isAdminActive(mDeviceAdminSample);
		Log.d("cube", "active? :" + active);
		// if (!active) {
		// Intent iAntent = new Intent(
		// DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		// iAntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
		// mDeviceAdminSample);
		// iAntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
		// context.getString(R.string.add_admin_extra_app_text));
		// iAntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(iAntent);
		// }
		onActualReceive(context, intent);

	}

	@Override
	public Intent convertMessage2Intent(Message message) {
		if (isValidType(message)) {
			Intent intent = assembleIntent(message.getContent());
			intent.putExtra("command", message.message);
			return intent;
		} else {
			return null;
		}
	}

	protected abstract boolean isValidType(Message message);

	protected abstract Intent assembleIntent(String messageContent);

	protected abstract void onActualReceive(Context context, Intent intent);
}
