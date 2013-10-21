package com.foreveross.chameleon.push.mina.library.protocol;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Packet;

/**
 * [业务解码]<BR>
 * 1.对于业务来说，解码后的对象好为Packet内业务对象而不是Packet对象<BR>
 * 但由于过程中会用到反射，根据不完全测试性能将下降大约二十倍，所以<BR>
 * 丢弃对业务对象的自动转型，直接返回Packet对象<BR>
 * 
 * TODO[FENGWEILI] 以后有代替反射的快速算法再为代替
 * 
 * @author 冯伟立
 * @version [Testj, 2013-6-26]
 */
public class ProtobufDecoder extends CumulativeProtocolDecoder {

	/**
	 * [解包]<BR>
	 * TODO[FENGWEILI] 测试断连包接收不完整的情况下，可能引发的问题
	 * 
	 * @see org.apache.mina.filter.codec.CumulativeProtocolDecoder#doDecode(org.apache.mina.core.session.IoSession,
	 *      org.apache.mina.core.buffer.IoBuffer,
	 *      org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		if (in.prefixedDataAvailable(4)) {
			int len = in.getInt();
			byte[] buf = new byte[len];
			in.get(buf);
			if (len == 1) {
				out.write(IoBuffer.wrap(buf));
			} else {
				Packet packet = Packet.parseFrom(buf);
				out.write(packet);
			}
			return true;
		}
		return false;
	}

}