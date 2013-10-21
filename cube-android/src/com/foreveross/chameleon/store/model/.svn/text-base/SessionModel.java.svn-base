/**
 * 
 */
package com.foreveross.chameleon.store.model;

import java.util.Collection;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * [会话模型]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-11]
 */
@DatabaseTable(tableName = "sessionModel")
public class SessionModel extends BaseModel<SessionModel, String> implements
		IDObject<String> {
	//群组聊天
	public final static String SESSION_ROOM = "room";
	//个人聊天
	public final static String SESSION_SINGLE = "single";
	/**
	 * [类型,个人还是群聊]
	 */
	@DatabaseField
	private String fromType;
	/**
	 * [个人jid或群组jid]
	 */
	@DatabaseField
	private String fromWhich;

	/**
	 * [目标对象]
	 */
	@DatabaseField
	private String toWhich;

	/**
	 * [目标对象]
	 */
	@DatabaseField(id = true, generatedId = false)
	private String chatter;

	public String getChatter() {
		return chatter;
	}

	public void setChatter(String chatter) {
		this.chatter = chatter;
	}

	/**
	 * [状态]
	 */
	@DatabaseField
	private String status;
	/**
	 * [最后一条消息]
	 */
	@DatabaseField
	private String lastContent;
	/**
	 * [最新消息条数]
	 */
	@DatabaseField
	private int unreadMessageCount;

	/**
	 * [消息发送时间]
	 */
	@DatabaseField
	private long sendTime;

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public String getFromWhich() {
		return fromWhich;
	}

	public void setFromWhich(String fromWhich) {
		this.fromWhich = fromWhich;
	}

	public String getToWhich() {
		return toWhich;
	}

	public void setToWhich(String toWhich) {
		this.toWhich = toWhich;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLastContent() {
		return lastContent;
	}

	public void setLastContent(String lastContent) {
		this.lastContent = lastContent;
	}

	public int getMessageCount() {
		return unreadMessageCount;
	}

	public void setMessageCount(int messageCount) {
		this.unreadMessageCount = messageCount;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-9-11 上午9:19:55
	 */
	@Override
	public String getMyId() {
		return chatter;
	}

	public void setMyId(String chatter) {
		this.chatter = chatter;
	}

	public void clearNewMessageCount() {
		unreadMessageCount = 0;
	}

	public void increaseCount() {
		++unreadMessageCount;
	}

	public void increaseCountBy(int count) {
		unreadMessageCount = unreadMessageCount + count;
	}

	public void decreaseCount() {
		--unreadMessageCount;

	}

	public void descreaseCountBy(int count) {
		unreadMessageCount = unreadMessageCount - count;
	}
}
