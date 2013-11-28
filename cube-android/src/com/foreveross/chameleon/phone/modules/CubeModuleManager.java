package com.foreveross.chameleon.phone.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * [模块管理器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-13]
 */
public class CubeModuleManager {
	/**
	 * [主面板]
	 */
	public final static int IN_MAIN = 0;
	/**
	 * [已经安装面板]
	 */
	public final static int IN_INSTALLED = 1;
	/**
	 * [未安装面板]
	 */
	public final static int IN_UNINSTALLED = 2;
	/**
	 * [可更新面板]
	 */
	public final static int IN_UPDATABLE = 3;

	private static CubeModuleManager cubeModuleManager = new CubeModuleManager();

	private CubeModuleManager() {

	}

	/**
	 * [得到单例]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-9-16 上午10:40:29
	 */
	public static CubeModuleManager getInstance() {
		return cubeModuleManager;
	}

	public void init(CubeApplication app) {
		Set<CubeModule> modules = app.getModules();
		if (null != modules) {
			modules.remove(null);
		}
		identifier_map.clear();
		identifier_new_version_map.clear();
		identifier_old_version_map.clear();

		all_map.clear();
		Map<CubeModule, Integer> main_maptemp = new HashMap<CubeModule, Integer>();
		for (String key : main_map.keySet()) {
			List<CubeModule> list = main_map.get(key);
			for (CubeModule cubeModule : list) {
				main_maptemp.put(cubeModule, cubeModule.getMsgCount());
			}
		}
		main_map.clear();

		installed_map.clear();
		uninstalled_map.clear();
		updatable_map.clear();
		allSet.clear();

		for (CubeModule module : app.getOldUpdateModules().values()) {
			identifier_old_version_map.put(module.getIdentifier(), module);
		}
		for (CubeModule module : app.getNewUpdateModules().values()) {
			identifier_new_version_map.put(module.getIdentifier(), module);
		}

		for (CubeModule cubeModule : modules) {
			// allSet包含已安装对象以及待升级对象
			allSet.add(cubeModule);
			// 权限管理
			if (cubeModule.getPrivileges() != null) {
				identifier_map.put(cubeModule.getIdentifier(), cubeModule);
				getSetByCategory(all_map, cubeModule.getCategory()).add(
						cubeModule);
				// 已安装/正在删除状态
				if (cubeModule.getModuleType() == CubeModule.INSTALLED
						|| cubeModule.getModuleType() == CubeModule.DELETING) {

					if (!cubeModule.isHidden()) {
						getSetByCategory(main_map, cubeModule.getCategory())
								.add(cubeModule);
					}
					getSetByCategory(installed_map, cubeModule.getCategory())
							.add(cubeModule);
					// 未安装
				} else if (cubeModule.getModuleType() == CubeModule.UNINSTALL) {

					getSetByCategory(uninstalled_map, cubeModule.getCategory())
							.add(cubeModule);
					// 正在安装
				} else if (cubeModule.getModuleType() == CubeModule.INSTALLING) {

					getSetByCategory(uninstalled_map, cubeModule.getCategory())
							.add(cubeModule);
					if (!cubeModule.isHidden()) {
						getSetByCategory(main_map, cubeModule.getCategory())
								.add(cubeModule);
					}
					// 正在升级
				} else if (cubeModule.getModuleType() == CubeModule.UPGRADING) {

					getSetByCategory(updatable_map, cubeModule.getCategory())
							.add(cubeModule);
					if (!cubeModule.isHidden()) {
						// 移除旧的，增添新的
						CubeModule oldModule = getIdentifier_old_version_map()
								.get(cubeModule.getIdentifier());
						getSetByCategory(main_map, cubeModule.getCategory())
								.remove(oldModule);
						getSetByCategory(main_map, cubeModule.getCategory())
								.add(cubeModule);
					}
					// 可升级的
				} else if (cubeModule.getModuleType() == CubeModule.UPGRADABLE) {

					getSetByCategory(updatable_map, cubeModule.getCategory())
							.add(cubeModule);

				}

			}
		}

		for (String key : main_map.keySet()) {
			List<CubeModule> list = main_map.get(key);
			for (CubeModule module : list) {
				if (main_maptemp.containsKey(module)){
					module.setMsgCount(main_maptemp.get(module));
				}
			}
		}

	}

