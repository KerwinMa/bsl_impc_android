package com.foreveross.chameleon.store.model;

import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MessageModule")
public class MessageModule extends BaseModel<MessageModule, Long> {

	@DatabaseField(generatedId = true)
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@DatabaseField
	private String title;
	@DatabaseField
	private String msgContent;
	@DatabaseField
	private String recordId;
	@DatabaseField
	private String msgTime;
	@DatabaseField
	private String msgDate;
	@DatabaseField
	private String msgSort;

	@DatabaseField
	private Boolean msgRead;

	private Boolean msgSelected;

	private Boolean msgEditable;

	private CubeModule msgModule;

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getMsgTime() {
		return msgTime;
	}

	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}

	public String getMsgSort() {
		return msgSort;
	}

	public void setMsgSort(String msgSort) {
		this.msgSort = msgSort;
	}

	public Boolean getMsgRead() {
		return msgRead;
	}

	public void setMsgRead(Boolean msgRead) {
		this.msgRead = msgRead;
	}

	public Boolean getMsgSelected() {
		return msgSelected;
	}

	public void setMsgSelected(Boolean msgSelected) {
		this.msgSelected = msgSelected;
	}

	public Boolean getMsgEditable() {
		return msgEditable;
	}

	public void setMsgEditable(Boolean msgEditable) {
		this.msgEditable = msgEditable;
	}

	public String getMsgDate() {
		return msgDate;
	}

	public void setMsgDate(String msgDate) {
		this.msgDate = msgDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the recordId
	 */
	public String getRecordId() {
		return recordId;
	}

	/**
	 * @param recordId
	 *            the recordId to set
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public CubeModule getMsgModule() {
		return msgModule;
	}

	public void setMsgModule(CubeModule msgModule) {
		this.msgModule = msgModule;
	}
}
