package com.foreveross.chameleon.phone.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.push.cubeparser.type.AbstractMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.util.Pool;

/**
 * [信息组模型]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-10-11]
 */
public class MsgModel {
	private final static Logger log = LoggerFactory.getLogger(MsgModel.class);
	/**
	 * [是否展开]
	 */
	private boolean expend;
	private String identifier;
	/**
	 * [组名]
	 */
	private String groupName;
	/**
	 * [本组最后一条消息时间]
	 */
	private long lastMessageTime;

	/**
	 * [消息总条数]
	 */
	private int msgCount;

	/**
	 * [未读消息条数]
	 */
	private int unreadMsgCount;

	/**
	 * [是否处于编辑状态]
	 */
	private boolean isEditable;
	/**
	 * [包含的消息]
	 */
	private Map<String, AbstractMessage<?>> msgMap = new TreeMap<String, AbstractMessage<?>>();

	/**
	 * [内容为msgMap.values(),但为了方便使用再用List窗器]
	 */
	private List<AbstractMessage<?>> dataList = new ArrayList<AbstractMessage<?>>();

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * [删除多条消息]<BR>
	 * 1.从消息列表移除<BR>
	 * 2.总数减一<BR>
	 * 3.如果此消息未读，未读减一<BR>
	 * 4.得到最后一条发送的消息时间<BR>
	 * 
	 * @param message
	 *            2013-10-11 上午11:05:29
	 */
	public synchronized void removeMessages(Set<String> messageIds) {
		int rmdMsgCount = 0;
		int rmdUnreadMsgCount = 0;
		final List<AbstractMessage<?>> rmdList = new ArrayList<AbstractMessage<?>>();
		for (String messageId : messageIds) {
			AbstractMessage<?> am = msgMap.get(messageId);
			if (am != null) {
				if (!am.isHasRead()) {
					++rmdUnreadMsgCount;
				}
				++rmdMsgCount;
				rmdList.add(am);
			} else {
				log.warn("message {} for remove not found in {}", messageId,
						groupName);
			}
		}
		decreaseMsgCountBy(rmdMsgCount);
		decreaseUnreadMsgCountBy(rmdUnreadMsgCount);
		Pool.getPool().execute(new Runnable() {
			@Override
			public void run() {
				for (AbstractMessage<?> rmdAm : rmdList) {
					rmdAm.delete();
				}
			}
		});
	}

	/**
	 * [得到最后一条消息的发送时间]<BR>
	 * [功能详细描述] 2013-10-11 上午11:16:56
	 */
	private void findLastMessageTime() {
		Collection<AbstractMessage<?>> msgList = msgMap.values();
		for (AbstractMessage<?> am : msgList) {
			if (am.getSendTime() > lastMessageTime) {
				lastMessageTime = am.getSendTime();
			}
		}
	}

	/**
	 * [获取某一类型对象列表]<BR>
	 * [功能详细描述]
	 * 
	 * @param clazz
	 * @return 2013-10-11 下午2:14:10
	 */
	public <T> List<T> getMessageByType(Class<T> clazz) {
		Collection<AbstractMessage<?>> msgList = msgMap.values();
		List<T> list = new ArrayList<T>();
		for (AbstractMessage<?> am : msgList) {
			if (clazz.isAssignableFrom(am.getClass())) {
				list.add(clazz.cast(am));
			}
		}
		return list;
	}

	/**
	 * [增加一条信息]<BR>
	 * [功能详细描述]
	 * 
	 * @param message
	 *            2013-10-11 上午11:18:00
	 */
	public synchronized void addMessage(AbstractMessage<?> message) {
		if (message.getGroupBelong().equals(groupName)) {
			// 未读数据
			if (!message.isHasRead()) {
				log.debug("加入的消息{},已经状态为{}", message.getMesssageId(),
						message.isHasRead());
				increaseUnreadMsgCount();
			}
			msgMap.put(message.getMesssageId(), message);
			dataList.add(0, message);
			increaseMsgCount();
		} else {
			log.warn("本组名为{},加入消息组为{},加入失败!", groupName,
					message.getGroupBelong());
		}
	}

