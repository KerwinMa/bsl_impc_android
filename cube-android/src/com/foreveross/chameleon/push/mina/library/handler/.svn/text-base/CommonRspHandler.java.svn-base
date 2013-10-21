/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.handler;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Common_Rsp;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-7-11]
 */
public class CommonRspHandler extends AbstractCommandHandler<Common_Rsp> {

	private final Logger log = LoggerFactory.getLogger(CommonRspHandler.class);

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param session
	 * @param t
	 *            2013-7-11 上午11:09:53
	 */
	@Override
	protected void processCmd(IoSession session, Common_Rsp t) {
		log.info("CommonRspHandler:" + t.getSucess()
				+ " reason:" + t.getReason());
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param protoBytes
	 * @return 2013-7-11 上午11:09:53
	 */
	@Override
	protected Common_Rsp newInstance(byte[] protoBytes) {
		// TODO Auto-generated method stub
		try {
			return Common_Rsp.parseFrom(protoBytes);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-7-11 上午11:09:36
	 */

}
