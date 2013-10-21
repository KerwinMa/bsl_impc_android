package com.foreveross.chameleon.phone.chat.collect;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;
import com.foreveross.chameleon.util.PushUtil;
import com.foreveross.chameleon.util.TimeUnit;
import com.foreveross.chameleon.util.imageTool.CubeImageTools;

public class CollectedAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<UserModel> userData;
	private Filter filter;

	public CollectedAdapter(Context context, List<UserModel> userData,
			Filter filter) {
		this.context = context;
		this.userData = userData;
		this.filter = filter;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return userData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Log.e("CollectedAdapter", "convertView" + convertView);
		if (null == convertView) {
			holder = new ViewHolder();
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserModel friend = userData.get(position);

		ConversationMessage conversation = friend.getLastMessage();
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

		holder.nameTv.setText(friend.getName());
		
		if (getHeadIcon(friend) != -1){
			holder.headIv.setImageResource(getHeadIcon(friend));
		}
		holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
				holder.headIv, friend.getUnreadMessageCount()));
//		holder.collectedBox.setFocusable(false); // checkbox优先级高，设置保证OnItemClickListener正常或者可以在checkBox外面包上一层
//		setListener(holder, position);
		return convertView;
	}

	class ViewHolder {
		ImageView headIv;
		TextView nameTv;
		TextView conversationTv;
		TextView conversationTimeTv;
		ImageView collectedBox;
		boolean isCheck = true;
	}

	private void setListener(final ViewHolder holder, final int position) {
		holder.collectedBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final UserModel friend = userData.get(position);
				userData.remove(friend);
				friend.setFavor(false);
				CollectedAdapter.this.notifyDataSetChanged();
				//刷新好友的列表状态
				IMModelManager.instance().getFriendContainer().notifyContentChange();
				/*
				// TODO Auto-generated method stub

				final UserModel friend = userData.get(position);
				Application application = Application.class.cast(context
						.getApplicationContext());
				HttpRequestAsynTask collectedFriendTask = new HttpRequestAsynTask(
						context) {

					@Override
					protected void doPostExecute(String result) {
						Log.e("collectedFriendTask", result);
						super.doPostExecute(result);

						if (result != null) {

						}
						userData.remove(friend.getJid());
						CollectedAdapter.this.notifyDataSetChanged();
						//刷新好友的列表状态
						IMModelManager.instance().getFriendContainer().notifyContentChange();
					}

				};
				String url = URL.CHATDELETE
						+ "/"
						+ Preferences.getUserName(Application.sharePref)
						+ "@"
						+ application.getChatManager().getConnection()
								.getServiceName() + "/" + friend.getJid();
				collectedFriendTask.execute(url, "", HttpUtil.UTF8_ENCODING,
						HttpUtil.HTTP_GET);

			*/}
		});
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return filter;
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
