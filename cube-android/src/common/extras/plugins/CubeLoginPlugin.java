package common.extras.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.store.core.ModelCreator;
import com.foreveross.chameleon.store.core.ModelFinder;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.AbstractContainerModel;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.CheckNetworkUtil;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.Pool;
import com.foreveross.chameleon.util.Preferences;

/**
 * <BR>
 * [功能详细描述] 登录插件
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-6-13]
 */
public class CubeLoginPlugin extends CordovaPlugin {

	private final static Logger log = LoggerFactory
			.getLogger(CubeLoginPlugin.class);
	private Application application = null;

	public boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		application = Application.class.cast(this.cordova.getActivity()
				.getApplicationContext());
		log.debug("execute action {} in backgrund thread!", action);
		if (action.equals("login")) {
			login(args, callbackContext);
		} else if (action.equals("getAccountMessage")) {
			getStoredAccount(callbackContext);

		}
		return true;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 * @param callbackContext
	 * @throws JSONException
	 *             2013-9-16 下午3:15:48
	 */
	private void login(JSONArray args, CallbackContext callbackContext)
			throws JSONException {
		if (args.length() < 3) {
			Toast.makeText(cordova.getActivity(), "传入参数少于3,请检查！",
					Toast.LENGTH_SHORT).show();
			return;
		}
		String username = args.getString(0).toLowerCase();
		String password = args.getString(1).toLowerCase();
		boolean isremember = args.getBoolean(2);
		if (checkLogin(username, password)) {
			processLogined(isremember, username, password, callbackContext);
		}

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param username
	 * @param password
	 *            2013-9-16 下午3:16:39
	 */
	private boolean checkLogin(String username, String password) {
		if (!CheckNetworkUtil.checkNetWork(cordova.getActivity())) {
			Toast.makeText(cordova.getActivity(), "网络异常，请检查设置！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		if (isEmpty(username) || isEmpty(password)) {
			Log.i("AAA", "密码账号空");
			Toast.makeText(cordova.getActivity(), "用户名和密码都不能为空，请检查！",
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param callbackContext
	 * @throws JSONException
	 *             2013-9-16 下午3:14:16
	 */
	private void getStoredAccount(CallbackContext callbackContext)
			throws JSONException {
		boolean isRemember = Preferences.getIsRemember(Application.sharePref);
		String passString = Preferences.getPassword(Application.sharePref);
		String nameString = Preferences.getUserName(Application.sharePref);
		JSONObject job = new JSONObject();
		job.put("username", nameString);
		job.put("isRemember", isRemember);
		if (isRemember) {
			job.put("password", passString);
		} else {
			job.put("password", "");
		}
		callbackContext.success(job.toString());
	}

	private Intent successIntent = null;

	private void processLogined(boolean isremember, String name, String pass,
			final CallbackContext callbackContext) {
		final String username = name.trim();
		final String password = pass.trim();
		String deviceId = DeviceInfoUtil.getDeviceId(cordova.getActivity());
		String appId = cordova.getActivity().getPackageName();
		if (isremember) {
			Preferences.saveUser(password, username, isremember,
					Application.sharePref);
		} else {
			Preferences.saveUser("", username, isremember,
					Application.sharePref);
		}
		Preferences.savePWD(pass, Application.sharePref);
		if (PadUtils.isPad(application)) {
			successIntent = new Intent(cordova.getActivity(),
					FacadeActivity.class);
			successIntent.putExtra("direction", 1);
			successIntent.putExtra("type", "web");
			successIntent.putExtra("isPad", true);
			successIntent.putExtra("value", URL.PAD_MAIN_URL);

		} else {  
			successIntent = new Intent(cordova.getActivity(),
					FacadeActivity.class);
			successIntent.putExtra("isPad", false);
			successIntent.putExtra("value", URL.PHONE_MAIN_URL);
		}

		HttpRequestAsynTask loginTask = new HttpRequestAsynTask(
				cordova.getActivity()) {
			@Override
			protected void doPostExecute(String json) {
				
				try {
					Log.i("AAAAA", "json = " + json);
					if (json.equals("500")) {

						if (needProgressDialog) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									context);
							builder.setTitle("提示");
							builder.setMessage("密码或用户名错误");
							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							Dialog dialog = builder.create();
							dialog.show();
						}
					} else {

						JSONObject jb = new JSONObject(json);
						String result = jb.getString("result");

						if ("true".equals(result)) {
							
							
							if(!jb.getBoolean("hasOperation"))
							{
								AlertDialog.Builder builder = new AlertDialog.Builder(
										context);
								builder.setTitle("提示");
								builder.setMessage("用户没有操作权限请联系管理员");
								builder.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
											}
										});
								Dialog dialog = builder.create();
								dialog.show();
								return;
							}
							
							callbackContext.success("登录成功");
							String sessionKey = jb.getString("sessionKey");
							Application.isAppExit = false;
							Log.i("KKK", "无错误并开始正常登录 ");
							Preferences.saveAppMainView(false, Application.sharePref);
							Preferences.saveUserInfo(username, sessionKey,
									Application.sharePref);
							//南航业务代码
							//保存用户信息
							
							Preferences.saveSex(jb.getString("sex"), Application.sharePref);
							Preferences.saveZhName(jb.getString("zhName"), Application.sharePref);
							Preferences.savePhone(jb.getString("phone"), Application.sharePref);
							Preferences.savePrivileges(jb.getString("privileges"), Application.sharePref);
							
							markLogined();
							cordova.getActivity().startActivity(successIntent);
							application.setLoginType(TmpConstants.LOGIN_ONLINE);
							StaticReference.userMC = ModelCreator.build(
									application, username);
							StaticReference.userMf = ModelFinder.build(
									application, username);

							new AsyncTask<Void, Void, Void>() {

								@Override
								protected Void doInBackground(Void... params) {
									// 查询所有存储用户(没有用户组)
									List<UserModel> userModels = StaticReference.userMf
											.queryForAll(UserModel.class);

									for (UserModel userModel : userModels) {
										// 从json中恢复用户组,多个用户之前共用相用的用户组
										if (!userModel.hasResoreGroup()) {
											userModel.restoreGroups();
										}
										// 查找用户历史记录
										userModel.findHistory(-1);
										// 如果有相同
										if (!IMModelManager.instance()
												.containUserModel(
														userModel.getJid())) {
											IMModelManager.instance()
													.addUserModel(userModel);
										}

									}
									
									List<SessionModel> sessionModels = StaticReference.userMf
											.queryForAll(SessionModel.class);
									IMModelManager.instance().getSessionContainer().addStuffs(sessionModels);
									
									 //更新用户标签
									application.refreshRegisrer();
									
									return null;
								}

								protected void onPostExecute(Void result) {
									application.loginChatClient(username,
											username);
									
								};
							}.execute();
							// initXmppDataFromDB();

						} else {
							// {"result":"false","code":"2100007","message":"Username is not exist or Wrong password! ,错误码[2100007]"}
							String message = jb.getString("message");
							Log.i("KKK", "登录错误并返回 ");
							Toast.makeText(cordova.getActivity(), message,
									Toast.LENGTH_SHORT).show();
							callbackContext.error(message);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();

				}
				Log.i("KKK", "Login Result = " + json);
			}

			@Override
			public void doHttpFail(Exception e) {
				super.doHttpFail(e);
				callbackContext.error(loginResultMessage("登录失败，请检查网络", false));
			}

		};
		loginTask.setDialogContent("正在登录...");
		loginTask.setLockScreen(true);
		loginTask.setShowProgressDialog(true);
		loginTask.setNeedProgressDialog(true);
		StringBuilder sb = new StringBuilder();
		sb = sb.append("Form:username=").append(username).append(";password=")
				.append(password).append(";deviceId=")
				.append(deviceId.toLowerCase().trim()).append(";appKey=")
				.append(application.getCubeApplication().getAppKey()).append(";appId=").append(appId);
		String s = sb.toString();
		loginTask.execute(URL.LOGIN, s, HttpUtil.UTF8_ENCODING,
				HttpUtil.HTTP_POST);
	}

	public void markLogined() {
		Application application = (Application) cordova.getActivity()
				.getApplication();
		application.setHasLogined(true);
	}

	public String loginResultMessage(String message, boolean isSuccess) {

		try {
			JSONObject jb = new JSONObject();
			jb.put("isSuccess", isSuccess);
			jb.put("message", message);
			return jb.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";
	}

	
}