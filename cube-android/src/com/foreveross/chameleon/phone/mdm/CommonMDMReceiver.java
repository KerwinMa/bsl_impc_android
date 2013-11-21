package com.foreveross.chameleon.phone.mdm;

import com.foreveross.chameleon.push.tmp.CommonMessageReceiver;
import com.foreveross.chameleon.push.tmp.Message;

public abstract class CommonMDMReceiver extends CommonMessageReceiver {

	public static final String MDM_COMMAND = "MDM";

	public boolean isValidType(Message message) {
		String messageType = message.getCommand().toUpperCase();
		if (messageType.startsWith(MDM_COMMAND)) {
			return validateSubMessageType(messageType);
		} else {
			return false;
		}
	}

	protected abstract boolean validateSubMessageType(String messageType);

}
