/*
 * 文件名: FileCacheUtil.java
 * 版    权：  Copyright Administrator Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 杨兴朗
 * 创建时间:2012-8-10
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.foreveross.chameleon.phone.chat.voice;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 声音文件存储大小控制<BR>
 * <h2>可以设置SD卡剩余多少时执行清理 </h2></br>
 * @code
 * FREE_SD_SPACE_NEEDED_TO_CACHE = 10; //当SD卡剩余少于10MB的时候会执行清除
 * @endcode
 * 可以设置存放录音文件大于多少时执行清理
 * @code
 * CACHE_SIZE = 10; //当存放录音文件大于10MB的时候会执行清除
 * @endcode
 * 计算存储目录下的文件大小，
 * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定 *
 * 那么删除40%最近没有被使用的文件 ,应用启动时调用下面code即可
 * @code
 * new RecordCacheUtil().removeCache(RecordUtil.getVoiceDefaultPath());
 * @endcode
 * @author 杨兴朗
 * @version [RCS Client V100R001C03, 2012-8-10] 
 */
public class RecordCacheUtil {
    
    private static final String TAG = RecordCacheUtil.class.getSimpleName();
    
    /**
     * 1MB = 1024*1024B
     */
    private static final int MB = 1024 * 1024;
    
    /**
     * SD卡剩余空间值
     */
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
    
    /**
     * 缓存大小
     */
    private static final int CACHE_SIZE = 10;
    
    
    public RecordCacheUtil() {
        
    }

    /**
     * 计算存储目录下的文件大小，
     * 当文件总大小大于规定的CACHE_SIZE或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定 *
     * 那么删除40%最近没有被使用的文件 *
     * 
     * @param dirPath
     */
    public void removeCache(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        int dirSize = 0;
        for (int i = 0; i < files.length; i++) {
            dirSize += files[i].length();
        }
        if (dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            int removeFactor = (int) ((0.4 * files.length) + 1);
            Arrays.sort(files, new FileLastModifSort());
            Log.d(TAG, "Clear some expiredcache files ");
            for (int i = 0; i < removeFactor; i++) {
                files[i].delete();
            }
        }
    }
    
    /**
     * @Description:计算sdcard上的剩余空间
     * @return MB
     */
    private int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }
    
    
    /**
     * 根据文件的最后修改时间进行排序<BR>
     * [功能详细描述]
     * @author 杨兴朗
     * @version [RCS Client V100R001C03, 2012-8-10] 
     */
    class FileLastModifSort implements Comparator<File> {
        public int compare(File arg0, File arg1) {
            if (arg0.lastModified() > arg1.lastModified()) {
                return 1;
            } else if (arg0.lastModified() == arg1.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
