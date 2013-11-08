package com.foreveross.chameleon.phone.chat.collect;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.pad.fragment.MucAddFirendFragment;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.Preferences;
import com.foreveross.chameleon.util.PushUtil;

public class CollectedAdapter extends BaseAdapter implements Filterable {

	private Context context;
	private List<UserModel> userData;
	private Filter filter;
	public boolean showCollectDelete;

	public CollectedAdapter(Context context, boolean showCollectDelete,
			List<UserModel> userData, Filter filter) {
		this.context = context;
		this.showCollectDelete = showCollectDelete;
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
					R.layout.item_collect, null, false);
			holder.headIv = (ImageView) convertView
					.findViewById(R.id.item_group_head_iv);
			holder.deleteIv = (ImageView) convertView
					.findViewById(R.id.item_group_delete);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.item_group_friend_name_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UserModel friend = userData.get(position);

		if (showCollectDelete) {
			holder.deleteIv.setVisibility(View.VISIBLE);
			holder.deleteIv
					.setOnClickListener(new DeleteOnClickListener(friend));
		} else {
			holder.deleteIv.setVisibility(View.GONE);
		}

		holder.nameTv.setText(friend.getName());

		if (getHeadIcon(friend) != -1) {
			holder.headIv.setImageResource(getHeadIcon(friend));
		}
		holder.headIv.setImageBitmap(PushUtil.drawPushCount(context,
				holder.headIv, friend.getUnreadMessageCount()));
		// holder.collectedBox.setFocusable(false); //
		// checkbox优先级高，设置保证OnItemClickListener正常或者可以在checkBox外面包上一层
		// setListener(holder, position);
		return convertView;
	}

	class ViewHolder {
		ImageView headIv;
		TextView nameTv;
		ImageView collectedBox;
		boolean isCheck = true;
		ImageView deleteIv;
	}

	class DeleteOnClickListener implements OnClickListener {
		String who;
		UserModel userModel;

		public DeleteOnClickListener(UserModel userModel) {
			this.userModel = userModel;
		}

		@Override
		public void onClick(View v) {

			// 提示是否删除好友
			new AlertDialog.Builder(context)
					.setTitle("是否删除收藏好友")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Application application = Application.class
											.cast(context
													.getApplicationContext());
									HttpRequestAsynTask collectedFriendTask = new HttpRequestAsynTask(
											context) {

										@Override
										protected void doPostExecute(
												String result) {
											Log.e("collectedFriendTask", result);
											super.doPostExecute(result);

											if (result != null) {
												notifyDataSetChanged();
												Toast.makeText(context,
														"删除收藏成功",
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									};
									if (userModel.isFavor()) {
										userModel.setFavor(false);
										userModel.update();
										String url = URL.CHATDELETE
												+ "/"
												+ Preferences
														.getUserName(Application.sharePref)
												+ "@"
												+ application.getChatManager()
														.getConnection()
														.getServiceName() + "/"
												+ userModel.getJid()
												+ URL.getSessionKeyappKey();
										collectedFriendTask.execute(url, "",
												HttpUtil.UTF8_ENCODING,
												HttpUtil.HTTP_GET);
										IMModelManager.instance()
												.getFavorContainer()
												.notifyContentChange();
									}

								}
							}).setNegativeButton("取消", null).show();
		};
	};

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		return filter;
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

	public boolean isShowCollectDelete() {
		return showCollectDelete;
	}

	public void setShowCollectDelete(boolean showCollectDelete) {
		this.showCollectDelete = showCollectDelete;
	}

}
