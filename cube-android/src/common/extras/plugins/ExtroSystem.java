package common.extras.plugins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.CubeConstants;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.phone.activity.MultiSystemActivity;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.store.core.ModelCreator;
import com.foreveross.chameleon.store.core.ModelFinder;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.MultiUserInfoModel;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.SystemInfoModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.DESEncrypt;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.foreveross.chameleon.util.GeolocationUtil;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.Preferences;
import com.google.gson.Gson;

/**
 * <BR>
 * [功能详细描述] 切换登录插件
 * 
 */
public class ExtroSystem extends CordovaPlugin {

	private final static Logger log = LoggerFactory
			.getLogger(ExtroSystem.class);
	private Application application = null;
	
	private HttpRequestAsynTask loginTask;
	
	private boolean logining;
	
	private CallbackContext callback = null;

	public boolean isEmpty(String str) {
		return str == null || str.trim().equals("");
	}


	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		callback = callbackContext;
		application = Application.class.cast(this.cordova.getActivity()
				.getApplicationContext());
		log.debug("execute action {} in backgrund thread!", action);
		return false;
/*		if (action.equals("login")) {
			String username = args.getString(0).toLowerCase();
			String password = args.getString(1).toLowerCase();
			String systemid = args.getString(2).toLowerCase();
			processLogined(username, password,systemid, null,
					callbackContext);
		} else if (action.equals("cancle")){
			loginTask.cancel(true);
			logining = false;
		}else if (action.equals("listAllExtroSystem")) {
			ArrayList<SystemInfoModel> list = getSystemInfoList();
			final ExtroSystem plugin = this;
			cordova.setActivityResultCallback(plugin);
			Intent intent = new Intent(cordova.getActivity(),
					MultiSystemActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("username", Preferences.getUserName(Application.sharePref));
			bundle.putString("password", "");
			bundle.putBoolean("isremember", true);
			bundle.putBoolean("isoutline", false);
			bundle.putSerializable("systemlist", list);
			intent.putExtras(bundle);
			cordova.setActivityResultCallback(plugin);
			cordova.getActivity().startActivityForResult(intent,
					FacadeActivity.SYSTEMDIALOG);
		}
		return true;*/
	}


	private ArrayList<SystemInfoModel> getSystemInfoList(){
		String username = Preferences.getUserName(Application.sharePref);
		ArrayList<SystemInfoModel> arrayList = new ArrayList<SystemInfoModel>();
		if (StaticReference.userMf == null) {
			StaticReference.userMC = ModelCreator.build(
					application, username);
			StaticReference.userMf = ModelFinder.build(application,
					username);
		}
		try {
			arrayList.addAll(StaticReference.userMf
					.queryBuilder(SystemInfoModel.class).where()
					.eq("username", username).query());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return arrayList;
	}
	
	private Intent successIntent = null;
	
	public CallbackContext getCallback() {
		return callback;
	}
	public void processLogined(String name, String pass, String id, SystemInfoModel model ,
			final CallbackContext callbackContext) {
		logining = true;
		if (model != null){
			Gson gson = new Gson();
			if ("".equals(pass) || pass == null ){
				callbackContext.success(gson.toJson(model));
				return;
			} else {
				callbackContext.error(gson.toJson(model));
			}
		}
		final String username = name.trim();
		final String password = pass.trim();
		final String systemid = id.trim();
		String deviceId = DeviceInfoUtil.getDeviceId(cordova.getActivity());
		String appId = cordova.getActivity().getPackageName();
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

		loginTask = new HttpRequestAsynTask(
				cordova.getActivity()) {
			
			@Override
			protected String doInBackground(String... params) { // 后台执行
				String result = super.doInBackground(params);
				if (logining){
					return result;
				} else {
					return null;
				}
			}
			
			@Override
			protected void doPostExecute(String json) {
				if (json == null){
					return;
				}
				try {
					JSONObject jb = new JSONObject(json);
					boolean error = jb.has("errmsg");

					if (error) {
						String errmsg = jb.getString("errmsg");
						Toast.makeText(cordova.getActivity(), errmsg,
								Toast.LENGTH_SHORT).show();
						callbackContext.error(errmsg);
					} else {
						boolean loginOK = jb.getBoolean("loginOK");
						if (loginOK) {
							callbackContext.success("登录成功");
							String sessionKey = jb.getString("sessionKey");
							Application.isAppExit = false;
							Log.i("KKK", "无错误并开始正常登录 ");
							Preferences.saveAppMainView(false,
									Application.sharePref);
							Preferences.saveUserInfo(username, sessionKey,
									Application.sharePref);
							// 南航业务代码
							// 保存用户信息

							Preferences.saveSex(jb.getString("sex"),
									Application.sharePref);
							Preferences.saveZhName(jb.getString("zhName"),
									Application.sharePref);
							Preferences.savePhone(jb.getString("phone"),
									Application.sharePref);
							Preferences.savePrivileges(
									jb.getString("privileges"),
									Application.sharePref);

							JSONArray jay = jb.getJSONArray("authSysList");
							
							for (int i = 0; i < jay.length(); i++) {
								JSONObject jsob = (JSONObject) jay.get(i);
								boolean curr = jsob.getBoolean("curr");
								
								if (curr){
									String alias = (String) jsob.get("alias");
									String systemId = (String) jsob.get("id");
									String systemName = (String) jsob
											.get("sysName");
									// 保存当前的系统ID
									Preferences.saveSytemId(systemId,
											Application.sharePref);
									SystemInfoModel infoModel = new SystemInfoModel(
											alias, systemId, systemName, curr, username);

									MultiUserInfoModel multiUserInfoModel = new MultiUserInfoModel();
									multiUserInfoModel.setMD5Str(username, systemId);
									multiUserInfoModel.setUserName(username);
									multiUserInfoModel.setPassWord(password);
									multiUserInfoModel.setSystemId(systemId);
									StaticReference.userMf.createOrUpdate(infoModel);
									StaticReference.userMf
											.createOrUpdate(multiUserInfoModel);
								}
							}
							markLogined();
							// 从本地读取文件cube.json
							application.getCubeApplication().loadApplication();
							cordova.getActivity().startActivity(successIntent);
							application.setLoginType(TmpConstants.LOGIN_ONLINE);
							if (!GeolocationUtil.isOpenGPSSettings(application)) {
								Intent GPSIntent = new Intent();
								GPSIntent
										.setClassName("com.android.settings",
												"com.android.settings.widget.SettingsAppWidgetProvider");
								GPSIntent
										.addCategory("android.intent.category.ALTERNATIVE");
								GPSIntent.setData(Uri.parse("custom:3"));
								try {
									PendingIntent.getBroadcast(application, 0,
											GPSIntent, 0).send();
								} catch (PendingIntent.CanceledException e) {
									e.printStackTrace();
								}
							} else {
								GeolocationUtil.isGPSON = true;
							}
							new AsyncTask<String, Void, Void>() {
								@Override
								protected Void doInBackground(String... strings) {
									Location location = GeolocationUtil
											.getNewLocation(application);
									if (location == null) {
										// 发送通知
										Intent intent = new Intent(
												"com.foss.geoReload");
										cordova.getActivity().sendBroadcast(
												intent);
										return null;
									}
									double longitude = location.getLongitude();
									double latitude = location.getLatitude();
									String sessionKey = strings[0];
									String deviceId = DeviceInfoUtil
											.getDeviceId(application);
									JSONObject json = new JSONObject();
									try {
										json.put("deviceId", deviceId);
										JSONArray tmpArray = new JSONArray();
										tmpArray.put(0, longitude);
										tmpArray.put(1, latitude);
										// double[] arrays = new
										// double[]{longitude,latitude};
										json.put("position", tmpArray);
										HttpPost post = new HttpPost(
												URL.GEOPOSITION_URL
														+ "?sessionKey="
														+ sessionKey);
										post.addHeader("Accept",
												"application/json");
										post.addHeader("Content-Type",
												"application/json");
										post.setEntity(new StringEntity(json
												.toString(), "utf-8"));
										HttpClient client = new DefaultHttpClient();
										HttpResponse response = client
												.execute(post);
										if (response.getStatusLine()
												.getStatusCode() == 200) {
											Log.v("GEO_SUCCESS_TAG",
													json.toString());
										} else {
											BufferedReader br = new BufferedReader(
													new InputStreamReader(
															response.getEntity()
																	.getContent()));
											String line = "";
											StringBuffer stringBuffer = new StringBuffer();
											while ((line = br.readLine()) != null) {
												stringBuffer.append(line);
											}
											Log.e("GEO_FAILD_PARAMS_TAG",
													json.toString());
											Log.e("GEO_URL",
													URL.GEOPOSITION_URL
															+ "?sessionKey="
															+ sessionKey);
											Log.e("GEO_FAILD_TAG",
													stringBuffer.toString());
										}
									} catch (JSONException e) {
										e.printStackTrace();
									} catch (UnsupportedEncodingException e) {
										e.printStackTrace();
									} catch (ClientProtocolException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}

									return null;
								}
							}.execute(sessionKey);

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
									IMModelManager.instance()
											.getSessionContainer()
											.addStuffs(sessionModels);

									// 更新用户标签
									application.refreshRegisrer();

									return null;
								}

								protected void onPostExecute(Void result) {
									application.loginChatClient(username,
											username);

								};
							}.execute();
							// initXmppDataFromDB();

						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
		String encryptPass = DESEncrypt.encryptString(cordova.getActivity()
				.getPackageName(), password);
		sb = sb.append("Form:username=").append(username).append(";password=")
				.append(encryptPass).append(";deviceId=")
				.append(deviceId.toLowerCase().trim()).append(";appKey=")
				.append(application.getCubeApplication().getAppKey())
				.append(";appIdentify=").append(appId).append(";sysId=")
				.append(systemid)
				.append(";encrypt=").append(true);
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
