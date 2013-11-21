package common.extras.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.util.CommonUtils;

/**
 * <BR>
 * [功能详细描述] 获取模块信息插件
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-6-13]
 */
public class CubeModuleListPlugin extends CordovaPlugin {

	private final static Logger log = LoggerFactory
			.getLogger(CubeModuleListPlugin.class);

	@Override
	public boolean execute(final String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		cordova.getThreadPool().execute(new Runnable() {

			@Override
			public void run() {
				log.debug("execute action {} in backgrund thread!", action);
				String result = null;
				if (action.equals("upgradableList")) {
					log.debug("obtain upgradable list...");
					result = CommonUtils.getGson().toJson(
							CubeModuleManager.getInstance().getUpdatable_map());
					log.debug("upgradable list result is {} ", result);
				} else if (action.equals("installList")) {
					log.debug("obtain installed list...");
					result = CommonUtils.getGson().toJson(
							CubeModuleManager.getInstance().getInstalled_map());
					log.debug("installed list result is {} ", result);
				} else if (action.equals("uninstallList")) {
					log.debug("obtain uninstall list...");
					try {
						result = CommonUtils.getGson().toJson(
								CubeModuleManager.getInstance()
								.getUninstalled_map());
					} catch (Exception e) {
						System.out.println(e.getMessage());	
					}
					log.debug("uninstall list result is {} ", result);
				} else if (action.equals("mainList")) {
					log.debug("obtain main  list...");
					result = CommonUtils.getGson().toJson(
							CubeModuleManager.getInstance().getMain_map());
					log.debug("main list result is {} ", result);
				} else {
					log.debug("action {} is not been proccessed!");
				}
				if (result != null) {
					System.out.println("rererere "+result);
					// TODO[FENGWEILI] 是否有必要运行在UI线程?
					cordova.getActivity().runOnUiThread(
							new MyRunnable(callbackContext, result));
				}

			}
		});
		return true;
	}
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-9-16 上午10:47:40
	 */
	class MyRunnable implements Runnable {
		private String result;
		private CallbackContext callbackContext;

		public MyRunnable(CallbackContext callbackContext, String result) {
			this.callbackContext = callbackContext;
			this.result = result;
		}


		@Override
		public void run() {
			callbackContext.success(result);
		}

	}
}