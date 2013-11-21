package com.foreveross.chameleon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileWriterUtil {

	/**
	 * 把内容写入Environment.getExternalStorageDirectory()目录中，文件名为fileName
	 * 
	 * @param content
	 *            写入文件的内容
	 * @param fileName
	 *            文件名
	 */
	public static void write2SdcardRoot(String content, String fileName) {
		File sharedDir = Environment.getExternalStorageDirectory();
		sharedDir.mkdirs();
		File file = new File(sharedDir, fileName);
		// 写文件
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content.toString().getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 在sdcard中创建目录
	 * 
	 * @param dirpath
	 *            目录路径，比如"a/b"，最终目录为：/sdcard/a/b
	 * @return true:创建成功
	 */
	public static boolean mkdirInSdcard(String dirpath) {
		dirpath = dirpath.trim();
		Log.d("cube", "dirpath:" + dirpath);
		boolean success = false;
		String sdcardPath = Environment.getExternalStorageDirectory().getPath();

		if (isSdcardExistAndWriteable()) {
			if (validate(dirpath)) {
				String[] pathArr = dirpath.split("/");
				String parentPath = sdcardPath;
				for (String path : pathArr) {
					String newDirpath = parentPath + "/" + path;
					Log.d("cube", "newDirpath to mkdir:" + newDirpath);
					File newDir = new File(newDirpath);
					if (!newDir.exists()) {
						// 中途出错，直接返回false
						if (!newDir.mkdirs()) {
							return false;
						}
					}
					parentPath = newDirpath;
				}
				// 全部执行完，返回成功
				success = true;
			}

		}
		return success;
	}

	/**
	 * 判断dirpath是否合法
	 * 
	 * @param dirpath
	 * @return
	 */
	private static boolean validate(String dirpath) {
		// TODO 待补充逻辑
		if (dirpath.startsWith("/") || dirpath.startsWith("//")) {
			return false;
		}
		return true;
	}

	/**
	 * 检测存储卡是否插入
	 * 
	 * @return
	 */
	public static boolean isSdcardExistAndWriteable() {
		/** 检查介质 **/
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// 可以读写介质
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// 只读
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			// 发生了某些错误
			externalStorageAvailable = externalStorageWriteable = false;
		}

		Log.d("cube", "external storage available:" + externalStorageAvailable);
		Log.d("cube", "external storage writeable:" + externalStorageWriteable);

		if (externalStorageAvailable && externalStorageWriteable) {
			return true;
		} else {
			return false;
		}
	}

}
