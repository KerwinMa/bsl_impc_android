package com.foreveross.chameleon.util.imageTool;

import android.graphics.Bitmap;

/***
 * 
 * @author 
 *
 */
public class SyncImageItem 
{
	public enum SyncImageItemState
	{
		UNKNOW,
		LOADING,
		FAILED,
		SUCCESS
	}
	private Bitmap mBitmap;
	private SyncImageItemState mState;
	
	
	public SyncImageItem()
	{
		mBitmap=null;
		mState=SyncImageItemState.UNKNOW;
	}
	
	public SyncImageItem(Bitmap bm)
	{
		this();
		mBitmap=bm;
		if(null!=mBitmap)
			mState=SyncImageItemState.SUCCESS;
	}
	
	public void setBitmap(Bitmap bm)
	{
		mBitmap=bm;
	}
	
	public void setState(SyncImageItemState state)
	{
		mState=state;
	}
	
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	public SyncImageItemState getState()
	{
		return mState;
	}
	
}
