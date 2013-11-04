package common.extras.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.phone.activity.AdminActivity;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.PadUtils;

public class DeviceRegisterPlugin extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		Application application = Application.class.cast(cordova.getActivity().getApplication());
		String deviceId = DeviceInfoUtil.getDeviceId(application);
		String appKey = application.getCubeApplication().getAppKey();
		final CallbackContext callback = callbackContext;
		if(action.equals("queryDevcieInfo"))
		{
			HttpRequestAsynTask task = new HttpRequestAsynTask(application) {

				@Override
				protected void doPostExecute(String result) {
					if(!"".equals(result))
					{
						try {
							JSONObject json = new JSONObject(result);
							callback.success(json);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					else
					{
						callback.error("");
					}
				}
				
			};
			task.setNeedProgressDialog(false);
			task.setShowProgressDialog(false);
			String url = URL.BASE_WS +"/csair-extention/deviceRegInfo/get/"+deviceId+"?appKey="+appKey;
			task.execute(new String[]{url,"",HttpUtil.UTF8_ENCODING,HttpUtil.HTTP_GET});
		}
		else if(action.equals("updateDevice"))
		{
			JSONObject json =  args.getJSONObject(0);
			HttpRequestAsynTask task = new HttpRequestAsynTask(application) {

				@Override
				protected void doPostExecute(String result) {
					if(!"".equals(result))
					{
						try {
							JSONObject json = new JSONObject(result);
							callback.success(json);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					else
					{
						callback.error("");
					}
				}
				
			};
			task.setNeedProgressDialog(false);
			task.setShowProgressDialog(false);
			StringBuffer buff = new StringBuffer();
			buff.append("Form:id=").append(json.getString("id")).append(";name=").append(json.getString("name"))
			.append(";staffCode=").append(json.getString("staffCode")).append(";dept=").append(json.getString("dept"))
			.append(";email=").append(json.getString("email")).append(";telPhone=").append(json.getString("telPhone"))
			.append(";deviceSrc=").append(json.getString("deviceSrc")).append(";deviceId=").append(json.getString("deviceId"));
			String url = URL.BASE_WS +"/csair-extention/deviceRegInfo/update";
			task.execute(new String[]{url,buff.toString(),HttpUtil.UTF8_ENCODING,HttpUtil.HTTP_POST});
			
		}
		else if(action.equals("submitInfo"))
		{
			JSONObject json =  args.getJSONObject(0);
			HttpRequestAsynTask task = new HttpRequestAsynTask(application) {

				@Override
				protected void doPostExecute(String result) {
					if(!"".equals(result))
					{
						try {
							JSONObject json = new JSONObject(result);
							callback.success(json);
							
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					else
					{
						callback.error("");
					}
				}
				
			};
			task.setNeedProgressDialog(false);
			task.setShowProgressDialog(false);
			StringBuffer buff = new StringBuffer();
			buff.append("Form:name=").append(json.getString("name"))
			.append(";staffCode=").append(json.getString("staffCode")).append(";dept=").append(json.getString("dept"))
			.append(";email=").append(json.getString("email")).append(";telPhone=").append(json.getString("telPhone"))
			.append(";deviceSrc=").append(json.getString("deviceSrc")).append(";deviceId=").append(json.getString("deviceId"));
			String url = URL.BASE_WS +"/csair-extention/deviceRegInfo/reg";
			task.execute(new String[]{url,buff.toString(),HttpUtil.UTF8_ENCODING,HttpUtil.HTTP_POST});

		}
		else if(action.equals("redrectMain"))
		{
			// 平板
			if (PadUtils.isPad(application)) {
				Intent i = new Intent(cordova.getActivity(), FacadeActivity.class);
				i.putExtra("url", URL.PAD_LOGIN_URL);
				i.putExtra("isPad", true);
				cordova.getActivity().startActivity(i);
				cordova.getActivity().finish();
			} else {// 手机
				Intent i = new Intent(cordova.getActivity(), FacadeActivity.class);
				i.putExtra("url", URL.PHONE_LOGIN_URL);
				i.putExtra("isPad", false);
				cordova.getActivity().startActivity(i);
				cordova.getActivity().finish();
			}
		}
		return super.execute(action, args, callbackContext);
	}
	

}
