package com.foreveross.chameleon.store.model;

import java.io.Serializable;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "SystemInfoModel")
public class SystemInfoModel extends BaseModel<SystemInfoModel, String>  implements Serializable{

	private static final long serialVersionUID = 28849247123732893L;
	public SystemInfoModel(String alias, String systemId, String systemName,
			boolean curr, String userName) {
		super();
		this.alias = alias;
		this.systemId = systemId;
		this.systemName = systemName;
		this.curr = curr;
		this.userName = userName;
	}

	public SystemInfoModel() {

	}

	/**
	 * [别名]
	 */
	@DatabaseField
	private String alias = null;
	/**
	 * [当前系统ID]
	 */
	@DatabaseField(id = true, generatedId = false)
	private String systemId = null;
	/**
	 * [系统名称]
	 */
	@DatabaseField
	private String systemName = null;
	
	/**
	 * [是否为当前登录系统]
	 */
	@DatabaseField
	private boolean  curr = false;
	
	/**
	 * [使用这个系统的用户]
	 */
	@DatabaseField
	private String  userName = null;
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public boolean isCurr() {
		return curr;
	}

	public void setCurr(boolean curr) {
		this.curr = curr;
	}	


}