	/**
	 * [增加多条消息]<BR>
	 * [功能详细描述]
	 * 
	 * @param messages
	 *            2013-10-11 上午11:25:52
	 */
	public synchronized void addMessages(List<AbstractMessage<?>> messages) {
		int comingUnreadMsgCount = 0;
		int comingMsgCount = 0;
		for (AbstractMessage<?> message : messages) {
			if (message.getGroupBelong().equals(groupName)) {
				// 未读数据
				if (!message.isHasRead()) {
					log.debug("加入的消息{},已经状态为{}", message.getMesssageId(),
							message.isHasRead());
					++comingUnreadMsgCount;
				}
				msgMap.put(message.getMesssageId(), message);
				++comingMsgCount;
			} else {
				log.warn("本组名为{},加入消息组为{},加入失败!", groupName,
						message.getGroupBelong());
			}
		}
		dataList.clear();
		dataList.addAll(messages);
		increaseUnreadMsgCountBy(comingUnreadMsgCount);
		increaseMsgCountBy(comingMsgCount);
	}

	/**
	 * [是否有消息选 中]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-10-11 上午11:16:13
	 */
	public boolean hasMessageSelected() {
		Collection<AbstractMessage<?>> msgList = msgMap.values();
		for (AbstractMessage<?> message : msgList) {
			if (message.isSelected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * [删除选中的消息]<BR>
	 * [功能详细描述] 2013-10-11 上午11:42:30
	 */
	public synchronized void removeSelectedMessages() {
		final List<AbstractMessage<?>> sms = getSelectedMessages();
		List<AbstractMessage<?>> umsms = getUnreadMessageInSelectedMessages();
		decreaseMsgCountBy(sms.size());
		decreaseUnreadMsgCountBy(umsms.size());
		for (AbstractMessage<?> am : sms) {
			msgMap.remove(am.getMesssageId());
		}
		dataList.retainAll(msgMap.values());
		Pool.getPool().execute(new Runnable() {

			@Override
			public void run() {
				for (AbstractMessage<?> am : sms) {
					am.delete();
				}
			}
		});

	}

	/**
	 * [读取选中的消息]<BR>
	 * [功能详细描述] 2013-10-11 上午11:42:30
	 */
	public synchronized void markSelectedMessages() {
		final List<AbstractMessage<?>> umsms = getUnreadMessageInSelectedMessages();
		for (AbstractMessage<?> am : umsms) {
			am.setHasRead(true);
		}
		decreaseUnreadMsgCountBy(umsms.size());
		Pool.getPool().execute(new Runnable() {

			@Override
			public void run() {
				for (AbstractMessage<?> am : umsms) {
					am.update();
				}
			}
		});

	}

	/**
	 * [得到所有选取的消息]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-10-11 上午11:10:30
	 */
	private List<AbstractMessage<?>> getSelectedMessages() {
		List<AbstractMessage<?>> selectedMessages = new ArrayList<AbstractMessage<?>>();
		Collection<AbstractMessage<?>> msgList = msgMap.values();
		for (AbstractMessage<?> message : msgList) {
			if (message.isSelected()) {
				selectedMessages.add(message);
			}
		}
		return selectedMessages;
	}

	/**
	 * [设置所有元素选中状态]<BR>
	 * [功能详细描述]
	 * 
	 * @param selected
	 *            2013-10-12 上午11:55:48
	 */
	public void setSelectAll(boolean selected) {
		for (AbstractMessage<?> am : dataList) {
			am.setSelected(selected);
		}
	}

	/**
	 * [得到所有选取消息中的未读消息]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-10-11 上午11:10:30
	 */
	private List<AbstractMessage<?>> getUnreadMessageInSelectedMessages() {
		List<AbstractMessage<?>> unreadMessageInSelectedMessages = new ArrayList<AbstractMessage<?>>();
		List<AbstractMessage<?>> selectedMessages = getSelectedMessages();
		for (AbstractMessage<?> selectedMessage : selectedMessages) {
			if (!selectedMessage.isHasRead()) {
				unreadMessageInSelectedMessages.add(selectedMessage);
			}
		}
		return unreadMessageInSelectedMessages;
	}

	public void removeAll() {
		this.unreadMsgCount = 0;
		this.msgCount = 0;
		Pool.getPool().execute(new Runnable() {

			@Override
			public void run() {
				for (AbstractMessage<?> selectedMessage : dataList) {
					selectedMessage.delete();
				}
			}
		});

	}

	/**
	 * [读取一条消息]<BR>
	 * [功能详细描述]
	 * 
	 * @param am
	 *            2013-10-12 上午11:55:10
	 */
	public void readMessage(AbstractMessage<?> am) {
		if (!am.isHasRead()) {
			am.read();
			decreaseUnreadMsgCount();
		}
	}

	public void readMessages(Set<String> ids) {
		int unreadCount = 0;
		for (String id : ids) {
			AbstractMessage<?> am = msgMap.get(id);
			if (!am.isHasRead()) {
				am.read();
				unreadCount++;
			}
		}
		decreaseUnreadMsgCountBy(unreadCount);
	}

	public void readMessage(int position) {
		readMessage(dataList.get(position));
	}

	public void readAllMessages() {
		int unreadCount = 0;
		for (AbstractMessage<?> am : dataList) {
			if (!am.isHasRead()) {
				am.read();
				unreadCount++;
			}
		}
		decreaseUnreadMsgCountBy(unreadCount);

	}

	/**********************************************************************************
	 * 
	 * getter setter
	 * 
	 **********************************************************************************/
	public boolean isExpend() {
		return expend;
	}

	public long getLastMessageTime() {
		return lastMessageTime;
	}

	public void setLastMessageTime(long lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}

	public List<AbstractMessage<?>> getMsgList() {
		return dataList;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		for (AbstractMessage<?> am : dataList) {
			am.setEditable(isEditable);
		}
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setExpend(boolean expend) {
		this.expend = expend;
	}

	/**********************************************************************************
	 * 
	 * 计数器
	 * 
	 **********************************************************************************/

	public void clearMsgCount() {
		msgCount = 0;
	}

	public void clearUnreadMsgCount() {
		unreadMsgCount = 0;
	}

	public void increaseMsgCount() {
		++msgCount;
	}

	public void increaseUnreadMsgCount() {
		++unreadMsgCount;
	}

	public void decreaseMsgCount() {
		--msgCount;
	}

	public void decreaseUnreadMsgCount() {
		--unreadMsgCount;
	}

	public void increaseMsgCountBy(int count) {
		msgCount += count;
	}

	public void increaseUnreadMsgCountBy(int count) {
		unreadMsgCount += count;
	}

	public void decreaseMsgCountBy(int count) {
		msgCount -= count;
	}

	public void decreaseUnreadMsgCountBy(int count) {
		unreadMsgCount -= count;
	}

	public void notifyUnreadChange() {
		CubeModule cubeModule = null;
		if (identifier != null
				&& !TmpConstants.ANNOUCE_RECORD_IDENTIFIER.equals(identifier)
				&& (cubeModule = CubeModuleManager.getInstance()
						.getCubeModuleByIdentifier(identifier)) != null) {
			cubeModule.setMsgCount(unreadMsgCount);
		}
		// 如果是公告模块，则重新计算新消息条数
		else if (TmpConstants.ANNOUCE_RECORD_IDENTIFIER.equals(identifier)) {
			Pool.getPool().execute(new Runnable() {
				@Override
				public void run() {
					CubeModule cubeModule = CubeModuleManager.getInstance()
							.getCubeModuleByIdentifier(identifier);
					long unreadCount;
					try {
						unreadCount = StaticReference.defMf
								.queryBuilder(NoticeModuleMessage.class)
								.where().eq("hasRead", false).countOf();
						if (cubeModule != null){
							cubeModule.setMsgCount((int) unreadCount);
						}
					} catch (Exception e) {
						log.error("query NoticeModule error!", e);
					}

				}
			});
		}
	}

	public boolean isEmpty() {
		return dataList.isEmpty();
	}

}
