package com.foreveross.chameleon.store.model;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * [群聊组]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-26]
 */
@DatabaseTable(tableName = "ChatDataModel")
public class ChatDataModel extends BaseModel<ChatDataModel, String>{
	public ChatDataModel() {

	}

	@DatabaseField(id = true, generatedId = false)
	private String roomJid = null;
	@DatabaseField
	private String creatorJid = null;
	@DatabaseField
	private boolean mycreate = false;
	
	public String getCreatorJid() {
		return creatorJid;
	}
	public void setCreatorJid(String creatorJid) {
		this.creatorJid = creatorJid;
	}
	public String getRoomJid() {
		return roomJid;
	}
	public void setRoomJid(String roomJid) {
		this.roomJid = roomJid;
	}
	public boolean getMycreate() {
		return mycreate;
	}
	public void setMycreate(boolean mycreate) {
		this.mycreate = mycreate;
	}
}