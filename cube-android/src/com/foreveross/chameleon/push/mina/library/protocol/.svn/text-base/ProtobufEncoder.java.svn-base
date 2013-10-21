/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Packet;
import com.foreveross.chameleon.push.mina.library.util.ProtoIoBuffer;
import com.google.protobuf.GeneratedMessage;

/**
 * [协议编码器]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-6-26]
 */
public class ProtobufEncoder extends ProtocolEncoderAdapter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.ProtocolEncoder#encode(org.apache.mina.core
	 * .session.IoSession, java.lang.Object,
	 * org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		IoBuffer ioBuffer = null;
		if(IoBuffer.class.isAssignableFrom(message.getClass())){
			ioBuffer = IoBuffer.class.cast(message);
		}else{
			Packet packet = ProtoIoBuffer.compPacket((GeneratedMessage)message);
			byte[] buf = packet.toByteArray();
			int len=buf.length;
			ioBuffer = IoBuffer.allocate(len+4);
			ioBuffer.putInt(len);
			ioBuffer.put(buf);
			ioBuffer.flip();	
		}
		out.write(ioBuffer);
		out.flush();
	}

}
