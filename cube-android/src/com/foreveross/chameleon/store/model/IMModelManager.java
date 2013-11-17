package com.foreveross.chameleon.store.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.Log;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.ModelChangeEvent;

/**
 * [非线程安全即时通讯模型管理器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-27]
 */
public class IMModelManager {

	private final static Logger log = LoggerFactory
			.getLogger(IMModelManager.class);

	private IMModelManager() {
		addUserGroupModel(favorContainer);
	}

	private static IMModelManager imModelManager = new IMModelManager();

	public static synchronized IMModelManager instance() {
		return imModelManager;
	}

	/**
	 * [记录userModel的在线状态]
	 */
	private final Map<String, String> userStatuMap = new HashMap<String, String>();
	/**
	 * [记录所有的用户组或容器]<BR>
	 * key:组的groupCode<BR>
	 */
	private final Map<String, AbstractContainerModel<String, UserModel>> usergroupMap = new HashMap<String, AbstractContainerModel<String, UserModel>>();
	/**
	 * [记录所有的用户]<BR>
	 * key:userModel的jid<BR>
	 */
	private final Map<String, UserModel> userMap = new HashMap<String, UserModel>();

	/**
	 * [记录用户对应的房间]<BR>
	 * jid<BR>
	 */
	private final Map<String, List<ChatGroupModel>> roomMap = new HashMap<String, List<ChatGroupModel>>();
	
	/**
	 * [记录所有收藏好友的JID]<BR>
	 */
	private final ArrayList<String> collectUserList = new ArrayList<String>();

	/***************************************************************************
	 * 
	 * 状态 相关
	 * 
	 * ****************************************************************************/

