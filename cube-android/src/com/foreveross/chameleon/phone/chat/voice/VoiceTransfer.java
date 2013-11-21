/*
 * 文件名: VoiceTransfer.java
 * 版    权：  Copyright Administrator Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨兴朗
 * 创建时间:2012-8-9
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.foreveross.chameleon.phone.chat.voice;

import java.io.File;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import android.util.Log;

/**
 * 语音文件传输<BR>
 * [功能详细描述]
 * 
 * @version [RCS Client V100R001C03, 2012-8-9]
 */
public class VoiceTransfer {
	private static final String TAG = VoiceTransfer.class.getSimpleName();

	private static VoiceTransfer instance;

	private ReceiverVoiceListener voiceListener;

	private VoiceTransfer() {

	}

	/**
	 * 单例<BR>
	 * [功能详细描述]
	 * 
	 * @return
	 */
	public synchronized static VoiceTransfer getInstance() {
		if (null == instance) {
			instance = new VoiceTransfer();
		}
		return instance;
	}

	/**
	 * 发送文件<BR>
	 * [功能详细描述]
	 * 
	 * @param [connection] xmpp连接
	 * @param [record] 录音对象
	 * @param [sendVoiceListener] 发送状态回调
	 */
	public synchronized void sendVoice(Connection connection,
			String receiverJid, Record record,
			final SendVoiceListener sendVoiceListener) {

		String userIDres = receiverJid + "/AMPClient";
		Log.d(TAG, "voicePath=" + record.getFilePath());
		Log.d(TAG, "userIDres=" + userIDres);
		FileTransferManager ftManager = new FileTransferManager(connection);

		final OutgoingFileTransfer transfer = ftManager
				.createOutgoingFileTransfer(userIDres);
		try {
			// String fileName, long fileSize,
			// String description
			File file = new File(record.getFilePath());
			String fileName = record.getFilePath();
			fileName = fileName.substring((fileName.lastIndexOf("/") + 1));
			transfer.sendFile(file, String.valueOf(record.getTotalTime()));
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						Log.d(TAG, "发送进度：" + transfer.getProgress());
						if (null != sendVoiceListener) {
							sendVoiceListener.progress(transfer.getStatus(),
									(int) (transfer.getProgress() * 100));
						}
						if (FileTransfer.Status.complete.equals(transfer
								.getStatus())) {
							break;
						} else if (FileTransfer.Status.cancelled
								.equals(transfer.getStatus())
								|| FileTransfer.Status.error.equals(transfer
										.getStatus())) {
							break;
						}
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}
					}

				}
			}).start();
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置监听文件传输<BR>
	 * [功能详细描述]
	 * 
	 * @param connection
	 */
	public synchronized void setReceiverVoiceListener(Connection connection) {
		// 创建文件传输管理
		final FileTransferManager manager = new FileTransferManager(connection);

		// 创建监听
		manager.addFileTransferListener(new FileTransferListener() {

			public void fileTransferRequest(final FileTransferRequest request) {
				// 监查此请求是否应该被接受
				// 接受
				new Thread(new Runnable() {

					@Override
					public void run() {
						if (!RecordUtil.hasVoiceDefaultPath()) {
							// 如果不存在SD卡则暂时不接收了
							return;
						}
						IncomingFileTransfer transfer = request.accept();
						String requestor = request.getRequestor();
						try {
							String fileName = request.getFileName();
							// System.out.println("fileName=" + fileName);
							fileName = RecordUtil.getVoiceDefaultPath()
									+ fileName;
							// System.out.println("voicePath=" + fileName);
							transfer.recieveFile(new File(fileName));
							while (true) {
								try {
									Thread.sleep(350);
									if (FileTransfer.Status.complete
											.equals(transfer.getStatus())) {
										// 下载完成，通知界面更新
										long totalTime = 0;
										try {
											totalTime = Long.parseLong(request
													.getDescription());
										} catch (NumberFormatException e) {
											e.printStackTrace();
										}
										if (null != voiceListener) {

											requestor = StringUtils
													.parseBareAddress(requestor);
											voiceListener.completeTransfer(
													requestor, new Record(
															fileName,
															totalTime,
															"complete"));
										}
										break;
									} else if (FileTransfer.Status.cancelled
											.equals(transfer.getStatus())
											|| FileTransfer.Status.error
													.equals(transfer
															.getStatus())) {
										break;
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
									break;
								}
							}
						} catch (XMPPException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}
		});
	}

	public void setVoiceListener(ReceiverVoiceListener voiceListener) {
		this.voiceListener = voiceListener;
	}

}
