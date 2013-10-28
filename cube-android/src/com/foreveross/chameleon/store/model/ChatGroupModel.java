package com.foreveross.chameleon.store.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.muc.MucBroadCastEvent;
import com.foreveross.chameleon.phone.muc.MucManager;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.util.Pool;

/**
 * [群聊组]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-26]
 */
public class ChatGroupModel extends AbstractUserGroupModel implements
		MessageSender {

	private String creatorJid = null;
	private String roomJid = null;
	private int unreadMessageCount = 0;
	private transient List<ConversationMessage> conversations = new ArrayList<ConversationMessage>();

	private long kickTime;
	
	public List<ConversationMessage> getConversations() {
		return conversations;
	}

	public void setConversations(List<ConversationMessage> conversations) {
		this.conversations = conversations;
	}

	private transient ConversationMessage lastMessage;

	public ConversationMessage getLastMessage() {
		if (lastMessage == null){
			if (conversations.size() == 0) {
				return null;
			} else{
				setLastMessage(conversations.get(conversations.size() - 1));
				return conversations.get(conversations.size() - 1);
			}
		} else {
			return lastMessage;
		}
	}

	public void setLastMessage(ConversationMessage lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getRoomJid() {
		if (roomJid == null)
			roomJid = this.getGroupCode();
		return roomJid;
	}

	public void setRoomJid(String roomJid) {
		this.roomJid = roomJid;
	}

	public String getCreatorJid() {
		return creatorJid;
	}

	public void setCreatorJid(String creatorJid) {
		this.creatorJid = creatorJid;
	}

	public boolean isCreator(Context context, String userJid) {
		if (creatorJid != null){
			return creatorJid.equals(userJid) ;
		}
		return false;
	}

	public void addConversationMessage(ConversationMessage conversationMessage) {
		if (lastMessage != null
				&& lastMessage.getLocalTime() < conversationMessage
						.getLocalTime()) {
			conversations.add(conversationMessage);
			lastMessage = conversationMessage;
		} else if (lastMessage == null) {
			conversations.add(conversationMessage);
			lastMessage = conversationMessage;
		}}

	public void findHistory(int limit) {
		conversations.clear();
		try {
			if (limit == -1) {
				conversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", roomJid).query());
			} else {
				long count = StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", roomJid).countOf();
				long offset = count > limit ? count - limit : 0l;
				conversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class)
						.limit((long) limit).offset(offset).where()
						.eq("chater", roomJid).query());
			}
		} catch (SQLException e) {
		}

	}
	
	public List<ConversationMessage> findLastHistory(int limit) {
		List<ConversationMessage> lastConversations = new ArrayList<ConversationMessage>();
		try {
			if (limit == -1) {
				lastConversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", roomJid).query());
			} else {
				long count = StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", roomJid).countOf();
				long offset = count > limit ? count - limit : 0l;
				lastConversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class)
						.limit((long) limit).offset(offset).where()
						.eq("chater", roomJid).query());
			}
		} catch (SQLException e) {
		}
		return lastConversations;
	}

	// /**
	// * [发送消息]<BR>
	// * [功能详细描述]
	// *
	// * @return 2013-8-27 下午7:39:45
	// */
	// public void sendMessage(ConversationMessage conversationMessage) {
	// // TODO[FENGWEILI] 
	//
	// }

	/**
	 * [踢人]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 *            2013-9-16 下午4:46:12
	 */
	public void kick(Context context, String jid) {
		removeStuff(jid);
		Log.i("test", "testkick");
		MucManager.getInstanse(context).kick(getRoomJid(), jid);
	}

	/**
	 * [修改名称]<BR>
	 * [功能详细描述] 2013-9-16 下午4:48:13
	 */
	public void rename(Context context, String roomName) {
		this.groupName = roomName;
		MucManager.getInstanse(context).reNameRoom(getRoomJid(), roomName);
		renameUserGroup(roomName);
	}

	private void renameUserGroup(String roomName) {
		this.groupName = roomName;
		Pool.getPool().execute(new Runnable() {

			@Override
			public void run() {
				List<UserModel> list = getList();
				for (UserModel userModel : list) {
					userModel.transGroups();
					userModel.update();
				}
			}
		});
	}

	// /**
	// * [添加成员]<BR>
	// * [功能详细描述]
	// *
	// * @param userModel
	// * 2013-9-16 下午4:49:19
	// */
	// public void addMember(Context context,UserModel userModel) {
	// getList().add(userModel);
	// List<UserModel> users
	//
	// }

	/**
	 * [添加成员]<BR>
	 * [功能详细描述]
	 * 
	 * @param models
	 *            2013-9-16 下午4:49:14
	 */
	public void addMembers(Context context, UserModel... models) {
		final Context addcontext = context;
		final UserModel[] addmodels = models;
		final ChatGroupModel model = this;
		new Thread() {
			@Override
			public void run() {
				super.run();
				boolean success = MucManager.getInstanse(addcontext).invite(model, Arrays.asList(addmodels));
				if (success){
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
					.post(MucBroadCastEvent.PUSH_MUC_ADDFRIEND_SUCCESS);
					addMembersToList(Arrays.asList(addmodels));
				}
				else {
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
					.post(MucBroadCastEvent.PUSH_MUC_ADDFRIEND_FAIL);
				}
			}
		}.start();
	}
	
	public void addMembersToList(List<UserModel> list){
		List<UserModel> userModels = getList();
		for (UserModel userModel : list){
			if (!userModels.contains(userModel)){
				addStuff(userModel);
			}
		}
		
	}

	public List<UserModel> findUninvestedUsers() {
		List<UserModel> list = new ArrayList<UserModel>(IMModelManager
				.instance().getUserMap().values());
		list.removeAll(getList());
		return list;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param context
	 * @param conversationMessage
	 *            2013-9-17 上午10:08:10
	 */
	@Override
	public void sendMessage(Context context,
			ConversationMessage conversationMessage) {
		MucManager.getInstanse(context).sendMucMessage(conversationMessage);
	}

	public void clearNewMessageCount() {
		unreadMessageCount = 0;
	}

	public void increaseCount() {
		++unreadMessageCount;
	}

	public void increaseCountBy(int count) {
		unreadMessageCount = unreadMessageCount + count;
	}

	public void decreaseCount() {
		--unreadMessageCount;
	}

	public void descreaseCountBy(int count) {
		unreadMessageCount = unreadMessageCount - count;
	}

	public int getUnreadMessageCount() {
		return unreadMessageCount;
	}

	public long getKickTime() {
		return kickTime;
	}

	public void setKickTime(long kickTime) {
		this.kickTime = kickTime;
	}
}