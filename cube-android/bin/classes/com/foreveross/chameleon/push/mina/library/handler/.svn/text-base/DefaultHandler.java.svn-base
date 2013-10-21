/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.handler;

import org.apache.mina.core.session.IoSession;

import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Common_Rsp;
import com.foreveross.chameleon.push.mina.library.util.ProtoIoBuffer;
import com.google.protobuf.AbstractMessageLite;

/**
 * [默认处理器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */
public class DefaultHandler extends AbstractCommandHandler<AbstractMessageLite> {
	/* (non-Javadoc)
	 * @see org.apache.mina.example.tcp.cmdhandler.AbstractCommandHandler#processCmd(org.apache.mina.core.session.IoSession, java.lang.Object)
	 */
	@Override
	protected void processCmd(IoSession session, AbstractMessageLite t) {
		Common_Rsp common_Rsp = Common_Rsp.newBuilder().setSucess(false).setReason("no handler found for this command,check please!").build(); 
		session.write(ProtoIoBuffer.toIoBuffer((common_Rsp)));
		
		
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.example.tcp.cmdhandler.AbstractCommandHandler#newInstance(byte[])
	 */
	@Override
	protected AbstractMessageLite newInstance(byte[] protoBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-6-21 下午1:01:17
	 */

}
