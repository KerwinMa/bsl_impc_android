package com.foreveross.chameleon.phone.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;

public class MultiAccountActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiaccount);
		Button mulitaccount_ok = (Button) findViewById(R.id.mulitaccount_ok);
		mulitaccount_ok.setOnClickListener(this);
		setFinishOnTouchOutside (false); 

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.mulitaccount_ok){
			Application.class
			.cast(this.getApplicationContext())
			.logOff();
		}
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return true;
	}
}
