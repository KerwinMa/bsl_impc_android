package com.foreveross.chameleon;

import android.content.Context;
import android.os.Environment;

import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.foreveross.chameleon.util.Preferences;

public class URL {


	//请在cube.properties中配置
	public static String ANNOUNCE = null;
	public static String BASE_WEB = null;
	public static String MUC_BASE = null;
	public static String BASE_WS = null;
	
	//请在cube.properties中配置
	public static String PAD_MAIN_URL = null;
	public static String PAD_LOGIN_URL = null;
	public static String PHONE_MAIN_URL = null;
	public static String PHONE_LOGIN_URL = null;
	public static String PHONE_REGISTER_URL = null;
	public static String PAD_REGISTER_URL = null;
	
	public static String UPLOAD_URL = BASE_WEB + "csair-mam/attachment/clientUpload";
	public static String SYNC = BASE_WS + "csair-extension/api/csairauth/privileges";
	public static String UPLOAD = BASE_WS + "csair-mam/api/mam/attachment/upload";
	public static String LOGIN = BASE_WS+ "csair-extension/api/csairauth/login";
	public static String UPDATE = BASE_WS+ "csair-mam/api/mam/clients/update/android";
	public static String SNAPSHOT = BASE_WS + "csair-mam/api/mam/clients/widget/";
	public static  String PUSH_BASE_URL = BASE_WS + "csair-push/api/";
	public static  String CHECKIN_URL = PUSH_BASE_URL+ "checkinservice/checkins";
	public static  String CHECKOUT_URL = PUSH_BASE_URL+ "checkinservice/checkout";
	public static  String FEEDBACK_URL = PUSH_BASE_URL + "receipts";
	//下载后更新服务端计数
	public static String UPDATE_RECORD = BASE_WS+ "csair-mam/api/mam/clients/update/appcount/android/";
	public static String GEOPOSITION_URL=BASE_WS + "csair-mam/api/mam/device/position/add";
//	public static String GETPUSHMESSAGE =PUSH_BASE_URL+"receipts/none-receipts/";
	public static String GETPUSHMESSAGE  =URL.PUSH_BASE_URL+"push-msgs/none-receipts/";



	public static String MUC_ALLROOM = MUC_BASE + "csair-im/api/chat/queryAllRoom/"; // :jid/:status
	public static String MUC_AddMembers = MUC_BASE + "csair-im/api/chat/addMembers"; // :jid/:sex/:status/:roomid/:
	public static String MUC_AddMember = MUC_BASE + "csair-im/api/chat/addMember"; // :jid/:username/:
	public static String MUC_QueryMembers = MUC_BASE + "csair-im/api/chat/query/"; // :roomid/:
	public static String MUC_DeleteMember = MUC_BASE + "csair-im/api/chat/deleteMember/"; // :roomId/:jid
	public static String MUC_DeleteMembers = MUC_BASE + "csair-im/api/chat/deleteMembers"; // :jid/:
	public static String MUC_UpdateStatue = MUC_BASE + "csair-im/api/chat/updateStatue"; // :jid/:statue/:
	public static String MUC_DeleteRoom = MUC_BASE + "csair-im/api/chat/deleteRoom/"; // :roomid/:
	public static String MUC_ReRoomName = MUC_BASE + "csair-im/api/chat/roommember/roomname";// :roomid/:roomName/:
	public static String CHATDELETE = MUC_BASE + "csair-im/api/chat/delete";// userId/jid get
	public static String CHATSAVE = MUC_BASE + "csair-im/api/chat/save";// chat/save/:jid/:username/:sex/:status/:userId
	public static String CHATSHOW = MUC_BASE + "csair-im/api/chat/show";// :userId get
	public static String CHATUPDATE = MUC_BASE + "csair-im/api/chat/update";// :jid/:status
																// post

	public static String getDownloadUrl(Context context, String bundle) {
		return getDownloadUrl(context, bundle, false);
	}
	
	public static String getDownloadUrl(Context context, String bundle,boolean isEnconde) {
		String DOWNLOAD = BASE_WS + "csair-mam/api/mam/clients/files/";
		String sessionKey = Preferences.getSESSION(Application.sharePref);
		String appKey = Application.class.cast(context.getApplicationContext()).getCubeApplication().getAppKey();
		if(isEnconde)
		{
			return DOWNLOAD + bundle + "?sessionKey=" + sessionKey + "&appKey="+ appKey+"&encode=true";
		}
		else
		{
			return DOWNLOAD + bundle + "?sessionKey=" + sessionKey + "&appKey="+ appKey;
		}
		
	}
	
	public static String getUpdateAppplicationUrl(Context context, String bundle) {
		String DOWNLOAD = BASE_WS + "csair-mam/api/mam/clients/files/";
		String appKey = Application.class.cast(context.getApplicationContext()).getCubeApplication().getAppKey();
		return DOWNLOAD + bundle + "?&appKey="+ appKey;
	}
	
	public static String getSessionKeyappKey() {
		String sessionKey = Preferences.getSESSION(Application.sharePref);
		String appKey = CubeApplication.getInstance(CubeApplication.getmContext()).getAppKey();
		return "?sessionKey=" + sessionKey + "&appKey="+ appKey;
	}
	
	public static String getSessionKey() {
		String sessionKey = Preferences.getSESSION(Application.sharePref);
		return sessionKey;
	}
	
	public static String getAppKey() {
		String appKey = CubeApplication.getInstance(CubeApplication.getmContext()).getAppKey();
		return appKey;
	}
	
	public static String getSdPath(Context context,String identifier) {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/" + context.getPackageName();
		String url = path + "/www/" + identifier;
		return url;
	}
}
