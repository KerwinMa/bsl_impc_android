package com.foreveross.chameleon.phone.modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.phone.chat.chatroom.PicutureDetailActivity;
import com.foreveross.chameleon.phone.chat.chatroom.TxtDetailActivity;
import com.foreveross.chameleon.push.cubeparser.type.AbstractMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.util.Pool;
import common.extras.plugins.FileIntent;

public class NoticeListAdapter extends BaseAdapter {
	private Context context;
	private List<NoticeModuleMessage> noticeModules;

	public NoticeListAdapter(Context context,
			List<NoticeModuleMessage> noticeModules) {
		NoticeModuleMessageComparator comparator = new NoticeModuleMessageComparator();
		Collections.sort(noticeModules, comparator);
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
			noticeItem.title = (TextView) convertView.findViewById(R.id.title);
			noticeItem.date = (TextView) convertView.findViewById(R.id.date);
			noticeItem.content = (TextView) convertView
					.findViewById(R.id.content);
			noticeItem.checkBox = (CheckBox) convertView
					.findViewById(R.id.checkBox);
			noticeItem.msg_readStatus = (TextView) convertView
					.findViewById(R.id.msg_readStatus);
			noticeItem.msgBody = convertView.findViewById(R.id.msgbody);
			noticeItem.attachmentsView = (LinearLayout) convertView
					.findViewById(R.id.attachmentsView);
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
							CubeModule cubeModule = CubeModuleManager
									.getInstance()
									.getModuleByIdentify(
											TmpConstants.ANNOUCE_RECORD_IDENTIFIER);
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
			noticeItem.attachmentsView.setVisibility(View.GONE);
		} else {
			noticeItem.attachmentsView.removeAllViews();
			ArrayList<AttachmentModel> arrayList = parseJson(noticeModuleMessage
					.getAttachment());
			for (AttachmentModel attachmentModel : arrayList) {
				RelativeLayout layout = (RelativeLayout) LayoutInflater.from(
						context).inflate(R.layout.notice_attach_icon, null);
				ImageView new_attach_icon = (ImageView) layout
						.findViewById(R.id.new_attach_icon);
				TextView new_attach_name = (TextView) layout
						.findViewById(R.id.new_attach_name);
				TextView new_attach_size = (TextView) layout
						.findViewById(R.id.new_attach_size);
				TextView new_attach_download = (TextView) layout
						.findViewById(R.id.new_attach_download);
				if (attachmentModel.getStatus().equals(AttachmentModel.downloaded)){
					new_attach_download.setText("[已下载]");
				} else {
					new_attach_download.setText("[末下载]");
				}
				new_attach_name.setText(attachmentModel.getFileName());
				new_attach_size.setText(attachmentModel.getFileSize() + "Kb");
				ProgressBar new_attach_progressBar = (ProgressBar) layout
						.findViewById(R.id.new_attach_progressBar);
				String fileName = attachmentModel.getFileName();
				if (fileName.endsWith(".png") || fileName.endsWith(".PNG")
						|| fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) {
					new_attach_icon.setImageResource(R.drawable.image_default);
				} else if (fileName.endsWith(".pdf")
						|| fileName.endsWith(".PDF")) {
					new_attach_icon.setImageResource(R.drawable.pdf_default);
				} else if (fileName.endsWith(".txt")
						|| fileName.endsWith(".TXT")) {
					new_attach_icon.setImageResource(R.drawable.txt_default);
				}
				if (attachmentModel.getStatus().equals(AttachmentModel.downloading)){
					new_attach_progressBar.setVisibility(View.VISIBLE);
				}
				layout.setOnClickListener(new AttachOnClickListener(
						attachmentModel, new_attach_progressBar , new_attach_download));
				noticeItem.attachmentsView.addView(layout, 0);
			}
		}

