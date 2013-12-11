/**
 * 
 */
package com.foreveross.chameleon.pad.fragment;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.util.DeviceInfoUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author apple
 *
 */
public class AboutFragment extends Fragment {
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private TextView version;
	private TextView devideId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.app_about, container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initValue(view);
	}
	private void initValue(View view) {
//		PadUtils.setSceenSize(this);
		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("关于我们");

		version = (TextView) view.findViewById(R.id.about_version);
		devideId = (TextView) view.findViewById(R.id.about_device);
		String versionText = Application.class.cast(this.getAssocActivity().getApplicationContext())
				.getCubeApplication().getVersion();
		version.setText(versionText);
		devideId.setText(DeviceInfoUtil.getDeviceId(this.getAssocActivity()));

	}
	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.title_barleft: 
				if (getAssocActivity() instanceof FacadeActivity) {
					((FacadeActivity) getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
				}
				break;
			}
		}
	};

}
