/**
 * 
 */
package com.foreveross.chameleon.pad.fragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.ConnectStatusChangeEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.activity.PushSettingActivity;
import com.foreveross.chameleon.push.client.NotificationService;
import com.foreveross.chameleon.push.mina.library.service.MinaPushService;
import com.foreveross.chameleon.push.mina.library.util.NetworkUtil;
import com.foreveross.chameleon.util.SharedPreferencesUtil;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author apple
 *
 */
public class PushSettingFragment extends Fragment {
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
	
	private Application application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getEventBus(TmpConstants.COMMNAD_INTENT, ThreadEnforcer.MAIN)
		.register(this);
		application = Application.class.cast(this.getAssocActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.app_pushsetting, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initValue(view);
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

	private void initValue(View view) {
		notificationService = application.getNotificationService();
		minaPushService = application.getMinaPushService();
		
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.register(this);

		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("即时通讯设置");

		
		/** --------------------------------- 即时通信界面控制 ---------------------------------------*/
		cbStatusChat = (TextView) view.findViewById(R.id.pushsetting_cbstatus_xmpp);
		chatCheckBox = (CheckBox) view.findViewById(R.id.pushsetting_cb_xmpp);
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
				if(!NetworkUtil.isNetworkConnected(application)) {
					Toast.makeText(application, "网络异常，请检查设置！",
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
		cbStatusMina = (TextView) view.findViewById(R.id.pushsetting_cbstatus_mina);
		minaCheckBox = (CheckBox) view.findViewById(R.id.pushsetting_cb_mina);
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
				if(!NetworkUtil.isNetworkConnected(application)) {
					Toast.makeText(application, "网络异常，请检查设置！",
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
					SharedPreferencesUtil.getInstance(application)
							.saveBoolean(TmpConstants.SELECT_OPEN, true);
				} else {

					cbStatusMina.setText("正在关闭...");
					minaPushService.disConnected();
					SharedPreferencesUtil.getInstance(application)
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
		
		cbStatusOpenfire = (TextView) view.findViewById(R.id.pushsetting_cbstatus_openfire);
		openfireCheckBox = (CheckBox) view.findViewById(R.id.pushsetting_cb_openfire);
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
				if(!NetworkUtil.isNetworkConnected(application)) {
					Toast.makeText(application, "网络异常，请检查设置！",
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
					SharedPreferencesUtil.getInstance(application)
							.saveBoolean(TmpConstants.SELECT_OPEN, true);
				} else {

					cbStatusOpenfire.setText("正在关闭...");
					notificationService.disconnect(application.getPushManager());
					SharedPreferencesUtil.getInstance(application)
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
				if (getAssocActivity() instanceof FacadeActivity) {
					((FacadeActivity) getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
				}
				break;

			}
		}
	};
	@Override
	public void onResume() {
		super.onResume();
		application.setShouldSendChatNotification(true);
		application.setShouldSendNoticeNotification(true);
		application.setShouldSendMessageNotification(true);
	};

	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
		.unregister(this);
	}

	@Subscribe
	public void onShowToastEvent(String showToastEvent) {
		if (ConnectStatusChangeEvent.SHOW_TOAST.equals(showToastEvent)) {
			Toast.makeText(application, "网络异常，请检查设置！", Toast.LENGTH_SHORT).show();
			cbStatusMina.setText("未连接");
			minaCheckBox.setChecked(false);
			cbStatusChat.setText("未连接");
			chatCheckBox.setChecked(false);
			cbStatusOpenfire.setText("未连接");
			openfireCheckBox.setChecked(false);
		}
	}
	

}
