package com.foreveross.chameleon.util;

import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfoUtil {

	/**
	 * 获取deviceid
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String androidid = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String tmDevice, tmSerial;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();

		Log.d("cube", "之前androidid=" + androidid + " tmDevice=" + tmDevice
				+ " tmSerial=" + tmSerial);

		if (androidid == null) {
			androidid = "";
		}
		if (tmSerial == null) {
			tmSerial = "";
		}

		Log.d("cube", "之后androidid=" + androidid + " tmDevice=" + tmDevice
				+ " tmSerial=" + tmSerial);

		UUID deviceUuid = new UUID(androidid.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		Log.d("cube", "uniqueId = " + uniqueId);
		return uniqueId;
		// url = URLEncoder.encode(url, HttpRequestAsynTask.QUERY_ENCODING);
		// String encryptedUniqueId = DESEncryptAndGzipUtil.encrypt(uniqueId)
		// .trim();
		// try {
		// encryptedUniqueId = URLEncoder.encode(encryptedUniqueId,
		// HttpUtil.ENCODING);
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// Log.e("mytag", "deviceid url encode失败。");
		// }
		// Log.d("mytag", "编码后的uniqueId=" + encryptedUniqueId);
		// return encryptedUniqueId;

	}

	/**
	 * 获取app version
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		String app_ver = "";
		int app_vercode = 1;
		try {
			app_ver = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
			app_vercode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			Log.d("cube", "app_ver=" + app_ver);
			Log.d("cube", "app_vercode=" + app_vercode);
		} catch (NameNotFoundException e) {
			Log.v("cube", e.getMessage());
		}
		return app_ver + "." + app_vercode;
	}
}
