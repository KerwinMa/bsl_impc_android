/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.util;

import org.apache.mina.core.session.IoSession;

/**
 * [SessionHelper]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-6-25]
 */
public class SessionHelper {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * @param args
	 * 2013-6-25 上午11:09:46
	 */
	public static Long getSessionId(IoSession session){
		Boolean auth = (Boolean)session.getAttribute("auth");
		if(auth!=null&&auth){
			return session.getId();
		}
		return (long) session.hashCode();
	}

}
