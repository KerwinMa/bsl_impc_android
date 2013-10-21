package com.foreveross.chameleon.phone.activity;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Player
{
    
    private static MediaPlayer player = new MediaPlayer();
    
    public static boolean isStop = true;
    
    public static Handler handler = new Handler()
    {
        /**
         * 重载方法
         * 
         * @param msg
         */
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 1)
            {
                Log.i("KKK", "销毁MediaPlayer");
                // if (isStop == true && !player.isPlaying())
                // {
                // player.stop();
                Log.i("KKK", "销毁MediaPlayer2");
                player.reset();
                Log.i("KKK", "销毁MediaPlayer3");
                // player.release();
                // }
            }
            super.handleMessage(msg);
        }
    };
    
    public Player()
    {
        // player = new MediaPlayer();
    }
    
    /* 播放制定路径下的文件 */
    public void playMusic(String path)
    {
        
        try
        {
            player.reset();
            player.setDataSource(path);
            player.prepare();
            player.start();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
            Log.e("voice","IllegalStateException");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("voice","IOException");
        }
        
    }
}
