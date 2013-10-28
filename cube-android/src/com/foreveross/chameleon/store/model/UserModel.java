package com.foreveross.chameleon.store.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import android.content.Context;

import com.foreveross.chameleon.push.client.RosterEntryWrapper;
import com.foreveross.chameleon.store.core.BaseModel;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.util.CommonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * [用户]<BR>
 * 1.不论共享用户/一般用户
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-26]
 */
@DatabaseTable(tableName = "userModel")
public class UserModel extends BaseModel<UserModel, String> implements
		IDObject<String>, MessageSender {

	public UserModel() {

	}

	/**
	 * [jid]
	 */
	@DatabaseField(id = true, generatedId = false)
	private String jid = null;
	/**
	 * [用户名]
	 */
	@DatabaseField
	private String name = null;
	/**
	 * [性别]
	 */
	@DatabaseField
	private String sex = null;

	/**
	 * [是否收藏]
	 */
	@DatabaseField
	private boolean favor = false;
	/**
	 * [是否已经读]
	 */
	@DatabaseField
	private int unreadMessageCount = 0;

	/**
	 * [gropuCode不能留空]
	 */
	@DatabaseField
	private String groupJson = null;
	/**
	 * [作用描述]
	 */
	private transient Properties properties = new Properties();

	public ConversationMessage getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(ConversationMessage lastMessage) {
		this.lastMessage = lastMessage;
	}

	/**
	 * [在/离线状态]
	 */
	private transient String status = Presence.Type.unavailable.name();

	private transient ConversationMessage lastMessage;

	/**
	 * [最后一条发送消息]
	 */
	private transient ConversationMessage lastSendMessage;
	/**
	 * [最后一条接收消息]
	 */
	private transient ConversationMessage lastReceivedMessage;

	private transient List<ConversationMessage> conversations = new ArrayList<ConversationMessage>();

	public void addConversationMessage(ConversationMessage conversationMessage) {
		conversations.add(conversationMessage);
	}

	public void addConversationMessages(
			ConversationMessage... conversationMessages) {
		for (ConversationMessage con : conversationMessages) {
			addConversationMessage(con);
		}
	}

	public List<ConversationMessage> getConversations() {
		return conversations;
	}

	public void setConversations(List<ConversationMessage> conversations) {
		this.conversations = conversations;
	}

	private transient Map<String, AbstractContainerModel<String, UserModel>> groupMap = new HashMap<String, AbstractContainerModel<String, UserModel>>();

	public boolean hasUnreadMessage() {
		return unreadMessageCount != 0;
	}

	public String getGroupJson() {
		return groupJson;
	}

	public void setGroupJson(String groupJson) {
		this.groupJson = groupJson;
	}

	public Collection<AbstractContainerModel<String, UserModel>> getGroups() {
		return groupMap.values();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public boolean hasRead() {
		return unreadMessageCount == 0;
	}

	public int getUnreadMessageCount() {
		return unreadMessageCount;
	}

	public void setUnreadMessageCount(int unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}

	public ConversationMessage findLastSendMessage() {
		if (lastSendMessage != null) {
			return lastSendMessage;
		}
		return null;
	}

	public ConversationMessage lastSendMessage() {
		if (lastReceivedMessage != null) {
			return lastReceivedMessage;
		}
		return null;
	}

	public ConversationMessage findLastMessage() {
		ConversationMessage conversationMessage = null;
		if (conversations.size() > 0) {
			conversationMessage = conversations.get(conversations.size() - 1);
		}
		lastMessage = conversationMessage;
		return lastMessage;
	}

	public void findHistory(int limit) {
		try {
			if (limit == -1) {
				conversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", jid).query());
			} else {
				long count = StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", jid).countOf();
				long offset = count > limit ? count - limit : 0l;
				conversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class)
						.limit((long) limit).offset(offset).where()
						.eq("chater", jid).query());
			}

		} catch (SQLException e) {
			// TODO[FENGWEILI] ADD LOG
		}

	}
	
	public List<ConversationMessage> findLastHistory(int limit) {
		List<ConversationMessage> lastConversations = new ArrayList<ConversationMessage>();
		try {
			if (limit == -1) {
				lastConversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", jid).query());
			} else {
				long count = StaticReference.userMf
						.queryBuilder(ConversationMessage.class).where()
						.eq("chater", jid).countOf();
				long offset = count > limit ? count - limit : 0l;
				lastConversations.addAll(StaticReference.userMf
						.queryBuilder(ConversationMessage.class)
						.limit((long) limit).offset(offset).where()
						.eq("chater", jid).query());
			}

		} catch (SQLException e) {
			// TODO[FENGWEILI] ADD LOG
		}
		return lastConversations;
	}

	public void clearNewMessageCount() {
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();
		for (AbstractContainerModel<String, UserModel> group : groups) {
			if (group instanceof FriendGroupModel){
				group.descreaseCountBy(unreadMessageCount);
			}
		}
		unreadMessageCount = 0;
	}

	public void increaseCount() {
		++unreadMessageCount;
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();
		for (AbstractContainerModel<String, UserModel> group : groups) {
			if (group instanceof FriendGroupModel){
				group.increaseCount();
			}
		}
	}

	public void increaseCountBy(int count) {
		unreadMessageCount = unreadMessageCount + count;
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();
		for (AbstractContainerModel<String, UserModel> group : groups) {
			if (group instanceof FriendGroupModel){
				group.increaseCountBy(count);
			}
		}
	}

	public void decreaseCount() {
		--unreadMessageCount;
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();
		for (AbstractContainerModel<String, UserModel> group : groups) {
			if (group instanceof FriendGroupModel){
				group.decreaseCount();
			}
		}
	}

	public void descreaseCountBy(int count) {
		unreadMessageCount = unreadMessageCount - count;
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();
		for (AbstractContainerModel<String, UserModel> group : groups) {
			if (group instanceof FriendGroupModel){
				group.descreaseCountBy(count);
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public boolean isFavor() {
		return favor;
	}

	public void setFavor(boolean favor) {
		this.favor = favor;
		if (favor) {
			IMModelManager.instance().getFavorContainer().addStuff(this);
		} else {
			IMModelManager.instance().getFavorContainer().removeStuff(this);
		}
	}

	/**
	 * [从所有组中移除]<BR>
	 * [功能详细描述] 2013-8-29 上午10:04:36
	 */
	public void killMe() {
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();
		for (AbstractContainerModel<String, UserModel> group : groups) {
			group.removeStuff(this);
		}
	}

	/**
	 * [检查组是否存在,如果不存在,则]<BR>
	 * [功能详细描述]
	 * 
	 * @param wrapper
	 *            2013-8-29 上午11:00:42
	 */
	public void sync(RosterEntryWrapper wrapper) {
		String jid = wrapper.getUser();
		String name = wrapper.getName();
		this.setJid(jid);
		if (name == null || name.trim().equals("")) {
			this.setName(StringUtils.parseName(jid));
		} else {
			this.setName(name);
		}

		this.setSex("unknow");
		Collection<AbstractContainerModel<String, UserModel>> rgroups = wrapper
				.getGroups();

		for (AbstractContainerModel<String, UserModel> rg : rgroups) {
			addGroup(rg);
		}
		this.transGroups();
	}

	private transient boolean restore = false;

	/**
	 * [从json格式中恢复所有组]<BR>
	 * [功能详细描述] 2013-8-29 上午10:05:41
	 */
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-8-29 上午11:34:39
	 */
	public void restoreGroups() {
		if (restore) {
			throw new UnsupportedOperationException("只能从恢复一次!");
		}
		// 获取所有组
		JsonParser jsonParser = new JsonParser();
		JsonElement groupjsonElement = jsonParser.parse(groupJson);

		Set<Entry<String, JsonElement>> entries = groupjsonElement
				.getAsJsonObject().entrySet();

		List<AbstractContainerModel<String, UserModel>> flist = new ArrayList<AbstractContainerModel<String, UserModel>>();

		for (Entry<String, JsonElement> entry : entries) {
			if (entry.getKey().equals(FriendGroupModel.class.getName())) {
				List<FriendGroupModel> friendGroupModels = CommonUtils
						.getGson().fromJson(entry.getValue(),
								new TypeToken<List<FriendGroupModel>>() {
								}.getType());
				flist.addAll(friendGroupModels);
			} else if (entry.getKey().equals(ChatGroupModel.class.getName())) {
				List<ChatGroupModel> chatGroupModels = CommonUtils.getGson()
						.fromJson(entry.getValue(),
								new TypeToken<List<ChatGroupModel>>() {
								}.getType());
				flist.addAll(chatGroupModels);
			} else if (entry.getKey().equals(FavorContainer.class.getName())) {
				List<FavorContainer> favorGroupModels = CommonUtils.getGson()
						.fromJson(entry.getValue(),
								new TypeToken<List<FavorContainer>>() {
								}.getType());
				flist.addAll(favorGroupModels);
			}
		}

		// 遍历检查所有
		for (AbstractContainerModel<String, UserModel> group : flist) {
			addGroup(group);
		}
	}

	/**
	 * [把当前所有组,用json格式存储起来]<BR>
	 * [功能详细描述] 2013-8-29 上午10:05:08
	 */
	public void transGroups() {
		Map<String, List<AbstractContainerModel<String, UserModel>>> map = new HashMap<String, List<AbstractContainerModel<String, UserModel>>>();
		Collection<AbstractContainerModel<String, UserModel>> groups = groupMap
				.values();

		for (AbstractContainerModel<String, UserModel> model : groups) {
			List<AbstractContainerModel<String, UserModel>> list = null;
			if ((list = map.get(model.getClass().getName())) == null) {
				map.put(model.getClass().getName(),
						list = new ArrayList<AbstractContainerModel<String, UserModel>>());
			}
			list.add(model);
		}
		this.groupJson = CommonUtils.getGson().toJson(map);
	}

	public void addGroup(AbstractContainerModel<String, UserModel> groupModel) {

		if (!IMModelManager.instance().containGroup(groupModel.getGroupCode())) {
			IMModelManager.instance().addUserGroupModel(groupModel);
		} else {
			groupModel = IMModelManager.instance().getUserGroupModel(
					groupModel.getGroupCode());
		}

		// if (!groupMap.containsKey(groupModel.getGroupCode())) {
		groupMap.put(groupModel.getGroupCode(), groupModel);
		// }

		if (!groupModel.containStuff(this)) {
			groupModel.addStuff(this);
		}

	}

	public boolean hasResoreGroup() {
		return restore;
	}

	public boolean hasGroup() {
		return groupMap.size() != 0;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-27 下午7:44:49
	 */
	@Override
	public String getMyId() {
		return jid;
	}

	public void sendMessage(Context context, ConversationMessage message) {

	}

	public void freeGroup(String groupCode) {
		groupMap.remove(groupCode);
		this.transGroups();
	}
}