	/**
	 * [记录模块标识与模块的关系]<BR>
	 * identifier-->CubeModule<BR>
	 */
	private volatile Map<String, CubeModule> identifier_map = new HashMap<String, CubeModule>();
	/**
	 * [记录所有模块分类与模块的关系]<BR>
	 * category-->List<CubeModule><BR>
	 */
	private volatile Map<String, List<CubeModule>> all_map = new HashMap<String, List<CubeModule>>();
	/**
	 * [记录主页面模块分类与模块的关系]<BR>
	 * category-->List<CubeModule><BR>
	 */
	private volatile Map<String, List<CubeModule>> main_map = new HashMap<String, List<CubeModule>>();
	/**
	 * [记录安装页面模块分类与模块的关系]<BR>
	 * category-->List<CubeModule><BR>
	 */
	private volatile Map<String, List<CubeModule>> installed_map = new HashMap<String, List<CubeModule>>();

	/**
	 * [记录未安装页面模块分类与模块的关系]<BR>
	 * category-->List<CubeModule><BR>
	 */
	private volatile Map<String, List<CubeModule>> uninstalled_map = new HashMap<String, List<CubeModule>>();
	/**
	 * [记录可更新页面模块分类与模块的关系]<BR>
	 * category-->List<CubeModule><BR>
	 */
	private volatile Map<String, List<CubeModule>> updatable_map = new HashMap<String, List<CubeModule>>();
	/**
	 * [记录所有老板版本]<BR>
	 * identifier-->CubeModule<BR>
	 */
	private volatile Map<String, CubeModule> identifier_old_version_map = new HashMap<String, CubeModule>();
	/**
	 * [记录所有新版本]<BR>
	 * identifier-->CubeModule<BR>
	 */
	private volatile Map<String, CubeModule> identifier_new_version_map = new HashMap<String, CubeModule>();
	/**
	 * [记录所有模块]
	 */
	private volatile Set<CubeModule> allSet = new HashSet<CubeModule>();

	public Set<CubeModule> getAllSet() {
		return allSet;
	}

	public Map<String, CubeModule> getIdentifier_old_version_map() {
		return identifier_old_version_map;
	}

	public Map<String, CubeModule> getIdentifier_new_version_map() {
		return identifier_new_version_map;
	}

	public Map<String, CubeModule> getIdentifier_map() {
		return Collections.unmodifiableMap(identifier_map);
	}

	public Map<String, List<CubeModule>> getInstalled_map() {
		return Collections.unmodifiableMap(installed_map);
	}

	public Map<String, List<CubeModule>> getUninstalled_map() {
		return Collections.unmodifiableMap(uninstalled_map);
	}

	public Map<String, List<CubeModule>> getUpdatable_map() {
		return Collections.unmodifiableMap(updatable_map);
	}

	public Map<String, List<CubeModule>> getMain_map() {
		return Collections.unmodifiableMap(main_map);
	}

	public Map<String, List<CubeModule>> getAll_map() {
		return Collections.unmodifiableMap(all_map);
	}

	// idenfiier=(identifier+build)
	public CubeModule getCubeModuleByIdentifier(String identifier) {
		return identifier_map.get(identifier);
	}

	public List<CubeModule> getSetByCategory(int inWhere, String category) {
		switch (inWhere) {
		case IN_MAIN:
			return main_map.get(category);
		case IN_INSTALLED:
			return installed_map.get(category);
		case IN_UNINSTALLED:
			return uninstalled_map.get(category);
		case IN_UPDATABLE:
			return updatable_map.get(category);
		default:
			return null;
		}

	}

	public Map<String, List<CubeModule>> getMapByWhrere(int inWhere) {
		switch (inWhere) {
		case IN_MAIN:
			return main_map;
		case IN_INSTALLED:
			return installed_map;
		case IN_UNINSTALLED:
			return uninstalled_map;
		case IN_UPDATABLE:
			return updatable_map;
		default:
			return null;
		}
	}

	public void add2Whrere(int inWhere, CubeModule cubeModule) {
		switch (inWhere) {
		case IN_MAIN:
			getSetByCategory(main_map, cubeModule.getCategory())
					.add(cubeModule);
		case IN_INSTALLED:
			getSetByCategory(installed_map, cubeModule.getCategory()).add(
					cubeModule);
		case IN_UNINSTALLED:
			getSetByCategory(uninstalled_map, cubeModule.getCategory()).add(
					cubeModule);
		case IN_UPDATABLE:
			getSetByCategory(updatable_map, cubeModule.getCategory()).add(
					cubeModule);
		}
	}

