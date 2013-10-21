package com.foreveross.chameleon.pad.component;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NativeViewFragment extends Fragment {
	public NativeViewFragment() {

	}

	private View contentView;

	public void init(View contentView) {
		this.contentView = new ClosableWindow(this.getAssocActivity(),
				contentView);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return contentView;
	}

}
