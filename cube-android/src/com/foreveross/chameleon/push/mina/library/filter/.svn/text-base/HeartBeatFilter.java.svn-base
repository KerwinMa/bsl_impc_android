package com.foreveross.chameleon.push.mina.library.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

import android.util.Log;

/**
 * [心跳过滤器]<BR>
 * 1.mina在Filter层次实现心跳功能<BR>
 * 2.心跳在客户端与主机都是须要的<BR>
 * 3.实践证明心跳间隔时间最好设置少于60秒<BR>
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-20]
 */
/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-20]
 */
public class HeartBeatFilter extends KeepAliveFilter {

	/**
	 * 发送心跳时间间隔
	 */
	private static final int INTERVAL = 60;
	/**
	 * 发送心跳超时时间
	 */
	private static final int TIMEOUT = 30;

	/**
	 * @param messageFactory
	 *            产生心跳消息的工厂
	 */
	public HeartBeatFilter(KeepAliveMessageFactory messageFactory) {
		super(messageFactory, IdleStatus.BOTH_IDLE, new ExceptionHandler(),
				INTERVAL, TIMEOUT);

	}

	public HeartBeatFilter(String clientName) {
		super(new KeepAliveMessageFactoryImpl(clientName),
				IdleStatus.BOTH_IDLE, new ExceptionHandler(), INTERVAL, TIMEOUT);
		/*** 此消息不会继续传递，不会被业务层看见 ***/
		this.setForwardEvent(false);

	}
}

/**
 * [心跳异常处理机制]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-20]
 */
class ExceptionHandler implements KeepAliveRequestTimeoutHandler {

	/**
	 * 当发送心跳超时时关闭连接
	 */
	public void keepAliveRequestTimedOut(KeepAliveFilter filter,
			IoSession session) throws Exception {
		session.close(true);
		Log.d("HeartBeatFilter",session.getId() + ":keepAliveRequestTimedOut");
	}
}

/**
 * [判断和定制心跳消息]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-20]
 */
class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
	/**
	 * clientName 客户端名称
	 */
	private String clientName;

	public KeepAliveMessageFactoryImpl(String clientName) {
		this.clientName = clientName;
	}

	/**
	 * 代表心跳请求
	 */
	private static final byte int_req = -1;
	/**
	 * 代表心跳响应
	 */
	private static final byte int_rep = -2;
	/**
	 * 心跳字节码包裹
	 */
	private static final IoBuffer KAMSG_REQ = IoBuffer.allocate(5).putInt(1)
			.put(int_req).rewind();
	/**
	 * 心跳字节码包裹
	 */
	private static final IoBuffer KAMSG_REP = IoBuffer.allocate(5).putInt(1)
			.put(int_rep).rewind();

	/**
	 * [定制请请求心跳消息]<BR>
	 * 1.发送心跳包时调用
	 * 
	 * @author 冯伟立
	 * @version [DVRTest, 2013-6-20]
	 */
	public Object getRequest(IoSession session) {
		Log.d("HeartBeatFilter",clientName + " geting heartbeat request for session "
				+ session.getId());
		return KAMSG_REQ.duplicate();
	}

	/**
	 * [定制响应心跳消息]<BR>
	 * 1.响应心跳包时调用
	 * 
	 * @author 冯伟立
	 * @version [DVRTest, 2013-6-20]
	 */
	public Object getResponse(IoSession session, Object request) {
		Log.d("HeartBeatFilter",clientName + " geting heartbeat response for session "
				+ session.getId());
		return KAMSG_REP.duplicate();
	}

	/**
	 * [判断是否属于请求心跳信息]<BR>
	 * [功能详细描述]
	 * 
	 * @author 冯伟立
	 * @version [DVRTest, 2013-6-20]
	 */
	public boolean isRequest(IoSession session, Object message) {
		if (!(message instanceof IoBuffer))
			return false;
		IoBuffer realMessage = (IoBuffer) message;
		byte b = realMessage.get();
		realMessage.rewind();
		return b == int_req;
	}

	/**
	 * [判断是否属于响应心跳信息]<BR>
	 * [功能详细描述]
	 * 
	 * @author 冯伟立
	 * @version [DVRTest, 2013-6-20]
	 */
	public boolean isResponse(IoSession session, Object message) {
		if (!(message instanceof IoBuffer))
			return false;
		IoBuffer realMessage = (IoBuffer) message;
		byte b = realMessage.get();
		realMessage.rewind();
		return b == int_rep;
	}
}