/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-7-11]
 */
public class NetworkUtil {

	public  static Boolean isNetworkConnected(Context context) {  
		ConnectivityManager manager = (ConnectivityManager) context    
		              .getApplicationContext().getSystemService(    
		                     Context.CONNECTIVITY_SERVICE);    
		          
		       if (manager == null) {    
		           return false;    
		       }    
		          
		       NetworkInfo networkinfo = manager.getActiveNetworkInfo();    
		          
		       if (networkinfo == null || !networkinfo.isAvailable()) {    
		           return false;    
		       }    
		     
		       return true;    
		    } 

}
