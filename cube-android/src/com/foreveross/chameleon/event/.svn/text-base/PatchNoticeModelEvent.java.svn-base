/**
 * 
 */
package com.foreveross.chameleon.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.foreveross.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;


/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-3]
 */
public class PatchNoticeModelEvent implements Delayed {

	private List<NoticeModuleMessage> patch = new ArrayList<NoticeModuleMessage>();

	public NoticeModuleMessage lastNoticeModuleMessage(){
		if(patch.isEmpty()){
			return null;
		}
		return patch.get(patch.size()-1);
	}
	public boolean isEmpty(){
		return patch.isEmpty();
	}
	public void addNoticeModuleMessage(NoticeModuleMessage noticeModuleMessage){
		patch.add(noticeModuleMessage);
	}
	public List<NoticeModuleMessage> getPatch() {
		return patch;
	}

	public void setPatch(List<NoticeModuleMessage> patch) {
		this.patch = patch;
	}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * @param another
		 * @return
		 * 2013-9-3 下午4:14:29
		 */
	@Override
	public int compareTo(Delayed another) {
		// TODO Auto-generated method stub
		return 0;
	}

		/**
		 * [一句话功能简述]<BR>
		 * [功能详细描述]
		 * @param unit
		 * @return
		 * 2013-9-3 下午4:14:29
		 */
	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		return 0;
	}

}
