/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.api;

/**
 * [SessionIdAware]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [mina_android_lib, 2013-7-8]
 */
public interface SessionIdAware {
	
	public void sessionIdCreated(Long sessionId,MinaMobileClient minaMobileClient);

	public void sessionIdDestroyed(Long sessionId);
}
