/**
 * 
 */
package com.foreveross.chameleon;

import android.os.Environment;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-16]
 */
public class TmpConstants {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-7-16 上午11:29:14
	 */

	public static final String SELECT_OPEN="SELECT_OPEN";
	// public static final String XMPP_ACTION = "xmpp.connection";
	// 取得ROOT
	public static final String ROOT = "CubeRecorder";

	// 所有本地语音文件的存储位置
	public static final String RECORDER_SEND_PATH = ROOT + "/VoiceSend";

	// 所有服务器语音文件的存储位置
	public static final String RECORDER_RECEIVE_PATH = ROOT + "/VoiceReceive";
	
	// 所有服务器附件文件的存储位置
	public static final String ATTACHMENT_PATH = ROOT + "/attachment";
	public static int LOGIN_ONLINE = 0x0001;
	public static int LOGIN_OUTLINE = 0x0002;

	public static final String COMMNAD_INTENT = "push.type.command";
	public static final String ANN_INTENT = "push.type.announcement";
	public static final String MODULE_CONTENT_INTENT = "push.type.modulecontent";
	public static final String MDM_CONTENT_INTENT = "push.type.mdmcontent";
	public static final String SYSTEM_CONTENT_INTENT = "push.type.systemcontent";

	public static final String EVENTBUS_SYSTEM = "EVENTBUS_SYSTEM";
	// 队列名称
	
	
	public static final String EVENTBUS_REFRESH = "EVENTBUS_REFRESH";
	public static final String EVENTBUS_COMMON = "EVENTBUS_COMMON";
	public static final String EVENTBUS_PUSH = "EVENTBUS_PUSH";
	public static final String EVENTBUS_MODULE_CHANGED = "EVENTBUS_MODULE_CHANGED";
	public static final String EVENTBUS_CHAT = "EVENTBUS_CHAT";
	public static final String EVENTBUS_ANNOUNCEMENT = "EVENTBUS_ANNOUNCEMENT";

	public static final String EVENTBUS_MESSAGE_COUNT = "EVENTBUS_MESSAGE_COUNT";
	public static final String EVENTBUS_MESSAGE_CONTENT = "EVENTBUS_MESSAGE_CONTENT";
	public static final String VIEW_MESSAGE_PRESENCE = "VIEW_MESSAGE_PRESENCE";
	public static final String VIEW_MESSAGE_COUNT_PRESENCE = "VIEW_MESSAGE_COUNT_PRESENCE";
	public static final String VIEW_ANNOUNCE_PRESENCE = "VIEW_ANNOUNCE_PRESENCE";
	public static final String VIEW_ANNOUNCE_COUNT_PRESENCE = "VIEW_ANNOUNCE_COUNT_PRESENCE";
	public static final String VIEW_CHAT_PRESENCE = "VIEW_CHAT_PRESENCE";
	public static final String VIEW_CHAT_COUNT_PRESENCE = "VIEW_CHAT_COUNT_PRESENCE";
	public static final String EVENTBUS_ANNOUNCE_COUNT = "EVENTBUS_ANNOUNCE_COUNT";
	public static final String EVENTBUS_ANNOUNCE_CONTENT = "EVENTBUS_ANNOUNCE_CONTENT";
	
	public static final String EVENTBUS_MUC_BROADCAST = "EVENTBUS_MUC_BROADCAST";
	
	public static final String EVENTBUS_MUTIPLEACCOUNT_BROADCAST = "EVENTBUS_MUTIPLEACCOUNT_BROADCAST";

	public static final String MESSAGE_RECORD_IDENTIFIER = "com.foss.message.record";
	public static final String ANNOUCE_RECORD_IDENTIFIER = "com.foss.announcement";
	public static final String CHAT_RECORD_IDENTIFIER = "com.foss.chat";

	public static final String FRIEND_GROUP_PREFIX = "frdg_";
	public static final String FAVOR_GROUP_PREFIX = "fvrg_";
	public static final String CHAT_GROUP_PREFIX = "ctg_";
	
	// 所有异常错误位置
	public static final String LOG_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/" + "CUBE"  +"/Log/";
}
