/**
 * 
 */
package com.foreveross.chameleon.push.cubeparser.type;

import com.j256.ormlite.field.DatabaseField;

/**
 * [模块消息]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-22]
 */
public abstract class ModuleMessage<T> extends AbstractMessage<T> {

	public ModuleMessage() {
		super();
	}

	/**
	 * @param sendTime
	 * @param messsageId
	 * @param title
	 * @param content
	 */
	@DatabaseField
	protected String identifier;
	@DatabaseField
	protected boolean linkable = false;
	@DatabaseField
	protected String userName;

	public boolean isLinkable() {
		return linkable;
	}

	public void setLinkable(boolean linkable) {
		this.linkable = linkable;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ModuleMessage(long sendTime, String messsageId, String title,
			String content) {
		super(sendTime, messsageId, title, content);
	}

}
