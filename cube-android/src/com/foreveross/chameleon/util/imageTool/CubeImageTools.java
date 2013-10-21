package com.foreveross.chameleon.util.imageTool;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CubeImageTools {
	
	/**
	 * @param �????��?��????大�? ????? ????????itmap ???2 缩�?????��?宽度�????3 缩�?????��?�?��
	 * @return �??缩�????BitmapDrawable对象
	 * */
	public static BitmapDrawable resizeImage(Bitmap bitmap, int w, int h) {

        // load the origial Bitmap
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                        height, matrix, true);
        // make a Drawable from Bitmap to allow to set the Bitmap
        // to the ImageView, ImageButton or what ever
        return new BitmapDrawable(resizedBitmap);
	}
	
	/***
	 * @param ???�??id?��?�???��??????????? �??View �???? ?��?�??id
	 * @return �??bitmap对象
	 * */
	public static Bitmap getBitmapFormID(Resources res,int id){
		Bitmap tmp;
		tmp=((BitmapDrawable)res.getDrawable(id)).getBitmap();
		return tmp;
	}
	
	/**?��?????��????�?*/
    public static Bitmap getRoundBitmap(Bitmap bitmap,float roundPx){  
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
        final int color = 0xff424242;  
        final Paint paint = new Paint();  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        final RectF rectF = new RectF(rect);  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
        return output;  
    }   
    
    /**?��?�??影�??��??��?*/  
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){  
        final int reflectionGap = 4;  
        int width = bitmap.getWidth();  
        int height = bitmap.getHeight();  
        Matrix matrix = new Matrix();  
        matrix.preScale(1, -1);  
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,0, height/2, width, height/2, matrix, false);  
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);  
        Canvas canvas = new Canvas(bitmapWithReflection);  
        canvas.drawBitmap(bitmap, 0, 0, null);  
        Paint deafalutPaint = new Paint();  
        canvas.drawRect(0, height,width,height + reflectionGap,deafalutPaint);  
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);  
        Paint paint = new Paint();  
        LinearGradient shader = new LinearGradient(0,bitmap.getHeight(), 0, bitmapWithReflection.getHeight()  
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);  
        paint.setShader(shader);  
        // Set the Transfer mode to be porter duff and destination in  
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
        // Draw a rectangle using the paint with our linear gradient  
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);  
        return bitmapWithReflection;  

    }
    
    public static Bitmap drawableToBitmap(Drawable drawable){  
    	if(null==drawable){
			return null;
		}
        int width = drawable.getIntrinsicWidth();  
        int height = drawable.getIntrinsicHeight();  
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE?Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565);  
        Canvas canvas = new Canvas(bitmap);  
        drawable.setBounds(0,0,width,height);  
        drawable.draw(canvas);  
        return bitmap;  
    }  
}
