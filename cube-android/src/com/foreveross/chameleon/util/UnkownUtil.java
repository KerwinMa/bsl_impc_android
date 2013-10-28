/**
 * 
 */
package com.foreveross.chameleon.util;

import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.AsyncTask;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-16]
 */
public class UnkownUtil {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-7-16 上午11:37:55
	 */
	public static List<String> getSort(final int type,
			Map<String, List<CubeModule>> searchResule) {
		List<String> categoryList = new ArrayList<String>();
		Map<String, List<CubeModule>> groupModels = null;
		if (searchResule == null) {
			switch (type) {
			case CubeModuleManager.IN_MAIN:
				groupModels = CubeModuleManager.getInstance().getMain_map();
				break;
			case CubeModuleManager.IN_INSTALLED:
				groupModels = CubeModuleManager.getInstance()
						.getInstalled_map();
				break;
			case CubeModuleManager.IN_UNINSTALLED:
				groupModels = CubeModuleManager.getInstance()
						.getUninstalled_map();
				break;
			case CubeModuleManager.IN_UPDATABLE:
				groupModels = CubeModuleManager.getInstance()
						.getUpdatable_map();
				break;
			default:
				break;
			}
		} else {
			groupModels = searchResule;
		}
		Iterator<Map.Entry<String, List<CubeModule>>> iter = groupModels
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, List<CubeModule>> entry = (Map.Entry<String, List<CubeModule>>) iter
					.next();
			String key = (String) entry.getKey();
			List<CubeModule> values = (List<CubeModule>) entry.getValue();
			if (null == values || values.size() == 0) {
				continue;
			}
			categoryList.add(key);
		}
		Collections.sort(categoryList, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				String rule = null;
				switch (type) {
				case CubeModuleManager.IN_MAIN:
					rule = "< 业务  < 基本";
					break;
				case CubeModuleManager.IN_INSTALLED:
					rule = "< 基本  < 业务";
					break;
				case CubeModuleManager.IN_UNINSTALLED:
					rule = "< 基本  < 业务";
					break;
				case CubeModuleManager.IN_UPDATABLE:
					rule = "< 基本  < 业务";
					break;
				default:
					break;
				}
				RuleBasedCollator collator = null;
				try {
					collator = new RuleBasedCollator(rule);
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return collator.compare(o1.toString(), o2.toString());
			}
		});
		return categoryList;
	}

	/**
	 * 判断最顶层Activity是否为搜索好友页面
	 **/
	public static boolean isTopActivity(Context context,
			Class<? extends Activity> clazz) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationInfo().packageName;
		String activityName = clazz.getName();
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {

			String topActivityName = tasksInfo.get(0).topActivity
					.getClassName();
			String topPackageName = tasksInfo.get(0).topActivity
					.getPackageName();
			if (packageName.equals(topPackageName)
					&& activityName.equals(topActivityName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMessageViewPresence(Context context) {
		Application application = (Application) context.getApplicationContext();
		return application.isMessageViewPresence();
	}

	public static boolean isNoticeViewPresence(Context context) {
		Application application = (Application) context.getApplicationContext();
		return application.isNoticeViewPresence();
	}

	public static boolean isChatViewPresence(Context context) {
		Application application = (Application) context.getApplicationContext();
		return application.isChatViewPresence();
	}

	public static Application getApplication(Context context) {
		if (context != null){
			return Application.class.cast(context.getApplicationContext());
		}
		return null;
	}
	public static void runAsyncInUIThread(Context context,final AsyncTask<?, ?, ?> asyncTask){
		getApplication(context).getUIHandler().post(new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				asyncTask.execute();
			}
		});
	}
	public static void runOnUIThread(Context context,Runnable runnable){
		getApplication(context).getUIHandler().post(runnable);
	}
}
