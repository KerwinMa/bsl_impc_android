package com.foreveross.chameleon;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.csair.impc.R;
import com.foreveross.chameleon.util.DESEncryptAndGzipUtil;
import com.foreveross.chameleon.util.HttpUtil;

/**
 * @author fengweili</br> 2011-8-13 上午11:31:07
 */
public class CubeConstants {

	public final static int CUBE_CONFIG = R.raw.cube1;
	public final static boolean DEMO = false;
	public static String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOKfbHS+q59D22ZHw8VL/ewC0OVsyJRSUEnErPEACK7qW6cg3eeP/Q5Qjj+HQdchfzx1wQbsgGlCHu+tprqzzkcbFTnfa9nD8Gewu2VT9TObekpEwCwQMEEcQYgUAxD78loBKUgINFFrFaSn8I3Vad5ymEcVlo7ETkKu1/lS+g6LAgMBAAECgYB1Myee1MDjE+/SXIjlbyB5vxcTn4e4FT3KeLlLxc230CHoM/ou+GtRzN1UA3pMbNllhix2jTb3uKdRIshYRAcIC9mygR9grBRyzE8uqqe+vOjaGgrbaEVS/74M+WjmwNraOmh1VD06ghGkg12xf2iTLHrXSrIUtLPXEuD8SaBY0QJBAPIKUJ1FquGJxKpw+uF3CYB4+HitTpA1ax1IIpwXobdZhgQMn//A2ie9ecxBrUNSYb/WPy7zrZATLlPUR5eYZokCQQDvsXikTbuv9bmn3lZm/mgwDSmu2co9A+2L2xR8v0E9nu4wUTxuwyGVx1iKKui4Q9XB5uZQv4LCu4fWl3LHdEdzAkA32xGHedBZhAWSn8gFyAa1UzVkA/qhZPJ3K3JxOzLisRIwVQmHZ+XwTdWRwYZOhvBv6O1j1HA1U3fZeJ+c6FqhAkBCYZ4NstF169Gc4gB/yZlFJYATwpE10K6q+uNzoOwKisdgbj8UVcopVun4aeXFklPSvYWvezpVf+Yg0hShlFxtAkASy5aYxNF+TG1rc4sfZxZnjHmzevMqVeugcaXiMwa1ShApirpf0zPVHiJbC51ihQF94eFRZIS/Dce6a5qifncY";

	// 新生支付接口地址
	// public final static String HNAPAY_HOST_URL =
	// "https://www.hnapay.com/mpay/";
	public final static String HNAPAY_HOST_ROOT_URL = "http://210.22.91.6/";
	public final static String HNAPAY_HOST_URL = HNAPAY_HOST_ROOT_URL + "mpay/";
	// 动车接口地址
	public final static String TRAIN_HOST_URL = "http://www1.skyecho.com/cgishell/golden/admin/train/train_data_inf.pl";

	public static final String UPDATE_FILE_NAME = "HNAPAY.apk";
	public static final long UPDATE_PERIOD = 12 * 3600 * 1000;

	public static String TERMINAL_CODE = "";
	// // 订单详细
	// public static String URL_ORDER_DETAIL_QUERY = HNAPAYConstants.HOST_URL
	// + "CSMBP/data/order/getOrderDetail.do?type=mobile";
	// // 航班查询
	// public static String URL_FLIGHT_QUERY = HNAPAYConstants.HOST_URL
	// + "CSMBP/data/order/getAvPrice.do?type=mobile";
	// 更新
	public static String UPDATE_URL = HNAPAY_HOST_URL
			+ "data/order/generatetoken.do?type=mobile";

	public static String UPDATE_FILE_URL = HNAPAY_HOST_URL
			+ "data/order/generatetoken.do?type=mobile";

	public static String PUSH_URL = HNAPAY_HOST_URL
			+ "public/api/queryPushInfo";

	// 旅游&酒店

	// search nearby
	public static final String LOCATION_SEARCH_NEARBY_TREVAL_URL = HNAPAY_HOST_URL
			+ "public/api/queryNearTravelInfo";
	public static final String LOCATION_SEARCH_NEARBY_HOTEL_URL = HNAPAY_HOST_URL
			+ "public/api/queryNearHotelInfo";

	// search by keyword
	public static final String LOCATION_SEARCH_KEYWORD_TREVAL_URL = HNAPAY_HOST_URL
			+ "public/api/queryTravelInfo";
	public static final String LOCATION_SEARCH_KEYWORD_HOTEL_URL = HNAPAY_HOST_URL
			+ "public/api/queryHotelInfo";

	// poi detail
	public static final String LOCATION_TREVAL_DETAIL_URL = HNAPAY_HOST_URL
			+ "public/api/queryTravelInfoDetail";
	public static final String LOCATION_HOTEL_DETAIL_URL = HNAPAY_HOST_URL
			+ "public/api/queryHotelInfoDetail";

	// comments
	public static final String LOCATION_QUERY_COMMENT = HNAPAY_HOST_URL
			+ "public/api/queryCommentInfo";
	public static final String LOCATION_ADD_COMMENT = HNAPAY_HOST_URL
			+ "public/api/addCommentInfo";

