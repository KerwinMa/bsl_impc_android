package com.foreveross.chameleon.phone.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.util.DeviceInfoUtil;
import com.foreveross.chameleon.util.PadUtils;

public class AboutActivity extends BaseActivity {
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private TextView version;
	private TextView devideId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_about);
		initValue();
	}

	private void initValue() {
//		PadUtils.setSceenSize(this);
		titlebar_left = (Button) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("关于我们");

		version = (TextView) findViewById(R.id.about_version);
		devideId = (TextView) findViewById(R.id.about_device);
		String versionText = Application.class.cast(this.getApplication())
				.getCubeApplication().getVersion();
		version.setText(versionText);
		devideId.setText(DeviceInfoUtil.getDeviceId(this));

	}

	protected void onResume() {
		super.onResume();
		
		application.setShouldSendChatNotification(true);
		application.setShouldSendNoticeNotification(true);
		application.setShouldSendMessageNotification(true);
	};
	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.title_barleft: 
				finish();
				break;
			}
		}
	};
}
