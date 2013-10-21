package com.foreveross.chameleon.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.foreveross.chameleon.phone.modules.CubeApplication;


/**
 * <BR>
 * [功能详细描述]
 * @author Amberlo
 * @version [CubeAndroid , 2013-7-11] 
 */
public class CheckUpdatableActivity extends Activity {
	
	
	AlertDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//透明activity
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(new View(this));
		
		if (dialog != null) {
			dialog.dismiss();
		}
		Intent i = getIntent();
		String msg = i . getStringExtra("message");
		final CubeApplication curApp = (CubeApplication) i.getSerializableExtra("curApp");
		final CubeApplication newApp = (CubeApplication) i.getSerializableExtra("newApp");
		dialog = new AlertDialog.Builder(CheckUpdatableActivity.this)
		.setTitle("应用更新")
		.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
				//启动下载activity
				Intent intent = new Intent(CheckUpdatableActivity.this, DownloadUpdateActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable(DownloadUpdateActivity.INTENT_CURRENT_APPLICATION, curApp);//当前版本
				bundle.putSerializable(DownloadUpdateActivity.INTENT_NEW_APPLICATION, newApp);//服务器最新版本
				intent.putExtras(bundle);
				startActivity(intent);
				
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
		dialog.show();
		
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			if(dialog!=null&&dialog.isShowing())
				dialog.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	
	
	
	
	
}
