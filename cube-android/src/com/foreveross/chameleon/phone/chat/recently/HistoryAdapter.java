package com.foreveross.chameleon.phone.chat.recently;

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
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;
import com.foreveross.chameleon.util.PushUtil;

public class HistoryAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<SessionModel> sessionData;
	private Filter filter;

	public HistoryAdapter(Context context, List<SessionModel> sessionData,
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
			holder.collectedBox = (ImageView) convertView
					.findViewById(R.id.collect_friend_icon);

			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}

		SessionModel session = sessionData.get(position);
		
		// 填充会话内容和时间
		if (session != null) {
			//暂时只显示文字
/*			if (conversation.getType().equals("voice")) {
				holder.conversationTv.setText("[声音]");
			} else if (conversation.getType().equals("image")) {
				holder.conversationTv.setText("[图片]");
			} else {
				holder.conversationTv.setText(session.getLastContent());
			}*/
			holder.conversationTv.setText(session.getLastContent());
			holder.conversationTimeTv.setText(LongToStr(session.getSendTime()));
		} else {
			holder.conversationTv.setText("");
			holder.conversationTimeTv.setText("");
		}
		SessionModel model = sessionData.get(position);
		String jid = model.getChatter();
		String name = "";
		//如果历史记录是群组
		if (SessionModel.SESSION_ROOM.equals(model.getFromType())){
			ChatGroupModel chatGroupModel = IMModelManager.instance().
					getChatRoomContainer().getStuff(jid);
			if (chatGroupModel != null){
				name = chatGroupModel.getGroupName();
				holder.nameTv.setText(name);
				holder.headIv.setImageResource(R.drawable.ico_02);
				holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
						holder.headIv, chatGroupModel.getUnreadMessageCount()));
			} else {
				StaticReference.defMf.deleteById(jid, SessionModel.class);
				return null;
				// 清除多余数据
			}
		} else {
			UserModel userModel = IMModelManager.instance().getUserModel(jid);
			if (userModel != null){
				name = userModel.getName();
				holder.nameTv.setText(name);
				if (getHeadIcon(userModel) != -1){
					holder.headIv.setImageResource(getHeadIcon(userModel));
				}
				holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
						holder.headIv, userModel.getUnreadMessageCount()));
			}
		}
		// 根据类型判断,设置头像和人名/组名
		// 群组功能暂时不做，现全是用户头像
/*		if (conversation.getRoomJid() != null) {
			MucRoomModel room = roomData.get(conversation.getRoomJid());
			if (room != null) {
				holder.nameTv.setText(room.getName());
				holder.headIv.setImageResource(R.drawable.ico_02);
				holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
						holder.headIv, room.getIsRead()));
			} else {
				holder.nameTv.setText("");
			}
		} else {
			UserModel user = userData.get(conversation.getChater());
			if (user != null) {
				holder.nameTv.setText(user.getName());
				user.setIcon(holder.headIv);
				holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
						holder.headIv, user.getIsRead()));
			} else {
				holder.nameTv.setText("");
			}
		}*/
		holder.collectedBox.setVisibility(View.GONE);
		return convertView;
	}

	class ChildHolder {
		// ** 头像 *//*
		ImageView headIv;
		// ** 好友名或组名 *//*
		TextView nameTv;
		// ** 最后会话内容 *//*
		TextView conversationTv;
		// ** 最后会话时间 *//*
		TextView conversationTimeTv;
		// ** 是否未收藏好友 *//*
		ImageView collectedBox;
	}

	@Override
	public Filter getFilter() {
		return filter;
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
