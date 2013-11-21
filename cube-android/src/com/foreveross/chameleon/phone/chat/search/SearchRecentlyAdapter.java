package com.foreveross.chameleon.phone.chat.search;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;

public class SearchRecentlyAdapter extends BaseAdapter {
	private Context context;
	private List<SessionModel> data;
	
	public SearchRecentlyAdapter(Context context, List<SessionModel> data) {
		this.context=context;
		this.data=data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_search, null, false);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_search_icon);
			holder.name = (TextView) convertView.findViewById(R.id.item_search_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SessionModel sessionModel=data.get(position);
		String name = null;
		String chatter = sessionModel.getChatter();
		if (SessionModel.SESSION_ROOM.equals(sessionModel.getFromType())){
			
			ChatGroupModel chatGroupModel = 
					IMModelManager.instance().getChatRoomContainer().getStuff(chatter);
			if (chatGroupModel != null){
				name = chatGroupModel.getGroupName();
				holder.icon.setImageResource(R.drawable.ico_02);
			}
		} else {
			UserModel userModel = IMModelManager.instance().getUserModel(chatter);
			if (userModel != null){
				name = userModel.getName();
			}
			if (getHeadIcon(userModel) != -1) {
				holder.icon.setImageResource(getHeadIcon(userModel));
			}
		}
		holder.name.setText(name);
//		friend.setIcon(holder.icon);
//	    holder.icon.setImageBitmap(PushUtil.drawPushCount(context,holder.icon,friend.getIsRead()));
		return convertView;
	}

	class ViewHolder {
		TextView name;
		ImageView icon;
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
	
	public int getHeadIcon(UserModel userModel) {
		String sex = userModel.getSex();
		String status = userModel.getStatus();
		if (sex == null || status == null) {
			return -1;
		}
		if ("female".equals(sex)) {
			if (UserStatus.USER_STATE_AWAY.equals(status)) {
				return R.drawable.chatroom_female_online;
			} else if (UserStatus.USER_STATE_BUSY.equals(status)) {
				return R.drawable.chatroom_female_online;
			} else if (UserStatus.USER_STATE_OFFLINE.equals(status)) {
				return R.drawable.chatroom_female_outline;
			} else if (UserStatus.USER_STATE_ONLINE.equals(status)) {
				return R.drawable.chatroom_female_online;
			}
		} else if ("male".equals(sex)) {
			if (UserStatus.USER_STATE_AWAY.equals(status)) {
				return R.drawable.chatroom_male_online;
			} else if (UserStatus.USER_STATE_BUSY.equals(status)) {
				return R.drawable.chatroom_male_online;
			} else if (UserStatus.USER_STATE_OFFLINE.equals(status)) {
				return R.drawable.chatroom_male_outline;
			} else if (UserStatus.USER_STATE_ONLINE.equals(status)) {
				return R.drawable.chatroom_male_online;
			}
		} else {
			if (UserStatus.USER_STATE_AWAY.equals(status)) {
				return R.drawable.chatroom_unknow_online;
			} else if (UserStatus.USER_STATE_BUSY.equals(status)) {
				return R.drawable.chatroom_unknow_online;
			} else if (UserStatus.USER_STATE_OFFLINE.equals(status)) {
				return R.drawable.chatroom_unknow_outline;
			} else if (UserStatus.USER_STATE_ONLINE.equals(status)) {
				return R.drawable.chatroom_unknow_online;
			}
		}
		return -1;
	}
	
}
