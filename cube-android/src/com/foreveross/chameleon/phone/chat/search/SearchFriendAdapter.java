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
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.PushUtil;

public class SearchFriendAdapter extends BaseAdapter {
	private Context context;
	private List<UserModel> data;
	
	public SearchFriendAdapter(Context context, List<UserModel> data) {
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
		UserModel friend=data.get(position);
		holder.name.setText(friend.getName());
//		friend.setIcon(holder.icon);
//	    holder.icon.setImageBitmap(PushUtil.drawPushCount(context,holder.icon,friend.getIsRead()));
		return convertView;
	}

	class ViewHolder {
		TextView name;
		ImageView icon;
	}
}
