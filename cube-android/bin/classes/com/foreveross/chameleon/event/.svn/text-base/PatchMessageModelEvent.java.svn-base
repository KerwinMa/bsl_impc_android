/**
 * 
 */
package com.foreveross.chameleon.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.foreveross.chameleon.push.cubeparser.type.AbstractMessage;
import com.foreveross.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessageStub;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-3]
 */
public class PatchMessageModelEvent implements Delayed {
	private List<ChanmeleonMessage> patch = new ArrayList<ChanmeleonMessage>();

	public boolean lastIsNotice() {
		return (lastChanmeleonMessage().getPackedMessage() instanceof NoticeModuleMessageStub);
	}

	public ChanmeleonMessage lastChanmeleonMessage() {
		return patch.get(patch.size() - 1);
	}

	public boolean isEmpty() {
		return patch.isEmpty();
	}

	public void addChanmeleonMessage(ChanmeleonMessage chanmeleonMessage) {
		patch.add(chanmeleonMessage);
	}

	public List<ChanmeleonMessage> getPatch() {
		return patch;
	}

	public List<AbstractMessage<?>> getPacked() {
		List<AbstractMessage<?>> list = new ArrayList<AbstractMessage<?>>();
		for (ChanmeleonMessage chanmeleonMessage : patch) {
			list.add(chanmeleonMessage.getPackedMessage());
		}
		return list;
	}

	public void setPatch(List<ChanmeleonMessage> patch) {
		this.patch = patch;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param another
	 * @return 2013-9-3 下午4:14:00
	 */
	@Override
	public int compareTo(Delayed another) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param unit
	 * @return 2013-9-3 下午4:14:00
	 */
	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}
}
