/**
 * 
 */
package com.foreveross.chameleon.store.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * [普通好友分组面板]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-27]
 */
public class FriendContainer extends
		AbstractContainerModel<String, FriendGroupModel> {

	/**
	 * @param userModels
	 */

	private Map<String, UserModel> userMap = new HashMap<String, UserModel>();
	private Set<FriendGroupModel> userGroups = new HashSet<FriendGroupModel>();

	public FriendContainer(){
		
	}
	public FriendContainer(List<UserModel> userModels) {
		init(userModels);
	}

	public void init(List<UserModel> userModels) {
		addUserModels(userModels.toArray(new UserModel[userModels.size()]));
	}

	public void addUserModel(UserModel userModel) {
		addUserModelInner(userModel);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addUserModelInner(UserModel userModel) {
		if (userModel.hasResoreGroup()) {
			userModel.restoreGroups();
		}
//		findFriendGroupType(userModel.getGroups());
	}

	private void findFriendGroupType(List<AbstractUserGroupModel> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		for (AbstractUserGroupModel group : list) {
			if (group instanceof FriendGroupModel) {
				userGroups.add((FriendGroupModel) group);
			}
		}
	}



	public void addUserModels(UserModel... userModels) {
		for (UserModel userModel : userModels) {
			addUserModel(userModel);
		}
	}

	public boolean containUserModel(String jid) {
		return userMap.containsKey(jid);
	}

	public UserModel getUserModel(String jid) {
		return userMap.get(jid);
	}

}
