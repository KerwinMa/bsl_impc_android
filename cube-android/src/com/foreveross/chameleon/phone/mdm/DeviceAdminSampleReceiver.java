package com.foreveross.chameleon.phone.mdm;

import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.csair.impc.R;

/**
 * Sample implementation of a DeviceAdminReceiver. Your controller must
 * provide one, although you may or may not implement all of the methods
 * shown here.
 * 
 * All callbacks are on the UI thread and your implementations should not
 * engage in any blocking operations, including disk I/O.
 */
public class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
	void showToast(Context context, String msg) {
		String status = context.getString(R.string.admin_receiver_status,
				msg);
		Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
		showToast(context,
				context.getString(R.string.admin_receiver_status_enabled));
	}

	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		super.onDisableRequested(context, intent);
		return context
				.getString(R.string.admin_receiver_status_disable_warning);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);
		showToast(context,
				context.getString(R.string.admin_receiver_status_disabled));
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		super.onPasswordChanged(context, intent);
		showToast(
				context,
				context.getString(R.string.admin_receiver_status_pw_changed));
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		super.onPasswordFailed(context, intent);
		showToast(context,
				context.getString(R.string.admin_receiver_status_pw_failed));
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		super.onPasswordSucceeded(context, intent);
		showToast(
				context,
				context.getString(R.string.admin_receiver_status_pw_succeeded));
	}

	@Override
	public DevicePolicyManager getManager(Context context) {
		return super.getManager(context);
	}

	@Override
	public ComponentName getWho(Context context) {
		return super.getWho(context);
	}

	@SuppressLint("NewApi")
	@Override
	public void onPasswordExpiring(Context context, Intent intent) {
		super.onPasswordExpiring(context, intent);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	
}