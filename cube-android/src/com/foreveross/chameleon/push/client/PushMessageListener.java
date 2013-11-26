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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.Intent;

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.ConversationChangedEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.PatchMessageModelEvent;
import com.foreveross.chameleon.event.PatchNoticeModelEvent;
import com.foreveross.chameleon.phone.activity.MessageActivity;
import com.foreveross.chameleon.phone.activity.NoticeActivity;
import com.foreveross.chameleon.phone.modules.MessageFragmentModel;
import com.foreveross.chameleon.push.cubeparser.type.AbstractMessage;
import com.foreveross.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.foreveross.chameleon.push.cubeparser.type.MDMMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.store.core.BaseModel;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.UnkownUtil;
import com.squareup.otto.ThreadEnforcer;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]接收推送监听器
 * 
 * @author Amberlo
 * @version [CubeAndroid, 2013-11-19]
 */
public class PushMessageListener implements PacketListener {
	private Context context;
	private DelayQueue<Delayed> delayQueue = new DelayQueue<Delayed>();
	private final static Logger log = LoggerFactory.getLogger(PushMessageListener.class);
	private PropertiesUtil propertiesUtil;
	private com.foreveross.chameleon.Application application;
	public  PushMessageListener (){
		
	}
	

	public PushMessageListener (Context context, XmppManager xmppManager) {
		this.application = com.foreveross.chameleon.Application.class
				.cast(context.getApplicationContext());
		this.context = context;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Delayed delayed = delayQueue.take();
						if (delayed instanceof ChanmeleonMessage) {
							sendMDM((MDMMessage) (ChanmeleonMessage.class
									.cast(delayed).getPackedMessage()));
						} else {
							if (delayed instanceof PatchNoticeModelEvent) {
								sendNoticeModuleMessage((PatchNoticeModelEvent) (delayed));
							} else {
								sendMessage((PatchMessageModelEvent)(delayed));
							}

						}

					} catch (InterruptedException e) {
						log.error("take queue error!", e);
					}
				}
			}
		}).start();

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				// 如果缓冲区为空,则返回
				if (buffer.isEmpty()) {
					return;
				}
				List<Delayed> subBuffer = null;
				synchronized (PushMessageListener.this) {
					subBuffer = new ArrayList<Delayed>(buffer);
					buffer.clear();
				}
				PatchMessageModelEvent messageModelEvent = new PatchMessageModelEvent();
				PatchNoticeModelEvent noticModelEvent = new PatchNoticeModelEvent();
				// Map<String, Integer> countMap = new HashMap<String,
				// Integer>();
				int messageCount = 0;
				for (Delayed delayed : subBuffer) {

					ChanmeleonMessage chanmeleonMessage = ChanmeleonMessage.class
							.cast(delayed);
					// if (chanmeleonMessage.getPackedMessage() instanceof
					// ModuleMessage) {
					// ModuleMessage mm = ModuleMessage.class
					// .cast(chanmeleonMessage.getPackedMessage());
					// Integer count = countMap.get(mm.getIdentifier());
					// countMap.put(mm.getIdentifier(), count == null ? 1
					// : count + 1);
					// }

					if (chanmeleonMessage.savable()) {
						BaseModel baseModel = chanmeleonMessage
								.getPackedMessage();
						StaticReference.defMf.createOrUpdate(baseModel);
						if (baseModel instanceof NoticeModuleMessage) {
							StaticReference.defMf
									.createOrUpdate(NoticeModuleMessage.class
											.cast(baseModel).stub());
							NoticeModuleMessage noticeModuleMessage = NoticeModuleMessage.class
									.cast(baseModel);
							noticModelEvent
									.addNoticeModuleMessage(noticeModuleMessage);
							messageModelEvent
									.addChanmeleonMessage(new ChanmeleonMessage(
											noticeModuleMessage.stub()));
							++messageCount;
						} else if (!(baseModel instanceof MDMMessage)) {
							messageModelEvent
									.addChanmeleonMessage(chanmeleonMessage);
							++messageCount;
						} else {
							delayQueue.add(delayed);

						}
					}
				}

				if (!messageModelEvent.isEmpty()) {
					delayQueue.add(messageModelEvent);
				}
				if (!noticModelEvent.isEmpty()) {
					delayQueue.add(noticModelEvent);
				}

				// Set<Map.Entry<String, Integer>> countEntries = countMap
				// .entrySet();
				// for (Map.Entry<String, Integer> countEntry : countEntries) {
				// if (TmpConstants.MESSAGE_RECORD_IDENTIFIER
				// .equals(countEntry.getKey())) {
				// continue;
				// }
				// CubeModule noticeModule = CubeModuleManager.getInstance()
				// .getCubeModuleByIdentifier(countEntry.getKey());
				// if (noticeModule != null) {
				// noticeModule.increaseMsgCountBy(countEntry.getValue());
				// }
				// }

