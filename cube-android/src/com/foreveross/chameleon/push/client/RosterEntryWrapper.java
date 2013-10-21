/**
 * 
 */
package com.foreveross.chameleon.push.client;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.util.StringUtils;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.store.model.AbstractContainerModel;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.FriendGroupModel;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.Preferences;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-29]
 */
public class RosterEntryWrapper {

	private RosterEntry rosterEntry;

	public boolean equals(Object arg0) {
		return rosterEntry.equals(arg0);
	}

	public boolean equalsDeep(Object arg0) {
		return rosterEntry.equalsDeep(arg0);
	}

	private Set<AbstractContainerModel<String, UserModel>> rset = new HashSet<AbstractContainerModel<String, UserModel>>();

	public Collection<AbstractContainerModel<String, UserModel>> getGroups() {
		Collection<RosterGroup> rg = rosterEntry.getGroups();
		for (RosterGroup group : rg) {
			// TODO[FENGWIELI] name change to code
			FriendGroupModel friendGroupModel = (FriendGroupModel) IMModelManager
					.instance().getUserGroupModel(group.getName());
			if (friendGroupModel == null) {
				friendGroupModel = new FriendGroupModel();
				friendGroupModel.setGroupCode(group.getName());
				friendGroupModel.setGroupName(group.getName());
				rset.add(friendGroupModel);
				IMModelManager.instance().addUserGroupModel(friendGroupModel);
			} else {
				rset.add(friendGroupModel);
			}
		}

		getChatGroup(rset);
		return rset;
	}

	
	// ChatGroupModel
	public void getChatGroup(Set<AbstractContainerModel<String, UserModel>> rset) {
		List<ChatGroupModel> list = IMModelManager.instance().getChatGroupModelsByJid(
						StringUtils.parseBareAddress(rosterEntry.getUser()));
		
//		List<ChatGroupModel> list = IMModelManager.instance()
//				.getChatGroupModelsByJid(
//						Preferences.getUserName(Application.sharePref)+"@snda-192-168-2-32");
		if(list!=null){
			rset.addAll(list);
		}
	}

	public void addGroup(AbstractContainerModel<String, UserModel> model) {
		AbstractContainerModel<String, UserModel> userGroupModel = IMModelManager
				.instance().getUserGroupModel(model.getGroupCode());
		if (userGroupModel != null) {
			rset.add(userGroupModel);
		} else {
			rset.add(model);
			IMModelManager.instance().addUserGroupModel(model);
		}
	}

	public String getName() {
		return rosterEntry.getName();
	}

	public ItemStatus getStatus() {
		return rosterEntry.getStatus();
	}

	public ItemType getType() {
		return rosterEntry.getType();
	}

	public String getUser() {
		return rosterEntry.getUser();
	}

	public int hashCode() {
		return rosterEntry.hashCode();
	}

	public void setName(String arg0) {
		rosterEntry.setName(arg0);
	}

	public String toString() {
		return rosterEntry.toString();
	}

	public RosterEntryWrapper(RosterEntry rosterEntry) {
		this.rosterEntry = rosterEntry;
	}

}
