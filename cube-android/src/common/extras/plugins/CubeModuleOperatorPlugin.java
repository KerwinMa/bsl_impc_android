package common.extras.plugins;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.BroadcastConstans;
import com.foreveross.chameleon.CubeAndroid;
import com.foreveross.chameleon.CubeConstants;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.phone.activity.AppDetailActivity;
import com.foreveross.chameleon.phone.activity.SettingsActivity;
import com.foreveross.chameleon.phone.modules.ApplicationSyncListener;
import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.modules.MessageFragmentModel;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.AutoDownloadRecord;
import com.foreveross.chameleon.store.model.AutoShowViewRecord;
import com.foreveross.chameleon.util.FileCopeTool;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.Pool;
import com.foreveross.chameleon.util.Preferences;
import com.google.gson.Gson;

/**
 * <BR>
 * [功能详细描述] 模块操作插件
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-6-9]
 */
public class CubeModuleOperatorPlugin extends CordovaPlugin {

	private Boolean fristTimeDownload = true;
	private AlertDialog needDownloadDialog;
	private AlertDialog needUpdateDialog;
	private List<CubeModule> unInstalledModules;// 自动下载list
	private List<CubeModule> updateModules;// 自动更新list
	private List<CubeModule> isAutoShowModules;// 自动弹出模块list
	private List<AutoDownloadRecord> autoDownloadRecord;// 数据库记录

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		Application app = Application.class.cast(cordova.getActivity()
				.getApplicationContext());
		Log.i("AAA", "action is =" + action);
		if (args.length() > 0) {
			String setting = args.getString(0).toLowerCase();
			if ("setting".equals(setting)) {
				boolean outline = Preferences.getOutLine(Application.sharePref);
				if (outline) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							cordova.getActivity());
					builder.setTitle("提示");
					builder.setMessage("离线状态不能管理模块");
					builder.setPositiveButton("确定", null);
					Dialog dialog = builder.create();
					dialog.show();
					return true;
				}
			}
		}
		if (action.equals("sync")) {
			// 只有同步不需要用到identifier

			boolean outline = Preferences.getOutLine(Application.sharePref);
			CubeApplication cubeApp = app.getCubeApplication();
			if (outline) {
				cubeApp.loadApplication();
				if(args.length() >0){
					Toast.makeText(cordova.getActivity(), "离线不能管理模块", Toast.LENGTH_SHORT).show();
					callbackContext.error("");
					return false;
				}
				callbackContext.success("sync success");
			}
			else {
				cubeApp.sync(new ApplicationSyncListener() {

					@Override
					public void syncStart() {
						// showCustomDialog(true);
					}

					@Override
					public void syncFinish() {

						// cancelDialog();
						callbackContext.success("sync success");
						String name = Preferences
								.getUserName(Application.sharePref);
						unInstalledModules = new ArrayList<CubeModule>();
						updateModules = new ArrayList<CubeModule>();
						isAutoShowModules = new ArrayList<CubeModule>();
						// 获得自动更新列表
						if (CubeModuleManager.getInstance().getUpdatable_map()
								.size() != 0) {
							for (List<CubeModule> list : CubeModuleManager
									.getInstance().getUpdatable_map().values()) {
								updateModules.addAll(list);
							}
						}

						try {
							// 查出数据库 用户不需要自动下载的模块列表
							autoDownloadRecord = StaticReference.userMf
									.queryBuilder(AutoDownloadRecord.class).query();
						} catch (SQLException e) {
							e.printStackTrace();
						}

						// 获得弹出窗口
						if (CubeModuleManager.getInstance().getInstalled_map()
								.size() != 0) {
							for (List<CubeModule> list : CubeModuleManager
									.getInstance().getInstalled_map().values()) {

								isAutoShowModules.addAll(list);
							}
						}

						Boolean isExit = false;//
						if (CubeModuleManager.getInstance().getUninstalled_map()
								.size() != 0) {
							for (List<CubeModule> list : CubeModuleManager
									.getInstance().getUninstalled_map().values()) {
								for (CubeModule c : list) {
									// 判断是否有自动下载的字段
									if (c.isAutoDownload()) {
										if (autoDownloadRecord.size() != 0) {
											for (AutoDownloadRecord a : autoDownloadRecord) {
												// 判断需要自动下载的模块 在数据库里是否标识为不下载 1 为下载 0
												// 为不下
												if (c.getIdentifier().equals(
														a.getIdentifier())
														&& a.getHasShow().equals(
																"1")) {
													unInstalledModules.add(c);// 把需要自动下载的模块列表增加到list中
													isExit = true;
													break;
												} else if (c.getIdentifier()
														.equals(a.getIdentifier())
														&& a.getHasShow().equals(
																"0")) {
													isExit = true;
													break;
												}
											}
										}
										if (!isExit) {
											unInstalledModules.add(c);
										}
									}
								}
							}
						}
						// 运行网业务
						// if(CubeModuleManager.getInstance().getUninstalled_map().size()!=0){
						// for(List<CubeModule> list :
						// CubeModuleManager.getInstance().getUninstalled_map().values()){
						// for(CubeModule c :list) {
						// //判断是否有自动下载的字段
						// if(c.isAutoDownload()) {
						// unInstalledModules.add(c);
						// }
						// }
						// }
						// }

						// //弹出更新窗口
						if (updateModules.size() != 0) {
							showUpdateAlert(name);
						}
						// 弹出下载窗口
						if (unInstalledModules.size() != 0
								&& Preferences.getAutoDownload(name,
										Application.sharePref)) {
							showDownloadAlert(name);
						}

						List<AutoShowViewRecord> autoShowViewRecordlist = null;
						try {
							// 查出数据库 自动弹出窗口的信息
							autoShowViewRecordlist = StaticReference.userMf
									.queryBuilder(AutoShowViewRecord.class).query();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						// 判断是不是主界面 如果是管理界面 直接跳出 不做弹出模块处理
						if (Preferences.getAppMainView(Application.sharePref)) {
							return;
						}
						Preferences.saveAppMainView(true, Application.sharePref);
						// 自动弹出指定模块界面
						for (List<CubeModule> list : CubeModuleManager
								.getInstance().getInstalled_map().values()) {
							for (CubeModule c : list) {
								if (c.isAutoShow()
										&& autoShowViewRecordlist.size() == 0) {
									Preferences.saveAppMainView(true,
											Application.sharePref);
									gotoModule(c);// 自动跳转模块
									// 自动弹出模块后 存储当前时间
									AutoShowViewRecord autoShowViewRecord = new AutoShowViewRecord();
									autoShowViewRecord.setShowTime(System
											.currentTimeMillis());
									autoShowViewRecord.setShowIntervalTime(c
											.getShowIntervalTime());
									autoShowViewRecord.setUserName(Preferences
											.getUserName(Application.sharePref));
									autoShowViewRecord.setTimeUnit(c.getTimeUnit());
									StaticReference.userMf
											.createOrUpdate(autoShowViewRecord);
									return;
								} else {
									if (c.isAutoShow()
											&& saveViewModule(
													autoShowViewRecordlist.get(0),
													c)) {

										Preferences.saveAppMainView(true,
												Application.sharePref);
										gotoModule(c);// 自动跳转模块
										// 更新时间
										AutoShowViewRecord autoShowViewRecord = autoShowViewRecordlist
												.get(0);
										autoShowViewRecord.setShowTime(System
												.currentTimeMillis());
										autoShowViewRecord.setShowIntervalTime(c
												.getShowIntervalTime());
										autoShowViewRecord
												.setUserName(Preferences
														.getUserName(Application.sharePref));
										autoShowViewRecord.setTimeUnit(c
												.getTimeUnit());
										StaticReference.userMf
												.createOrUpdate(autoShowViewRecord);
										return;
									}
								}
							}
						}
					}

					@Override
					public void syncFail() {
						// cancelDialog();
						// callbackContext.error("sync error");
						callbackContext.success("sync success");
					}
				}, cubeApp, cordova.getActivity(), true);
			}

		} else if (action.equals("setting")) {
			Intent i = new Intent();
			i.setClass(cordova.getActivity(), SettingsActivity.class);
			cordova.getActivity().startActivity(i);
		} else if (action.equals("manager")) {
			cordova.getActivity().sendBroadcast(
					new Intent(BroadcastConstans.JumpToCubeManager));
		} else if (action.equals("setTheme")) {// 设置皮肤
			Intent i = new Intent(BroadcastConstans.CHANGE_SKIN);
			cordova.getActivity().sendBroadcast(i);

		} else if (action.equals("chat")) {

		} else {
			String identifier = args.getString(0);

			CubeModule module = CubeModuleManager.getInstance()
					.getModuleByIdentify(identifier);
			if (module == null)
				return false;
			if (action.equals("upgrade")) {
				// 升级模块，先取出新版本的模块
				module = CubeModuleManager.getInstance()
						.getIdentifier_new_version_map().get(identifier);
				app.upgrade(module);
			} else if (action.equals("install")) {
				// 安装模块
				app.install(module);
			} else if (action.equals("uninstall")) {
				// 卸载模块
				Log.i("AAA", "进入删除");
				app.uninstall(module);
			} else if (action.equals("checkDepends")) {
				// 检查依赖
			} else if (action.equals("showModule")) {
				String type = args.getString(1);

				// 效能监控点击模块保存数据
				Application.class.cast(cordova.getActivity().getApplication())
						.saveModulerRecord(module);

				if (type.equals("main")) {
					gotoModule(module);
				} else {
					Intent i = new Intent();
					if (PadUtils.isPad(cordova.getActivity())) {
						PropertiesUtil propertiesUtil = PropertiesUtil
								.readProperties(
										CubeModuleOperatorPlugin.this.cordova
												.getActivity(),
										CubeConstants.CUBE_CONFIG);
						String moduleDetailFragment = propertiesUtil.getString(
								"moduleDetailFragment", "");
						i.putExtra("identifier", module.getIdentifier());
						if (type.equals("upgrade")) {
							i.putExtra("FROM_UPGRAGE", "FROM_UPGRAGE");
						}
						i.putExtra("version", module.getVersion());
						i.putExtra("build", module.getBuild());
						i.putExtra("direction", 2);
						i.putExtra("type", "fragment");
						i.putExtra("value", moduleDetailFragment);
						i.setClass(cordova.getActivity(), FacadeActivity.class);
						cordova.getActivity().startActivity(i);
					} else {
						Intent intent = new Intent();
						intent.putExtra("identifier", module.getIdentifier());
						i.putExtra("version", module.getVersion());
						i.putExtra("build", module.getBuild());
						if (CubeModule.UPGRADABLE == module.getModuleType()
								|| CubeModule.UPGRADING == module
										.getModuleType()) {
							intent.putExtra("FROM_UPGRAGE", "FROM_UPGRAGE");
						}
						intent.setClass(cordova.getActivity(),
								AppDetailActivity.class);
						cordova.getActivity().startActivity(intent);
					}

				}
			}
		}
		return true;
	}

	// 弹出更新提示
	public void showUpdateAlert(final String name) {
		StringBuffer sb = new StringBuffer();
		for (CubeModule module : updateModules) {
			sb.append("[" + module.getName() + " " + module.getVersion() + "]"
					+ "\n");
		}

		needUpdateDialog = new AlertDialog.Builder(cordova.getActivity())
				.setTitle("提示")
				.setMessage("需要更新以下模块吗?" + "\n" + sb.toString())

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(cordova.getActivity(), "正在更新模块",
								Toast.LENGTH_LONG).show();
						// 更新相关模块
						Intent i = new Intent(BroadcastConstans.SHOWDIOLOG);
						cordova.getActivity().sendBroadcast(i);
						for (CubeModule module : updateModules) {
							Application.class.cast(
									cordova.getActivity()
											.getApplicationContext()).upgrade(
									module);
							// Intent i = new
							// Intent(BroadcastConstans.RefreshMainPage);
							// i.putExtra("identifier", module.getIdentifier());
							// i.putExtra("type", "upgrade");
							// cordova.getActivity().sendBroadcast(i);

						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	// 判断数据库的时间 是否已超过服务器返回时间
	public boolean saveViewModule(AutoShowViewRecord autoShowViewRecord,
			CubeModule module) {
		if (module.getShowIntervalTime().equals("")
				|| module.getShowIntervalTime() == null
				|| module.getShowIntervalTime().length() == 0) {
			return false;
		}
		int interval = Integer.parseInt(module.getShowIntervalTime());
		if (interval == 0) {
			return false;
		}
		@SuppressWarnings("null")
		long time = autoShowViewRecord.getShowTime();
		if (module.getTimeUnit().equals("H")) {// 时
			if ((System.currentTimeMillis() - time) > (interval * 60 * 60 * 1000l)) {

				return true;
			}
		} else if (module.getTimeUnit().equals("M")) {// 分
			if ((System.currentTimeMillis() - time) > (interval * 60 * 1000l)) {
				return true;
			}
		} else if (module.getTimeUnit().equals("S")) {// 秒
			if ((System.currentTimeMillis() - time) > (interval * 1000l)) {

				return true;
			}
		}
		return false;
	}

	// 弹出下载提示窗口
	public void showDownloadAlert(final String name) {
		StringBuffer sb = new StringBuffer();
		// 提示需要下载的模块
		for (CubeModule module : unInstalledModules) {
			sb.append("[" + module.getName() + " " + " " + module.getVersion()
					+ "]" + "\n");
		}
		needDownloadDialog = new AlertDialog.Builder(cordova.getActivity())

				.setTitle("提示")
				.setMessage("需要下载模块吗?" + "\n" + sb.toString())
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(cordova.getActivity(), "正在下载模块",
								Toast.LENGTH_LONG).show();
						// 下载模块
						Intent i = new Intent(BroadcastConstans.SHOWDIOLOG);
						cordova.getActivity().sendBroadcast(i);
						for (CubeModule module : unInstalledModules) {
							Application.class.cast(
									cordova.getActivity()
											.getApplicationContext()).install(
									module);
						}

						Pool.getPool().execute(new Runnable() {
							@Override
							public void run() {

								// 存储数据 如果点击不下载 以后都不提示
								for (CubeModule c : unInstalledModules) {
									AutoDownloadRecord autoDownloadRecord = new AutoDownloadRecord();
									autoDownloadRecord.setHasShow("0");
									autoDownloadRecord.setIdentifier(c
											.getIdentifier());
									autoDownloadRecord.setUserName(Preferences
											.getUserName(Application.sharePref));
									StaticReference.userMf
											.createOrUpdate(autoDownloadRecord);
								}
							}
						});

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// //存储数据
						Pool.getPool().execute(new Runnable() {
							@Override
							public void run() {
								// 存储数据 如果点击不下载 以后都不提示
								for (CubeModule c : unInstalledModules) {
									AutoDownloadRecord autoDownloadRecord = new AutoDownloadRecord();
									autoDownloadRecord.setHasShow("0");
									autoDownloadRecord.setIdentifier(c
											.getIdentifier());
									autoDownloadRecord.setUserName(Preferences
											.getUserName(Application.sharePref));
									StaticReference.userMf
											.createOrUpdate(autoDownloadRecord);
								}
							}
						});
					}
				}).show();
	}

	public void gotoModule(CubeModule module) {
		if (module != null
				&& !TmpConstants.MESSAGE_RECORD_IDENTIFIER.equals(module
						.getIdentifier())
				&& !TmpConstants.ANNOUCE_RECORD_IDENTIFIER.equals(module
						.getIdentifier())) {
			MessageFragmentModel.instance().readAllRecordsByModule(
					module.getName());
		}
		String identifier = module.getIdentifier();

		// 模块是否本地模块
		if (module.getLocal() == null) {
			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/" + cordova.getActivity().getPackageName();
			String url = path + "/www/" + identifier;
			// 检查文件是否存在
			if (new FileCopeTool(cordova.getActivity()).isfileExist(url,
					"index.html")) {

				Intent intent = new Intent();
				if (PadUtils.isPad(cordova.getActivity())) {
					intent.setClass(cordova.getActivity(), FacadeActivity.class);
					intent.putExtra("direction", 2);
					intent.putExtra("type", "web");
					intent.putExtra("value", "file:/" + url + "/index.html");
				} else {
					// TODO
					// intent.setClass(cordova.getActivity(),
					// FacadeActivity.class);
					intent.setClass(cordova.getActivity(), CubeAndroid.class);
					// intent.putExtra("type", "web");
					intent.putExtra("isPad", false);
					// intent.putExtra("value", "file://" + url +
					// "/index.html");

					intent.putExtra("from", "main");
					intent.putExtra("path", Environment
							.getExternalStorageDirectory().getPath()
							+ "/"
							+ cordova.getActivity().getPackageName());
					intent.putExtra("identify", identifier);
				}

				cordova.getActivity().startActivity(intent);

			} else {
				Toast.makeText(cordova.getActivity(), "文件缺失，请重新下载",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			// 模块为本地模块
			Intent intent = new Intent();
			if (PadUtils.isPad(cordova.getActivity())) {
				PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(
						CubeModuleOperatorPlugin.this.cordova.getActivity(),
						CubeConstants.CUBE_CONFIG);
				String className = propertiesUtil.getString(
						module.getIdentifier(), "");

				if (!TextUtils.isEmpty(className)) {
					intent.putExtra("direction", 2);
					intent.putExtra("type", "fragment");
					intent.putExtra("value", className);
					intent.setClass(cordova.getActivity(), FacadeActivity.class);
					cordova.getActivity().startActivity(intent);
				}
			} else {
				// 手机跳转Activity

				PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(
						CubeModuleOperatorPlugin.this.cordova.getActivity(),
						CubeConstants.CUBE_CONFIG);
				String className = propertiesUtil.getString(
						"phone_" + module.getIdentifier(), "");
				Intent i = new Intent();
				i.setClassName(cordova.getActivity(), className);
				cordova.getActivity().startActivity(i);
			}

		}
	}

	public Dialog progressDialog;

	public void showCustomDialog(boolean cancelable) {
		if (progressDialog == null) {
			progressDialog = new Dialog(this.cordova.getActivity(),
					R.style.dialog);
			progressDialog.setContentView(R.layout.dialog_layout);
		}

		if (progressDialog.isShowing()) {
			return;
		}
		progressDialog.setCancelable(cancelable);
		progressDialog.show();
	}

	public void cancelDialog() {
		if (progressDialog == null) {
			return;
		}
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}
	
	class OffLine {
		private boolean offline = false;

		@Override
		public String toString() {
			return "offLine [offline=" + offline + "]";
		}
	}
}