//				CubeModule messageModule = CubeModuleManager.getInstance()
//						.getCubeModuleByIdentifier(
//								TmpConstants.MESSAGE_RECORD_IDENTIFIER);
//				if (messageModule != null) {
//					messageModule.increaseMsgCountBy(messageCount);
//				}
			}
		}, 0, 3000);
	}

	private void doNotify() {

		EventBus.getEventBus(TmpConstants.EVENTBUS_MESSAGE_CONTENT,
				ThreadEnforcer.MAIN).post(new ConversationChangedEvent());
	}
	
	public void sendMDM(MDMMessage mdmMessage) {
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(mdmMessage);
	}

	public void sendNoticeModuleMessage(
			PatchNoticeModelEvent patchNoticeModelEvent) {
		if (UnkownUtil.isNoticeViewPresence(context)) {
			EventBus.getEventBus(TmpConstants.EVENTBUS_ANNOUNCE_CONTENT,
					ThreadEnforcer.MAIN).post(patchNoticeModelEvent);
		}
		// MessageFragmentModel.instance().addMessages(
		// new ArrayList<AbstractMessage<?>>(patchNoticeModelEvent
		// .getPatch()));
		if (application.shouldSendNoticeNotification()) {
			sendNoticeNotification(patchNoticeModelEvent);
		}
	}
	//公告
	public void sendNoticeNotification(
			final PatchNoticeModelEvent patchNoticeModelEvent) {
		final Intent intent = new Intent();
		intent.setClass(application, FacadeActivity.class);
		if (PadUtils.isPad(application)) {
			if (!application.isHasLogined()) {
				intent.putExtra("url", URL.PAD_LOGIN_URL);
				intent.putExtra("isPad", true);
			} else {
				String noticeViewClassName = propertiesUtil.getString(
						"com.foss.announcement", "");
				intent.putExtra("direction", 2);
				intent.putExtra("type", "fragment");
				intent.putExtra("value", noticeViewClassName);
			}

		} else {
			if (!application.isHasLogined()) {
				application.setModuleName("moduleName");
				intent.putExtra("url", URL.PHONE_LOGIN_URL);
				intent.putExtra("isPad", false);
			} else {
				intent.setClass(application, NoticeActivity.class);
			}
		}
		application.getUIHandler().post(new Runnable() {

			@Override
			public void run() {

				NoticeModuleMessage noticeModuleMessage = patchNoticeModelEvent
						.lastNoticeModuleMessage();
				Notifier.notifyInfo(context, R.drawable.appicon,
						Constants.ID_NOTICE_NOTIFICATION,
						noticeModuleMessage.getTitle(),
						noticeModuleMessage.getContent(), intent);

			}
		});

	}

	/**
	 * [包括模块与系统消息]<BR>
	 * [功能详细描述]
	 * 
	 * @param moduleContent
	 *            2013-8-22 下午12:01:52
	 */
	public void sendMessage(PatchMessageModelEvent pathMessageModelEvent ) {
		// if (UnkownUtil.isMessageViewPresence(context)) {
		// EventBus.getEventBus(TmpConstants.EVENTBUS_MESSAGE_CONTENT,
		// ThreadEnforcer.MAIN).post(pathMessageModelEvent);
		// }
		MessageFragmentModel.instance().addMessages(
				new ArrayList<AbstractMessage<?>>(pathMessageModelEvent
						.getPacked()));
		if (!pathMessageModelEvent.lastIsNotice()
				&& application.shouldSendMessageNotification()) {
			sendMessageNotification(pathMessageModelEvent);
		}

	}
	//消息
	private void sendMessageNotification(
			final PatchMessageModelEvent patchMessageModelEvent ) {
		final Intent intent = new Intent();
		intent.setClass(application, FacadeActivity.class);
		if (PadUtils.isPad(application)) {
			if (!application.isHasLogined()) {
				intent.putExtra("url", URL.PAD_LOGIN_URL);
				intent.putExtra("isPad", true);
			} else {
				String messageViewClassName = propertiesUtil.getString(
						"com.foss.message.record", "");
				intent.putExtra("direction", 2);
				intent.putExtra("type", "fragment");
				intent.putExtra("value", messageViewClassName);
			}

		} else {
			if (!application.isHasLogined()) {
				intent.putExtra("url", URL.PHONE_LOGIN_URL);
				intent.putExtra("isPad", false);
				
				application.setModuleName("moduleName");
			//	application.setPushUrl(patchMessageModelEvent.lastChanmeleonMessage().getPackedMessage().getModuleUrl());
			} else {
				
				intent.setClass(application, MessageActivity.class);
			}
		}
		application.getUIHandler().post(new Runnable() {
			
			@Override
			public void run() {
				ChanmeleonMessage chanmeleonMessage = patchMessageModelEvent
						.lastChanmeleonMessage();
				Notifier.notifyInfo(context, R.drawable.appicon,
						Constants.ID_MESSAGE_NOTIFICATION, chanmeleonMessage
								.getPackedMessage().getTitle(),
						chanmeleonMessage.getPackedMessage().getContent(),
						intent);
			}
		});

	}
	
	private List<Delayed> buffer = Collections
			.synchronizedList(new ArrayList<Delayed>());


	@Override
	public void processPacket(Packet packet) {
		log.debug("NotificationPacketListener.processPacket()...");
		log.debug("packet.toXML()=" + packet.toXML());
		
		if (packet instanceof Message) {
			Message message = Message.class.cast(packet);
			
				synchronized (PushMessageListener.this) {
					try {
						buffer.addAll(NotificationPushContent.parseRemoteModel(message,context));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		}

	}
	

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
//		pool.shutdown();
	}


}
