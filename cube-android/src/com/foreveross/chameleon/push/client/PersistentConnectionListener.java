/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreveross.chameleon.push.client;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.StreamError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.ConnectStatusChangeEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.MultiAccountEvent;
import com.foreveross.chameleon.push.client.XmppManager.Type;
import com.squareup.otto.ThreadEnforcer;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-9]
 */
public class PersistentConnectionListener implements ConnectionListener {

	private final static Logger log = LoggerFactory
			.getLogger(ConnectivityReceiver.class);

	private final XmppManager xmppManager;

	public PersistentConnectionListener(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
	}

	private void sendBroadcastWithStatus(String channel, String status) {
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.post(new ConnectStatusChangeEvent(channel, status));
	}
	@Override
	public void connectionClosed() {
		log.info("xmpp connectionClosed()...");
		if(xmppManager.getType()==Type.CHAT){
			sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
					ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
			
		}else{
			sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
					ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
		}
//		if(!NetworkUtil.isNetworkConnected(xmppManager.getNotificationService())){
//			
//			((Application)xmppManager.getNotificationService().getApplication()).caculateHeartBeartRemain();	
//		}
//		

	}

	@Override
	public synchronized void connectionClosedOnError(Exception e) {
		log.debug("xmpp connectionClosedOnError()...");

		if (e instanceof XMPPException) {
			log.debug("exception is XMPPException");
			XMPPException xmppEx = (XMPPException) e;
			StreamError error = xmppEx.getStreamError();
			String reason = error.getCode();
			if ("conflict".equals(reason)) {
				// 当前账号已在别处登录
				log.info("xmpp冲突，被迫下线", e);
				try {
					if (xmppManager.isConnected()) {
						log.debug("xmpp manager is connecting,disconnect it!");
						xmppManager.disconnect();
						
					}
				} catch (Exception e1) {
					log.error("close xmpp connection error!", e1);
				}
				// 发送广播通知Activity弹窗关闭应用
				
				EventBus.getEventBus(TmpConstants.EVENTBUS_MUTIPLEACCOUNT_BROADCAST)
				.post(MultiAccountEvent.MultiAccount);
				
//				xmppManager.getNotificationService().sendBroadcast(
//						new Intent("com.xmpp.mutipleAccount"));
			}
		} else {
			log.debug("exception is not XMPPException");
			if (xmppManager.isConnected()) {
				log.debug("xmpp manager is connecting,disconnect it!");
				xmppManager.disconnect();
				xmppManager.stopReconnectionThread();
			}
			log.debug("start reconnect thread to connect...");
			xmppManager.startReconnectionThread();
		}

	}

	@Override
	public synchronized void reconnectingIn(int seconds) {
		log.info("xmpp reconnectingIn()...");
	}
	
	@Override
	public synchronized void reconnectionFailed(Exception e) {
		log.error("xmpp reconnectionFailed()...", e);
	}

	@Override
	public void reconnectionSuccessful() {
		log.info("xmpp reconnectionSuccessful()...");
		if(xmppManager.getType()==Type.CHAT){
			sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
					ConnectStatusChangeEvent.CONN_STATUS_ONLINE);
		}else{
			sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
					ConnectStatusChangeEvent.CONN_STATUS_ONLINE);
		}
		
	}

}
