package com.foreveross.chameleon.store.model;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "viewModuleRecord")
public class ViewModuleRecord extends BaseModel<ViewModuleRecord, Long> {
	
	
	@DatabaseField(generatedId = true)
	private Long myid;

	public Long getMyid() {
		return myid;
	}

	public void setMyid(Long myid) {
		this.myid = myid;
	}

	@DatabaseField
	private String moduleName = null;
	@DatabaseField
	private String appName = null;
	@DatabaseField
	private String userName = null;
	@DatabaseField
	private String datetimes = null;
	@DatabaseField
	private String className =null;
	@DatabaseField
	private String methodName = null;
	@DatabaseField
	private String action = null;

	public String getAppName() {
		return appName;
	}
	

	public String getDatetimes() {
		return datetimes;
	}

	public void setDatetimes(String datetimes) {
		this.datetimes = datetimes;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}


	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
