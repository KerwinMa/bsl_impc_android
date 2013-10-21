package com.foreveross.chameleon.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.csair.impc.R;

public class MyRadioButton extends RadioButton{
	//设置DrawableTop
	Drawable top;
	//分割线
	Bitmap line;
	Context context;
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint p = new Paint();
		
		if(top!=null){
			//top.draw(canvas);
		//	BitmapDrawable bit = (BitmapDrawable) top;
			Bitmap bitmap = drawabletoBitmap(top);
			canvas.drawBitmap(bitmap, (this.getWidth()-bitmap.getWidth())>>1, (getHeight()-bitmap.getHeight())>>1, p);
			canvas.drawBitmap(line, 0, 0, p);
		}
		
		//super.onDraw(canvas);
	}
	Bitmap drawabletoBitmap(Drawable d){
		int h = d.getIntrinsicHeight();
		int w = d.getIntrinsicWidth();
		Bitmap.Config config = d.getOpacity()!= PixelFormat.OPAQUE?
				Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		d.draw(canvas);
		//Bitmap.createBitmap(source, x, y, width, h, m, filter)
		//Matrix matrix = new Matrix();
		//bitmap.
		int lenght = getHeight();
		int lendip = px2dip(context,lenght);
		if(lendip-10>0){
			lenght =dip2px(context,lendip-10);
		}
		return Bitmap.createScaledBitmap(bitmap, lenght,lenght, true)
				;
	}
	
	
	
	int intrinsicWidth;
	int intrinsicHeight;
	@Override
	public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
			Drawable top, Drawable right, Drawable bottom) {
		this.top = top;
		/*intrinsicWidth=this.top.getIntrinsicWidth();
		intrinsicHeight = this.top.getIntrinsicHeight();
		
		System.out.println("intrinsicWidth="+intrinsicWidth+"___intrinsicHeight="+intrinsicHeight);
	*/
		super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
	}
	
	public MyRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	/*	System.out.println("================MyRadioButton:"+this.getHeight());
		int cont = attrs.getAttributeCount();
		for(int i = 0;i<cont;i++){
			System.out.println("                getAttributeName:"+attrs.getAttributeName(i));
			System.out.println("                getAttributeValue:"+attrs.getAttributeValue(i));
			if(attrs.getAttributeName(i).endsWith("layout_height")){
				height = attrs.getAttributeIntValue(i, 20);
				System.out.println("================MyRadioButton_height:"+height);
				
			}
		}*/
		this.context = context;
		line = BitmapFactory.decodeResource(getResources(), R.drawable.limit);
	}
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
	    final float scale = context.getResources().getDisplayMetrics().density;
	    return (int) (dpValue * scale + 0.5f);
	}
	 
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
	    final float scale = context.getResources().getDisplayMetrics().density;
	    return (int) (pxValue / scale + 0.5f);
	}
}
