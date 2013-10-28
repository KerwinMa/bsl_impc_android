package com.foreveross.chameleon.push.cubeparser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;

import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.foreveross.chameleon.push.cubeparser.type.CommonModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.MDMMessage;
import com.foreveross.chameleon.push.cubeparser.type.ModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.SystemMessage;
import com.foreveross.chameleon.push.mina.library.Constants;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.MapEntity;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.MessageContent;

public class PushContentParser {
	/**
	 * title:"", content:"", messageType:1, sendId:"", sendTime:"xx", extras:{
	 * moduleIdentifer:"", //模块的identifer，例如，公告模块为com.foss.announcement ...
	 * //根据不同模块自身需求，定义自已需要的键值，例如，公告模块定义announceId表示公告ID announceId:""
	 * //公告模块的公告ID }
	 * */

	public static List<Delayed> parseRemoteModel(MessageContent messageContent) {
		List<Delayed> l = new ArrayList<Delayed>();
		// 获取接口固定字段
		String title = messageContent.getTitle();
		String content = messageContent.getContent();
		String messageType = messageContent.getType();
		String messsageId = messageContent.getId();
		long sendTime = messageContent.getSendTime();
		// 根据消息类型，转码成对应对象
		ChanmeleonMessage messageDelay = null;
		if (Constants.MESSAGE_TYPE_SYSTEM.equals(messageType)) {
			messageDelay = new ChanmeleonMessage(new SystemMessage(sendTime,
					messsageId, title, content));
		} else if (Constants.MESSAGE_TYPE_MODULE.equals(messageType)) {
			// 模块类型消息
			String moduleIdentifer = getMapEntityValue(messageContent,
					"moduleIdentifer", String.class);
			String moduleName = getMapEntityValue(messageContent, "moduleName",
					String.class);

			boolean moduleBadgeBool = true;
			boolean busiDetailBool = true;

			try {
				String moduleBadge = getMapEntityValue(messageContent,
						"moduleBadge", String.class);

				moduleBadgeBool = moduleBadge == null ? false : Boolean
						.valueOf(moduleBadge);
				String busiDetail = getMapEntityValue(messageContent,
						"busiDetail", String.class);
				busiDetailBool = busiDetail == null ? false : Boolean
						.valueOf(busiDetail);

			} catch (Exception e) {
				e.printStackTrace();
			}

			CubeModule cubeModule = CubeModuleManager.getInstance()
					.getCubeModuleByIdentifier(moduleIdentifer);
			if (cubeModule != null) {
				cubeModule.setDisplayBadge(moduleBadgeBool);
			}
			if (moduleIdentifer != null
					&& moduleIdentifer.equals("com.foss.announcement")) {
				// 公告消息
				String noticeId = getMapEntityValue(messageContent,
						"announceId", String.class);
				NoticeModuleMessage noticeModuleMessage = new NoticeModuleMessage(
						sendTime, messsageId, title, content);
				noticeModuleMessage.setIdentifier(moduleIdentifer);
				noticeModuleMessage.setNoticeId(noticeId);
				messageDelay = new ChanmeleonMessage(noticeModuleMessage);
			} else {
				// 模块消息
				CommonModuleMessage commonModuleMessage = new CommonModuleMessage(
						sendTime, messsageId, title, content);
				commonModuleMessage.setIdentifier(moduleIdentifer);
				commonModuleMessage
						.setGroupBelong(moduleName == null ? moduleIdentifer
								: moduleName);
				messageDelay = new ChanmeleonMessage(commonModuleMessage);
			}
			ModuleMessage.class.cast(messageDelay.getPackedMessage())
					.setLinkable(busiDetailBool);

		}

		else if (Constants.MESSAGE_TYPE_MDM.equals(messageType)) {
			// 设备消息
			messageDelay = new ChanmeleonMessage(new MDMMessage(sendTime,
					messsageId, title, content, getMapEntityValue(
							messageContent, "mdm", String.class)));
		}

		if (messageDelay != null) {
			l.add(messageDelay);
		}
		return l;
	}

	public static <T> T getMapEntityValue(MessageContent messageContent,
			String key, Class<T> zz) {
		List<MapEntity> mapEntitys = messageContent.getExtrasList();
		for (MapEntity map : mapEntitys) {
			if (map.getKey().equals(key)) {
				return zz.cast(map.getValue());
			}
		}
		return null;
	}

}
