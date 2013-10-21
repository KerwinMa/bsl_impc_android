/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * [包回调 注册]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-7-11]
 */
public class CallbackStore {

	private static CallbackStore callbackStore = new CallbackStore();


	private Map<Long, PacketCollector> map = Collections
			.synchronizedMap(new HashMap<Long, PacketCollector>());

	private CallbackStore() {

	}

	public static CallbackStore instance() {
		return callbackStore;
	}

	public PacketCollector getPacketCollector(Long id) {
		return map.get(id);
	}

	public void setPacketCollector(Long id, PacketCollector packetCollector) {
		map.put(id, packetCollector);
	}
}
