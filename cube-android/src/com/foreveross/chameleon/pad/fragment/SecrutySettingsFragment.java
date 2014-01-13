/**
 * 
 */
package com.foreveross.chameleon.pad.fragment;

import com.csair.impc.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * @author zj
 *
 */
public class SecrutySettingsFragment extends Fragment {
	private EditText day_time;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.secruty_settings, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		day_time = (EditText) view.findViewById(R.id.day_time);
	}
	
}
