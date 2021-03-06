package com.foreveross.chameleon.phone.modules;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.CubeConstants;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.ModuleChangedEvent;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.phone.modules.task.UnZipTask;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.util.FileCopeTool;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.otto.ThreadEnforcer;

public class CubeApplication implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1698650844370589053L;

	private String name = null;

	private String releaseNote = null;

	private String icon = null;

	private String bundle = null;

	private String platform = null;

	private String version = null;

	private int build = 0;

	private String identifier = null;
	private Set<CubeModule> internalModules = new HashSet<CubeModule>();
	// 已安装的模块
	private Set<CubeModule> modules = new HashSet<CubeModule>();
	// 模块可升级时，旧版本和新版本的容器
	private Map<String, CubeModule> oldUpdateModules = new HashMap<String, CubeModule>();
	private Map<String, CubeModule> newUpdateModules = new HashMap<String, CubeModule>();

	private static CubeApplication instance;
	
	private CubeApplication localCubeApplication;

	public transient FileCopeTool tool;

	private transient Context context;

	private static Context mContext;
	private String appKey = "";

	public static CubeApplication getInstance(Context context) {
		if (instance == null) {
			instance = new CubeApplication(context);
		}
		return instance;
	}

	public static CubeApplication resetInstance(Context context) {
		instance = null;
		return getInstance(context);
	}

	public CubeApplication(Context context) {

		this.context = context;
		mContext = context;
		tool = new FileCopeTool(context);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReleaseNote() {
		return releaseNote;
	}

	public void setReleaseNote(String releaseNote) {
		this.releaseNote = releaseNote;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Set<CubeModule> getModules() {
		return modules;
	}

	public void setModules(Set<CubeModule> modules) {
		this.modules = modules;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void copyProperties(CubeApplication a) {
		this.identifier = a.getPackageName();
		this.build = a.getVersionCode();
		this.icon = a.getIcon();
		this.modules = a.getModules();
		this.name = a.getName();
		this.releaseNote = a.getReleaseNote();
		this.version = a.getVersionName();
		this.platform = a.getPlatform();
		this.appKey = a.getAppKey();
	}

	public void loadApplication() {
		// 拿到当前登陆的用户名
		String username = Preferences.getUserName(Application.sharePref);
		String systemId = Preferences.getSystemId(Application.sharePref);
		String path = Environment.getExternalStorageDirectory().getPath();
		if(!tool.isfileExist(path+"/" + context.getPackageName()+"/www", "com.csair.deviceregist"))
		{
			tool.CopyAssets("www/com.csair.deviceregist",path+"/" + context.getPackageName()+"/www/com.csair.deviceregist");
		}
		if (this.isUserExist(username , systemId)) {
			// 运行目录读取
			System.out.println("运行时目录读取");
			
			String results = tool.readerFile(
					path + "/" + context.getPackageName() , "Cube-" + username + "_"
							+ systemId + ".json");
			CubeApplication app = buildApplication(results);
			// 同步前先显示ui
			boolean outline = Preferences.getOutLine(Application.sharePref);
			if (outline) {
				CubeModuleManager.getInstance().init(app);
			}
			app.context = this.context;
			// 同步预置模块
			if (app.getBuild() != getVersionCode()) {
				syncAsset(app);
				try {
					tool.copyOneFileToSDCard("www/cordova.js", Environment
							.getExternalStorageDirectory().getPath()
							+ "/"
							+ context.getPackageName() + "/www/", "cordova.js");
					tool.copyOneFileToSDCard("www/index.html", Environment
							.getExternalStorageDirectory().getPath()
							+ "/"
							+ context.getPackageName() + "/www/", "index.html");
					System.out.println("copy cordova.js");
				} catch (IOException e) {
					Log.v("COPY_CORDOVA_TAG", "copy cordova.js文件出错");
				}
			}
			copyProperties(app);

		} else {
			// assets目录读取
			System.out.println("assets目录读取");
			String results = tool.getFromAssets("Cube.json");
			CubeApplication app = buildApplication(results);
			app.context = this.context;
			copyProperties(app);
			Application application = Application.class.cast(this.context);
			localCubeApplication = null;
			if(application.fileIsExist("CubeConfig.json"))
			{
				String newRes = tool.getFromAssets("ConfigModule/CubeConfig.json");
				if(newRes != null && newRes.length()>0)
				{
					Log.e("BUILD_TAG", newRes);
					localCubeApplication = buildApplication(newRes,"local");
					localCubeApplication.context = this.context;
					
				}
			}
//			if(newApp != null)
//			{
//				instance = compareAndSetApp(this, newApp);
//				internalModules.addAll(instance.getModules());
//				Log.e("LOAD_TAG", "加载cubeconfig.json");
//			}

			// for (CubeModule each : app.getModules()) {
			// if(each.getLocal()!=null)
			// {
			// each.setModuleType(CubeModule.INSTALLED);
			// } else {
			// each.setModuleType(CubeModule.UNINSTALL);
			// }
			// }
			/** 安装应用信息 */
			try {
				install();
				// syncConfigModule();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadLocalModule()
	{
		if(localCubeApplication != null)
		{
			instance = compareAndSetApp(this, localCubeApplication);
			internalModules.addAll(instance.getModules());
			Log.e("LOAD_TAG", "加载cubeconfig.json");
			
		}
	}
	
	
	/**
	 * 下载 cubejson
	 */
	public void downloadCubeJosn(
			final DownloadCubeJsonSyncListener downloadListener,
			String username, String sessionKey) {
		HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
			}

			@Override
			public void doPreExecuteBeforeDialogShow() {
				super.doPreExecuteBeforeDialogShow();
			}

			@Override
			protected void doPostExecute(String result) {
				downloadListener.downloadFinish();
				System.out.println("下载的结果:" + result);
				CubeApplication app = buildApplication(result);
				CubeModuleManager.getInstance().init(app);

			}

			@Override
			protected void doHttpFail(Exception e) {
				Log.v("sync", "下载失败");
				downloadListener.downloadFail();
				e.printStackTrace();
			}
		};
		String url = "http://10.108.1.100:9091/mam/api/mam/clients/modules/com.foreveross.chameleon/?username="
				+ username + "&sessionKey=" + sessionKey;
		System.out.println("网络请求url == " + url);
		// 3、真正发起http请求
		task.setLockScreen(true);
		task.setShowProgressDialog(true);
		task.setNeedProgressDialog(true);
		task.execute(url, "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
	}

	/**
	 * 判断运行时目录是否存在Cube.json 文件。
	 * 
	 * @return
	 */
	public boolean isInstalled(String name) {
		String path = Environment.getExternalStorageDirectory().getPath();
		Boolean isExist = tool.isfileExist(
				path + "/" + context.getPackageName(), "Cube.json");
		return isExist;
	}

	/**
	 * 判断当前设备是否存在登陆用户的数据
	 * 
	 * @return
	 */
	public boolean isUserExist(String name,String systemId) {
		String path = Environment.getExternalStorageDirectory().getPath();
		Boolean isExist = tool
				.isfileExist(path + "/" + context.getPackageName(), "Cube-"
						+ name + "_" + systemId + ".json");
		return isExist;
	}

	public boolean isUserFileNull() {

		return true;
	}

	/**
	 * 安装应用，将文件复制到运行时目录
	 * 
	 */
	public void install() throws IOException {
		// 拿到当前登陆的用户名
		String username = Preferences.getUserName(Application.sharePref);
		String systemId = Preferences.getSystemId(Application.sharePref);
		if (isUserExist(username , systemId)) {
			tool.deleteFile(Environment.getExternalStorageDirectory().getPath()
					+ "/" + context.getPackageName() + "/" + "Cube-" + username + "_" + systemId+".json");
			System.out.println("删除原有的Cube.json");
		}
		// 复制Assets文件夹中的Cube.json 文件到运行时目录。
		tool.copyOneFileToSDCard("Cube.json",
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ context.getPackageName() + "/", "Cube.json");
		// add by zhoujun begin;
		// 安装时将cordova.js复制到sdcard中
		tool.copyOneFileToSDCard("www/cordova.js",
				Environment.getExternalStorageDirectory().getPath() + "/"
						+ context.getPackageName() + "/www/", "cordova.js");
        
       
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // 复制ASSETS文件夹中的WWW整个文件夹到运行时目录。
		// tool.CopyAssets("www", Environment
		// .getExternalStorageDirectory().getPath()
		// + "/"
		// + context.getPackageName() + "/www");
		//
		// System.out.println("copy cordova.js");
		// }
		// }).start();

	}

	/**
	 * @author Amberlo 安装预装模块 1.读取sdcard中cube.json
	 *         2.读取cubeConfig.json文件，变成CubeApplication对象
	 *         3.将asset文件夹下未安装模块zip文件拷贝到sdcard中 4.save();
	 */
	public void syncConfigModule() {
		String results = tool.getFromAssets("ConfigModule/CubeConfig.json");
		final CubeApplication configApp = buildApplication(results);
		final String basePath = Environment.getExternalStorageDirectory()
				.getPath() + "/" + context.getPackageName();
		tool.CopyAssets("ConfigModule", basePath);
		final CubeApplication app = this;
		for (final CubeModule module : configApp.getModules()) {

			UnZipTask unZipTask = new UnZipTask(context, app, module) {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					module.setModuleType(CubeModule.INSTALLING);
					module.setProgress(100);
				}

				@Override
				protected void onPostExecute(Boolean result) {
					super.onPostExecute(result);
					if (result) {
						module.setModuleType(CubeModule.INSTALLED);
						CubeModuleManager.getInstance().removeFormUninstalled(
								module);
						CubeModuleManager.getInstance().add2Installed(module);
						EventBus.getEventBus(
								TmpConstants.EVENTBUS_MODULE_CHANGED,
								ThreadEnforcer.MAIN).post(
								new ModuleChangedEvent(module.getIdentifier(),
										module.getCategory()));
						boolean outline = Preferences.getOutLine(Application.sharePref);
						if (!outline){
							save(app);
						}
					} else {
						module.setModuleType(CubeModule.UNINSTALL);
						if (!module.isHidden()) {
							CubeModuleManager.getInstance().removeFormMain(
									module);
						}

						Toast.makeText(context, "安装失败!", Toast.LENGTH_SHORT)
								.show();

						EventBus.getEventBus(
								TmpConstants.EVENTBUS_MODULE_CHANGED,
								ThreadEnforcer.MAIN).post(
								new ModuleChangedEvent(module.getIdentifier(),
										module.getCategory()));
					}

				}
			};

			unZipTask.execute();

		}

		app.getModules().addAll(configApp.getModules());
		boolean outline = Preferences.getOutLine(Application.sharePref);
		if (!outline){
			app.save(app);
		}
	}

	/**
	 * 根据本地cube.json文件与assets文件同步
	 * 
	 * @param
	 * @return
	 */
	private void syncAsset(CubeApplication sdCardApp) {
		// assets目录读取
		System.out.println("assets目录读取");
		String results = tool.getFromAssets("Cube.json");
		CubeApplication assetApp = buildApplication(results);

		// sdCardApp.build=assetApp.build;
		sdCardApp.bundle = assetApp.bundle;
		sdCardApp.icon = assetApp.icon;
		sdCardApp.releaseNote = assetApp.releaseNote;
		sdCardApp.identifier = assetApp.identifier;
		sdCardApp.name = assetApp.name;
		sdCardApp.platform = assetApp.platform;
		sdCardApp.version = assetApp.version;

		compareAssetBase(sdCardApp, assetApp);

	}

	public void compareAssetBase(CubeApplication oldApp, CubeApplication newApp) {

		Set<CubeModule> set = oldApp.getModules();
		Set<CubeModule> newSet = new HashSet<CubeModule>();
		// 删除旧的本地模块
		for (CubeModule cubeModule : set) {
			if (null != cubeModule.getLocal()) {
				newSet.add(cubeModule);
			}
		}
		set.removeAll(newSet);

		// 把asset拷贝进json文件
		set.addAll(newApp.getModules());

	}

	/**
	 * 与服务器同步应用状态，获取模块更新
	 */
	public void sync(final ApplicationSyncListener listener,
			final CubeApplication app, Context context, boolean dialogNeed) {
		HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
				listener.syncStart();
			}

			@Override
			public void doPreExecuteBeforeDialogShow() {
				super.doPreExecuteBeforeDialogShow();
				listener.syncStart();
			}

			@Override
			protected void doPostExecute(String result) {
				if (result == null) {
					listener.syncFail();
					return;
				}
				// {"message":"获取应用信息失败！","result":"error"}
				try {
					JSONObject jb = new JSONObject(result);
					if (jb.getString("result").equals("error")) {
						listener.syncFail();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				CubeApplication remote_app = CubeApplication
						.buildApplication(result);
				if (remote_app.getModules() == null) {
					listener.syncFail();
					return;
				}
				CubeApplication comparedCubeApp = compareAndSetApp(app,
						remote_app);
				Application.class.cast(context.getApplicationContext())
						.setCubeApplication(comparedCubeApp);
				CubeModuleManager.getInstance().init(comparedCubeApp);
				context.sendBroadcast(new Intent("com.csair.cubeModelChange")
						.putExtra("identifier", "none"));

				
				boolean outline = Preferences.getOutLine(Application.sharePref);
				if (!outline){
					save(app);
				}
				listener.syncFinish();
			}

			@Override
			protected void doHttpFail(Exception e) {
				Log.v("sync", "同步失败");
				listener.syncFail();
				e.printStackTrace();
			}
		};
		// String url =
		// "http://58.215.176.89:9000/m/apps/com.foreveross.cube.android/4";
		// String url = URL.BASE + "m/apps/" + getPackageName() + "/"+
		// app.getVersionCode()
		// +"?"+"token="+Preferences.getToken(Application.sharePref) + "&"
		// +"timeStamp="+ time;
		
		String appId = app.getPackageName();
		String version = app.getVersion();
		String platform = "android";
		String username = Preferences.getUserName(Application.sharePref);
		StringBuilder sb = new StringBuilder();
		sb = sb.append("Form:username=").append(username)
				.append(";platform=").append(platform)
				.append(";version=").append(version)
				.append(";identifier=").append(appId)
				.append(";sysId=").append(Preferences.getSystemId(Application.sharePref))
				;
		// 3、真正发起http请求
		task.setLockScreen(false);
		task.setDialogContent("正在同步...");
		task.setShowProgressDialog(dialogNeed);
		task.setNeedProgressDialog(dialogNeed);
		String s = sb.toString();
		String url = URL.SYNC + "?" +"sessionKey=" + Preferences.getSESSION(Application.sharePref);
		task.execute(url, s, HttpUtil.UTF8_ENCODING,
				HttpUtil.HTTP_POST);
	}

	public synchronized CubeApplication compareAndSetApp(
			CubeApplication oldOne, CubeApplication newOne) {
		if (oldOne == null) {
			return newOne;
		}

		Set<CubeModule> oldSet = new HashSet<CubeModule>(oldOne.getModules());
		Set<CubeModule> newSet = new HashSet<CubeModule>(newOne.getModules());
		// Map<String, CubeModule> oldHash = bulidMap(oldSet);
		Map<String, CubeModule> newHash = bulidMap(newSet);

		Map<String, CubeModule> unstallMap = new HashMap<String, CubeModule>();
		Map<String, CubeModule> installedMap = new HashMap<String, CubeModule>();
		Map<String, CubeModule> updatableMap = new HashMap<String, CubeModule>();

		oldOne.getOldUpdateModules().clear();
		oldOne.getNewUpdateModules().clear();
		for (CubeModule cubeModule : oldSet) {
			for (CubeModule module : newSet) {
				// 同步本地已存在模块和服务器模块信息
				if (cubeModule.getIdentifier().equals(module.getIdentifier())) {
					// cubeModule.setVersion(module.getVersion());
					cubeModule.setCategory(module.getCategory());
					cubeModule.setAutoDownload(module.isAutoDownload());
					cubeModule.setAutoShow(module.isAutoShow());
					cubeModule.setTimeUnit(module.getTimeUnit());
					cubeModule.setName(module.getName());
					cubeModule.setReleaseNote(module.getReleaseNote());
					cubeModule.setShowIntervalTime(module.getShowIntervalTime());
					cubeModule.setPrivileges(module.getPrivileges());
					cubeModule.setHidden(module.isHidden());
					cubeModule.setDownloadUrl(URL.getDownloadUrl(context,
							module.getBundle(),true));
					cubeModule.setSortingWeight(module.getSortingWeight());
					cubeModule.setInstallIcon(URL.getDownloadUrl(context,module.getIcon()));
					
					
					if (cubeModule.getLocal() == null ) {
						if(cubeModule.getModuleType()!=CubeModule.INSTALLED){
							
							cubeModule.setIcon(URL.getDownloadUrl(context,module.getIcon()));
						}else{
							if(!isExist(module,"icon.img")) {
								if(isExist(module,"icon.png")) {
									cubeModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
									
								}else {
									cubeModule.setIcon(URL.getDownloadUrl(context,module.getIcon()));
								}
							}
							//设置默认图标
						}
					}else {
						PropertiesUtil propertiesUtil = PropertiesUtil
								.readProperties(mContext, CubeConstants.CUBE_CONFIG);
						// 判断本地模块是否存在
						String icon = propertiesUtil.getString(
								"icon_"+cubeModule.getIdentifier(), "");
						cubeModule.setIcon(icon);
					}
					// +"?sessionKey="
					// + Preferences.getSESSION(Application.sharePref)
					// + "?appKey="
					// + oldOne.getAppKey());
					break;
				}
			}
			switch (cubeModule.getModuleType()) {
			case CubeModule.DELETING: {
				installedMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.INSTALLED: {
				installedMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.UNINSTALL: {
				unstallMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.INSTALLING: {
				unstallMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.UPGRADABLE: {
				updatableMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			case CubeModule.UPGRADING: {
				updatableMap.put(cubeModule.getIdentifier(), cubeModule);
				break;
			}
			}
		}

		for (CubeModule cubeNew : newSet) {
			if(cubeNew.getIdentifier().contains("chat")) {
				System.out.println("nm");
			}
			String identify = cubeNew.getIdentifier();
			int build = cubeNew.getBuild();
			if (unstallMap.get(identify) == null
					&& installedMap.get(identify) == null) {

				cubeNew.setDownloadUrl(URL.getDownloadUrl(context,
						cubeNew.getBundle(),true));
				// cubeNew.setIcon(URL.DOWNLOAD +
				// cubeNew.getIcon()+"?sessionKey="
				// + Preferences.getSESSION(Application.sharePref)
				// + "?appKey="
				// + oldOne.getAppKey());
				if(cubeNew.getLocal()!=null) {
					PropertiesUtil propertiesUtil = PropertiesUtil
							.readProperties(mContext, CubeConstants.CUBE_CONFIG);
					// 判断本地模块是否存在
					String icon = propertiesUtil.getString(
							"icon_"+cubeNew.getIdentifier(), "");
					cubeNew.setIcon(icon);
				}else {
					cubeNew.setIcon(URL.getDownloadUrl(context, cubeNew.getIcon()));
					
				}
				unstallMap.put(cubeNew.getIdentifier(), cubeNew);

			} else if (unstallMap.get(identify) != null
					&& build != unstallMap.get(identify).getBuild()) {
				cubeNew.setDownloadUrl(URL.getDownloadUrl(context,
						cubeNew.getBundle()));
				// cubeNew.setIcon(URL.DOWNLOAD +
				// cubeNew.getIcon()+"?sessionKey="
				// + Preferences.getSESSION(Application.sharePref)
				// + "?appKey="
				// + oldOne.getAppKey());
				cubeNew.setIcon(URL.getDownloadUrl(context, cubeNew.getIcon()));
				unstallMap.remove(identify);
				unstallMap.put(cubeNew.getIdentifier(), cubeNew);

			} else if (installedMap.get(identify) != null) {
				CubeModule x = installedMap.get(identify);
				
				if (build <= x.getBuild()) {
					x.setUpdatable(false);
					updatableMap.remove(identify);
					
					// CubeModuleManager.getIdentifier_old_version_map().remove(x.getIdentifier());
					// CubeModuleManager.getIdentifier_new_version_map().remove(cubeNew.getIdentifier());
					// oldOne.getOldUpdateModules().remove(x.getIdentifier());
					// oldOne.getNewUpdateModules().remove(cubeNew.getIdentifier());

				} else if (build > x.getBuild()) {
					x.setUpdatable(true);
					cubeNew.setModuleType(CubeModule.UPGRADABLE);
					cubeNew.setUpdatable(true);
					// cubeNew.setDownloadUrl(URL.DOWNLOAD +
					// cubeNew.getBundle());
					// cubeNew.setIcon(URL.DOWNLOAD +
					// cubeNew.getIcon()+"?sessionKey="
					// + Preferences.getSESSION(Application.sharePref)
					// + "?appKey="
					// + oldOne.getAppKey());
					cubeNew.setDownloadUrl(URL.getDownloadUrl(context,
							cubeNew.getBundle(),true));
					cubeNew.setIcon(URL.getDownloadUrl(context,
							cubeNew.getIcon()));
					updatableMap.put(cubeNew.getIdentifier(), cubeNew);
					// CubeModuleManager.getIdentifier_old_version_map().put(x.getIdentifier(),
					// x);
					// CubeModuleManager.getIdentifier_new_version_map().put(cubeNew.getIdentifier(),
					// cubeNew);
					oldOne.getOldUpdateModules().put(x.getIdentifier(), x);
					oldOne.getNewUpdateModules().put(cubeNew.getIdentifier(),
							cubeNew);

				}
			}

		}

		CopyOnWriteArraySet<CubeModule> set = new CopyOnWriteArraySet<CubeModule>(
				oldSet);
		for (CubeModule module : set) {
			if (!newSet.contains(module)
					&& newHash.get(module.getIdentifier()) == null) {
				unstallMap.remove(module.getIdentifier());
				installedMap.remove(module.getIdentifier());
				updatableMap.remove(module.getIdentifier());
			}
		}
		// 1.未安装：有新旧对象，则取新对象
		// 2.已安装: 看是否有更新对象，如果没有再设置为UPGRADABLE,再设置updatable为true
		// 如果有更新对象，则比较两版本的区别，取最新版

		oldOne.getModules().clear();
		oldOne.getModules().addAll(installedMap.values());
		oldOne.getModules().addAll(updatableMap.values());
		oldOne.getModules().addAll(unstallMap.values());
		
		
		// oldOne.getModules().add(oldHash.get("com.foss.voice"));
		// oldOne.getModules().add(oldHash.get("com.foss.message.record"));
		// oldOne.getModules().add(oldHash.get("com.foss.feedback"));
		// oldOne.getModules().add(oldHash.get("com.foss.announcement"));
		// oldOne.getModules().add(oldHash.get("com.foss.chat"));
		// oldOne.getModules().add(oldHash.get("com.foss.settings"));
		return oldOne;
	}
	
	public boolean isExist(CubeModule cubeModule,String name) {
		String path = URL.getSdPath(context, cubeModule.getIdentifier())+"/"+name;
		File f= new File(path);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public Map<String, CubeModule> bulidMap(Set<CubeModule> list) {
		Map<String, CubeModule> hash = new HashMap<String, CubeModule>();
		for (CubeModule cubeModule : list) {
			if (cubeModule == null) {
				continue;
			}
			hash.put(cubeModule.getIdentifier(), cubeModule);
		}
		return hash;
	}

	public void copyNeededProps(CubeModule src, CubeModule dest) {

		dest.setCategory(src.getCategory());
		// dest.setDownloading(src.isDownloading());
		dest.setDownloadUrl(src.getDownloadUrl());
		dest.setIcon(src.getIcon());
		dest.setIdentifier(src.getIdentifier());
		dest.setName(src.getName());
		dest.setProgress(src.getProgress());
		dest.setReleaseNote(src.getReleaseNote());
		dest.setUpdatable(src.getBuild() > dest.getBuild() ? true : false);
		dest.setVersion(src.getVersion());
		dest.setBuild(src.getBuild());
	}
	public static CubeApplication buildApplication(String json,String flag)
	{
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		CubeApplication result = gson.fromJson(json, CubeApplication.class);
		Set<CubeModule> unExistModules = new HashSet<CubeModule>();
		if("".equals(flag))
		{
			if (result.getModules() != null) {
			
				for (CubeModule each : result.getModules()) {
					if (each.getLocal() != null) {
						// 判断本地模块在本地是否存在如果不存在就删除该模块
						// 南航统一移动平台业务需要勿删
						
						
						
						PropertiesUtil propertiesUtil = PropertiesUtil
								.readProperties(mContext, CubeConstants.CUBE_CONFIG);
						// 判断本地模块是否存在
						if (!propertiesUtil.containsValue(each.getIdentifier())) {
							unExistModules.add(each);
						}
						
						
						each.setModuleType(CubeModule.INSTALLED);
					} else if (each.getModuleType() == -1) {
						each.setModuleType(CubeModule.UNINSTALL);
						// each.setDownloadUrl(URL.DOWNLOAD + each.getBundle());
						// each.setIcon(URL.DOWNLOAD + each.getIcon());
					} else {
						// each.setDownloadUrl(URL.DOWNLOAD + each.getBundle());
						// each.setIcon(URL.DOWNLOAD + each.getIcon());
					}
				}
				//result.getModules().remove(null);
			}
			// 特殊标记用于批量替换勿改
//		// 删除不存在的模块
			//效能监控点击模块保存数据
			
			
			result.getModules().removeAll(unExistModules);
			unExistModules.clear();
		}
		else if("local".equals(flag))
		{
			if (result.getModules() != null) {
			
				for (CubeModule each : result.getModules()) {
					if (each.getLocal() != null) {
						
						PropertiesUtil propertiesUtil = PropertiesUtil
								.readProperties(mContext, CubeConstants.CUBE_CONFIG);
						// 判断本地模块是否存在
						if (!propertiesUtil.containsValue(each.getIdentifier())) {
							unExistModules.add(each);
						}
						each.setModuleType(CubeModule.INSTALLED);
					} else if (each.getModuleType() == -1) {
						each.setModuleType(CubeModule.UNINSTALL);
//						 each.setDownloadUrl(URL.getDownloadUrl(mContext, each.getBundle()));
						// each.setIcon(URL.DOWNLOAD + each.getIcon());
					} else if(each.getModuleType() == 1){
						each.setModuleType(CubeModule.INSTALLED);
//						each.setAutoDownload(true);
						each.setPrivileges("rw");
						
						// each.setDownloadUrl(URL.DOWNLOAD + each.getBundle());
						// each.setIcon(URL.DOWNLOAD + each.getIcon());
					}
				}
			}
			result.getModules().removeAll(unExistModules);
			unExistModules.clear();
		}
		return result;
	}
	public static CubeApplication buildApplication(String json) {
		return buildApplication(json, "");
	}

	public void save(CubeApplication app) {

		String userName = Preferences.getUserName(Application.sharePref);
		String systemId = Preferences.getSystemId(Application.sharePref);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String json = gson.toJson(app.translate());
		try {
			tool.writeToJsonFile("Cube-"+userName + "_" + systemId + ".json",
					Environment.getExternalStorageDirectory().getPath() + "/"
							+ context.getPackageName() + "/", json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void removeModule(CubeModule module) {
		String identify = module.getIdentifier();
		tool.deleteFile(Environment.getExternalStorageDirectory().getPath()
				+ "/" + context.getPackageName() + "/" + identify + ".zip");
		tool.deleteFolder(Environment.getExternalStorageDirectory().getPath()
				+ "/" + context.getPackageName() + "/www/" + identify);
		instance.getModules().remove(module);
		boolean outline = Preferences.getOutLine(Application.sharePref);
		if (!outline){
			save(instance);
		}
	}

	public CubeModule getModuleByIdentify(String identify) {
		CubeModule module = null;
		for (CubeModule m : instance.getModules()) {
			if (m.getIdentifier().equals(identify))
				module = m;
		}
		return module;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPackageName() {
		return context.getPackageName();
	}

	public String getVersionName() {
		PackageManager pm = context.getPackageManager();// context为当前Activity上下文
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";

	}

	public int getVersionCode() {
		PackageManager pm = context.getPackageManager();// context为当前Activity上下文
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// //获取时间戳
	// public long getTime(){
	// Date date = new Date();
	// long time = date.getTime();
	// return time;
	// }
	static class ModuleTypeTypeAdapter implements JsonSerializer<ModuleType>,
			JsonDeserializer<ModuleType> {

		@Override
		public ModuleType deserialize(JsonElement je, Type type,
				JsonDeserializationContext jd) throws JsonParseException {
			if (je.getAsString() == null || je.getAsString().equals("")) {
				return ModuleType.UNINSTALLED;
			}
			return ModuleType.returnModuleType(je.getAsString());
		}

		@Override
		public JsonElement serialize(ModuleType mt, Type type,
				JsonSerializationContext json) {
			if (mt == null) {
				return new JsonPrimitive("INSTALLED");
			}
			return new JsonPrimitive(mt.name());

		}
	}

	public CubeApplication translate() {
		CubeApplication application = new CubeApplication(context);
		application.loadApplication();
		application.setBuild(this.getBuild());
		application.setBundle(this.getBundle());
		application.setIcon(this.getIcon());
		application.setIdentifier(this.getIdentifier());
		application.setName(this.getName());
		application.setPlatform(this.getPlatform());
		application.setReleaseNote(this.getReleaseNote());
		application.setAppKey(this.getAppKey());
		Set<CubeModule> se = new HashSet<CubeModule>();
		for (CubeModule cubeModule : this.getModules()) {
			CubeModule module = new CubeModule();
			module.setBuild(cubeModule.getBuild());
			module.setCategory(cubeModule.getCategory());
			// module.setDownloading(cubeModule.isDownloading());
			module.setDownloadUrl(cubeModule.getDownloadUrl());
			module.setIcon(cubeModule.getIcon());
			module.setAutoDownload(cubeModule.isAutoDownload());
			module.setInstallIcon(cubeModule.getInstallIcon());
			module.setIdentifier(cubeModule.getIdentifier());
			module.setModuleType(cubeModule.getModuleType());
			module.setName(cubeModule.getName());
			module.setProgress(cubeModule.getProgress());
			module.setReleaseNote(cubeModule.getReleaseNote());
			module.setUpdatable(cubeModule.isUpdatable());
			module.setVersion(cubeModule.getVersion());
			module.setLocal(cubeModule.getLocal());
			module.setHidden(cubeModule.isHidden());
			module.setPushMsgLink(cubeModule.getPushMsgLink());
			module.setPrivileges(cubeModule.getPrivileges());
			module.setSortingWeight(cubeModule.getSortingWeight());
			if (cubeModule.getModuleType() == CubeModule.DELETING
					|| cubeModule.getModuleType() == CubeModule.INSTALLING) {
				// CubeModuleManager.removeFormMain(cubeModule);
				module.setModuleType(CubeModule.UNINSTALL);
			} else if (cubeModule.getModuleType() == CubeModule.UPGRADING) {
				module.setModuleType(CubeModule.UPGRADABLE);
			}
			// else if(cubeModule.isHidden()){
			// CubeModuleManager.removeFormMain(cubeModule);
			// }

			se.add(module);
			application.setModules(se);
		}

		application.setVersion(this.getVersion());
		return application;
	}

	public Map<String, CubeModule> getOldUpdateModules() {
		return oldUpdateModules;
	}

	public void setOldUpdateModules(Map<String, CubeModule> oldUpdateModules) {
		this.oldUpdateModules = oldUpdateModules;
	}

	public Map<String, CubeModule> getNewUpdateModules() {
		return newUpdateModules;
	}

	public void setNewUpdateModules(Map<String, CubeModule> newUpdateModules) {
		this.newUpdateModules = newUpdateModules;
	}

	public String getAppKey() {
		return PropertiesUtil.readProperties(mContext, CubeConstants.CUBE_CONFIG).getString(
				"appKey", "");

	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public static Context getmContext() {
		return mContext;
	}

	public Set<CubeModule> getInternalModules() {
		return internalModules;
	}

	public void setInternalModules(Set<CubeModule> internalModules) {
		this.internalModules = internalModules;
	}
	
}
