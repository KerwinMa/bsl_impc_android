package com.foreveross.chameleon.phone.modules;

public enum ModuleType {
	
	PRE_INSTALLED,UNINSTALLED,INSTALLED,UPDATABLE;
	
	public  static ModuleType returnModuleType(String name){
		ModuleType[] modules = ModuleType.values();
		for(ModuleType moduleType:modules){
			if(moduleType.name().equals(name)){
				return moduleType;
			}
		}
		return null;
	};
}
