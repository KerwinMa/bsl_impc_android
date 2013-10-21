/**
 * 
 */
package com.foreveross.chameleon.store.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-27]
 */
public abstract class AbstractUserGroupModel extends
		AbstractContainerModel<String, UserModel> implements IDObject<String> {

	private String belongContainer = null;

	public String getBelongContainer() {
		return belongContainer;
	}

	public void setBelongContainer(String belongContainer) {
		this.belongContainer = belongContainer;
	}

	public List<UserModel> getFavors() {
		List<UserModel> list = getList();
		List<UserModel> l = new ArrayList<UserModel>();
		for (UserModel userModel : list) {
			if (userModel.isFavor()) {
				l.add(userModel);
			}
		}
		return l;
	}

	public String getMyId() {
		return groupCode;
	}

	@Override
	public List<UserModel> search(String prefix) {
		List<UserModel> filteredList = new ArrayList<UserModel>();
		List<UserModel> ul = getList();
		for (UserModel user : ul) {
			String jid = user.getJid();
			if (jid != null) {
				String s[] = jid.split("@");
				jid = s[0];
			}
			if ((jid.contains(prefix))
					|| (user.getName() != null && user.getName().contains(
							prefix))) {
				filteredList.add(user);
			}
		}
		return filteredList;
	}

	public void sort() {
		Collections.sort(getList(), new Comparator<UserModel>() {

			@Override
			public int compare(UserModel lhs, UserModel rhs) {
				int v1 = UserStatus.USER_STATE_ONLINE.equals(lhs.getStatus()) ? 1
						: 0;
				int v2 = UserStatus.USER_STATE_ONLINE.equals(rhs.getStatus()) ? 1
						: 0;
				int returnValue = 0;
				if ((returnValue = v2 - v1) != 0) {
					return returnValue;
				}
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		});
	}

}
