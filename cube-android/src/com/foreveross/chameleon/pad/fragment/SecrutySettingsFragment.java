/**
 * 
 */
package com.foreveross.chameleon.pad.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.util.Preferences;

/**
 * @author zj
 *
 */
public class SecrutySettingsFragment extends Fragment {
	private EditText day_time;
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.secruty_settings, null);
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
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		day_time = (EditText) view.findViewById(R.id.day_time);
		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("安全设置");
		day_time.setOnEditorActionListener(new EditText.OnEditorActionListener() {  
			  
		    @Override  
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {  
		        if (actionId == EditorInfo.IME_ACTION_DONE) {  
		            InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0); 
		            String txt = String.valueOf(v.getText());
		            try {
						int num = Integer.parseInt(txt);
						if(num >31 || num <=0)
						{
							showDialog();
							return false;
						}
						else
						{
							Preferences.saveDayTime(num, Application.sharePref);
						}
					} catch (NumberFormatException e) {
						showDialog();
						return false;
						
					}
		            
		              
		             
		              
		            return true;    
		        }  
		        return false;  
		    }  
		      
		});

	}
	
	private void showDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getAssocActivity()).setTitle("错误提示").setMessage("请输入0～31之间的整数").setNegativeButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();								
			}
		});
		builder.create();
		builder.show();
	}
	
}
