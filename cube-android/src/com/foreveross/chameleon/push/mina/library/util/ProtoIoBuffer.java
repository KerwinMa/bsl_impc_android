/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.util;

import org.apache.mina.core.buffer.IoBuffer;

import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Packet;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * [bytes与IoBuffer转化]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */
public class ProtoIoBuffer {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param generatedMessage
	 *            2013-6-21 下午1:49:14
	 */
	public static IoBuffer toIoBuffer(GeneratedMessage generatedMessage) {
		Packet packet = compPacket(generatedMessage);
		byte[] buf = packet.toByteArray();
		IoBuffer ioBuffer = IoBuffer.allocate(buf.length);
		ioBuffer.put(buf);
		ioBuffer.flip();
		return ioBuffer;

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-6-21 下午1:49:14
	 */
	public static Packet toPacket(IoBuffer ioBuffer) {
		byte[] buf = new byte[ioBuffer.limit()];
		ioBuffer.get(buf);
		try {
			return Packet.parseFrom(buf);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [非严格判断]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return 2013-6-21 下午4:27:03
	 */
	//TODO[FENGWEILI]
	public static boolean isVaildProto(IoBuffer ioBuffer) {
		
		byte[] buf= new byte[ioBuffer.limit()];
		ioBuffer.get(buf);
		ioBuffer.rewind();
		try {
			Packet.parseFrom(buf);
		} catch (InvalidProtocolBufferException e) {
			return false;
		}
		return true;
	}
	/**
	 * [非严格判断]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return 2013-6-21 下午4:27:03
	 */
	public static Packet compPacket(GeneratedMessage abstractMessageLite) {
		ByteString bs = abstractMessageLite.toByteString();
		Descriptor descriptor = abstractMessageLite.getDescriptorForType();
		String typeName = descriptor.getName();
		Packet packet = Packet.newBuilder()
				.setTypeName(typeName).setPbfBytes(bs).build();
		return packet;
	}
	
}
