package com.foreveross.chameleon.update;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.phone.modules.CubeApplication;

/**
 * 手动检查更新处理
 * @author Justin
 *
 */
public class ManualCheckUpdateListener extends AutoCheckUpdateListener{

	private ProgressDialog loadingDialog;
	
	public ManualCheckUpdateListener(Context context) {
		super(context);

		//检测等待对话框
		loadingDialog = new ProgressDialog(context);
		loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loadingDialog.setTitle("版本更新");
		loadingDialog.setMessage("正在检测更新，请稍后...");
		loadingDialog.setCancelable(false);
	}

	@Override
	public void onCheckStart() {
		loadingDialog.show();
	}

	@Override
	public void onUpdateAvaliable(CubeApplication curApp, CubeApplication newApp) {
		loadingDialog.dismiss();
		
		super.onUpdateAvaliable(curApp, newApp);
	}

	@Override
	public void onUpdateUnavailable() {
		Log.d("VersionUpdate", "显示没有更新");
		
		loadingDialog.dismiss();
		
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle("版本更新")
		.setMessage("暂未发现有版本更新")
		.setPositiveButton("确定", null)
		.create();
		dialog.show();
	}

	@Override
	public void onCheckError(Throwable error) {
		Log.d("VersionUpdate", "显示下载错误");
		
		loadingDialog.dismiss();
		
		//提示更新失败
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle("更新检测失败")
		.setMessage(error.getMessage())
		.setPositiveButton("确定", null)
		.create();
		dialog.show();
	}

}
