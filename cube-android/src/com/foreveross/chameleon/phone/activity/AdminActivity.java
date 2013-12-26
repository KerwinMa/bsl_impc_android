package com.foreveross.chameleon.phone.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.csair.impc.R;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.device.DeviceRegisteTask;
import com.foreveross.chameleon.phone.mdm.DeviceAdminSampleReceiver;
import com.foreveross.chameleon.util.CommonUtils;
import com.foreveross.chameleon.util.PadUtils;

public class AdminActivity extends BaseActivity {
	public static final int REQUEST_CODE = 1;
	public static final int RESULT_CODE = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getAdmin()==1) {
			DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			ComponentName mDeviceAdminSample = new ComponentName(this,DeviceAdminSampleReceiver.class);
			if(mDPM.isAdminActive(mDeviceAdminSample))
			{
				mDPM.removeActiveAdmin(mDeviceAdminSample);
			}
			CommonUtils.OpenRotation(application);
			new DeviceRegisteTask(this.getApplicationContext()){

				@Override
				protected void onPostExecute(Boolean result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					if(result)
					{
						actionActivity();
					}
					else
					{
						if (PadUtils.isPad(application)) {
							Intent i = new Intent(AdminActivity.this, FacadeActivity.class);
							i.putExtra("url", URL.PAD_LOGIN_URL);
							i.putExtra("showRegister", true);
							i.putExtra("isPad", true);
							startActivity(i);
						} else {// 手机
							Intent i = new Intent(AdminActivity.this, FacadeActivity.class);
							i.putExtra("url", URL.PHONE_REGISTER_URL);
							i.putExtra("isPad", false);
							startActivity(i);
						}
						finish();
						application.getActivityManager().popActivity(AdminActivity.this);
					}
//					finish();
				}
				
			}.execute();
			
		}
	}

	// 是否已激活 ,如无 弹出激活窗口
	public int getAdmin() {
		DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mDeviceAdminSample = new ComponentName(this,
				DeviceAdminSampleReceiver.class);
		boolean mAdminActive = mDPM.isAdminActive(mDeviceAdminSample);
//		if (!mAdminActive) {
////			Intent intent = new Intent(
////					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
////			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
////					mDeviceAdminSample);
////			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
////					getString(R.string.add_admin_extra_app_text));
////			startActivityForResult(intent, REQUEST_CODE);
//			//return false;
//		}
		return 1;
	}

	// 跳转
	public void actionActivity() {
		// 平板
		if (PadUtils.isPad(application)) {
			Intent i = new Intent(AdminActivity.this, FacadeActivity.class);
			i.putExtra("url", URL.PAD_LOGIN_URL);
			i.putExtra("isPad", true);
			startActivity(i);
		} else {// 手机
			Intent i = new Intent(AdminActivity.this, FacadeActivity.class);
			i.putExtra("url", URL.PHONE_LOGIN_URL);
			i.putExtra("isPad", false);
			startActivity(i);
		}
		finish();
		application.getActivityManager().popActivity(AdminActivity.this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode != RESULT_CODE) {
			finish();
			application.getActivityManager().popActivity(AdminActivity.this);
		} else {
			actionActivity();
		}
	}

}
