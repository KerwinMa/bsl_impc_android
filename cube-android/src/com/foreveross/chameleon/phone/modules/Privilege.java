package com.foreveross.chameleon.phone.modules;

import java.io.Serializable;
import java.util.List;

public class Privilege implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roleName;
	private List<Operation> operation;

	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public List<Operation> getOperation() {
		return operation;
	}
	public void setOperation(List<Operation> operation) {
		this.operation = operation;
	}


	class Operation implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String id;
		String name; 
	}
}
