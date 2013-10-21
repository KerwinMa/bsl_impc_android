/**
 * 
 */
package com.foreveross.chameleon.push.cubeparser.type;

import com.j256.ormlite.table.DatabaseTable;

/**
 * [系统消息]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-22]
 */
@DatabaseTable(tableName = "SystemMessage")
public class SystemMessage extends AbstractMessage<SystemMessage> {
	public SystemMessage() {
		super();
	}

	/**
	 * @param sendTime
	 * @param messsageId
	 * @param title
	 * @param content
	 */
	public SystemMessage(long sendTime, String messsageId, String title,
			String content) {
		super(sendTime, messsageId, title, content);
		this.groupBelong = "系统";
	}

}
