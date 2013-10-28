package com.foreveross.chameleon.push.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.ModelChangeEvent;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述] //TODO[未实现]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-9]
 */
public class MyRosterListener implements RosterListener {

	private final static Logger log = LoggerFactory
			.getLogger(MyRosterListener.class);

	private NotificationService notificationService;

	public MyRosterListener(NotificationService notificationService) {
		this.notificationService = notificationService;
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if (!statusMap.isEmpty()) {
					IMModelManager.instance().putStatusMap(statusMap);
					statusMap.clear();
				}
			}
		}, 0, 2000);
	}

	private Map<String, String> statusMap = new HashMap<String, String>();

	@Override
	public void entriesAdded(Collection<String> users) {
		log.debug("entries {} are added", Arrays.toString(users.toArray()));
		for (String jid : users) {
			jid = StringUtils.parseBareAddress(jid);
			if (!IMModelManager.instance().containUserModel(jid)) {
				RosterEntry rosterEntry = notificationService
						.getRosterManager().getRosterEntry(jid);
				UserModel userModel = new UserModel();
				userModel.sync(new RosterEntryWrapper(rosterEntry));
				IMModelManager.instance().addUserModel(userModel);
				StaticReference.userMf.createOrUpdate(userModel);
			}
		}
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
				new ModelChangeEvent());
	}

	@Override
	public void entriesDeleted(Collection<String> users) {
		log.debug("entries {} are deleted", Arrays.toString(users.toArray()));
		for (String jid : users) {
			jid = StringUtils.parseBareAddress(jid);
			if (IMModelManager.instance().containUserModel(jid)) {
				UserModel userModel = IMModelManager.instance().getUserModel(
						jid);
				if (userModel != null){
					userModel.killMe();
					userModel.delete();

					StaticReference.userMf.delete(userModel);
				}
			}
		}
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
				new ModelChangeEvent());
	}

	@Override
	public void entriesUpdated(Collection<String> users) {
		log.debug("entries {} are updated", Arrays.toString(users.toArray()));
		for (String jid : users) {
			UserModel userModel = null;
			jid = StringUtils.parseBareAddress(jid);
			if ((userModel = IMModelManager.instance().getUserModel(jid)) != null) {
				RosterEntry rosterEntry = notificationService
						.getRosterManager().getRosterEntry(jid);
				userModel.sync(new RosterEntryWrapper(rosterEntry));
				StaticReference.userMf.createOrUpdate(userModel);
			}
		}
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
				new ModelChangeEvent());
	}

	@Override
	public void presenceChanged(Presence presence) {
		log.debug("presence form {} changed to state:{}", presence.getFrom(),
				presence.getStatus());
		statusMap.put(StringUtils.parseBareAddress(presence.getFrom()),
				presence.getType().name());
	}

}
