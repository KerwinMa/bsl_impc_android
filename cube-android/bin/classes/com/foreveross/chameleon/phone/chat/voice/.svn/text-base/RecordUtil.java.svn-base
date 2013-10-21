/*
 * 文件名: RecordUtil.java
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

import android.os.Environment;
import android.util.Log;


/**
 * 录音相关工具方法<BR>
 * [功能详细描述]
 * @author 杨兴朗
 * @version [RCS Client V100R001C03, 2012-8-9] 
 */
public final class RecordUtil {
    private static final String TAG = RecordUtil.class.getSimpleName();
    
    private static String voiceDefaultPath;
    
    /**
     * 判断是否存在目录
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     */
    public static boolean hasVoiceDefaultPath() {
        if (!RecordUtil.existSDCard()) {
            return false;
        }
        File filePath = new File(RecordUtil.getVoiceDefaultPath());
        if (!filePath.exists()) {
            return filePath.mkdirs();
        }
        return true;
    }
    
    /**
     * 录音目录<BR>
     * [功能详细描述]
     * @return
     */
    public static String getVoiceDefaultPath() {
        if (null == voiceDefaultPath) {
            voiceDefaultPath = Environment.getExternalStorageDirectory()
                    .getPath() + "/amp/voice/";
        }
        return voiceDefaultPath;
    }
    
    /**
     * sd卡是否存在，可读写<BR>
     * [功能详细描述]
     * @return
     */
    public static boolean existSDCard() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.i(TAG, "没有加载SD卡");
            return false;
        }
        return true;
    }
    
}
