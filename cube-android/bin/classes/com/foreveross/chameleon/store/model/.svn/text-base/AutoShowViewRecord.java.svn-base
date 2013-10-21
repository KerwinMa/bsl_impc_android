package com.foreveross.chameleon.store.model;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="AutoShowViewRecord")
public class AutoShowViewRecord extends BaseModel<AutoShowViewRecord, Long>{
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
	private String timeUnit; // 默认为空，可输入“H”、“M”、“S”
	@DatabaseField
	private String showIntervalTime;// 时间间隔
	@DatabaseField
	private long showTime;


	public long getShowTime() {
		return showTime;
	}

	public void setShowTime(long showTime) {
		this.showTime = showTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getShowIntervalTime() {
		return showIntervalTime;
	}

	public void setShowIntervalTime(String showIntervalTime) {
		this.showIntervalTime = showIntervalTime;
	}

	
}
