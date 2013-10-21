package com.foreveross.chameleon.phone.view;

import java.util.ArrayList;
import java.util.List;

import com.foreveross.chameleon.phone.modules.CubeModule;

public class RefreshEventManager {
	 public enum RefreshType {
		 INSTALL_READY,
		 INSTALL_START,
		 INSTALL_REFRESH,
		 
		 UPDATE_READY,
		 UPDATE_START,
	
		 UNZIP_FINISH,
		 UNZIP_FAILED,
		 
		 DELETE_READY,
		 DELETE_START,
		 DELETE_REFRESH,
		 DELETE_FINISH,
		 DELETE_FAILED,
		 
		 HTTP_FAILED

	 }
	private static List<RefreshEventListener> cubeModulelistener = new ArrayList<RefreshEventListener>();
	public static void addRefreshListener(RefreshEventListener listener){
	    	if(null != listener){
	    		cubeModulelistener.add(listener);
	    	}
	    }
	    
		public static void notifyRefreshEvent(final RefreshType refreshType,final CubeModule ret) {
	        if (cubeModulelistener != null) {
	        	for (final RefreshEventListener listener : cubeModulelistener) {
	        		new Thread() {
	        			@Override
	        			public void run() {
	        				listener.onRefreshed(refreshType,ret);
	        			}
	        		}.start();
				}
	        }
	    }
	    
	    public static void removeRefreshListener(RefreshEventListener listener) {
	    	if (cubeModulelistener != null) {
	    		cubeModulelistener.remove(listener);
	    	}
	    }
    public interface RefreshEventListener {
        void onRefreshed(RefreshType refreshType,CubeModule ret);
    }

}
