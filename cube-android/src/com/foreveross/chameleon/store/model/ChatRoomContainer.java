/**
 * 
 */
package com.foreveross.chameleon.store.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.muc.MucBroadCastEvent;
import com.foreveross.chameleon.phone.muc.MucManager;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.util.Pool;
import com.foreveross.chameleon.util.Preferences;

/**
 * [群聊组面板]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-27]
 */
public class ChatRoomContainer extends
		AbstractContainerModel<String, ChatGroupModel> {

	/**
	 * [创建聊天室]<BR>
	 * [功能详细描述] 2013-9-16 下午4:46:18
	 */
	public void createChatRoom(Context ctx,ChatGroupModel chatGroupModel, Collection<UserModel> list) {
		UserModel  me = IMModelManager.instance().getMe();

		if(me == null){
			return;
		}
		if (me.getJid() != null){
			chatGroupModel.setCreatorJid(me.getJid());
		}
		List<UserModel> invitors = new ArrayList<UserModel>();
		for(UserModel fris :list){
			invitors.add(fris);
		}
		
		MucManager.getInstanse(ctx).createMutiUserChatroom(chatGroupModel, me, invitors);
		//发消息刷新界面
		EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST).post(
				MucBroadCastEvent.PUSH_MUC_MANAGER_MEMBER);
	}



	/**
	 * [被邀请进聊天室]<BR>
	 * [功能详细描述]
	 * 
	 * @param roomId
	 *            2013-9-22 下午4:46:22
	 */
	public void Invita(Context context,String userJid) {		
		//重新构建数据源
		MucManager.getInstanse(context).obtainRooms(userJid);
	}
	
	/**
	 * [群员离开]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 *            2013-9-16 下午4:47:42
	 */
	public void leave(Context context,String roomJid) {
		MucManager.getInstanse(context).leaveRoom(roomJid);
		freeRelationship(roomJid);
	}

	/**
	 * [解散]<BR>
	 * [功能详细描述] 2013-9-16 下午4:48:54
	 */
	public void free(Context context,String roomJid) {
		MucManager.getInstanse(context).destroyRoom(roomJid);
		freeRelationship(roomJid);
	}
	
	/**
	 * [房间与组之前的关系]<BR>
	 * [功能详细描述]
	 * 
	 * @param roomJid
	 *            2013-9-22 下午3:48:38
	 */
	public void freeRelationship(final String roomJid) {

		final AbstractContainerModel<String, UserModel> acm = IMModelManager
				.instance().getUserGroupModel(roomJid);
		
		Pool.getPool().execute(new Runnable() {

			@Override
			public void run() {
				List<UserModel> list = acm.getList();
				for (UserModel userModel : list) {
					userModel.freeGroup(roomJid);
					userModel.update();
				}
				IMModelManager.instance().removeChatGroupModelsByJid(roomJid);
			}
		});

		//清除已读信息等信息
		ChatGroupModel chatGroupModel = getStuff(roomJid);
		CubeModule module = CubeModuleManager.getInstance()
				.getCubeModuleByIdentifier(TmpConstants.CHAT_RECORD_IDENTIFIER);
		if (module != null && chatGroupModel != null) {
			module.decreaseMsgCountBy(chatGroupModel.getUnreadMessageCount());
		}
		removeStuff(roomJid);
		// 清除历史记录的数据
		IMModelManager.instance().getSessionContainer().removeStuff(roomJid);
		IMModelManager.instance().getSessionContainer().notifyContentChange();
		StaticReference.defMf.deleteById(roomJid, SessionModel.class);
	}
	
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}
	
	@Override
	public void addStuffs(List<ChatGroupModel> vs) {
		// TODO Auto-generated method stub
		List<ChatGroupModel> us = getList();
		for(ChatGroupModel chatGroupModel : vs){
			if (!us.contains(chatGroupModel)){
				super.addStuff(chatGroupModel);
			}
		}
	}
}
