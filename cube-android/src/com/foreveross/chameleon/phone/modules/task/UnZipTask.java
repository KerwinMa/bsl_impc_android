package com.foreveross.chameleon.phone.modules.task;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.util.IOUtils;

public class UnZipTask extends AsyncTask<String, Integer, Boolean> {
	
	CubeModule module;
	CubeApplication app;
	Context context;
	public UnZipTask(Context context,CubeApplication app,CubeModule module) {
		this.module=module;
		this.app=app;
		this.context=context;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ThreadPlatformUtils.addTask2List(this);
	}

	public void doUnZipFailed(){
		
	}

	@Override
	protected Boolean doInBackground(String... params) {
		try {
			unZipAndInstall(context,app, module);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		ThreadPlatformUtils.finishTask(this);
		if(!result){
			doUnZipFailed();
		}
	}
	
	/**
	 * 解压模块
	 * 
	 * @param module
	 * @param listener
	 * @throws Exception 
	 */
	public static void unZipAndInstall(Context context,CubeApplication app, CubeModule module) throws Exception {
		
		if (null != module) {

			String basePath=Environment.getExternalStorageDirectory().getPath()+"/"+context.getPackageName();
			/** sb1是zip位置，sb2是解压缩位置 */
			StringBuilder sb1 = new StringBuilder();
			sb1.append(basePath).append("/").append(module.getIdentifier()).append(".zip");
			StringBuilder sb2 = new StringBuilder();
			sb2.append(basePath).append("/www/").append(module.getIdentifier());
			//TODO[FENGWEILI]
			File archive = new File(sb1.toString());
			try {
				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, sb2.toString());
				}

			} catch (Exception e) {
				
				Log.e("uzip", "Error while extracting file " + archive, e);
				throw e;
			}finally{
				
			}
			
		} else {
//			context.sendStickyBroadcast(new Intent("com.csair.cubeModelChange").putExtra(
//					"identifier", module.getIdentifier()));
		}
	}
	
	private static void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
			throws IOException {

		if (entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
//		String a = IOUtils.toString(entry.getName().getBytes("GBK"), "UTF-8");
//		Log.d("mytag", "entry name:" + a);
		if (!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}

//		Log.v("uzip", "Extracting: " + entry);
		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try {
			
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}
	}

	private static void createDir(File dir) {
		if (dir.exists()) {
			return;
		}
//		Log.v("uzip", "Creating dir " + dir.getName());
		if (!dir.mkdirs()) {
			throw new RuntimeException("Can not create dir " + dir);
		}
	
	}	
}
