/**
 * 
 */
package com.foreveross.chameleon.phone.modules;

import java.util.List;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-10-11]
 */
public interface ContentChangeListener {
	public void onContentChange(int unreadCount,int msgCount,List<MsgModel> msgModelList);
}
