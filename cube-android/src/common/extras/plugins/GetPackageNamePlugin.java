package common.extras.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.foreveross.chameleon.Application;

public class GetPackageNamePlugin extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		// TODO Auto-generated method stub
		System.out.println("调用了GetPackageNamePlugin");
		if(action.equals("getPackageName")){
			System.out.println("进入getPackageName");
			Application app = Application.class.cast(cordova.getActivity()
					.getApplicationContext());
			String packageName = app.getApplicationContext().getPackageName();
			JSONObject job = new JSONObject();
			job.put("packageName", packageName);
			System.out.println("进入getPackageName packageName="+packageName);
			callbackContext.success(job.toString());
		}
		return true;
		//return super.execute(action, args, callbackContext);
	}
	
}
