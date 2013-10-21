/**
 * 
 */
package com.foreveross.chameleon.event;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-20]
 */
public class PresenceEvent {

	public PresenceEvent(String identifier, boolean presence) {
		super();
		this.identifier = identifier;
		this.presence = presence;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean isPresence() {
		return presence;
	}

	private final String identifier;
	private final boolean presence;

}
