package com.foreveross.chameleon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

public class CommonUtils {

	public static final int MIN_PAD_WIDTH = 0;
	public static final int MIN_PAD_HEIGHT = 0;
	public static final int MIN_PAD_SIZE = 0;

	public static boolean OpenRotation(Context context)
	{
		int flag = Settings.System.getInt(context.getContentResolver(),
		        Settings.System.ACCELEROMETER_ROTATION, 0);
		    if (0 == flag) {
		      Settings.System.putInt(context.getContentResolver(),
		          Settings.System.ACCELEROMETER_ROTATION, 1);
		    }
		    return true;
	}
	
	
	
	public static boolean isPad(Context applicationContext) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) applicationContext
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		// int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		double diagonalPixels = Math.sqrt(Math.pow(screenWidth, 2)
				+ Math.pow(screenHeight, 2));
		double screenSize = diagonalPixels / (160 * density);
		return screenSize > MIN_PAD_SIZE;
	}


	public static String dateTime(long milis, String pattern) {
		return new SimpleDateFormat(pattern).format(new Date(milis));
	}

	private static Gson gson = new Gson();

	public static Gson getGson() {
		return gson;
	}

}
