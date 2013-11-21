package com.foreveross.chameleon.update;

import android.content.Intent;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.BroadcastConstans;
import com.foreveross.chameleon.phone.modules.CubeApplication;

public class CubeCheckUpdateListener implements CheckUpdateListener{
	
	Application application;
	public CubeCheckUpdateListener(Application application) {
		this.application=application;
	}
	
	@Override
	public void onCheckStart() {
		
	}

	@Override
	public void onUpdateAvaliable(CubeApplication curApp, CubeApplication newApp) {
		
		
		Intent i = new Intent();
		String msg = String.format("当前版本:%s\n最新版本:%s\n版本说明:\n%s", 
				curApp.getVersion(), 
				newApp.getVersion(),
				newApp.getReleaseNote());
		i.setAction(BroadcastConstans.APP_UPDATE);
		i.putExtra("message", msg);
		i.putExtra("curApp", curApp);
		i.putExtra("newApp", newApp);
//		application.sendBroadcast(i);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setClass(application.getApplicationContext(), CheckUpdatableActivity.class);
		application.startActivity(i);
	}

	@Override
	public void onUpdateUnavailable() {
		
	}

	@Override
	public void onCheckError(Throwable error) {
		
	}

	@Override
	public void onCancelled() {
		
	}

}
