package com.foreveross.chameleon.pad.fragment;

import android.support.v4.app.FragmentActivity;

public class ParentDroidFragment extends DroidGapFragment {

	@Override
	public FragmentActivity getActivity() {
		return getAssocActivity();
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		this.closableWindow.emptyBG();

	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName();
	}
	
}
