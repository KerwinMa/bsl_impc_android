package com.foreveross.chameleon.phone.modules;

import java.util.HashMap;

import com.foreveross.chameleon.store.model.SystemInfoModel;

public class LoginModel {

	private static LoginModel loginModel;
	private HashMap<String, SystemInfoModel> hasMap;

	public static synchronized LoginModel instance() {
		if (loginModel == null){
			loginModel = new LoginModel();
		}
		return loginModel;
	}

	public HashMap<String, SystemInfoModel> getHasMap() {
		return hasMap;
	}

	public boolean containSysId(String SysID){
		if (hasMap == null ){
			hasMap = new HashMap<String, SystemInfoModel>();
			return false;
		}
		return hasMap.containsKey(SysID);
	}
	
	public SystemInfoModel getSystemModel(String SysID){
		return hasMap.get(SysID);
	}
	
	public void putSysInfo(String SysID , SystemInfoModel model){
		if (hasMap == null ){
			hasMap = new HashMap<String, SystemInfoModel>();
		}
		hasMap.put(SysID, model);
	}
	
	public void clear(){
		if (hasMap != null){
			hasMap.clear();
		}
	}
}
