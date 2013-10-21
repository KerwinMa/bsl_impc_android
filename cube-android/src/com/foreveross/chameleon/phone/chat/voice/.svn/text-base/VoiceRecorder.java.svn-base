/*
 * 文件名: VoiceRecord.java
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

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;


/**
 * 负责录音<BR>
 * [功能详细描述]
 * @author 杨兴朗
 * @version [RCS Client V100R001C03, 2012-8-8] 
 */
public class VoiceRecorder {
    private static final String TAG = VoiceRecorder.class.getSimpleName();
    
    /**
     * 录音最长时间
     */
    public static final int MAX_RECORD_TIME = 60 * 1000;
    
    /**
     * 系统录音媒体库
     */
    private MediaRecorder mMediaRecorder;
    
    /**
     * 当前录音文件
     */
    private String currentVoicePath;
    
    /**
     * 初始化录音功能是否成功，是否存在SD卡，是否支持录音等
     */
    private boolean initSuccess;
    
    /**
     * 录音回调接口
     */
    private RecordListener recordListener;
    
    /**
     * 录音计时线程
     */
    private Thread tickThread;
    
    /**
     * 录音开始时间
     */
    private long startRecordTime;
    
    /**
     * 录音结束时间
     */
    private long stopRecordTime;
    
    public VoiceRecorder() {
        
    }

    
    public VoiceRecorder(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    /**
     * 停止录音<BR>
     * [功能详细描述]
     * @return
     */
    public boolean stopRecord(){
        if(initSuccess == false){
            return false;
        }
        //stopRecordTime += MAX_RECORD_TIME; 
        if(tickThread.isAlive()){
            tickThread.interrupt();
        }
        mMediaRecorder.stop();
        mMediaRecorder.release();
        return true;
    }
    
    /**
     * 开始录音<BR>
     * 录音时长不能超过60秒
     * @return
     */
    public boolean startRecord(){
        try {
            getMediaRecorder();
            if(initSuccess == false){
                return false;
            }
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            tickThread = new Thread() {
                
                @Override
                public void run() {
                    while ((startRecordTime + MAX_RECORD_TIME) > stopRecordTime) {
                        try {
                            sleep(1000);
                            stopRecordTime = System.currentTimeMillis();
                            if(null != recordListener){
                                recordListener.recording((stopRecordTime - startRecordTime)/1000);
                            }
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                            if(null != recordListener){
                                stopRecordTime = System.currentTimeMillis();
                                recordListener.finishRecord(new Record(currentVoicePath, (stopRecordTime - startRecordTime)/1000, "录音正常结束."));
                                return;
                            }
                        }
                    }
                    if(null != recordListener){
                        stopRecordTime = System.currentTimeMillis();
                        recordListener.finishRecord(new Record(currentVoicePath, (stopRecordTime - startRecordTime)/1000, "录音时长超过60秒."));
                    }
                }
                
            };
            tickThread.start();
            return initSuccess;
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return initSuccess;
    }
    
    /**
     * 获取最后一个录音文件位置<BR>
     * [功能详细描述]
     * @return
     */
    public String getCurrentVoicePath() {
        return currentVoicePath;
    }

    /**
     * 录音中状态监听<BR>
     * [功能详细描述]
     * @param recordListener
     */
    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }


    /**
     * 创建录音媒体对象，并设置录音参数
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     */
    private MediaRecorder getMediaRecorder() {
        currentVoicePath = null;
        getAudioFilePath();
        if(false == initSuccess){
            Log.i(TAG, "初始化录音参数失败：currentVoicePath为空");
            return null;
        }
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mMediaRecorder.setOutputFile(currentVoicePath);
        
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        return mMediaRecorder;
    }
    
    /**
     * 获得录音文件路径
     * [一句话功能简述]<BR>
     * [功能详细描述]
     * @return
     */
    private String getAudioFilePath() {
        if(!RecordUtil.hasVoiceDefaultPath()){
            initSuccess = false;
            return null;
        }
        startRecordTime = System.currentTimeMillis();
        File file = new File(RecordUtil.getVoiceDefaultPath() + startRecordTime
                + ".amr");
        currentVoicePath = file.getPath();
        Log.d(TAG, "mAudioFilePath=" + currentVoicePath);
        initSuccess = true;
        return currentVoicePath;
    }
    
    public int getAmplitude(){
        if(null != mMediaRecorder){
            return mMediaRecorder.getMaxAmplitude();
        }
        return 0;
    }
}
