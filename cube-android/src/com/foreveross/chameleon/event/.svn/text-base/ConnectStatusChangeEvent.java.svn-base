/**
 * 
 */
package com.foreveross.chameleon.event;

/**
 * [连接状态改变事件]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-19]
 */
public class ConnectStatusChangeEvent {

	public static final String CONN_CHANNEL_XMPP = "CONN_CHANNEL_XMPP";
	public static final String CONN_CHANNEL_MINA = "CONN_CHANNEL_MINA";
	public static final String CONN_STATUS_OFFLINE = "CONN_STATUS_OFFLINE";
	public static final String CONN_STATUS_ONLINE = "CONN_STATUS_ONLINE";
	
	public static final String SHOW_TOAST = "SHOW_TOAST";
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-7-19 上午10:59:48
	 */
	private final String channel;

	public String getChannel() {
		return channel;
	}

	private final String status;

	public String getStatus() {
		return status;
	}

	public ConnectStatusChangeEvent(String channel, String status) {
		this.channel = channel;
		this.status = status;
	}

}
