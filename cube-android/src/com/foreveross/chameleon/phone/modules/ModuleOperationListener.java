package com.foreveross.chameleon.phone.modules;


public interface ModuleOperationListener {

	public void opSuccess(OPType opType,CubeModule cubeModule);
	public void opInProgress(OPType opType,CubeModule cubeModule,int progresss);
	public void opFailed(OPType opType,CubeModule cubeModule,String reason);
}
