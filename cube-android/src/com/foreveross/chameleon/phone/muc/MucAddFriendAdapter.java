package com.foreveross.chameleon.phone.muc;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;


public class MucAddFriendAdapter extends BaseAdapter  implements Filterable {
//	Map<String,GroupModel> groupMap;
	Context context;
	List<UserModel> userData;
	Map<String, UserModel> selectFriends;
	private Filter filter;
	private IChoisedEventListener mListener;
	
	public MucAddFriendAdapter(Context context, List<UserModel> userData,Map<String, UserModel> selectFriends , Filter filter) {
		this.context = context;
		this.userData = userData;
		this.selectFriends = selectFriends;
		this.filter = filter;
		Log.e("----MucAddFriendAdapter", "userData:"+userData.size());
	}
	
	


	@Override
	public int getCount() {
		return userData.size();
	}

	@Override
	public Object getItem(int position) {
		return userData.get(position);
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_muc_addfriends, null, false);
			holder.layout= (RelativeLayout) convertView.findViewById(R.id.item_layout);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_search_icon);
			holder.name = (TextView) convertView.findViewById(R.id.item_search_name);
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.invite_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserModel friend=userData.get(position);
		holder.checkBox.setChecked(false);
		holder.layout.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(selectFriends.containsKey(friend.getJid())){
					mListener.onRemoveChoisedEvent(friend);
				}else{
					mListener.onAddChoisedEvent(friend , false);
				}
				
				MucAddFriendAdapter.this.notifyDataSetChanged();
			}
			
		});
			
			/*@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switch (buttonView.getId()) {
				case R.id.invite_checkbox:
					if(selectFriends.containsKey(friend.getJid())){
							selectFriends.remove(friend.getJid());
						}else{
							selectFriends.put(friend.getJid(), friend);
						}
					break;

				default:
					break;
				}
			}
		});*/
		if(selectFriends.containsKey(friend.getJid())){
			holder.checkBox.setChecked(true);
		}else{
			holder.checkBox.setChecked(false);
		}
		holder.name.setText(friend.getName());
		int id = getHeadIcon(friend);
		holder.icon.setImageResource(id);
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

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return filter;
	}
	
	public void setmListener(IChoisedEventListener mListener) {
		this.mListener = mListener;
	}

	public int getHeadIcon(UserModel userModel) {
		String sex = userModel.getSex();
		String status = userModel.getStatus();
		if (sex == null || status == null){
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
