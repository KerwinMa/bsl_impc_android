/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.handler;

import java.lang.reflect.Field;

import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.Log;

import com.foreveross.chameleon.push.mina.library.api.CallbackStore;
import com.foreveross.chameleon.push.mina.library.api.MinaMobileClient;
import com.foreveross.chameleon.push.mina.library.api.PacketCollector;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Auth_Rsp;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * [认证响应处理器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */
public class AuthRspHandler extends AbstractCommandHandler<Auth_Rsp> {
	private final static Logger log = LoggerFactory
			.getLogger(AuthRspHandler.class);

	public AuthRspHandler() {

	}

	@Override
	protected void processCmd(IoSession session, Auth_Rsp t) {
		if (t.getSucess()) {
			session.setAttribute("auth", true);
			changeSessionId(session, t.getSessionId());
			@SuppressWarnings("unchecked")
			PacketCollector<Auth_Rsp> pc = CallbackStore.instance()
					.getPacketCollector(t.getId());
			if (pc != null) {
				pc.onCollect(t);
			}
			doRegister(t);
			log.info("============================================================");
			log.info("认证成功Session:{}  所在IP为:{}", t.getSessionId(), t.getIp());
		} else {
			log.info("============================================================");
			log.info("认证失败，原因是:{} ", t.getReason());
		}
		log.info("============================================================");
	}

	@Override
	protected Auth_Rsp newInstance(byte[] protoBytes) {
		try {
			return Auth_Rsp.parseFrom(protoBytes);
		} catch (InvalidProtocolBufferException e) {
			Log.d("AuthRspHandler", "AuthRspHandler", e);
		}
		return null;
	}

	public void changeSessionId(IoSession session, long id) {
		if (session instanceof AbstractIoSession) {
			Field idField;
			try {
				idField = AbstractIoSession.class.getDeclaredField("sessionId");
				idField.setAccessible(true);
				idField.set(session, id);
			} catch (SecurityException e) {
				Log.e("AuthRspHandler", "changeSessionId", e);
			} catch (NoSuchFieldException e) {
				Log.e("AuthRspHandler", "changeSessionId", e);
			} catch (IllegalArgumentException e) {
				Log.e("AuthRspHandler", "changeSessionId", e);
			} catch (IllegalAccessException e) {
				Log.e("AuthRspHandler", "changeSessionId", e);
			}

		}

	}

	public void doAware(Auth_Rsp t) {

	}

	public void doRegister(Auth_Rsp t) {

	}

}
