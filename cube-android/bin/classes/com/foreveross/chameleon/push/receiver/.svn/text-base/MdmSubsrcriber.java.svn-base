package com.foreveross.chameleon.push.receiver;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.foreveross.chameleon.phone.mdm.MDM;
import com.foreveross.chameleon.push.cubeparser.type.MDMMessage;
import com.foreveross.chameleon.push.tmp.Message;
import com.squareup.otto.Subscribe;

public class MdmSubsrcriber {
	// public static final String CUBE_ACTION_MDM_CAMERA =
	// "org.foreveross.cube.action.mdm.CAMERA";
	// public static final String CUBE_ACTION_MDM_PASSWORD_RESET =
	// "org.foreveross.cube.action.mdm.PASSWORD_RESET";
	// public static final String CUBE_ACTION_MDM_PASSWORD_QUALITY =
	// "org.foreveross.cube.action.mdm.PASSWORD_QUALITY";
	// public static final String CUBE_ACTION_MDM_PASSWORD_EXPIRATION =
	// "org.foreveross.cube.action.mdm.PASSWORD_EXPIRATION";
	// public static final String CUBE_ACTION_MDM_LOCK_WIPE =
	// "org.foreveross.cube.action.mdm.LOCK_WIPE";
	// public static final String CUBE_ACTION_MDM_ENCRYPTION =
	// "org.foreveross.cube.action.mdm.ENCRYPTION";
	// public static final String CUBE_ACTION_MDM_WIPEDATA =
	// "org.foreveross.cube.action.mdm.WIPEDATA";
	// /*设置输入密码错误最大次数*/
	// public static final String CUBE_ACTION_MDM_MAXXIMUMFAILEDPASW =
	// "org.foreveross.cube.action.mdm.MaximumFailedPassword";
	//
	// /*设置锁屏时间*/
	// public static final String CUBE_ACTION_MDM_MAXTIMELOCK =
	// "org.foreveross.cube.action.mdm.MaximumTimeToLock";
	//
	// /*设置密码错误最小长度*/
	// public static final String CUBE_ACTION_MDM_PASWMINLENGTH =
	// "org.foreveross.cube.action.mdm.PasswordMinlength";
	//
	// /*删除当前应用管理组件*/
	// public static final String CUBE_ACTION_MDM_REMOVEACTIVIEADMIN =
	// "org.foreveross.cube.action.mdm.RemoveActiveAdmin";
	// /*删除当前应用管理组件*/
	// public static final String CUBE_ACTION_MDM_MESSAGE_COMENT_RECEIVER =
	// "org.foreveross.cube.push.CommonMessageReceiver";
	private Context context;
	public MdmSubsrcriber(Context context) {
		this.context = context;
		MDM.registerReceivers();
	}

	@Subscribe
	public void onMDMMessage(MDMMessage mdmMessage) {
		Log.d("mdm", "cmd");
		String mdm = mdmMessage.getCommand();
		Message msg=null;
		if ("LOCK_WIPE".equals(mdm)) {
			 msg = new Message("MDM/LOCK_WIPE", context, "");
		} else if (isMdmable()&&"CAMERA:OFF".equals(mdm)) {
			 msg = new Message("MDM/CAMERA:OFF", context, "");
		} else if (isMdmable()&&"CAMERA:ON".equals(mdm)) {
			 msg = new Message("MDM/CAMERA:ON", context, "");
		} else if (isMdmable()&&"RESET".equals(mdm)) {
			 msg = new Message("MDM/RESET", context, "");
		} else if (isMdmable()&&"STORAGE_SECURITY:ON".equals(mdm)) {
			 msg = new Message("MDM/STORAGE_SECURITY:ON", context, "");
		} else if (isMdmable()&&"STORAGE_SECURITY:OFF".equals(mdm)) {
			 msg = new Message("MDM/STORAGE_SECURITY:OFF", context, "");
		} else if (isMdmable()&&mdm.startsWith("PASSWORD:")) {
			String newPSW=mdm.substring(9, mdm.length());
			msg = new Message("MDM/PASSWORD:SET", context, newPSW);
		} 
		if(null!=msg){
			msg.broadcast();
		}

	}
	public boolean isMdmable(){
		if(Build.VERSION.SDK_INT < 11){
			Log.e("mdm","该功能不支持3.0以下的系统版本");
			return false;
		}else return true;
	}
}
