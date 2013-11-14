package com.foreveross.chameleon.store.model;

import java.io.Serializable;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "SystemInfoModel")
public class SystemInfoModel extends BaseModel<SystemInfoModel, String>  implements Serializable{

	private static final long serialVersionUID = 28849247123732893L;

	public SystemInfoModel(String alias, String sysId, String sysName,
			boolean curr, String username) {
		super();
		this.alias = alias;
		this.sysId = sysId;
		this.sysName = sysName;
		this.curr = curr;
		this.username = username;
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
	private String sysId = null;
	/**
	 * [系统名称]
	 */
	@DatabaseField
	private String sysName = null;
	
	/**
	 * [是否为当前登录系统]
	 */
	@DatabaseField
	private boolean  curr = false;
	
	/**
	 * [使用这个系统的用户]
	 */
	@DatabaseField
	private String  username = null;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getSysName() {
		return sysName;
	}

	public void setSysName(String sysName) {
		this.sysName = sysName;
	}

	public boolean isCurr() {
		return curr;
	}

	public void setCurr(boolean curr) {
		this.curr = curr;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "SystemInfoModel [alias=" + alias + ", sysId=" + sysId
				+ ", sysName=" + sysName + ", curr=" + curr + ", username="
				+ username + "]";
	}
}
