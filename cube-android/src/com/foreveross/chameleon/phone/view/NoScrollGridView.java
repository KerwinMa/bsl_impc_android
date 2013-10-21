package com.foreveross.chameleon.phone.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class NoScrollGridView extends GridView {
	
	
	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/**  
     * 设置不滚动  
     */  
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)   
    {   
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,   
                MeasureSpec.AT_MOST);   
        super.onMeasure(widthMeasureSpec, expandSpec);   
  
    }   
    
//    @Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//    	super.onInterceptTouchEvent(ev);
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			int x = (int) ev.getX();
//			int y = (int) ev.getY();
//			int itemnum = pointToPosition(x, y);
//			if (itemnum == AdapterView.INVALID_POSITION) {
//				break;
//			} else {
//				setSelector(R.drawable.listview_click_cell);
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			setSelector(new ColorDrawable(Color.TRANSPARENT));
//			break;
//		}
//		return false;
//	}
}
