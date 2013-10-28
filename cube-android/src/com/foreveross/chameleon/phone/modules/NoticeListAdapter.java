package com.foreveross.chameleon.phone.modules;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.push.cubeparser.type.AbstractMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.util.Pool;
import common.extras.plugins.FileIntent;

public class NoticeListAdapter extends BaseAdapter {
	private Context context;
	private List<NoticeModuleMessage> noticeModules;

	public NoticeListAdapter(Context context,
			List<NoticeModuleMessage> noticeModules) {
		this.context = context;
		this.noticeModules = noticeModules;
	}

	@Override
	public int getCount() {
		return noticeModules.size();
	}

	@Override
	public Object getItem(int position) {
		return noticeModules.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {
		NoticeItem noticeItem = null;
		final NoticeModuleMessage noticeModuleMessage = noticeModules
				.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.notice_item, null);
			noticeItem = new NoticeItem();
			noticeItem.attachment = (TextView) convertView
					.findViewById(R.id.attment);
			noticeItem.title = (TextView) convertView.findViewById(R.id.title);
			noticeItem.date = (TextView) convertView.findViewById(R.id.date);
			noticeItem.content = (TextView) convertView
					.findViewById(R.id.content);
			noticeItem.isread = (TextView) convertView
					.findViewById(R.id.isread);
			noticeItem.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			noticeItem.msg_readStatus = (TextView) convertView.findViewById(R.id.msg_readStatus);
			noticeItem.msgBody = convertView.findViewById(R.id.msgbody);
			convertView.setTag(noticeItem);
		} else {
			noticeItem = (NoticeItem) convertView.getTag();
		}

		noticeItem.title.setText(noticeModuleMessage.getTitle());
		noticeItem.date.setText(trans(System.currentTimeMillis(),
				noticeModuleMessage));
		noticeItem.content.setText(noticeModuleMessage.getContent());
		noticeItem.checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				noticeModuleMessage.setSelected(CheckBox.class.cast(v)
						.isChecked());
			}
		});
		noticeItem.msgBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!noticeModuleMessage.isHasRead()) {
					noticeModuleMessage.setHasRead(true);
					readMessage(noticeModuleMessage.getMesssageId());
					NoticeListAdapter.this.notifyDataSetChanged();
					Pool.getPool().execute(new Runnable() {

						@Override
						public void run() {
							noticeModuleMessage.update();
							CubeModule cubeModule = CubeModuleManager.getInstance()
									.getModuleByIdentify(TmpConstants.ANNOUCE_RECORD_IDENTIFIER);
							cubeModule.decreaseMsgCount();
						}
					});

				}

			}
		});
		if (noticeModuleMessage.isHasRead()) {
			noticeItem.date.setTextColor(Color.parseColor("#212121"));
			noticeItem.msg_readStatus.setText("已读");
			noticeItem.msg_readStatus.setTextColor(Color.BLACK);
		} else {
			noticeItem.date.setTextColor(Color.parseColor("#478ac9"));
			noticeItem.msg_readStatus.setText("未读");
			noticeItem.msg_readStatus.setTextColor(Color.RED);
		}

		noticeItem.checkBox.setChecked(noticeModuleMessage.isSelected());
		if (!noticeModuleMessage.isEditable()) {
			noticeItem.checkBox.setVisibility(View.GONE);
		} else {
			noticeItem.checkBox.setVisibility(View.VISIBLE);
		}

		if (TextUtils.isEmpty(noticeModuleMessage.getAttachment())) {
			noticeItem.attachment.setVisibility(View.GONE);
		} else {
			noticeItem.attachment.setVisibility(View.VISIBLE);
			noticeItem.attachment.setOnClickListener(new OnClickListener() {
				// TODO[FNGWEILI] check it
				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub
					String dirpath = Environment.getExternalStorageDirectory()
							+ "/"
							+ Application.class.cast(context)
									.getCubeApplication().getPackageName()
							+ "/www/com.foss.announcement";
					File file = new File(dirpath);
					File[] files = file.listFiles();
					String attachment = noticeModuleMessage.getAttachment();
					for (File f : files) {

						if (f.getName().contains(attachment)) {

							String fName = f.getName();
							int dotIndex = fName.lastIndexOf(".");

							String type = fName.substring(dotIndex + 1)
									.toLowerCase();

							Application.class.cast(context).openAttachment(
									format(type), f.getAbsolutePath());

						}
					}

				}
			});
		}

		return convertView;
	}
	
	public void readMessage(String messageId){
		MessageFragmentModel.instance().readNotice(messageId);
	}

	public class NoticeItem {
		public View msgBody;
		public TextView attachment;
		public TextView title;
		public TextView date;
		public TextView content;
		public TextView isread;
		public CheckBox checkBox;
		public TextView msg_readStatus;
	}

	private String format(String type) {
		if (type.equals("html")) {
			type = FileIntent.FILE_TEXT_HTML;
		} else if (type.equals("doc") || type.equals("docx")) {
			type = FileIntent.FILE_WORD;
		} else if (type.equals("xls") || type.equals("excel")) {
			type = FileIntent.FILE_EXCEL;
		} else if (type.equals("ppt") || type.equals("pptx")) {
			type = FileIntent.FILE_PPT;
		} else if (type.equals("chm")) {
			type = FileIntent.FILE_CHM;
		} else if (type.equals("pdf")) {
			type = FileIntent.FILE_PDF;
		}
		return type;
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
