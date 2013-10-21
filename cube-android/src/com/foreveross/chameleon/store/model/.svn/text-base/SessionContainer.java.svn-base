/**
 * 
 */
package com.foreveross.chameleon.store.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.foreveross.chameleon.store.core.StaticReference;

/**
 * [会话列表]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-27]
 */
public class SessionContainer extends
		AbstractContainerModel<String, SessionModel> {
	@Override
	public String getGroupCode() {
		return "SessionContainer";
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param v
	 *            2013-9-11 下午2:19:26
	 */
	@Override
	public void addStuff(SessionModel v) {
		super.addStuff(v);
	}

	public void addStuffs(List<SessionModel> vs){
		super.addStuffs(vs);
	}
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param id
	 * @param noneCreate
	 *            不存在是否创建
	 * @return 2013-9-11 下午2:26:07
	 */
	public SessionModel getSessionModel(String id, boolean noneCreate) {
		SessionModel sessionModel = getStuff(id);
		if (sessionModel != null) {
			return sessionModel;
		}
		if (noneCreate) {
			sessionModel = new SessionModel();
			sessionModel.setMyId(id);
			addStuff(sessionModel);
		}
		return sessionModel;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param prefix
	 * @return 2013-9-11 下午3:11:40
	 */
	@Override
	public Collection<SessionModel> search(String prefix) {
		List<SessionModel> sList = getList();
		List<SessionModel> filteredList = new ArrayList<SessionModel>();
		for (SessionModel sessionModel : sList) {
			String fromWhich = null;
			String jid = null;
			if (SessionModel.SESSION_ROOM.equals(sessionModel.getFromType())){
				String roomJid = sessionModel.getChatter();
				ChatGroupModel chatGroupModel = 
						IMModelManager.instance().getChatRoomContainer().getStuff(roomJid);
				if (chatGroupModel != null){
					fromWhich = chatGroupModel.getGroupName();
				}
			} else {
				UserModel userModel = IMModelManager.instance().getUserModel(sessionModel.getChatter());
				if (userModel != null){
					fromWhich = userModel.getName();
					String s[] = userModel.getJid().split("@");
					jid = s[0];
				}
			}
			if (fromWhich != null){
				if (fromWhich.contains(prefix) || jid.contains(prefix)) {
					filteredList.add(sessionModel);
				}
			}
		}
		return filteredList;
	}
	
	public void removeSession(final String roomJid) {
		removeStuff(roomJid);
		StaticReference.userMf.deleteById(roomJid, SessionModel.class);
	}
}
