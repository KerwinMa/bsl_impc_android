package com.foreveross.chameleon.phone.chat.search;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.phone.activity.ChatRoomActivity;
import com.foreveross.chameleon.phone.muc.IChoisedEventListener;
import com.foreveross.chameleon.phone.muc.MucAddFriendAdapter;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.PushUtil;

public class SearchGroupFriendAdapter extends BaseAdapter {
	private Context context;
	private List<UserModel> data;
	private IChoisedEventListener mListener;
	
	public SearchGroupFriendAdapter(Context context, List<UserModel> data) {
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
			holder.layout= (RelativeLayout) convertView.findViewById(R.id.item_layout);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_search_icon);
			holder.name = (TextView) convertView.findViewById(R.id.item_search_name);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.invite_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserModel friend=data.get(position);
		holder.name.setText(friend.getName());
		
		if (getHeadIcon(friend) != -1) {
			holder.icon.setImageResource(getHeadIcon(friend));
		}
		
		final ViewHolder viewholder = holder;
		holder.checkBox.setVisibility(View.GONE);
		holder.layout.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (PadUtils.isPad(context)) {
					Intent intent = new Intent();
					intent.putExtra("jid", friend.getJid());
					intent.putExtra("direction", 2);
					intent.putExtra("type", "fragment");
					intent.setClass(context,
							FacadeActivity.class);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context,
							ChatRoomActivity.class);
					intent.putExtra("jid", friend.getJid());
					context.startActivity(intent);
				}
			}
		});
//		friend.setIcon(holder.icon);
//	    holder.icon.setImageBitmap(PushUtil.drawPushCount(context,holder.icon,friend.getIsRead()));
		return convertView;
	}

	class ViewHolder {
		RelativeLayout layout;
		TextView name;
		ImageView icon;
		CheckBox checkBox;
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
	
	public void setmListener(IChoisedEventListener mListener) {
		this.mListener = mListener;
	}
}
