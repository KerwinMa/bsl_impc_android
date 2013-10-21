/**
 * 
 */
package com.foreveross.chameleon.event;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-9]
 */
public class XmppConnectEvent {

	private final boolean connected;

	public boolean isConnected() {
		return connected;
	}

	public XmppConnectEvent(final boolean connected) {
		this.connected = connected;
	}

}
