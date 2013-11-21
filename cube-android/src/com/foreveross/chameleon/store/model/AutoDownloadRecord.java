package com.foreveross.chameleon.store.model;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="AutoDownloadRecord")
public class AutoDownloadRecord extends BaseModel<AutoDownloadRecord, Long>{
	
	
	@DatabaseField(generatedId = true)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@DatabaseField
	private String userName;
	@DatabaseField
	private String hasShow;
	@DatabaseField
	private String identifier;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getHasShow() {
		return hasShow;
	}
	public void setHasShow(String hasShow) {
		this.hasShow = hasShow;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}


}
