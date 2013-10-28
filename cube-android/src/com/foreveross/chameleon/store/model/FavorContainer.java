/**
 * 
 */
package com.foreveross.chameleon.store.model;

import java.util.ArrayList;
import java.util.List;

/**
 * [收藏好友面板]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-27]
 */
public class FavorContainer extends AbstractContainerModel<String, UserModel> {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-9-11 上午11:04:44
	 */
	@Override
	public String getGroupCode() {
		
		return "FavorContainer";
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
	@Override
	public void addStuff(UserModel v) {
		// TODO Auto-generated method stub
		List<UserModel> list = getList();
		if (!list.contains(v)){
			super.addStuff(v);
		}
	}
}