	/**
	 * [记录所有的用户状态]<BR>
	 * 
	 * @param userStatuMap
	 *            2013-8-29 下午2:15:51
	 */
	public synchronized void putStatusMap(Map<String, String> userStatuMap) {
		userStatuMap.putAll(userStatuMap);
		Set<Entry<String, String>> entries = userStatuMap.entrySet();
		// 所有用户的组
		Set<AbstractContainerModel<String, UserModel>> userStatusGroup = new HashSet<AbstractContainerModel<String, UserModel>>();
		for (Entry<String, String> entry : entries) {
			UserModel userModel = null;
			if ((userModel = userMap.get(entry.getKey())) != null) {
				// 找到并改变此用户的状态
				userModel.setStatus(entry.getValue());
				// 记录当前用户所有的组
				userStatusGroup.addAll(userModel.getGroups());
			}
		}
		// 状态用户所在组排序
		for (AbstractContainerModel<String, UserModel> groupModel : userStatusGroup) {
			groupModel.sort();
		}
		// 通知model已经改变
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
				new ModelChangeEvent());

	}

	/**
	 * [获取某用户的在线状态]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 * @return 2013-9-11 上午10:40:08
	 */
	public String getStatus(String jid) {
		return userStatuMap.get(jid);
	}

	/**
	 * [清除所有相关记录]<BR>
	 * [功能详细描述] 2013-9-11 上午10:40:28
	 */
	public void clear() {
		friendContainer.clear();
		chatRoomContainer.clear();
		favorContainer.clear();
		sessionContainer.clear();
		roomMap.clear();
		userStatuMap.clear();
		userMap.clear();
		userStatuMap.clear();
		usergroupMap.clear();
	}

	/**
	 * [好友面板]
	 */
	private FriendContainer friendContainer = new FriendContainer();
	/**
	 * [群聊面板]
	 */
	private ChatRoomContainer chatRoomContainer = new ChatRoomContainer();
	/**
	 * [收藏好友面板]
	 */
	private FavorContainer favorContainer = new FavorContainer();
	/**
	 * [历史面板]
	 */
	private SessionContainer sessionContainer = new SessionContainer();

	/**
	 * [历史面板]
	 */
	public SessionContainer getSessionContainer() {
		return sessionContainer;
	}

	/**
	 * [好友面板]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-28 上午10:19:48
	 */
	public FriendContainer getFriendContainer() {
		return friendContainer;
	}

	/**
	 * [组聊面板]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-28 上午10:19:48
	 */
	public ChatRoomContainer getChatRoomContainer() {
		return chatRoomContainer;
	}

	/**
	 * [收藏好友面板]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-28 上午10:19:48
	 */
	public FavorContainer getFavorContainer() {
		return favorContainer;
	}

	/******************************************************************************************
	 * 
	 * 
	 *****************************************************************************************/

	/**
	 * [是否包含同名对象]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 * @return 2013-9-11 上午10:42:57
	 */
	public boolean containUserModel(String jid) {
		return userMap.containsKey(jid);
	}

	/**
	 * [是否包含同一对象(内存相等)]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 * @return 2013-9-11 上午10:42:57
	 */
	public boolean containUserModel(UserModel userModel) {
		UserModel model = userMap.get(userModel.getJid());
		return model != null && model == userModel;
	}

	/**
	 * [得到同名对象]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 * @return 2013-9-11 上午10:44:06
	 */
	public UserModel getUserModel(String jid) {
		return userMap.get(jid);
	}

	/**
	 * [是否包含同名组]<BR>
	 * [功能详细描述]
	 * 
	 * @param groupCode
	 * @return 2013-9-11 上午10:44:19
	 */
	public boolean containGroup(String groupCode) {
		return usergroupMap.containsKey(groupCode);
	}

	/**
	 * [是否包含同一组]<BR>
	 * [功能详细描述]
	 * 
	 * @param group
	 * @return 2013-9-11 上午10:45:22
	 */
	public boolean containGroup(AbstractContainerModel<String, UserModel> group) {
		AbstractContainerModel<String, UserModel> ug = usergroupMap.get(group
				.getGroupCode());
		return (ug != null) && (ug == group);
	}

	/**
	 * [名称相同,但对象不同]<BR>
	 * [功能详细描述]
	 * 
	 * @param group
	 * @return 2013-9-11 上午10:46:50
	 */
	public boolean equalButNotTheSame(AbstractUserGroupModel group) {
		AbstractContainerModel<String, UserModel> ug = usergroupMap.get(group
				.getGroupCode());
		return (ug != null) && (ug != group);
	}

	/**
	 * [得到指名组]<BR>
	 * [功能详细描述]
	 * 
	 * @param groupCode
	 * @return 2013-9-11 上午10:48:05
	 */
	public AbstractContainerModel<String, UserModel> getUserGroupModel(
			String groupCode) {
		return usergroupMap.get(groupCode);
	}

	/**
	 * [记录某用户状态]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 * @param status
	 *            2013-9-11 上午10:48:37
	 */
	public void putStatus(String jid, String status) {
		userStatuMap.put(jid, status);
	}

	/**
	 * [添加所有新建组]<BR>
	 * [功能详细描述]
	 * 
	 * @param groupModel
	 *            2013-8-29 下午1:59:14
	 */
	public void addUserGroupModel(
			AbstractContainerModel<String, UserModel> groupModel) {
		// 强制新建的组必先记录
		if (groupModel.size() != 0) {
//			throw new UnsupportedOperationException("添加的group,不能含有成员!");
			Log.e("IMModelManager" , "添加的group,不能含有成员!");
			return;
		}
		// 强制相同名组使用同一对象
		if (containGroup(groupModel.getGroupCode())) {
			Log.e("IMModelManager" , "已经存在此group,你是否新建了另一个!!");
			return;
//			throw new UnsupportedOperationException("已经存在此group,你是否新建了另一个!!");
		}
		usergroupMap.put(groupModel.getGroupCode(), groupModel);
		// 加入到应的容器中
		if (groupModel instanceof FriendGroupModel) {
			friendContainer.addStuff(FriendGroupModel.class.cast(groupModel));
		} else if (groupModel instanceof ChatGroupModel) {
			chatRoomContainer.addStuff(ChatGroupModel.class.cast(groupModel));
		}
	}

	/**
	 * [增加用户模型]<BR>
	 * 1.用户模型是唯一<BR>
	 * 2.用户模型中相同的Group是唯一对象<BR>
	 * 3.用户与组之前已经有联系关系<BR>
	 * 
	 * @param userModel
	 *            2013-8-29 上午9:53:08
	 */
	public synchronized void addUserModel(UserModel userModel) {
		// 强制同名用户使用同一对象
		if (containUserModel(userModel.getJid())) {
			throw new UnsupportedOperationException("已经存在,不能重复添加!");
		}
		// 强制用户与组必先相互有引用
		if (!interRef(userModel)) {
			throw new UnsupportedOperationException("group与userModel之前没有相互!");
		}
		// 设置状态
		String status = userStatuMap.get(userModel.getJid());
		if (status != null) {
			userModel.setStatus(status);
		}
		// 记录
		userMap.put(userModel.getJid(), userModel);
	}

	/**
	 * [用户与组之间是否相互关联]<BR>
	 * [功能详细描述]
	 * 
	 * @param userModel
	 * @return 2013-8-29 下午2:06:21
	 */
	private boolean interRef(UserModel userModel) {
		// 取得用户所有的组
		Collection<AbstractContainerModel<String, UserModel>> c = userModel
				.getGroups();
		// 如果组为空,或没值当返回
		if (c == null || c.isEmpty()) {
			return true;
		}
		// 如果用户中有容器没有引用此用户,当返回false
		for (AbstractContainerModel<String, UserModel> aug : c) {
			if (!aug.containStuff(userModel)) {
				log.info("user {} has a group {} not interref!",userModel.getJid(),aug.toString());
				aug.addStuff(userModel);
			}
		}
		return true;
	}

	public Map<String, UserModel> getUserMap() {
		return userMap;
	}

	public void addChatgroup(String jid, ChatGroupModel chatGroupModel) {
		List<ChatGroupModel> list = roomMap.get(jid);
		if (list == null) {
			roomMap.put(jid, list = new ArrayList<ChatGroupModel>());
		}
		list.add(chatGroupModel);
	}

	private UserModel userModel;

	public UserModel getMe() {
		return userModel;
	}

	public void setMe(UserModel userModel) {
		this.userModel = userModel;
	}

	public List<ChatGroupModel> getChatGroupModelsByJid(String jid) {
		return roomMap.get(jid);
	}
	
	public void removeChatGroupModelsByJid(String jid){
		roomMap.remove(jid);
		usergroupMap.remove(jid);
	}
	public void addCollectUserList(ArrayList<String> list){
		if(collectUserList != null){
			if (collectUserList.size() > 0){
				collectUserList.clear();
			}
			collectUserList.addAll(list);
		}
	}

	public ArrayList<String> getCollectUserList() {
		return collectUserList;
	}
	
	public void changUserFavor(ArrayList<String> list){
		for(String jid : list){
			UserModel userModel =  userMap.get(jid);
			if (userModel != null){
				userModel.setFavor(true);
				//保存状态至数据库
				userModel.update();
			}
		}
	}
	
	public void cleanCollectedData(){
		collectUserList.clear();
	}
}
