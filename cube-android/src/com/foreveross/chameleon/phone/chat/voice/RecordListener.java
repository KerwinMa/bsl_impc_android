/*
 * 文件名: RecordListener.java
 * 版    权：  Copyright Administrator Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨兴朗
 * 创建时间:2012-8-8
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.foreveross.chameleon.phone.chat.voice;

/**
 * 录音状态监听<BR>
 * [功能详细描述]
 * @author 杨兴朗
 * @version [RCS Client V100R001C03, 2012-8-8] 
 */
public interface RecordListener {
    /**
     * 录音结束<BR>
     * 返回录音对象
     * @param record
     */
    void finishRecord(Record record);
    
    /**
     * 录音中<BR>
     * 返回当前录制了多少秒
     * @param second
     */
    void recording(long second);
}
