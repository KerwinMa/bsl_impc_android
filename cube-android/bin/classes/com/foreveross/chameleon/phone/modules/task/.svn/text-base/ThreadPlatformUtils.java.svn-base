package com.foreveross.chameleon.phone.modules.task;

//import java.util.ArrayList;
//import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import android.os.AsyncTask;
import android.os.Build;

public class ThreadPlatformUtils {
	//自动下载模块数
	public static int autodownLoadTaskCout =0;
	public static int downloadTaskCount=0;
	public static int autodownLoadallcount =0;
	private static ArrayList<DownloadFileAsyncTask> downTaskList= new ArrayList<DownloadFileAsyncTask>();
	private static ArrayList<AsyncTask<?,?,?>> taskList = new ArrayList<AsyncTask<?,?,?>>();
	public static void executeByPalform(AsyncTask<String, ?, ?> task,ExecutorService pool,String ... params){
		if(android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1){
			task.executeOnExecutor(pool, params);
		}
		else task.execute(params);
	}
	
	public static int getAutodownLoadallcount() {
		return autodownLoadallcount;
	}



	public static void setAutodownLoadallcount(int autodownLoadallcount) {
		ThreadPlatformUtils.autodownLoadallcount = autodownLoadallcount;
	}



	public  static int getAutodownLoadTaskCout() {
		return autodownLoadTaskCout;
	}
	
	
	public static void resetAutodownLoadTaskCout() {
		autodownLoadTaskCout = 0;
	}
	
	public static void  addAutodownLoadTaskCout() {
		autodownLoadTaskCout++;
		autodownLoadallcount = autodownLoadTaskCout;
	}
	
	public static void SecreaseAutodownLoadTaskCout() {
		autodownLoadTaskCout--;
	}
	
	
	public static void addDownloadTask2List(DownloadFileAsyncTask task){
			downloadTaskCount++;
			downTaskList.add(task);
			taskList.add(task);
	}
	
	public static void finishDownloadTask(DownloadFileAsyncTask task){
		downloadTaskCount--;
		downTaskList.remove(task);
		taskList.remove(task);
	}
	
	public static void finishAllDownloadTask(){
		for(DownloadFileAsyncTask downloadTask:downTaskList){
			downloadTask.handler.sendEmptyMessage(0);
		}
		downloadTaskCount=0;
		downTaskList.clear();
		
	}
	
	public static void addTask2List(AsyncTask<?,?,?> task){
		taskList.add(task);
	}

	public static void finishTask(AsyncTask<?,?,?> task){
		taskList.remove(task);
	}
	
	public static void shutdownAllTask(){
		for(AsyncTask<?,?,?> task:taskList){
			if(task.getStatus()!=AsyncTask.Status.FINISHED)
			task.cancel(true);
		}
	}
}
