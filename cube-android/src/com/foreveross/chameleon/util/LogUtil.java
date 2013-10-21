package com.foreveross.chameleon.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;

/**
 * @ClassName: LogUtil
 * @Description: 全局日志管理类
 * @author <a href="mailto:turlet@163.com">turlet@163.com</a>
 * @date 2012-2-10 下午9:50:35
 * @version V1.0
 */
public class LogUtil {

	public static final String TAG = LogUtil.class.getSimpleName();

	/**
	 * 是否开启测试模式
	 */
	private static boolean debugger = true;

	/**
	 * 是否保存到SD卡
	 */
	private static boolean saveToSd = false;

	/**
	 * 保存LOG日志的目录
	 */
	public static final String SAVE_LOG_DIR_PATH = Environment
			.getExternalStorageDirectory() + "/amp/log";

	/**
	 * 保存LOG日志的路径
	 */
	private static String save_log_path = SAVE_LOG_DIR_PATH + "/amp.log";

	public static void d(String tag, String msg) {
		
		if (debugger) {
			Log.d(tag, msg);
			if (saveToSd && existSD()) {
				storeLog(tag, msg);
			}
		}
	}

	public static void i(String tag, String msg) {
		if (debugger) {
			Log.i(tag, msg);
			if (saveToSd && existSD()) {
				storeLog(tag, msg);
			}
		}
	}

	public static void w(String tag, String msg) {
		if (debugger) {
			Log.w(tag, msg);
			if (saveToSd && existSD()) {
				storeLog(tag, msg);
			}
		}
	}

	public static void e(String tag, String msg) {
		if (debugger) {
			Log.e(tag, msg);
			if (saveToSd && existSD()) {
				storeLog(tag, msg);
			}
		}
	}

	/**
	 * 将日志信息保存至SD卡
	 * 
	 * @param tag
	 *            LOG TAG
	 * @param msg
	 *            保存的打印信息
	 */
	public static void storeLog(String tag, String msg) {
		File fileDir = new File(SAVE_LOG_DIR_PATH);
		// 判断目录是否已经存在
		if (!fileDir.exists()) {
			if (!fileDir.mkdir()) {
				Log.e(tag, "Failed to create directory " + SAVE_LOG_DIR_PATH);
				return;
			}
		}
		File file = new File(save_log_path);
		// 判断日志文件是否已经存在
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					Log.e(tag, "Failed to create log file " + save_log_path);
					return;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			// 输出
			FileOutputStream fos = new FileOutputStream(file, true);
			PrintWriter out = new PrintWriter(fos);
			out.println(" --module--" + tag + " " + msg + '\r');
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean existSD() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return false;
		}
		return true;
	}
	   @SuppressWarnings("unchecked")
	    public static String makeLogTag(Class cls) {
	        return "Androidpn_" + cls.getSimpleName();
	    }
}
