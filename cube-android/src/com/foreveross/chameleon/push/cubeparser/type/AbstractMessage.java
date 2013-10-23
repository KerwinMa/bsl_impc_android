/**
 * 
 */
package com.foreveross.chameleon.push.cubeparser.type;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

import com.foreveross.chameleon.store.core.BaseModel;
import com.foreveross.chameleon.util.Pool;
import com.j256.ormlite.field.DatabaseField;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-22]
 */
public abstract class AbstractMessage<T> extends BaseModel<T, Long> {

	@DatabaseField(generatedId = true)
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AbstractMessage() {
		super();
	}

	/**
	 * [作用描述]
	 */
	@DatabaseField
	protected long sendTime = System.currentTimeMillis();
	@DatabaseField
	protected String messsageId;
	@DatabaseField
	protected String title;
	@DatabaseField
	protected String content;
	@DatabaseField
	protected String groupBelong = "未知";
	@DatabaseField
	protected boolean hasRead = false;
	@DatabaseField
	protected String userName;

	public boolean isHasRead() {
		return hasRead;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}

	protected boolean editable = false;
	protected boolean selected = false;

	public AbstractMessage(long sendTime, String messsageId, String title,
			String content) {
		super();
		this.sendTime = sendTime;
		this.messsageId = messsageId;
		this.title = title;
		this.content = content;
	}

	public AbstractMessage(long sendTime, String messsageId, String title) {
		super();
		this.sendTime = sendTime;
		this.messsageId = messsageId;
		this.title = title;
	}

	public String getMesssageId() {
		return messsageId;
	}

	public void setMesssageId(String messsageId) {
		this.messsageId = messsageId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDateTime(String pattern) {
		return new SimpleDateFormat(pattern).format(new Date(sendTime));
	}

	public String getGroupBelong() {
		return groupBelong;
	}

	public void setGroupBelong(String groupBelong) {
		this.groupBelong = groupBelong;
	}

	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void read() {
		setHasRead(true);
		Pool.getPool().execute(new Runnable() {
			@Override
			public void run() {
				AbstractMessage.this.update();
			}
		});
	}

	@Override
	public String toString() {
		return "AbstractMessage [id=" + id + ", sendTime=" + sendTime
				+ ", messsageId=" + messsageId + ", title=" + title
				+ ", content=" + content + ", groupBelong=" + groupBelong
				+ ", hasRead=" + hasRead + ", editable=" + editable
				+ ", selected=" + selected + "]";
	}

}