	public boolean removeFormWhrere(int inWhere, CubeModule cubeModule) {
		switch (inWhere) {
		case IN_MAIN:
			return getSetByCategory(main_map, cubeModule.getCategory()).remove(
					cubeModule);
		case IN_INSTALLED:
			return getSetByCategory(installed_map, cubeModule.getCategory())
					.remove(cubeModule);
		case IN_UNINSTALLED:
			return getSetByCategory(uninstalled_map, cubeModule.getCategory())
					.remove(cubeModule);
		case IN_UPDATABLE:
			return getSetByCategory(updatable_map, cubeModule.getCategory())
					.remove(cubeModule);
		default:
			return false;
		}
	}

	public void add2Main(CubeModule cubeModule) {
		removeFormMain(cubeModule);
		removeOldVersion(main_map, cubeModule);
		getSetByCategory(main_map, cubeModule.getCategory()).add(cubeModule);
	}

	public boolean removeFormMain(CubeModule cubeModule) {
		if (getSetByCategory(main_map, cubeModule.getCategory()).contains(
				cubeModule)) {
			return getSetByCategory(main_map, cubeModule.getCategory()).remove(
					cubeModule);
		} else
			return false;
	}

	public boolean removeFormUninstalled(CubeModule cubeModule) {
		return getSetByCategory(uninstalled_map, cubeModule.getCategory())
				.remove(cubeModule);
	}

	public boolean removeFormInstalled(CubeModule cubeModule) {
		return getSetByCategory(installed_map, cubeModule.getCategory())
				.remove(cubeModule);
	}

	public boolean removeFormUpdatable(CubeModule cubeModule) {
		return getSetByCategory(updatable_map, cubeModule.getCategory())
				.remove(cubeModule);
	}

	public boolean add2Uninstalled(CubeModule cubeModule) {
		removeFormUninstalled(cubeModule);
		removeOldVersion(uninstalled_map, cubeModule);
		return getSetByCategory(uninstalled_map, cubeModule.getCategory()).add(
				cubeModule);
	}

	public boolean add2Installed(CubeModule cubeModule) {
		removeFormInstalled(cubeModule);
		removeOldVersion(installed_map, cubeModule);
		return getSetByCategory(installed_map, cubeModule.getCategory()).add(
				cubeModule);
	}

	public boolean add2Updatable(CubeModule cubeModule) {
		removeFormUpdatable(cubeModule);
		removeOldVersion(updatable_map, cubeModule);
		return getSetByCategory(updatable_map, cubeModule.getCategory()).add(
				cubeModule);
	}

	private List<CubeModule> getSetByCategory(
			Map<String, List<CubeModule>> map, String category) {
		List<CubeModule> values = map.get(category);
		if (values == null) {
			map.put(category, values = new ArrayList<CubeModule>());
		}
		return values;
	}

	private void removeOldVersion(Map<String, List<CubeModule>> map,
			CubeModule oldVerison) {
		CubeModule oldVersion = getIdentifier_old_version_map().get(
				oldVerison.getIdentifier());
		if (oldVersion != null) {
			getSetByCategory(map, oldVerison.getCategory()).remove(oldVersion);
		}
	}

	public CubeModule getModuleByIdentify(String identify) {
		CubeModule module = null;
		for (CubeModule m : allSet) {
			if (m.getIdentifier().equals(identify)) {
				module = m;
				break;
			}
		}

		return module;
	}

	/**
	 * [得到最新版本]<BR>
	 * [功能详细描述]
	 * 
	 * @param identifier
	 * @return 2013-9-16 上午11:48:55
	 */
	public CubeModule getNewModuleByIdentify(String identify) {
		return identifier_new_version_map.get(identify);
	}

	/**
	 * [得到同一模块的版本个数]<BR>
	 * [功能详细描述]
	 * 
	 * @param identify
	 * @return 2013-9-16 上午11:48:30
	 */
	public int getModuleCount(String identify) {
		int size = 0;
		for (CubeModule m : allSet) {
			if (m.getIdentifier().equals(identify)) {
				size++;
			}
		}
		return size;
	}

}
