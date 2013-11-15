package com.foreveross.chameleon.phone.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.csair.impc.R;
import com.foreveross.chameleon.store.model.SystemInfoModel;

public class MultiSystemActivity extends Activity {
	private ListView multisystem_listview;
	private String userName;
	private String passWord;
	private Boolean isremember;
	private Boolean isoutline;
	private Boolean switchsys;
	private ArrayList<SystemInfoModel> arrayList;
	private Button multi_cancle;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multisystem);
		setFinishOnTouchOutside(false);
		multisystem_listview = (ListView) findViewById(R.id.multisystem_listview);
		multi_cancle = (Button) findViewById(R.id.multi_cancle);
		multi_cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MultiSystemActivity.this.finish();
			}
		});
		if (getIntent() != null && getIntent().getExtras() != null) {
			Bundle b = getIntent().getExtras();
			arrayList = (ArrayList<SystemInfoModel>) b
					.getSerializable("systemlist");
			userName = b.getString("username");
			passWord = b.getString("password");
			isremember = b.getBoolean("isremember");
			isoutline = b.getBoolean("isoutline");
			switchsys = b.getBoolean("switchsys");
		}
		MiltiSystemAdapter adapter = new MiltiSystemAdapter(this, arrayList,
				userName, passWord, isremember , isoutline , switchsys);
		multisystem_listview.setAdapter(adapter);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return true;
	}
}
