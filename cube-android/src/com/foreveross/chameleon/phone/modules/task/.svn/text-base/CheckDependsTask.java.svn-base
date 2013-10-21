package com.foreveross.chameleon.phone.modules.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;

public class CheckDependsTask extends
		AsyncTask<String, Integer, ArrayList<CubeModule>> {

	public CheckDependsTask() {
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ThreadPlatformUtils.addTask2List(this);
	}

	@Override
	protected void onPostExecute(ArrayList<CubeModule> result) {
		super.onPostExecute(result);
		ThreadPlatformUtils.finishTask(this);
	}

	/**
	 * @param param
	 *            [0]: String identifier; 模块唯一标识 param[1]: String path;
	 *            CubeModule.json所在路径
	 **/
	@Override
	protected ArrayList<CubeModule> doInBackground(String... params) {
		ArrayList<CubeModule> identifierList = new ArrayList<CubeModule>();
		String result = readDependsFile(params[0], params[1]);
		try {
			// 有json文件
			if (null != result) {
				JSONObject jb = new JSONObject(result);
				JSONObject dependencies = jb.getJSONObject("dependencies");
				if (dependencies.length() != 0) {
					Iterator<?> i = dependencies.keys();
					while (i.hasNext()) {
						String id = (String) i.next();
						// 检查是否安装该模块
						int build = dependencies.getInt(id);
						int size = CubeModuleManager.getInstance().getModuleCount(id);
						if(size == 0)
							//不存在该模块，自动跳过
							continue;
						if(size == 1) {
							CubeModule module = CubeModuleManager.getInstance().getModuleByIdentify(id);
							int type = module.getModuleType();
							if (type != CubeModule.INSTALLED) {
								Log.v("Depends", "缺少依赖模块");
								identifierList.add(module);
								Log.v("Depends", "该模块可能未安装/在下载中/在更新中/在删除中");
							} else {
								Log.v("Depends", "不缺少依赖模块");
							}
						} else {
							//模块有更新，取旧版本下载依赖
							CubeModule newModule = CubeModuleManager.getInstance().getIdentifier_new_version_map().get(id);
							CubeModule module = CubeModuleManager.getInstance().getIdentifier_old_version_map().get(id);
							if(module==null || newModule == null){
								continue;
							}
							int type = module.getModuleType();
							if (type == CubeModule.INSTALLED) {
								if (build > module.getBuild()) {
									Log.v("Depends", "存在版本小于依赖版本");
									identifierList.add(newModule);
								} else {
									Log.v("Depends", "不缺少依赖模块");
								}
							} else {
								Log.v("Depends", "缺少依赖模块");
								identifierList.add(module);
								Log.v("Depends", "该模块可能未安装/在下载中/在更新中/在删除中");
							}
						}

					}
				}

			} else {
				Log.v("Depends", "没有CubeModulejson文件");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return identifierList;

	}

	public static String readDependsFile(String identifier, String path) {

		String result = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path + identifier + "/CubeModule.json");
			try {
				FileInputStream in = new FileInputStream(s);
				// 获取文件的字节数
				int lenght = in.available();
				// 创建byte数组
				byte[] buffer = new byte[lenght];
				// 将文件中的数据读到byte数组中
				in.read(buffer);
				result = EncodingUtils.getString(buffer, "UTF-8");
			} catch (Exception e) {
			}
		}
		return result;
	}
}
