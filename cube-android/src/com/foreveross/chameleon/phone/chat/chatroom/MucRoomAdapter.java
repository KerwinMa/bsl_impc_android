package com.foreveross.chameleon.phone.chat.chatroom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.util.PushUtil;
import com.foreveross.chameleon.util.TimeUnit;

public class MucRoomAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<ChatGroupModel> sessionData;
	private Filter filter;

	public MucRoomAdapter(Context context, List<ChatGroupModel> sessionData,
			Filter filter) {
		this.context = context;
		this.sessionData = sessionData;
		this.filter = filter;
	}

	@Override
	public int getCount() {
		return sessionData.size();
	}

	@Override
	public Object getItem(int position) {

		return sessionData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (null == convertView) {
			holder = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_group_child, null, false);
			holder.headIv = (ImageView) convertView
					.findViewById(R.id.item_group_head_iv);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.item_group_friend_name_tv);
			holder.conversationTv = (TextView) convertView
					.findViewById(R.id.item_group_lastconversation_content);
			holder.conversationTimeTv = (TextView) convertView
					.findViewById(R.id.item_group_lastconversation_time);
			holder.roomIcon = (ImageView) convertView
					.findViewById(R.id.item_group_head_iv);
			ImageView friend = (ImageView) convertView
					.findViewById(R.id.collect_friend_icon);
			friend.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		ChatGroupModel model = sessionData.get(position);
		if (model != null) {
			ConversationMessage conversation = model.getLastMessage();
			if (conversation != null) {
				if (conversation.getType().equals("voice")) {
					holder.conversationTv.setText("[声音]");
				} else if (conversation.getType().equals("image")) {
					holder.conversationTv.setText("[图片]");
				} else {
					holder.conversationTv.setText(conversation.getContent());
				}
				if (conversation.getLocalTime() == 0){
					holder.conversationTimeTv.setText(TimeUnit.getStringDate());
				} else {
					holder.conversationTimeTv.setText(LongToStr(conversation.getLocalTime()));
				}
			} else {
				holder.conversationTv.setText("");
				holder.conversationTimeTv.setText("");
			}
			holder.nameTv.setText(model.getGroupName());
			holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
					holder.headIv, model.getUnreadMessageCount()));
		} else {
			holder.conversationTv.setText("");
			holder.conversationTimeTv.setText("");
		}
		holder.roomIcon.setImageResource(R.drawable.ico_02);
		if (model != null){
			holder.roomIcon.setImageBitmap(PushUtil.drawPushCount(context,
					holder.roomIcon, model.getUnreadMessageCount()));
		}
		return convertView;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return filter;
	}

	class ChildHolder {
		ImageView headIv;
		TextView nameTv;
		TextView conversationTv;
		TextView conversationTimeTv;
		ImageView roomIcon;
	}
	
	public static String LongToStr(long m) {
		String dateString = null;
		SimpleDateFormat formatter = new SimpleDateFormat();
		String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
		synchronized (formatter) {
			formatter.applyPattern(LONG_FORMAT);
			dateString = formatter.format(new Date(m));
		}
		return dateString;
	}

}
