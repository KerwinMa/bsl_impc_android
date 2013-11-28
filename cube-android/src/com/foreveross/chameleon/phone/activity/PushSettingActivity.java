package com.foreveross.chameleon.phone.activity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.ConnectStatusChangeEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.push.client.NotificationService;
import com.foreveross.chameleon.push.mina.library.service.MinaPushService;
import com.foreveross.chameleon.push.mina.library.util.NetworkUtil;
import com.foreveross.chameleon.util.SharedPreferencesUtil;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class PushSettingActivity extends BaseActivity {

	private final static Logger log = LoggerFactory
			.getLogger(PushSettingActivity.class);
	// title concern
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;

	private TextView cbStatusChat;
	private CheckBox chatCheckBox;
	
	
	private TextView cbStatusMina;
	private CheckBox minaCheckBox;
	
	private TextView cbStatusOpenfire;
	private CheckBox openfireCheckBox;
	
	private NotificationService notificationService = null;
	private MinaPushService minaPushService = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_pushsetting);
		initValue();
		EventBus.getEventBus(TmpConstants.COMMNAD_INTENT, ThreadEnforcer.MAIN)
				.register(this);
	}

	@Subscribe
	public void onConnectStatusChangeEvent(
			ConnectStatusChangeEvent connectStatusChangeEvent) {
		log.debug("receive connection status changed Event...");
		if (connectStatusChangeEvent.getChannel().equals(
				ConnectStatusChangeEvent.CONN_CHANNEL_CHAT)) {
			log.debug("xmpp connection status changed...");
			if (connectStatusChangeEvent.getStatus().equals(
					ConnectStatusChangeEvent.CONN_STATUS_ONLINE)) {
				log.debug("xmpp connection online...");
				cbStatusChat.setText("已连接");
				chatCheckBox.setChecked(true);
			} else {
				log.debug("xmpp connection offline...");
				cbStatusChat.setText("未连接");
				chatCheckBox.setChecked(false);
			}
		} else if(connectStatusChangeEvent.getChannel().equals(
				ConnectStatusChangeEvent.CONN_CHANNEL_MINA)){
			log.debug("mina connection status changed...");
			if (connectStatusChangeEvent.getStatus().equals(
					ConnectStatusChangeEvent.CONN_STATUS_ONLINE)) {
				log.debug("mina connection online...");
				cbStatusMina.setText("已连接");
				minaCheckBox.setChecked(true);
			} else {
				log.debug("mina connection offline...");
				cbStatusMina.setText("未连接");
				minaCheckBox.setChecked(false);
			}
		} else if(connectStatusChangeEvent.getChannel().equals(
				ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE)){
			log.debug("openfire connection status changed...");
			if (connectStatusChangeEvent.getStatus().equals(
					ConnectStatusChangeEvent.CONN_STATUS_ONLINE)) {
				log.debug("mina connection online...");
				cbStatusOpenfire.setText("已连接");
				openfireCheckBox.setChecked(true);
			} else {
				log.debug("mina connection offline...");
				cbStatusOpenfire.setText("未连接");
				openfireCheckBox.setChecked(false);
			}
		}
	}

	private void initValue() {
		notificationService = application.getNotificationService();
		minaPushService = application.getMinaPushService();
		
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.register(this);

		titlebar_left = (Button) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("即时通讯设置");

		
		/** --------------------------------- 即时通信界面控制 ---------------------------------------*/
		cbStatusChat = (TextView) findViewById(R.id.pushsetting_cbstatus_xmpp);
		chatCheckBox = (CheckBox) findViewById(R.id.pushsetting_cb_xmpp);
		if(notificationService==null){
			chatCheckBox.setClickable(false);
		}else{
			chatCheckBox.setClickable(true);
		}
		chatCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (notificationService == null){
					return;
				}
				if(!NetworkUtil.isNetworkConnected(PushSettingActivity.this)) {
					Toast.makeText(PushSettingActivity.this, "网络异常，请检查设置！",
							Toast.LENGTH_SHORT).show();
					cbStatusMina.setText("未连接");
					minaCheckBox.setChecked(false);
					cbStatusChat.setText("未连接");
					chatCheckBox.setChecked(false);
					return;
				}

				if (chatCheckBox.isChecked()) {
					cbStatusChat.setText("正在打开...");
					notificationService.reconnect(application.getChatManager());
				} else {
					cbStatusChat.setText("正在关闭...");
					notificationService.disconnect(application.getChatManager());
				}
			}
		});
		
		if(notificationService!=null){
			if (application.getNotificationService().isOnline(application.getChatManager())) {
				cbStatusChat.setText("已连接");
				chatCheckBox.setChecked(true);
			} else {
				cbStatusChat.setText("未连接");
				chatCheckBox.setChecked(false);
			}
		}
		
		/** --------------------------------- Mina界面控制 ---------------------------------------*/
		cbStatusMina = (TextView) findViewById(R.id.pushsetting_cbstatus_mina);
		minaCheckBox = (CheckBox) findViewById(R.id.pushsetting_cb_mina);
		if(minaPushService==null){
			minaCheckBox.setClickable(false);
		}else{
			minaCheckBox.setClickable(true);
		}
		minaCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (minaPushService == null){
					return;
				}
				if(!NetworkUtil.isNetworkConnected(PushSettingActivity.this)) {
					Toast.makeText(PushSettingActivity.this, "网络异常，请检查设置！",
							Toast.LENGTH_SHORT).show();
					cbStatusMina.setText("未连接");
					minaCheckBox.setChecked(false);
					cbStatusChat.setText("未连接");
					chatCheckBox.setChecked(false);
					return;
				}
				if (minaCheckBox.isChecked()) {
					cbStatusMina.setText("正在打开...");
					minaPushService.reconnect();
					SharedPreferencesUtil.getInstance(PushSettingActivity.this)
							.saveBoolean(TmpConstants.SELECT_OPEN, true);
				} else {

					cbStatusMina.setText("正在关闭...");
					minaPushService.disConnected();
					SharedPreferencesUtil.getInstance(PushSettingActivity.this)
							.saveBoolean(TmpConstants.SELECT_OPEN, false);
				}
			}
		});

		if(minaPushService!=null){
			if (application.getMinaPushService().isOnline()) {
				cbStatusMina.setText("已连接");
				minaCheckBox.setChecked(true);
			} else {
				cbStatusMina.setText("未连接");
				minaCheckBox.setChecked(false);
			}
		}
		
		
		
		/** --------------------------------- openfire界面控制 ---------------------------------------*/
		
		cbStatusOpenfire = (TextView) findViewById(R.id.pushsetting_cbstatus_openfire);
		openfireCheckBox = (CheckBox) findViewById(R.id.pushsetting_cb_openfire);
		if(notificationService==null){
			openfireCheckBox.setClickable(false);
		}else{
			openfireCheckBox.setClickable(true);
		}
		openfireCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (notificationService == null){
					return;
				}
				if(!NetworkUtil.isNetworkConnected(PushSettingActivity.this)) {
					Toast.makeText(PushSettingActivity.this, "网络异常，请检查设置！",
							Toast.LENGTH_SHORT).show();
					cbStatusOpenfire.setText("未连接");
					openfireCheckBox.setChecked(false);
					cbStatusChat.setText("未连接");
					openfireCheckBox.setChecked(false);
					return;
				}
				if (openfireCheckBox.isChecked()) {
					cbStatusOpenfire.setText("正在打开...");
					notificationService.reconnect(application.getPushManager());
					SharedPreferencesUtil.getInstance(PushSettingActivity.this)
							.saveBoolean(TmpConstants.SELECT_OPEN, true);
				} else {

					cbStatusOpenfire.setText("正在关闭...");
					notificationService.disconnect(application.getPushManager());
					SharedPreferencesUtil.getInstance(PushSettingActivity.this)
							.saveBoolean(TmpConstants.SELECT_OPEN, false);
				}
			}
		});

		if(notificationService!=null){
			if (application.getNotificationService().isOnline(application.getPushManager())) {
				cbStatusOpenfire.setText("已连接");
				openfireCheckBox.setChecked(true);
			} else {
				cbStatusOpenfire.setText("未连接");
				openfireCheckBox.setChecked(false);
			}
		}
		
		
	}

	
	
	
	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.title_barleft:
				finish();
				break;

			}
		}
	};

	protected void onResume() {
		super.onResume();
		application.setShouldSendChatNotification(true);
		application.setShouldSendNoticeNotification(true);
		application.setShouldSendMessageNotification(true);
	};

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-10-19 上午11:36:05
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.unregister(this);
	};

	@Subscribe
	public void onShowToastEvent(String showToastEvent) {
		if (ConnectStatusChangeEvent.SHOW_TOAST.equals(showToastEvent)) {
			Toast.makeText(this, "网络异常，请检查设置！", Toast.LENGTH_SHORT).show();
			cbStatusMina.setText("未连接");
			minaCheckBox.setChecked(false);
			cbStatusChat.setText("未连接");
			chatCheckBox.setChecked(false);
			cbStatusOpenfire.setText("未连接");
			openfireCheckBox.setChecked(false);
		}
	}
}
