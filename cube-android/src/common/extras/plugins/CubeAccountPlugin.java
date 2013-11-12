package common.extras.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.util.Preferences;

/**
 * @author zhoujun
 * 获取用户的中文名
 *
 */
public class CubeAccountPlugin extends CordovaPlugin{
	
	private final static Logger log = LoggerFactory
			.getLogger(CubeLoginPlugin.class);
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		
		log.debug("execute action {} in backgrund thread!", action);
		if (action.equals("getAccount")) {
			String accountName = Preferences.getZhName(Application.sharePref);
			JSONObject job = new JSONObject();
			job.put("accountname", accountName);
			callbackContext.success(job.toString());
		} 
		return true;
	}

}
