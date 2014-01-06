package com.foreveross.chameleon.phone.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;

public class SettingsActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity_layout);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(Application.isSettingOn)
		{
			Application.isSettingOn = false;
			finish();
		}
	}
	
}
