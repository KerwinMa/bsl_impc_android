package com.foreveross.chameleon.push.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.foreveross.chameleon.push.client.NotificationService;
import com.foreveross.chameleon.push.mina.library.service.MinaPushService;
import com.foreveross.chameleon.util.SharedPreferencesUtil;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-17]
 */
public class BootstrapReceiver extends BroadcastReceiver {
	private final static Logger log = LoggerFactory
			.getLogger(BootstrapReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
//		Boolean enableXmppService = SharedPreferencesUtil.getInstance(context)
//				.getBoolean(SharedPreferencesUtil.ENABLE_XMPP_SERVICE, true);
//		if (enableXmppService) {
//			log.debug("开机启动xmpp服务...");
//			context.startService(NotificationService.getIntent());
//		}
		Boolean enableMinaService = SharedPreferencesUtil.getInstance(context)
				.getBoolean(SharedPreferencesUtil.ENABLE_MINA_SERVICE, true);
		if (enableMinaService) {
			log.debug("开机启动mina服务...");
			context.startService(new Intent(context, MinaPushService.class));
		}
	}
}
