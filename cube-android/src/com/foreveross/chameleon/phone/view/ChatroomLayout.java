package com.foreveross.chameleon.phone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class ChatroomLayout extends LinearLayout {
	TouchCallback touchCallback;
	
	public ChatroomLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setTouchCallback(TouchCallback touchCallback){
		this.touchCallback = touchCallback;
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(touchCallback!=null){
			touchCallback.onTouchCallbackEvent();
		}
		return super.dispatchTouchEvent(ev);
	}
	
	public interface TouchCallback{
		public void onTouchCallbackEvent();
	}
	
}
