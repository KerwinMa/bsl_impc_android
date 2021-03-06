package com.foreveross.chameleon.phone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

import com.csair.impc.R;
import com.foreveross.chameleon.phone.modules.CubeListViewAdapter;

public class NoScrollListView extends ListView {

	public NoScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION) {
				break;
			} else {
				if (itemnum == 0) {
					if (itemnum == (getAdapter().getCount() - 1)) {
						// 只有一项
						setSelector(R.drawable.listview_click_top);
					} else {
						// 第一项
						setSelector(R.drawable.listview_click_top);
					}
				} else if (itemnum == (getAdapter().getCount() - 1))
					// 最后一项
					setSelector(R.drawable.listview_click_bottom);
				else {
					// 中间一项
					setSelector(R.drawable.listview_click_cell);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	protected void layoutChildren() {
		try{
			super.layoutChildren();
		}catch(IllegalStateException e){
			CubeListViewAdapter.class.cast(this.getAdapter()).notifyDataSetChanged();
		}
	}

}