	// member
	public static final String MEMBER_LOGIN_URL = HNAPAY_HOST_URL
			+ "public/api/login";
	public static final String MEMBER_GET_CHECK_CODE_URL = HNAPAY_HOST_URL
			+ "public/api/getCheckCode";
	public static final String MEMBER_VALIDATE_CHECK_CODE_URL = HNAPAY_HOST_URL
			+ "public/api/validateCheckCode";
	public static final String MEMBER_RESET_LOGIN_PWD_URL = HNAPAY_HOST_URL
			+ "public/api/resetLoginPassword";
	public static final String MEMBER_REGISTER_URL = HNAPAY_HOST_URL
			+ "public/api/register";
	public static final String MEMBER_LOGOUT_URL = HNAPAY_HOST_URL
			+ "private/api/offLogin";

	// account
	public static final String ACCOUNT_QUERY_INFO = HNAPAY_HOST_URL
			+ "private/api/queryAccInfo";
	public static final String ACCOUNT_QUERY_CARD_CHANNEL_INFO = HNAPAY_HOST_URL
			+ "private/api/queryCardChannel";
	public static final String ACCOUNT_BIND_CARD_INFO = HNAPAY_HOST_URL
			+ "private/api/bindCardInfo";
	public static final String ACCOUNT_QUERY_BLANCE = HNAPAY_HOST_URL
			+ "private/api/queryBalance";
	public static final String ACCOUNT_RESET_PAY_PWD_URL = HNAPAY_HOST_URL
			+ "private/api/resetPwdPassword";
	public static final String ACCOUNT_EDIT_PWD_URL = HNAPAY_HOST_URL
			+ "private/api/editPassword";
	public static final String ACCOUNT_PAYMENT_BIND_URL = HNAPAY_HOST_URL
			+ "private/api/queryBindCardInfo";
	public static final String ACCOUNT_UNBIND_CARD_INFO = HNAPAY_HOST_URL
			+ "private/api/unBindCardInfo";

	public static String getTerminalCode(Context context) {
		if (TERMINAL_CODE == null || "".equals(TERMINAL_CODE)) {
			TERMINAL_CODE = getProductType() + "|"
					+ getEncryptedDeviceId(context, false) + "|"
					+ getOSVersion();
		}
		return TERMINAL_CODE;
	}

	public static String getProductType() {
		// modelStr=Desire HD displayStr=GRI40 productStr=htc_ace deviceStr=ace
		// boardStr=spade
		// brandStr=htc_wwe versionStr=10 incrementalStr=47853
		// timeStr=1303500275000 userStr=sip host=AA100
		String productType = "Android";
		try {
			// String modelStr = Build.MODEL;
			// String displayStr = Build.DISPLAY;
			String productStr = Build.PRODUCT;
			// String deviceStr = Build.DEVICE;
			// String boardStr = Build.BOARD;
			// String brandStr = Build.BRAND;
			// String versionStr = Build.VERSION.SDK;
			// String incrementalStr = Build.VERSION.INCREMENTAL;
			// long timeStr = Build.TIME;
			// String userStr = Build.USER;
			// String host = Build.HOST;

			// Log.d("cube", "modelStr=" + modelStr + " displayStr=" +
			// displayStr
			// + " productStr=" + productStr + " deviceStr=" + deviceStr
			// + " boardStr=" + boardStr + " brandStr=" + brandStr
			// + " versionStr=" + versionStr + " incrementalStr="
			// + incrementalStr + " timeStr=" + timeStr + " userStr="
			// + userStr + " host=" + host);
			productType = productStr;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return productType;
	}

	public static String getOSVersion() {
		String osVersion = "7";
		try {
			String versionStr = Build.VERSION.SDK;
			osVersion = versionStr;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return osVersion;
	}

	public static String getEncryptedDeviceId(Context context, boolean encrypt) {

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
		// url = URLEncoder.encode(url, HttpRequestAsynTask.QUERY_ENCODING);
		if (encrypt) {
			uniqueId = DESEncryptAndGzipUtil.encrypt(uniqueId).trim();
		}
		try {
			uniqueId = URLEncoder.encode(uniqueId, HttpUtil.UTF8_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.e("cube", "deviceid url encode失败。");
		}
		Log.d("cube", "编码后的uniqueId=" + uniqueId);
		return uniqueId;

	}

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
			Log.v("mytag", e.getMessage());
		}
		return app_ver + "." + app_vercode;
	}

	// 订单
	public static final String ORDER_QUERY_ORDER_LIST_URL = HNAPAY_HOST_URL
			+ "private/api/queryOrderList";
	public static final String ORDER_PAY_ORDER_URL = HNAPAY_HOST_URL
			+ "private/api/orderPayInfo";
	public static final String ORDER_QUERY_ORDER_DETAIL_URL = HNAPAY_HOST_URL
			+ "private/api/queryOrderDetail";
	public static final String ORDER_PAY_BY_UNION_DEPOSIT_URL = HNAPAY_HOST_URL
			+ "private/api/orderUnionPay";

	public static final String ORDER_GENERATE_TRAIN_PAY_ORDER = HNAPAY_HOST_URL
			+ "private/api/generateOrder";

	// 支付
	public static final String PAY_QUERY_ACCOUNT_INFO_URL = HNAPAY_HOST_URL
			+ "private/api/queryAccInfo";
	public static final String PAY_QUERY_PREPAID_CARD_URL = HNAPAY_HOST_URL
			+ "private/api/queryBindCardInfo";
	public static final String PAY_PAYMENT_URL = HNAPAY_HOST_URL
			+ "private/api/payment";
	public static final String PAY_PAYMENT_QUOTA_URL = HNAPAY_HOST_URL
			+ "private/api/paymentQuota";
	public static final String PAY_RECHARGE_BY_UNION_DEPOSIT_URL = HNAPAY_HOST_URL
			+ "private/api/orderUnionDeposit";

}
