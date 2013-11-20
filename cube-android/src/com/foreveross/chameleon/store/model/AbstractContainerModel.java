package com.foreveross.chameleon.store.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.content.res.Resources.NotFoundException;

import com.csair.impc.R;
import com.foreveross.chameleon.phone.modules.CubeApplication;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述] ID: 装载物ID<BR>
 * V: 装载物<BR>
 * T:数据库对象<BR>
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-8-26]
 */
public abstract class AbstractContainerModel<ID, V extends IDObject<ID>> {

	/**
	 * [消息总数]
	 */
	protected int messageCount = 0;
	/**
	 * [组名]
	 */
	protected String groupName;
	/**
	 * [组编码]
	 */
	protected String groupCode;

	/**
	 * [装载物]
	 */
	protected transient List<V> list = new ArrayList<V>();

	protected transient Map<ID, V> stuffMap = new HashMap<ID, V>();

	public List<V> getList() {
		return list;
	}

	protected void setList(List<V> list) {
		this.list = list;
		notifyContentChange();
	}

	public V getStuff(ID id) {
		return stuffMap.get(id);
	}

	public void addStuff(V v) {
		list.add(v);
		stuffMap.put(v.getMyId(), v);
		notifyContentChange();
	}

	protected void addStuffs(List<V> vs) {
		for (V v : vs) {
			list.add(v);
			stuffMap.put(v.getMyId(), v);
		}
		notifyContentChange();
	}

	protected void removeStuff(V v) {
		list.remove(v);
		stuffMap.remove(v.getMyId());
		notifyContentChange();
	}

	protected void removeStuff(ID id) {
		list.remove(stuffMap.remove(id));
		notifyContentChange();
	}

	public boolean containStuff(ID id) {
		return stuffMap.containsKey(id);
	}

	public boolean containStuff(V v) {
		V oldV = stuffMap.get(v.getMyId());
		return oldV != null && oldV == v;
	}

	public void clear() {
		list.clear();
		stuffMap.clear();
		notifyContentChange();
	}

	public Collection<V> search(String prefix) {
		throw new UnsupportedOperationException();
	}

	public int size() {
		return list.size();
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		String gName = "airport." + groupName;
		Properties airportProps = new Properties();
		try {
			airportProps.load(CubeApplication.getmContext().getResources()
			.openRawResource(R.raw.airports));
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String name = airportProps.getProperty(gName);
		if (name != null && !"".equals(name)){
			this.groupName = name;
		} else {
			this.groupName = groupName;
		}
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public V getObject(int index) {
		return list.get(index);
	}

	void increaseCount() {
		++messageCount;
	}

	void increaseCountBy(int count) {
		messageCount = messageCount + count;
	}

	void decreaseCount() {
		--messageCount;
	}

	void descreaseCountBy(int count) {
		messageCount = messageCount - count;
	}

	public int getMessageCount() {
		return messageCount;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-28 下午2:59:39
	 */
	@Override
	public int hashCode() {
		return (groupCode == null ? "" : groupCode).hashCode();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param o
	 * @return 2013-8-28 下午3:00:19
	 */
	@Override
	public boolean equals(Object o) {
		if (this.getClass().isAssignableFrom(o.getClass())) {
			AbstractUserGroupModel other = AbstractUserGroupModel.class.cast(o);
			return (this.groupCode == null ? "" : groupCode).equals(other
					.getGroupCode());
		}
		return false;
	}

	private ContainerContentChangeListener containerContentChangeListener;

	public ContainerContentChangeListener getContainerContentChangeListener() {
		return containerContentChangeListener;
	}

	public void setContainerContentChangeListener(
			ContainerContentChangeListener containerContentChangeListener) {
		this.containerContentChangeListener = containerContentChangeListener;
	}

	public void notifyContentChange() {
		if (containerContentChangeListener != null) {
			containerContentChangeListener.onContentChange();
		}
	}

	public void sort() {

	}

}
