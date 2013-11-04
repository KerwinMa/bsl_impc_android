package com.foreveross.chameleon.phone.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;

//import org.jivesoftware.smackx.filetransfer.FileTransferManager;

public class ChatRoomActivity extends FragmentActivity {
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param savedInstanceState
	 *            2013-8-27 下午2:22:50
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity_layout);
		Application application = Application.class.cast(ChatRoomActivity.this
				.getApplication());
		application.getActivityManager().pushActivity(this);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Application application = Application.class.cast(ChatRoomActivity.this
				.getApplication());
		application.getActivityManager().popActivity(this);
	}

}
