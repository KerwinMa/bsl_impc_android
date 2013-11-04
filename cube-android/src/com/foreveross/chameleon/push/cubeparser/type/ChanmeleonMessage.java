/**
 * 
 */
package com.foreveross.chameleon.push.cubeparser.type;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * [所有消息都是ChanmeleonMessage]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-22]
 */
@DatabaseTable(tableName = "ChanmeleonMessage")
public class ChanmeleonMessage implements Delayed {

	public ChanmeleonMessage() {

	}

	/**
	 * [包裹对象]
	 */
	@DatabaseField(canBeNull = false, foreign = true)
	private AbstractMessage<?> packedMessage = null;

	public void setPackedMessage(AbstractMessage<?> packedMessage) {
		this.packedMessage = packedMessage;
	}

	private long startTime = 0l;

	public AbstractMessage<?> getPackedMessage() {
		return packedMessage;
	}

	public ChanmeleonMessage(AbstractMessage<?> packedMessage) {
		this.packedMessage = packedMessage;
	}
	public ChanmeleonMessage(AbstractMessage<?> packedMessage,String userName) {
		this.packedMessage = packedMessage;
		this.packedMessage.setUserName(userName);
	}
	@Override
	public int compareTo(Delayed another) {
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(startTime - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

	public void delay() {
		startTime = System.currentTimeMillis() + 200;
	}

	public boolean savable() {
		return packedMessage != null && !(packedMessage instanceof MDMMessage);
	}
}
