package com.foreveross.chameleon.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.foreveross.chameleon.phone.modules.CubeApplication;

/**
 * 只显示有更新，用于程序启动时
 * @author Justin
 *
 */
public class AutoCheckUpdateListener implements CheckUpdateListener {

	public static final String PREF_UPDATE_AVAILABLE_DIALOG_SHOWN = "PREF_UPDATE_AVAILABLE_DIALOG_SHOWN";
	Context context;
	AlertDialog dialog;
	
	public AutoCheckUpdateListener(Context context) {
		this.context = context;
	}
	
	@Override
	public void onCheckStart() {
	}
	
	@Override
	public void onUpdateAvaliable(final CubeApplication curApp, final CubeApplication newApp) {
		Log.d("VersionUpdate", "AutoCheckUpdateListener:有更新");
		
		if (dialog != null) {
			dialog.dismiss();
		}
		String oldVersion = curApp.getVersion();
		String newVersion = newApp.getVersion();
		String releaseNote = newApp.getReleaseNote();
		if(oldVersion==null){
			oldVersion="";
		}
		if(newVersion==null){
			newVersion="";
		}
		if(releaseNote==null){
			releaseNote="";
		}
		
		String msg = String.format("当前版本:%s\n最新版本:%s\n版本说明:\n%s", 
				oldVersion, 
				newVersion,
				releaseNote);
		
		dialog = new AlertDialog.Builder(context)
		.setTitle("应用更新")
		.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				//启动下载activity
				Intent intent = new Intent(context, DownloadUpdateActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(DownloadUpdateActivity.INTENT_CURRENT_APPLICATION, curApp);//当前版本
				bundle.putSerializable(DownloadUpdateActivity.INTENT_NEW_APPLICATION, newApp);//服务器最新版本
				intent.putExtras(bundle);
				context.startActivity(intent);
				
				
				
			}
		})
		.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		})
		.setOnKeyListener(new OnKeyListener() {
		    @Override
		    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		        if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
		            return true; // Pretend we processed it
		        }
		        return false; // Any other keys are still processed as normal
		    }
		})
		.create();
		try {
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpdateUnavailable() {
	}
	
	@Override
	public void onCheckError(Throwable error) {
		
	}

	@Override
	public void onCancelled() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
}
