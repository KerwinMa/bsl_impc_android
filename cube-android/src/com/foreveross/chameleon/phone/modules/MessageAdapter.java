package com.foreveross.chameleon.phone.modules;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.CubeAndroid;
import com.foreveross.chameleon.CubeConstants;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.phone.activity.NoticeActivity;
import com.foreveross.chameleon.push.cubeparser.type.AbstractMessage;
import com.foreveross.chameleon.push.cubeparser.type.ModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessageStub;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.util.FileCopeTool;
import com.foreveross.chameleon.util.PadUtils;

public class MessageAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<MsgModel> msgData;

	public MessageAdapter(Context context, List<MsgModel> msgData) {
		this.context = context;
		this.msgData = msgData;
	}

	// ---------------------------------------以下是child的回调函数-----------------------------------------//

	@Override
	public AbstractMessage<?> getChild(int groupPosition, int childPosition) {
		return MessageFragmentModel.instance().getReadOnlyMessage(
				groupPosition, childPosition);
	}

	@Override
	public long getChildId(int getChild, int childPosition) {
		return getChild(getChild, childPosition).getId();
	}

	private void jump(ModuleMessage<?> moduleMessage) {
		if (!existModule(moduleMessage.getIdentifier())) {
			Toast.makeText(this.context, "模块不存在,或被隐藏!", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		if (moduleMessage instanceof NoticeModuleMessageStub) {
			String messageId = NoticeModuleMessageStub.class
					.cast(moduleMessage).getMesssageId();
			Intent intent = new Intent();
			intent.putExtra("messageId", messageId);
			if (PadUtils.isPad(context)) {
				intent.setClass(context, FacadeActivity.class);
				PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(
						context, CubeConstants.CUBE_CONFIG);
				String noticeViewClassName = propertiesUtil.getString(
						"com.foss.announcement", "");
				intent.putExtra("direction", 2);
				intent.putExtra("type", "fragment");
				intent.putExtra("value", noticeViewClassName);

			} else {
				intent.setClass(context, NoticeActivity.class);
			}
			context.startActivity(intent);
		} else {
			jump2web(moduleMessage);
		}
	}

	private void jump2web(ModuleMessage<?> moduleMessage) {
		CubeModule module = CubeModuleManager.getInstance()
				.getCubeModuleByIdentifier(moduleMessage.getIdentifier());
		if (module != null
				&& !TmpConstants.MESSAGE_RECORD_IDENTIFIER.equals(module
						.getIdentifier())
				&& !TmpConstants.ANNOUCE_RECORD_IDENTIFIER.equals(module
						.getIdentifier())) {
			MessageFragmentModel.instance().readAllRecordsByModule(
					module.getName());
		}
		String path = Environment.getExternalStorageDirectory().getPath() + "/"
				+ context.getPackageName();
		String url = path + "/www/" + moduleMessage.getIdentifier();
		// 检查文件是否存在
		if (new FileCopeTool(context).isfileExist(url, "index.html")) {

			Intent intent = new Intent();
			if (PadUtils.isPad(context)) {
				intent.setClass(context, FacadeActivity.class);
				intent.putExtra("direction", 2);
				intent.putExtra("type", "web");
				intent.putExtra("value", "file:/" + url + "/index.html");
			} else {
				intent.setClass(context, CubeAndroid.class);
				intent.putExtra("isPad", false);
				intent.putExtra("from", "main");
				intent.putExtra("path", Environment
						.getExternalStorageDirectory().getPath()
						+ "/"
						+ context.getPackageName());
				intent.putExtra("identify", moduleMessage.getIdentifier());
			}

			context.startActivity(intent);

		} else {
			Toast.makeText(context, "文件缺失，请重新下载", Toast.LENGTH_SHORT).show();
		}
	}

	private boolean existModule(String identifier) {
		if (identifier.equals(TmpConstants.ANNOUCE_RECORD_IDENTIFIER)) {
			return true;
		}
		return CubeModuleManager.getInstance().getCubeModuleByIdentifier(
				identifier) != null;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final AbstractMessage<?> messageModule = msgData.get(groupPosition)
				.getMsgList().get(childPosition);
		ChildHolder msgContentItem = null;
		if (null == convertView) {
			msgContentItem = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.msg_content, null, false);
			msgContentItem.msg_title = (TextView) convertView
					.findViewById(R.id.msg_title);
			msgContentItem.msg_content = (TextView) convertView
					.findViewById(R.id.msg_content);
			msgContentItem.msg_time = (TextView) convertView
					.findViewById(R.id.msg_time);
			msgContentItem.msg_checkbox = (CheckBox) convertView
					.findViewById(R.id.msgcheckbox);
			msgContentItem.msg_readStatus = (TextView) convertView
					.findViewById(R.id.msg_readStatus);
			msgContentItem.msgBody = convertView.findViewById(R.id.msgbody);

			convertView.setTag(msgContentItem);
		} else {
			msgContentItem = (ChildHolder) convertView.getTag();
		}
		msgContentItem.msgBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MessageFragmentModel.instance().readMessage(groupPosition,
						childPosition);
				if (messageModule instanceof ModuleMessage) {
					boolean linkable = ModuleMessage.class.cast(messageModule)
							.isLinkable();
					if (linkable) {
						jump(ModuleMessage.class.cast(messageModule));
					}
				}

			}

		});
		msgContentItem.msg_title.setText(messageModule.getTitle());
		msgContentItem.msg_content.setText(messageModule.getContent());
		msgContentItem.msg_time.setText(trans(System.currentTimeMillis(),
				messageModule));
		msgContentItem.msg_checkbox.setChecked(messageModule.isSelected());
		if (messageModule.isHasRead()) {
			msgContentItem.msg_time.setTextColor(Color.parseColor("#212121"));
			msgContentItem.msg_readStatus.setText("已读");
			msgContentItem.msg_readStatus.setTextColor(Color.BLACK);
		} else {
			msgContentItem.msg_time.setTextColor(Color.parseColor("#478ac9"));
			msgContentItem.msg_readStatus.setText("未读");
			msgContentItem.msg_readStatus.setTextColor(Color.RED);
		}
		if (!messageModule.isEditable()) {
			msgContentItem.msg_checkbox.setVisibility(View.GONE);
		} else {
			msgContentItem.msg_checkbox.setVisibility(View.VISIBLE);
			msgContentItem.msg_checkbox.setChecked(messageModule.isSelected());
			msgContentItem.msg_checkbox
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							MessageFragmentModel.instance().selectMessage(
									groupPosition, childPosition,
									CheckBox.class.cast(v).isChecked());
						}
					});

		}
		return convertView;
	}

	class ChildHolder {
		TextView msg_title;
		TextView msg_content;
		TextView msg_time;
		TextView msg_read;
		TextView msg_readStatus;
		CheckBox msg_checkbox;
		View msgBody;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return msgData.get(groupPosition).getMsgList().size();
	}

	// ---------------------------------------以下是group的回调函数-----------------------------------------//

	@Override
	public MsgModel getGroup(int groupPosition) {
		return msgData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return msgData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// 初始化组件
		GroupViewHolder msgTitleItem;
		if (convertView == null) {
			msgTitleItem = new GroupViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.msg_title, null);
			msgTitleItem.msgIcon = (ImageView) convertView
					.findViewById(R.id.msg_icon);
			msgTitleItem.msgSort = (TextView) convertView
					.findViewById(R.id.msg_sort);
			msgTitleItem.msgNum = (TextView) convertView
					.findViewById(R.id.msg_num);
			msgTitleItem.delete_icon = (TextView) convertView
					.findViewById(R.id.delete_icon);
			convertView.setTag(msgTitleItem);

		} else {
			msgTitleItem = (GroupViewHolder) convertView.getTag();
		}
		// 得到组模型
		final MsgModel msgModel = msgData.get(groupPosition);
		if (msgModel.isEditable()) {
			msgTitleItem.delete_icon.setVisibility(View.VISIBLE);
		} else {
			msgTitleItem.delete_icon.setVisibility(View.INVISIBLE);
		}

		msgTitleItem.delete_icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(context)
						.setTitle("提示")
						.setMessage("确定删除？")
						.setNegativeButton("取消", null)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										MessageFragmentModel
												.instance()
												.removeGroup(
														msgModel.getGroupName());
									}
								}).show();
			}
		});

		if (isExpanded) {
			msgTitleItem.msgIcon.setImageResource(R.drawable.arrow_dowm);
		} else {
			msgTitleItem.msgIcon.setImageResource(R.drawable.arrow);
		}
		CubeModule module = CubeModuleManager.getInstance()
				.getModuleByIdentify(msgModel.getGroupName());
		if (module != null) {
			msgTitleItem.msgSort.setText(module.getName());
		} else {
			msgTitleItem.msgSort.setText(msgModel.getGroupName());
		}
		int msgCount = msgModel.getMsgCount();
		int unreadMsgCount = msgModel.getUnreadMsgCount();
		msgTitleItem.msgNum.setText(unreadMsgCount + "/" + msgCount);
		if (unreadMsgCount > 0) {
			msgTitleItem.msgNum.setTextColor(Color.RED);
		} else {
			msgTitleItem.msgNum.setTextColor(Color.WHITE);
		}
		return convertView;
	}

	class GroupViewHolder {
		/** 向下的箭头 */
		ImageView msgIcon;
		/** 消息标题 */
		TextView msgSort;
		/** 消息条数的圆背景 */
		TextView msgNum;
		TextView delete_icon;
	}

	/** 点击item是否变色 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private String trans(long current, AbstractMessage<?> msg) {
		long diff = current - msg.getSendTime();
		long day = diff / 86400000;
		long hour = (diff % 86400000) / 3600000;
		long min = (diff % 86400000 % 3600000) / 60000;
		String resultDate = null;
		if (day > 3) {
			resultDate = msg.getDateTime("yyyy-MM-dd");
		} else if (day <= 3 && day > 0) {
			resultDate = day + "天前";
		} else if (day == 0 && hour > 0) {
			resultDate = hour + "小时前";
		} else if (hour == 0 && min > 0) {
			resultDate = min + "分钟前";
		} else {
			resultDate = "最新信息";
		}
		return resultDate;
	}
}
