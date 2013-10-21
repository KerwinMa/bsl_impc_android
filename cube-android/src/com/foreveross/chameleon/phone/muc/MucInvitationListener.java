package com.foreveross.chameleon.phone.muc;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.InvitationListener;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.push.client.XmppManager;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.util.Preferences;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class MucInvitationListener implements InvitationListener{
	String Tag = "MucInvitationListener";
	Context context;
	XmppManager xmppManager;
	public MucInvitationListener(Context context,XmppManager xmppManager) {
		this.context = context;
		this.xmppManager=xmppManager;
	}
	
	@Override
	public void invitationReceived(Connection conn,
			String room, String inviter, String reason,
			String password, Message message) {
		
		Log.i(Tag, "邀请者：" + inviter + " 邀请理由：" + reason);
		
		try {
			MucManager.getInstanse(context).setInvitation(true);
			MucManager.getInstanse(context).setInvitationRoomJid(room);
			//被邀请自动加入组中
			String nickName = Preferences.getUserName(Application.sharePref)+ "@"+conn.getServiceName();
			IMModelManager.instance().getChatRoomContainer().Invita(context, nickName);
	        EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST).post(MucBroadCastEvent.PUSH_MUC_INITROOMS);
//	        Toast.makeText(context, "你已被邀请进群组"+room, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
