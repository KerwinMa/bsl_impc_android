package com.foreveross.chameleon.phone.modules;

import java.io.Serializable;

public class AttachmentModel  implements Serializable {

	private static final long serialVersionUID = 28849347123752893L;
	
	public static String downloading = "1";
	public static String downloaded = "2";
	public static String notdownload = "3";
	
	private String fileId;
	private String fileName;
	private String fileSize;
	private String type;
	private String filePath;
	
	private String status;
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
