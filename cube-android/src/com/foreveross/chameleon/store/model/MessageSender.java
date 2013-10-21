/**
 * 
 */
package com.foreveross.chameleon.store.model;

import android.content.Context;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-17]
 */
public interface MessageSender {
	public void sendMessage(Context context,
			ConversationMessage conversationMessage);
	
}
