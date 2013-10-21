/*
 * 文件名: Record.java
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

/**
 * 录音实体<BR>
 * [功能详细描述]
 * @author 杨兴朗
 * @version [RCS Client V100R001C03, 2012-8-9] 
 */
public class Record {
    
	
    /**
     * 录音文件路径 <br/>
     * 格式一般为:/mnt/sdcard/amp/voice/1344475029747.amr
     */
    private String filePath;
    
    /**
     * 录音时间 单位为秒
     */
    private long totalTime;
    
    /**
     * 录音结束时返回的原因，提示信息
     */
    private String reason;
    
    
    public Record(String filePath, long totalTime, String reason) {
        super();
        this.filePath = filePath;
        this.totalTime = totalTime;
        this.reason = reason;
    }

    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public long getTotalTime() {
        return totalTime;
    }
    
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Record [filePath=" + filePath + ", totalTime=" + totalTime
                + ", reason=" + reason + "]";
    }
    
}
