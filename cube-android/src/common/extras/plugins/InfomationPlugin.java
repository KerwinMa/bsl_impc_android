package common.extras.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.Preferences;

public class InfomationPlugin extends CordovaPlugin {
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if("getInformation".equals(action))
		{
			Application application = Application.class.cast(cordova.getActivity().getApplication());
			String deviceId = DeviceInfoUtil.getDeviceId(cordova.getActivity());
			String username = Preferences.getUserName(Application.sharePref);
			String device = "";
			if(PadUtils.isPad(cordova.getActivity()))
			{
				device = "Pad";
			}
			else 
			{
				device = "Phone";
			}
			String password = Preferences.getPasswordbak(Application.sharePref);
			String sessionKey = Preferences.getSESSION(Application.sharePref);
			String app = application.getAppIdentifier();
			String appKey = application.getCubeApplication().getAppKey();
			app = app.substring(0, app.lastIndexOf("."));
			String osVersion = String.valueOf(android.os.Build.MODEL);
			String appVersion = application.getAppVersion();
			String appBuild = String.valueOf(application.getAppBuild());
			String obtainSysData = new StringBuffer().append("{")
					.append("\"username\":").append("\"" + username + "\",")
					.append("\"password\":").append("\"" + password + "\",")
					.append("\"deviceId\":").append("\"" + deviceId + "\",")
					.append("\"device\":").append("\"" + device + "\",")
					.append("\"appKey\":").append("\"" + appKey + "\",")
					.append("\"app\":").append("\"" + app + "\",")
					.append("\"token\":").append("\"" + sessionKey + "\",")
					.append("\"osVersion\":").append("\"" + osVersion + "\",")
					.append("\"appVersion\":").append("\"" + appVersion + "\",")
					.append("\"build\":").append("\"" + appVersion + "\",")
					.append("\"appBuild\":").append(appBuild).append("}")
					.append("\"lastLoginTime\":").append("\"" + "xxxxxx" + "\",")

					.toString();
			JSONObject obj = new JSONObject(obtainSysData);
			echo(obj, callbackContext);
		}
		return true;
	}

	private void echo(JSONObject message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);// 这里的succsee函数，就是调用js的success函数
		} else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}
}