		return convertView;
	}

	public void readMessage(String messageId) {
		MessageFragmentModel.instance().readNotice(messageId);
	}

	public class NoticeItem {
		public View msgBody;
		public TextView title;
		public TextView date;
		public TextView content;
		public TextView isread;
		public CheckBox checkBox;
		public TextView msg_readStatus;
		public LinearLayout attachmentsView;
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

	public ArrayList<AttachmentModel> parseJson(String attachment) {
		if(attachment == null){
			return null;
		}
//		attachment = "[{fileId:T1hyJTByVT1RCvBVdK,fileName:steps.txt,fileSize:0} , {fileId:T1gtxTByZT1RCvBVdK,fileName:ormlite.pdf,fileSize:480} , {fileId:T1TRxTByZT1RCvBVdK,fileName:120.png,fileSize:22}]";
		ArrayList<AttachmentModel> attachmentModels = new ArrayList<AttachmentModel>();
		try {
			JSONArray jay = new JSONArray(attachment);
			for (int i = 0; i < jay.length(); i++) {
				AttachmentModel attachmentModel = new AttachmentModel();
				JSONObject jb = (JSONObject) jay.get(i);
				String fileId = (String) jb.get("fileId");
				String fileName = (String) jb.get("fileName");
				String fileSize = (String) jb.get("fileSize");
				String filePath = null;
				if (fileName.endsWith(".png") || fileName.endsWith(".PNG")
						|| fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) {
					attachmentModel.setType("png");
					filePath = fileId + ".png";
				} else if (fileName.endsWith(".pdf")
						|| fileName.endsWith(".PDF")) {
					attachmentModel.setType("pdf");
					filePath = fileId + ".pdf";
				} else if (fileName.endsWith(".txt")
						|| fileName.endsWith(".TXT")) {
					attachmentModel.setType("txt");
					filePath = fileId + ".txt";
				}
				
				String filePathtemp = null;
				if (fileName.endsWith(".png") || fileName.endsWith(".PNG")
						|| fileName.endsWith(".jpg") || fileName.endsWith(".JPG")) {
					attachmentModel.setType("png");
					filePathtemp = fileId + "temp" +".png";
				} else if (fileName.endsWith(".pdf")
						|| fileName.endsWith(".PDF")) {
					attachmentModel.setType("pdf");
					filePathtemp = fileId + "temp" + ".pdf";
				} else if (fileName.endsWith(".txt")
						|| fileName.endsWith(".TXT")) {
					attachmentModel.setType("txt");
					filePathtemp = fileId + "temp" +".txt";
				}
				
				// 如果文件存在
				String dir = Environment.getExternalStorageDirectory()
						.getPath() + "/" + TmpConstants.ATTACHMENT_PATH;
				File attachmentDir = new File(dir + "/" + filePath);
				if (attachmentDir.exists()) {
					attachmentModel.setFilePath(dir + "/" + filePath);
					attachmentModel.setStatus(AttachmentModel.downloaded);
				} else {
					attachmentModel.setStatus(AttachmentModel.notdownload);
				}
				File attachmentDirtemp = new File(dir + "/" + filePathtemp);
				if (attachmentDirtemp.exists()) {
					attachmentModel.setStatus(AttachmentModel.downloading);
				} 
				attachmentModel.setFileId(fileId);
				attachmentModel.setFileName(fileName);
				attachmentModel.setFileSize(fileSize);
				attachmentModels.add(attachmentModel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return attachmentModels;
	}

	class AttachOnClickListener implements OnClickListener {
		AttachmentModel attachmentModel;
		ProgressBar progressBar;
		TextView download;
		
		public AttachOnClickListener(AttachmentModel attachmentModel,
				ProgressBar progressBar , TextView download) {
			this.attachmentModel = attachmentModel;
			this.progressBar = progressBar;
			this.download = download;
		}

		@Override
		public void onClick(View v) {
			String downStatus = attachmentModel.getStatus();
			if (downStatus.equals(AttachmentModel.downloaded)){
				String type = attachmentModel.getType();
				if ("png".equals(type)) {
					Intent detailIntent = new Intent();
					detailIntent.putExtra("imagePath",
							attachmentModel.getFilePath());
					detailIntent
							.putExtra("showFlag", false);
					detailIntent
							.putExtra("showTitle", true);
					detailIntent
							.putExtra("filename", attachmentModel.getFileName());
					detailIntent.setClass(context,
							PicutureDetailActivity.class);
					context.startActivity(detailIntent);
				} else if ("pdf".equals(type)) {
					Uri uri = Uri.parse(attachmentModel
							.getFilePath());
					Intent intent = new Intent(context,
							MuPDFActivity.class);
					intent.putExtra("pdfTitle",
							attachmentModel.getFileName());
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					context.startActivity(intent);
				} else if ("txt".equals(type)) {
					Intent detailIntent = new Intent();
					detailIntent.putExtra("txtPath",
							attachmentModel.getFilePath());
					detailIntent.putExtra("txtTitle",
							attachmentModel.getFileName());
					detailIntent.setClass(context,
							TxtDetailActivity.class);
					context.startActivity(detailIntent);
				}
				return;
			} else if (downStatus.equals(AttachmentModel.downloading)){
				Toast.makeText(context, "文件正在下载", Toast.LENGTH_SHORT).show();
				return;
			}
			Dialog dialog = new AlertDialog.Builder(context)
					.setTitle(
							"文件名称：" + attachmentModel.getFileName()
									+ "    文件大小"
									+ attachmentModel.getFileSize() + "Kb")
					.setPositiveButton("打开",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String filePath = attachmentModel
											.getFilePath();
									if (filePath == null) {
										new AttachmentTask(progressBar,
												attachmentModel ,download , true).execute("");
										dialog.dismiss();
										return;
									}
									String type = attachmentModel.getType();
									if ("png".equals(type)) {
										Intent detailIntent = new Intent();
										detailIntent.putExtra("imagePath",
												attachmentModel.getFilePath());
										detailIntent
												.putExtra("showFlag", false);
										detailIntent
												.putExtra("showTitle", true);
										detailIntent
												.putExtra("filename", attachmentModel.getFileName());
										detailIntent.setClass(context,
												PicutureDetailActivity.class);
										context.startActivity(detailIntent);
									} else if ("pdf".equals(type)) {
										Uri uri = Uri.parse(attachmentModel
												.getFilePath());
										Intent intent = new Intent(context,
												MuPDFActivity.class);
										intent.putExtra("pdfTitle",
												attachmentModel.getFileName());
										intent.setAction(Intent.ACTION_VIEW);
										intent.setData(uri);
										context.startActivity(intent);
									} else if ("txt".equals(type)) {
										Intent detailIntent = new Intent();
										detailIntent.putExtra("txtPath",
												attachmentModel.getFilePath());
										detailIntent.putExtra("txtTitle",
												attachmentModel.getFileName());
										detailIntent.setClass(context,
												TxtDetailActivity.class);
										context.startActivity(detailIntent);
									}
									dialog.dismiss();
								}
							})
					.setNegativeButton("下载",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 下载文件
									String filePath = attachmentModel
											.getFilePath();
									if (filePath != null) {
										Toast.makeText(context, "文件已下载，请打开阅读",
												Toast.LENGTH_SHORT).show();
										return;
									}
									new AttachmentTask(progressBar,
											attachmentModel ,download).execute("");
									dialog.dismiss();
								}
							}).create();
			dialog.show();
		}

	};

	class AttachmentTask extends AsyncTask<String, Integer, String> {
		// 刷新进度条
		private ProgressBar progressBar;
		private AttachmentModel attachmentModel;
		private TextView download;
		private boolean open;

		public AttachmentTask(ProgressBar progressBar,
				AttachmentModel attachmentModel , TextView download) {
			this.progressBar = progressBar;
			this.attachmentModel = attachmentModel;
			this.download = download;
		}
		
		public AttachmentTask(ProgressBar progressBar,
				AttachmentModel attachmentModel , TextView download , boolean open) {
			this.progressBar = progressBar;
			this.attachmentModel = attachmentModel;
			this.download = download;
			this.open = open;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// 设置转圈子
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			if (attachmentModel.getStatus().equals(AttachmentModel.downloading)){
				return null;
			} else {
				attachmentModel.setStatus(AttachmentModel.downloading);
			}
			InputStream is = null;
			FileOutputStream output = null;
			String attachmentPath = null;
			String fileName = null;
			try {
				String dir = Environment.getExternalStorageDirectory()
						.getPath() + "/" + TmpConstants.ATTACHMENT_PATH;
				File attachmentDir = new File(dir);
				if (!attachmentDir.exists()) {
					attachmentDir.mkdirs();
				}
				String type = attachmentModel.getType();
				if ("png".equals(type)) {
					fileName = dir + "/" + attachmentModel.getFileId() + ".png";
					attachmentPath = dir + "/" + attachmentModel.getFileId()
							+ "temp" + ".png";
				} else if ("pdf".equals(type)) {
					fileName = dir + "/" + attachmentModel.getFileId() + ".pdf";
					attachmentPath = dir + "/" + attachmentModel.getFileId()
							+ "temp" + ".pdf";
				} else if ("txt".equals(type)) {
					fileName = dir + "/" + attachmentModel.getFileId() + ".txt";
					attachmentPath = dir + "/" + attachmentModel.getFileId()
							+ "temp" + ".txt";
				}
				File attachmentFile = new File(attachmentPath);
				// 如果文件存在则删除文件
				if (attachmentFile.exists()) {
					attachmentFile.delete();
				}
				String url = URL.getDownloadUrl(context,
						attachmentModel.getFileId());
				java.net.URL attachURL = new java.net.URL(url);
				HttpURLConnection connect = (HttpURLConnection) attachURL
						.openConnection();
				int attachSize = connect.getContentLength();
				if (attachSize == 0) {
					return null;
				}
				is = connect.getInputStream();
				output = new FileOutputStream(attachmentPath);
		         // 1K的数据缓冲   
		         byte[] bs = new byte[1024];   
		         // 读取到的数据长度   
		         int len;   
		         // 输出的文件流   
		         // 开始读取   
		         while ((len = is.read(bs)) != -1) {   
		        	 output.write(bs, 0, len);   
		         }  
				output.flush();
				File file1 = new File(attachmentPath);
				File file2 = new File(fileName);
				// 将下载的TEMP文件 重命名为新文件名
				boolean flag = file1.renameTo(file2);
				if (flag){
					return fileName;
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				// 关闭输入流等（略）
				if (output != null) {

					try {
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				attachmentModel.setFilePath(result);
				progressBar.setVisibility(View.GONE);
				attachmentModel.setStatus(AttachmentModel.downloaded);
				download.setText("[已下载]");
				if (open){
					String type = attachmentModel.getType();
					if ("png".equals(type)) {
						Intent detailIntent = new Intent();
						detailIntent.putExtra("imagePath",
								attachmentModel.getFilePath());
						detailIntent
								.putExtra("showFlag", false);
						detailIntent
								.putExtra("showTitle", true);
						detailIntent
								.putExtra("filename", attachmentModel.getFileName());
						detailIntent.setClass(context,
								PicutureDetailActivity.class);
						context.startActivity(detailIntent);
					} else if ("pdf".equals(type)) {
						Uri uri = Uri.parse(attachmentModel
								.getFilePath());
						Intent intent = new Intent(context,
								MuPDFActivity.class);
						intent.putExtra("pdfTitle",
								attachmentModel.getFileName());
						intent.setAction(Intent.ACTION_VIEW);
						intent.setData(uri);
						context.startActivity(intent);
					} else if ("txt".equals(type)) {
						Intent detailIntent = new Intent();
						detailIntent.putExtra("txtPath",
								attachmentModel.getFilePath());
						detailIntent.putExtra("txtTitle",
								attachmentModel.getFileName());
						detailIntent.setClass(context,
								TxtDetailActivity.class);
						context.startActivity(detailIntent);
					}
				}
			}
		}

	}
	
	private class NoticeModuleMessageComparator implements Comparator<NoticeModuleMessage> {
		@Override
		public int compare(NoticeModuleMessage object1, NoticeModuleMessage object2) {
			if (object1.getSendTime() < object2.getSendTime()) {
				return 1;
			}
			return -1;
		}
	}
	
	@Override
	public void notifyDataSetChanged() {
		// 对数据源进行排序
		NoticeModuleMessageComparator comparator = new NoticeModuleMessageComparator();
		Collections.sort(noticeModules, comparator);
		super.notifyDataSetChanged();
	}
}
