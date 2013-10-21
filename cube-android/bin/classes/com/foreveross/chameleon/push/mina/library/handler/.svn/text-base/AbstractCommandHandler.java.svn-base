/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.handler;

import org.apache.mina.core.session.IoSession;

/**
 * [抽象命令处理器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */
public abstract class AbstractCommandHandler<T> {

	/**
	 * [供外部使用，处理相应的protobuf字节码]<BR>
	 * [功能详细描述]
	 * 
	 * @param protoBytes
	 *            protobuf格式的字节码 2013-6-21 下午12:52:43
	 */
	public void process(IoSession session, byte[] protoBytes) {
		processCmd(session, newInstance(protoBytes));
	}

	/**
	 * [根据protobuf对象作处理]<BR>
	 * [功能详细描述]
	 * 
	 * @param session
	 * @param t
	 *            为转化后的protobuf对象 2013-6-25 上午9:51:33
	 */
	protected abstract void processCmd(IoSession session, T t);

	/**
	 * [把protobuf格式的字节码转化成protobuf对象]<BR>
	 * [功能详细描述]
	 * 
	 * @param protoBytes
	 *            protobuf格式的字节码
	 * @return 2013-6-25 上午9:51:30
	 */
	protected abstract T newInstance(byte[] protoBytes);

}
