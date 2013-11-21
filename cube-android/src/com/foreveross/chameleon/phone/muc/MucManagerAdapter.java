package com.foreveross.chameleon.phone.muc;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.push.client.XmppManager;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.Preferences;


public class MucManagerAdapter extends BaseAdapter {
	Context context;
	List<UserModel> data;
	ChatGroupModel model;
	/**该用户是否有权限*/
	boolean kickable;
	public MucManagerAdapter(Context context,List<UserModel> data,ChatGroupModel model,boolean kickable) {
		this.data= data;
		this.context=context;
		this.model = model;
		this.kickable=kickable;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public UserModel getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LinearLayout.inflate(context, R.layout.item_muc_manager,null);
			holder.userName = (TextView) convertView.findViewById(R.id.item_muc_manager_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.item_muc_manager_icon);
			holder.deleteBtn = (Button) convertView.findViewById(R.id.item_muc_manager_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserModel member = data.get(position);
		if(member.getName()!=null){
			holder.userName.setText(member.getName());
		}else{
			String userAccount = Preferences.getUserName(Application.sharePref);
			holder.userName.setText(userAccount);
		}
		if(kickable){
			String userAccount = Preferences.getUserName(Application.sharePref)+"@";
			if(member.getJid().startsWith(userAccount)){
				//是当前用户，不能剔除
				holder.deleteBtn.setVisibility(View.GONE);
			}else{
				holder.deleteBtn.setVisibility(View.VISIBLE);
			}
		}else{
			holder.deleteBtn.setVisibility(View.GONE);
		}
		holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context)
						.setTitle("是否删除该好友")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String fromWho = XmppManager.getMeJid();
										String toWho = model.getGroupCode();
										String content = member.getJid();
										ConversationMessage conversation = createConversation(
												content, fromWho, toWho,
												"killperson");
										MucManager.getInstanse(context)
												.sendMucMessage(conversation);
										model.kick(context, member.getJid());
										data.remove(member);
										notifyDataSetChanged();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
		return convertView;
	}
	
	class ViewHolder {
		
		/** 用户名 */
		TextView userName;
		/** 头像图片 */
		ImageView icon;
		/** 整个小组未读消息条数 */
		Button deleteBtn;

	}
	
	/**
	 * 构建一个会话对象
	 **/
	private ConversationMessage createConversation(String content,
			String fromWho, String toWho, String type) {
		ConversationMessage conversation = new ConversationMessage();
		conversation.setContent(content);
		conversation.setFromWho(fromWho);
		conversation.setToWho(toWho);
		conversation.setUser(fromWho);
		conversation.setChater(toWho);
		conversation.setLocalTime(System.currentTimeMillis());
		conversation.setType(type);
		return conversation;
	}
}
