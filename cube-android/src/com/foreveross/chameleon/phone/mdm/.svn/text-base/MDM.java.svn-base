package com.foreveross.chameleon.phone.mdm;

import com.foreveross.chameleon.push.tmp.Message;

public class MDM {
	public static final String CUBE_ACTION_MDM_CAMERA = "org.foreveross.cube.action.mdm.CAMERA";
	public static final String CUBE_ACTION_MDM_PASSWORD_RESET = "org.foreveross.cube.action.mdm.PASSWORD_RESET";
	public static final String CUBE_ACTION_MDM_PASSWORD_QUALITY = "org.foreveross.cube.action.mdm.PASSWORD_QUALITY";
	public static final String CUBE_ACTION_MDM_PASSWORD_EXPIRATION = "org.foreveross.cube.action.mdm.PASSWORD_EXPIRATION";
	public static final String CUBE_ACTION_MDM_LOCK_WIPE = "org.foreveross.cube.action.mdm.LOCK_WIPE";
	public static final String CUBE_ACTION_MDM_ENCRYPTION = "org.foreveross.cube.action.mdm.ENCRYPTION";
	public static final String CUBE_ACTION_MDM_WIPEDATA = "org.foreveross.cube.action.mdm.WIPEDATA";
	/*设置输入密码错误最大次数*/
	public static final String CUBE_ACTION_MDM_MAXXIMUMFAILEDPASW = "org.foreveross.cube.action.mdm.MaximumFailedPassword";
   
	/*设置锁屏时间*/
	public static final String CUBE_ACTION_MDM_MAXTIMELOCK = "org.foreveross.cube.action.mdm.MaximumTimeToLock";
	
	/*设置密码错误最小长度*/
	public static final String CUBE_ACTION_MDM_PASWMINLENGTH = "org.foreveross.cube.action.mdm.PasswordMinlength";
	
	/*删除当前应用管理组件*/
	public static final String CUBE_ACTION_MDM_REMOVEACTIVIEADMIN = "org.foreveross.cube.action.mdm.RemoveActiveAdmin";
	/*删除当前应用管理组件*/
	public static final String CUBE_ACTION_MDM_MESSAGE_COMENT_RECEIVER = "org.foreveross.cube.push.CommonMessageReceiver";
	
	public static final String CUBE_ACTION_MDM_STORAGE_SECURITY = "org.foreveross.cube.action.mdm.StorageSecurityReceiver"; 
	
	
	private static boolean registered = false;

	public static void registerReceivers() {
		if (!registered) {
			Message.addListener(new CameraMessageReceiver());
			Message.addListener(new EncryptionMessageReceiver());
			Message.addListener(new LockWipeMessageReceiver());
			Message.addListener(new PasswordExpirationMessageReceiver());
			Message.addListener(new PasswordQualityMessageReceiver());
			Message.addListener(new MaximumFailedPasswordReceiver());
			Message.addListener(new MaximumTimeToLockReceiver());
			Message.addListener(new PasswordMinlengthReceiver());
			Message.addListener(new RemoveActiveAdminReceiver());
			Message.addListener(new WipeDataReceiver());
			Message.addListener(new PasswordResetMessageReceiver());
			Message.addListener(new StorageSecurityReceiver());
			registered = true;
		}

	}

}
