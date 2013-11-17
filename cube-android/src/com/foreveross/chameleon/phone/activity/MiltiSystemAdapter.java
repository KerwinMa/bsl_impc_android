package com.foreveross.chameleon.phone.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.modules.LoginModel;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.MultiUserInfoModel;
import com.foreveross.chameleon.store.model.SystemInfoModel;

public class MiltiSystemAdapter extends BaseAdapter {

	private Context context;
	private List<SystemInfoModel> infoModels;
	private String userName;
	private String passWord;
	private Boolean isremember;
	private Boolean isoutline;
	private Boolean switchsys;
	
	private ArrayList<String> showArrayList;
	
	public MiltiSystemAdapter(Context context,
			List<SystemInfoModel> infoModels, String userName, String passWord,
			Boolean isremember , Boolean isoutline , Boolean switchsys) {
		super();
		this.context = context;
		this.infoModels = infoModels;
		this.userName = userName;
		this.passWord = passWord;
		this.isremember = isremember;
		this.isoutline = isoutline;
		this.switchsys = switchsys;
	}

	@Override
	public int getCount() {
		return infoModels.size();
	}

	@Override
	public Object getItem(int position) {

		return infoModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (null == convertView) {
			holder = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.miltisystem_item, null, false);

			holder.alias = (Button) convertView
					.findViewById(R.id.militisystem_tv);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		final SystemInfoModel infoModel = infoModels.get(position);
		if (switchsys){
			if (LoginModel.instance().containSysId(infoModel.getSysId())){
				holder.alias.setText(infoModel.getSysName() + "(已登录)");
			} else {
				holder.alias.setText(infoModel.getSysName());
			}
		} else {
			holder.alias.setText(infoModel.getSysName());
		}
		
		
		holder.alias.setTag(position);
		holder.alias.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SystemInfoModel infoModel = infoModels.get((Integer) v.getTag());
				if (!isoutline ){
					if (infoModel.isCurr()){
						Toast.makeText(context, "已经是当前系统，不需要切换", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				HashMap<String, Object> bundle = new HashMap<String, Object>();
				bundle.put("username", userName);
				bundle.put("password", passWord);
				bundle.put("isremember", isremember);
				bundle.put("isoutline", isoutline);
				bundle.put("systemmodel", infoModel);
				EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(bundle);
				if (context instanceof Activity){
					Activity activity = (Activity) context;
					activity.finish();
				}
			}
		});
		return convertView;
	}

	class ChildHolder {
		Button alias;
		// ** 最后会话内容 *//*
	}

}
