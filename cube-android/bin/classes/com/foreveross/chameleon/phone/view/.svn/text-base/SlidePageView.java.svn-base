package com.foreveross.chameleon.phone.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SlidePageView extends LinearLayout {

	private int mCurrentScreen = 0; 

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	public static int SNAP_VELOCITY = 600;
	private int mTouchSlop = 0;
	private float mLastionMotionX = 0;
	private float mLastMotionY = 0;
	private final static int SLOW_SPEED = 0;

	private VelocityTracker mVelocityTracker = null;
	private Scroller mScroller = null;

	private OnPageChangedListener onPageChangedListener;

	public void setOnPageChangedListener(
			OnPageChangedListener pageChangedListener) {
		this.onPageChangedListener = pageChangedListener;
	}

	public SlidePageView(Context context) {
		super(context);
		initParams();
	}

	public SlidePageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParams();

	}

	public void initParams() {
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public void setCurrentScreen(int mCurrentScreen) {
		this.mCurrentScreen = mCurrentScreen;
	}

	public int getCurrentScreen() {
		return mCurrentScreen;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		snapToScreen(getCurrentScreen(), SLOW_SPEED);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastionMotionX - x);
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastionMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;

			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}

		mVelocityTracker.addMovement(event);

		super.onTouchEvent(event);

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mScroller != null) {
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
			}
			mLastionMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			int detaX = (int) (mLastionMotionX - x);
			scrollBy(detaX, 0);
			mLastionMotionX = x;
			break;
		case MotionEvent.ACTION_UP:

			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);

			int velocityX = (int) velocityTracker.getXVelocity();

			if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
				snapToScreen(mCurrentScreen - 1, velocityX);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurrentScreen < (getVisbleChildCount() - 1)) {
				snapToScreen(mCurrentScreen + 1, velocityX);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			mTouchState = TOUCH_STATE_REST;

			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return true;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	private void snapToDestination() {

		int targetScreen = getCurrentScreen();
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View child = getChildAt(i);
			if ((child.getLeft() - getScrollX() <= getWidth() / 2)
					&& (child.getRight() - getScrollX() >= getWidth() / 2)) {
				targetScreen = i;
				break;
			}

		}
		snapToScreen(targetScreen, SLOW_SPEED);

	}

	/**
	 * scroll to target screen
	 * 
	 * @param targetScreen
	 * @param speed
	 *            unit pix/second
	 */
	private void snapToScreen(int targetScreen, int speed) {

		mCurrentScreen = targetScreen;
		if (mCurrentScreen > getChildCount() - 1) {
			mCurrentScreen = getChildCount() - 1;
		}
		View currentView = getChildAt(mCurrentScreen);
		if (currentView == null)
			return;
		Point centerPoint = getViewCenterPoint(currentView);
		int deltaX = centerPoint.x - getWidth() / 2 - getScrollX();
		int deltaY = 0;
		int duration;
		if (speed == SLOW_SPEED)
			duration = Math.abs(2 * deltaX);
		else
			duration = (int) Math.abs(deltaX / ((float) speed / 1000));
		this.mScroller.startScroll(getScrollX(), 0, deltaX, deltaY, duration);
		invalidate();
		if (onPageChangedListener != null)
			onPageChangedListener
					.onPageViewChanged(currentView, mCurrentScreen);

	}

	/**
	 * @param view
	 * @return the center point of this view
	 */
	private Point getViewCenterPoint(View view) {

		int left = view.getLeft();
		int right = view.getRight();
		int top = view.getTop();
		int bottom = view.getBottom();
		int x = left + (right - left) / 2;
		int y = left + (bottom - top) / 2;
		Point point = new Point(x, y);
		return point;
	}
	
	private int getVisbleChildCount(){
		
		int count = 0;
		
		for(int i=0;i<getChildCount();i++){
			if(getChildAt(i).getVisibility()==View.VISIBLE){
				count++;
			}
		}
		return count;
	}

	public static interface OnPageChangedListener {

		public void onPageViewChanged(View view, int currentScreen);

	}

}
