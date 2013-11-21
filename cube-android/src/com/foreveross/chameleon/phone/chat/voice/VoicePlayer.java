/*
 * 文件名: VoicePlayer.java
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
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;


/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 杨兴朗
 * @version [RCS Client V100R001C03, 2012-8-9]
 */
public class VoicePlayer {

	private static final String TAG = VoicePlayer.class.getSimpleName();

	private MediaPlayer mMediaPlayer;

	private CompletionListener listener;
	

	public VoicePlayer() {
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		} catch (Exception e) {
			Log.e(TAG, "init player error");
		}
	}

	public void setListener(CompletionListener listener) {
		this.listener = listener;
	}

	/**
	 * 根据文件路径播放声音<BR>
	 * [功能详细描述]
	 * 
	 * @param filePath
	 */
	public boolean playByPath(String filePath) {
//		System.out.println("播放====" + filePath);
		File file = new File(filePath);

		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(filePath);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
//							System.out.println("播放完");
							listener.onCompletion();
						}
					});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 暂停播放 [一句话功能简述]<BR>
	 * [功能详细描述]
	 */
	public void pause() {
		if (null != mMediaPlayer) {
			mMediaPlayer.pause();
		}
	}

	/**
	 * 停止播放 [一句话功能简述]<BR>
	 * [功能详细描述]
	 */
	public void stop() {
		if (null != mMediaPlayer) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public synchronized static String readTimeForUri(Activity context,
			String uri) {
		String totalTime = null;
		if (!uri.startsWith("/mnt")) {
			uri = "/mnt/" + uri;
		}
		ContentResolver cr = context.getContentResolver();

		Cursor c = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media.DURATION },
				MediaStore.Audio.Media.DATA + "= '" + uri + "'", null, null);

		if (c != null && c.moveToFirst()) {
			// 得到歌曲时分少
			int int_TotalTime = c.getInt(c
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
			int_TotalTime /= 1000;
			totalTime = int_TotalTime / 60 + "'" + int_TotalTime % 60 + "\"";
//			System.out.println(" int_TotalTime+" + int_TotalTime + "   ="
//					+ totalTime);
		}
		return totalTime;
	}

	public interface CompletionListener {
		void onCompletion();
	}
}