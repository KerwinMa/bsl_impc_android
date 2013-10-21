package com.foreveross.chameleon.store.model;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * <BR>
 * [功能详细描述]
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-7-12]
 */
@DatabaseTable(tableName = "ConversationMessage")
public class ConversationMessage extends BaseModel<ConversationMessage, Long> {

	@DatabaseField(generatedId = true)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * [哪个登陆用户的消息]
	 */
	@DatabaseField
	private String user;
	/**
	 * [从谁]
	 */
	@DatabaseField
	private String fromWho;
	/**
	 * [到谁]
	 */
	@DatabaseField
	private String toWho;
	
	/**
	 * [内容]
	 */
	@DatabaseField
	private String content;
	/**
	 * [本地时间]
	 */
	@DatabaseField
	private long localTime;

	/**
	 * [作用描述]
	 */
	@DatabaseField
	private String type;

	@DatabaseField
	private String picId;

	@DatabaseField
	private String chater;
	
	/**
	 * [类型,个人还是群聊]
	 */
	@DatabaseField
	private String fromType;

	/**
	 * 消息发送状态 已发送、未发送、发送中
	 */
	final public static int StatusFinish = 0x00;
	final public static int StatusSending = 0x01;
	final public static int StatusFailed = 0x02;
	final public static int StatusReceiving = 0x03;
	private int status;

	public String getFromWho() {
		return fromWho;
	}

	public void setFromWho(String from) {
		this.fromWho = from;
	}

	public String getToWho() {
		return toWho;
	}

	public void setToWho(String to) {
		this.toWho = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getLocalTime() {
		return localTime;
	}

	public void setLocalTime(long localTime) {
		this.localTime = localTime;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getChater() {
		return chater;
	}

	public void setChater(String chater) {
		this.chater = chater;
	}
	
	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	@Override
	public String toString() {
		return "Conversation [fromWho=" + fromWho + ", toWho=" + toWho
				+ ", content=" + content + ", localTime=" + localTime
				+ ", user=" + user + " type=" + type + ", picId=" + picId
				+ ", status=" + status + "]";
	}

}
