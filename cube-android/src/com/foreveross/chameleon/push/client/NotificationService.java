package com.foreveross.chameleon.push.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.foreveross.chameleon.CubeConstants;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.ConnectStatusChangeEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.MultiAccountEvent;
import com.foreveross.chameleon.event.XmppConnectEvent;
import com.foreveross.chameleon.phone.activity.MultiAccountActivity;
import com.foreveross.chameleon.push.client.XmppManager.RosterManager;
import com.foreveross.chameleon.push.client.XmppManager.Type;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.util.Pool;
import com.foreveross.chameleon.util.TimeUnit;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

/**
 * [通知服务]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-16]
 */
public class NotificationService extends Service {

	private final static Logger log = LoggerFactory
			.getLogger(NotificationService.class);

	private TelephonyManager telephonyManager;

	private BroadcastReceiver connectivityReceiver;

	private PhoneStateListener phoneStateListener;

	private XmppManager chatManager;

	private XmppManager pushManager;

	public class NotificationServiceBinder extends Binder {
		public NotificationService getService() {
			return NotificationService.this;
		}
	}

	public NotificationService() {
		connectivityReceiver = new ConnectivityReceiver(this);
		phoneStateListener = new PhoneStateChangeListener(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_MUTIPLEACCOUNT_BROADCAST,
				ThreadEnforcer.MAIN).register(this);
	}

	@Override
	public void onCreate() {
		log.debug("NotificationService  onCreate()...");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		chatManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,XmppManager.Type.CHAT);
		pushManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,XmppManager.Type.PUSH);

		rosterManager = chatManager.new RosterManager(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 0) {
					sendBroadcast(new Intent("push.model.change"));
				}
				sendBroadcast(new Intent("com.csair.cubeModelChange").putExtra(
						"identifier", msg.getData().getString("identifier")));
			}

		});
		log.debug("prepair connect for xmpp...");
		chatManager.prepairConnect();
		pushManager.prepairConnect();
		registerConnectivityReceiver();
	}

	@Override
	public void onDestroy() {
		log.debug("notificationService onDestroy()...");
		// EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).unregister(this);
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		log.debug("notificationService onBind()...");
		return new NotificationServiceBinder();
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, NotificationService.class);
	}

	public XmppManager getChatManager() {
		if (chatManager == null) {
			chatManager = new XmppManager(this,CubeConstants.CUBE_CONFIG, Type.CHAT);
		}
		return chatManager;
	}
	
	public XmppManager getPushManager() {
		if (pushManager == null) {
			pushManager = new XmppManager(this,CubeConstants.CUBE_CONFIG,Type.PUSH);
		}
		return pushManager;
	}
	
	public void connect(final String username, final String password, final XmppManager xmppManager) {

		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		log.debug("start connect to xmpp for user {}", username);
		Pool.run(new Runnable() {
			public void run() {
				if (xmppManager != null && !xmppManager.isAuthenticated()) {
					xmppManager.submitConnectReq(username, password);
				}
			}
		});
		// }
		// }, application.getRemainTimes() * 1000);

	}

	public void disconnect(final XmppManager xmppManager) {
		log.debug("start disconnect to xmpp");

		Pool.run(new Runnable() {
			public void run() {
				if (xmppManager.isConnected()) {
					xmppManager.disconnect();

				}
			}
		});
	}

	public void virtualDisconnect() {
		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
				ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
				ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
		sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_MINA,
				ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
	}

	private void sendBroadcastWithStatus(String channel, String status) {
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.post(new ConnectStatusChangeEvent(channel, status));
	}

	@Subscribe
	public void onConnectEvent(XmppConnectEvent connectEvent) {
		if (connectEvent.isConnected()) {
			registerConnectivityReceiver();
		} else {
			unregisterConnectivityReceiver();

		}
	}

	public boolean isConnected(XmppManager xmppManager) {
		return xmppManager.isConnected();
	}

	private void registerConnectivityReceiver() {
		log.debug("registerConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
		IntentFilter filter = new IntentFilter();
		filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(connectivityReceiver, filter);
	}

	private void unregisterConnectivityReceiver() {
		log.debug("unregisterConnectivityReceiver()...");
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(connectivityReceiver);
	}

	public void reconnect(XmppManager xmppManager) {
		// timer.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		log.debug("notification reconnect()...");
		xmppManager.reconnect();
		// }
		// }, application.getRemainTimes() * 1000);

	}

	private void stop() {
		log.debug("notification stop()...");
		unregisterConnectivityReceiver();
		getChatManager().disconnect();
		getPushManager().disconnect();
	}

	public void online(XmppManager xmppManager) {
		xmppManager.online();
	}

	public void offline(XmppManager xmppManager) {
		xmppManager.offline();
	}

	public boolean isOnline(XmppManager xmppManager) {
		return xmppManager.isOnline();
	}

	private String previousServiceName = null;

	public String getManagerServiceName(XmppManager xmppManager) {

		if (isOnline(xmppManager)) {
			return previousServiceName = xmppManager.getXmppServiceName();
		} else {
			return previousServiceName == null ? "" : previousServiceName;
		}

	}

	private RosterManager rosterManager;

	public RosterManager getRosterManager() {
		return rosterManager;
	}

	/**
	 * 提交对话至服务器
	 **/
	public void sendMessage(ConversationMessage conversation) {
		log.debug("send message content is {}", conversation.toString());
		org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
		message.setFrom(conversation.getFromWho());
		message.setType(org.jivesoftware.smack.packet.Message.Type.chat);
		message.setTo(conversation.getToWho());
		message.setBody(conversation.getContent());
		message.setSubject(conversation.getType());
		message.setProperty("sendDate", TimeUnit.LongToStr
				(conversation.getLocalTime(), TimeUnit.LONG_FORMAT));
		message.setProperty("uqID", conversation.getLocalTime());
		getChatManager().sendPacket(message);
	}

	@Subscribe
	public void onMutipleAccountEvent(String multiAccountEvent) {
		if (MultiAccountEvent.MultiAccount.equals(multiAccountEvent)) {
			sendBroadcast(new Intent("push.model.change"));
			Intent intent = new Intent(this, MultiAccountActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	}
	
}
