package com.foreveross.chameleon.push.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-7-15]
 */
public class ReconnectionThread extends Thread {
	private final static Logger log = LoggerFactory
			.getLogger(ReconnectionThread.class);

	/**
	 * xmpp管理器
	 */
	private final XmppManager xmppManager;

	/**
	 * 重连时间（秒）
	 */
	private int waiting;

	/**
	 * 重连接请求队列
	 */
	private BlockingQueue<Byte> flags = new ArrayBlockingQueue<Byte>(10, true);
	
	private boolean isStop = false; 
	
	
	public void addRequest(Byte flag) {
		flags.add(flag);
	}
	
	ReconnectionThread(XmppManager xmppManager) {
		this.xmppManager = xmppManager;
		this.waiting = 0;
		this.setName("reconnection thread");
	}

	public void run() {
		try {
			// 循环获取请求
			for (;;) {
				
				if(isStop){
					//关掉这个线程
					break;
				}
				
				
				flags.take();
				log.debug("take a xmpp reconnect req!");
				// 如果已经连接状态，则忽略
				if (xmppManager.isConnected()) {
					log.debug("the xmpp is connected ,ignore and continue!");
					continue;
				}
				waiting = 0;
				while (!isInterrupted()) {
					if (xmppManager.isConnected()) {
						log.debug("the xmpp is connected ,ignore and break!");
						break;
					}
					xmppManager.submitReconnectReq();
					log.debug("Trying to reconnect in {} seconds", waiting());
					Thread.sleep((long) waiting() * 1000L);
					waiting++;
				}
			}

		} catch (final InterruptedException e) {
			xmppManager.getConnectionListener().reconnectionFailed(e);
		}
	}
	
	public void stopReconnect(){
		isStop = true;
	}
	
	public boolean isThreadAlive(){
		return !isStop;
	}
	
	/**
	 * 
	 * [等待时间设置]<BR>
	 * 1.前七次等待十秒<BR>
	 * 2.八次到十二次等待一分钟<BR>
	 * 3.十三到二十次等待五分钟<BR>
	 * 4.二十次以上等待十二钟<BR>
	 * 
	 * @return 2013-7-15 下午4:51:21
	 */
	private int waiting() {
		if (waiting > 20) {
			return 600;
		}
		if (waiting > 13) {
			return 300;
		}
		return waiting <= 7 ? 10 : 60;
	}
	
	
}
