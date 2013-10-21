/**
 * 
 */
package com.foreveross.chameleon.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-3]
 */
public class NoticesDeletedInMessageEvent implements Delayed {

	private Set<String> patch = new HashSet<String>();

	public boolean isEmpty() {
		return patch.isEmpty();
	}

	public void addDeletedNoticeModuleMessage(String deletedId) {
		patch.add(deletedId);
	}

	public Set<String> getPatch() {
		return patch;
	}

	public void setPatch(Set<String> patch) {
		this.patch = patch;
	}

	public  boolean contain(String deletedId){
		return patch.contains(deletedId);
	}
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param another
	 * @return 2013-9-3 下午4:14:29
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
	 * @return 2013-9-3 下午4:14:29
	 */
	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}

}
