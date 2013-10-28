package com.foreveross.chameleon.phone.muc;

import java.util.HashMap;

import org.jivesoftware.smackx.muc.DefaultUserStatusListener;

import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;

/**
 * <BR>
 * [功能详细描述]群组里自身状态监控,一个群组绑定一个监听器
 * @author Amberlo
 * @version [V2com.foreveross.cube.activity.LoadingActivity , 2013-8-17] 
 */
public class MucUserStatusListener extends DefaultUserStatusListener{
	
	private static final String Tag = "UserStatusListener";
	private Context context;
	String roomJid;
	public MucUserStatusListener(Context context,String roomJid) {
		this.context = context;
		this.roomJid = roomJid;
	}
	
	
	@Override
	public void adminGranted() {
		Log.v(Tag, "adminGranted");
	}

	@Override
	public void adminRevoked() {
		Log.v(Tag, "adminRevoked");
	}

	@Override
	public void banned(String arg0, String arg1) {
		Log.v(Tag, "banned");
	}

	@Override
	public void kicked(String actor, String action) {
		
		Log.v(Tag, "kicked");
		//重新初始化群组列表，必须在ui线程上跑
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("muckill", MucBroadCastEvent.PUSH_MUC_KICKED);
		map.put("roomJid", roomJid);
		EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
		.post(map);
		
//		context.sendBroadcast(i);
//        android.os.Message msg = new android.os.Message();
//        msg.what = MucManager.MucInitMucRooms;
//        Application.class.cast(context.getApplicationContext()).getUIHandler().sendMessage(msg);
		
	}

	@Override
	public void membershipGranted() {
		Log.v(Tag, "membershipGranted");
	}

	@Override
	public void membershipRevoked() {
		Log.v(Tag, "membershipRevoked");
	}

	@Override
	public void moderatorGranted() {
		Log.v(Tag, "voiceRevoked");
		
	}

	@Override
	public void moderatorRevoked() {
		Log.v(Tag, "voiceRevoked");
		
	}

	@Override
	public void ownershipGranted() {
		Log.v(Tag, "voiceRevoked");
		
	}

	@Override
	public void ownershipRevoked() {
		Log.v(Tag, "voiceRevoked");
		
	}

	@Override
	public void voiceGranted() {

		Log.v(Tag, "voiceGranted");
	}

	@Override
	public void voiceRevoked() {
		Log.v(Tag, "voiceRevoked");
	}

}
