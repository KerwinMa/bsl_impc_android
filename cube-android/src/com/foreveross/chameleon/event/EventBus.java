package com.foreveross.chameleon.event;

import java.util.HashMap;
import java.util.Map;

import com.foreveross.chameleon.Application;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * [事件总线]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-16]
 */
public class EventBus extends Bus {

	private static Map<String, EventBus> map = new HashMap<String, EventBus>();

	private static Application application;

	public static void registerApp(Application app) {
		application = app;
	}

	public EventBus() {
		super(ThreadEnforcer.ANY);
	}

	public EventBus(ThreadEnforcer type) {
		super(type);
	}

	public synchronized static EventBus getEventBus(String name) {
		EventBus eventBus = map.get(name);
		if (eventBus == null) {
			map.put(name, eventBus = new EventBusProxy());
		}
		return eventBus;
	}

	public synchronized static EventBus getEventBus(String name,
			ThreadEnforcer type) {
		EventBus eventBus = map.get(name);
		if (eventBus == null) {
			map.put(name, eventBus = new EventBusProxy(type));
		}
		return eventBus;
	}

	static class EventBusProxy extends EventBus {

		private ThreadEnforcer type = ThreadEnforcer.ANY;

		public ThreadEnforcer getType() {
			return type;
		}

		public void setType(ThreadEnforcer type) {
			this.type = type;
		}

		@Override
		public void post(final Object obj) {
			
			if (type == ThreadEnforcer.MAIN) {
				application.getUIHandler().post(new Runnable() {
					@Override
					public void run() {
						EventBusProxy.super.post(obj);
					}

				});
				return;
			}
			EventBusProxy.super.post(obj);
		}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * 
		 * @param object
		 *            2013-8-14 下午2:44:40
		 */
		public EventBusProxy() {
			super();
		}

		public EventBusProxy(ThreadEnforcer type) {
			super(type);
			this.type = type;
		}

	}

}
