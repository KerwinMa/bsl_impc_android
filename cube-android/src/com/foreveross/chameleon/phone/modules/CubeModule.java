package com.foreveross.chameleon.phone.modules;

import java.io.Serializable;
import java.util.List;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.MessageCountChangeEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CubeModule implements Serializable {

	public static final int UNINSTALL = 0;
	public static final int INSTALLING = 1;
	public static final int INSTALLED = 2;
	public static final int DELETING = 3;
	public static final int UPGRADABLE = 4;
	public static final int UPGRADING = 5;
	public static final int PREINSTALL = 6;

	public CubeModule() {
		// EventBus.getInstallFinishEventBus().register(this);
	}

	private static final long serialVersionUID = 28869347123752893L;

	private String identifier = null;

	private String icon = null;

	private String name = null;

	private String version = null;

	private int build = 0;

	private String category = null;

	private String downloadUrl = null;

	private String releaseNote = null;

	private boolean updatable = false;

	private String bundle = null;

	private int moduleType = -1;// 是否安装了

	private String local = null; // 指向本地

	private boolean hidden = false;

	private int pushMsgLink = 1;

	private int noticeCount = 0;
	private int msgCount = 0;

	private String timeUnit; // 默认为空，可输入“H”、“M”、“S”
	private String showIntervalTime;// 时间间隔
	private boolean isAutoShow;// 是否自动弹出

	private int sortingWeight;// 权重
	private boolean autoDownload;// 是否自动下载
	private int showPushMsgCount;

	private String installIcon;// 保存icon的网络地址

	public String getInstallIcon() {
		return installIcon;
	}

	public void setInstallIcon(String installIcon) {
		this.installIcon = installIcon;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getShowIntervalTime() {
		return showIntervalTime;
	}

	public void setShowIntervalTime(String showIntervalTime) {
		this.showIntervalTime = showIntervalTime;
	}

	public int getSortingWeight() {
		return sortingWeight;
	}

	public boolean isAutoShow() {
		return isAutoShow;
	}

	public void setAutoShow(boolean isAutoShow) {
		this.isAutoShow = isAutoShow;
	}

	public void setSortingWeight(int sortingWeight) {
		this.sortingWeight = sortingWeight;
	}

	public boolean isAutoDownload() {
		return autoDownload;
	}

	public void setAutoDownload(boolean autoDownload) {
		this.autoDownload = autoDownload;
	}

	private boolean displayBadge = true;

	public boolean isDisplayBadge() {
		return displayBadge;
	}

	public void setDisplayBadge(boolean displayBadge) {
		this.displayBadge = displayBadge;
	}

	public void increaseMsgCountBy(int inCount) {
		msgCount = msgCount + inCount;
		notifyCountChange();
	}

	public void increaseMsgCount() {
		msgCount++;
		notifyCountChange();
	}

	public void decreaseMsgCountBy(int deCount) {
		msgCount = msgCount - deCount;
		msgCount = msgCount < 0 ? 0 : msgCount;
		notifyCountChange();
	}

	public void decreaseMsgCount() {
		msgCount--;
		msgCount = msgCount < 0 ? 0 : msgCount;
		notifyCountChange();
	}

	private String privileges;

	public boolean isUpdatable() {
		return updatable;
	}

	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}

	public int getModuleType() {
		return moduleType;
	}

	public void setModuleType(int moduleType) {
		// if(moduleType == INSTALLED){
		// EventBus.getInstallFinishEventBus().post(new FinishEvent(this));
		// }
		this.moduleType = moduleType;
	}

	// @Subscribe
	// public void onFinishEvent(FinishEvent sourceEvent){
	// if(sourceEvent.getSource().getIdentifier().equals(this.getIdentifier())){
	// this.setModuleType(INSTALLED);
	// }
	// }
	// *************************************//
	// private boolean isDownloading = false;

	private int progress = 0;

	public String getCategory() {
		if (category == null) {
			return "基本功能";
		}
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		// 本地模块，需要特殊处理
		// if(this.getLocal()!=null){
		// this.icon = "";
		// }else{

		// }
		this.icon = icon;
	}

	@Override
	public int hashCode() {
		return (identifier + build).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof CubeModule) {
			return ((CubeModule) o).getIdentifier().equals(identifier)
					&& ((CubeModule) o).getBuild() == build;
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return identifier + " " + version + " " + build +" "+name+" "+category +" "+ downloadUrl;
	}

	/**
	 * 从json字符串构建出模块对象
	 */
	public static CubeModule buildModule(String json) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		CubeModule result = gson.fromJson(json, CubeModule.class);
		return result;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getReleaseNote() {
		return releaseNote;
	}

	public void setReleaseNote(String releaseNote) {
		this.releaseNote = releaseNote;
	}

	// public boolean isDownloading() {
	// return isDownloading;
	// }
	//
	// public void setDownloading(boolean isDownloading) {
	// this.isDownloading = isDownloading;
	// }

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getNoticeCount() {
		return noticeCount;
	}

	public void setNoticeCount(int noticeCount) {
		this.noticeCount = noticeCount;
	}

	public String getPrivileges() {
		return privileges;
	}

	public void setPrivileges(String privileges) {
		this.privileges = privileges;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		if(this.msgCount != msgCount){
			this.msgCount = msgCount;
			notifyCountChange();
		}
	}

	public int getPushMsgLink() {
		return pushMsgLink;
	}

	public int getShowPushMsgCount() {
		return showPushMsgCount;
	}

	public void setShowPushMsgCount(int showPushMsgCount) {
		this.showPushMsgCount = showPushMsgCount;
	}

	public void setPushMsgLink(int pushMsgLink) {
		this.pushMsgLink = pushMsgLink;
	}

	private transient CountChangeListener countChangeListener = new CountChangeListener() {

		@Override
		public void onCountChange(int count, boolean displayBadge) {
			EventBus.getEventBus(TmpConstants.EVENTBUS_MESSAGE_COUNT).post(
					new MessageCountChangeEvent(
							CubeModule.this.getIdentifier(), count,
							displayBadge));
			
		}
	};

	public void setCountChangeListener(CountChangeListener countChangeListener) {
		this.countChangeListener = countChangeListener;
	}

	public void notifyCountChange() {
		if (countChangeListener != null) {
			countChangeListener.onCountChange(msgCount, displayBadge);
		}
	}
	
	
	

}
