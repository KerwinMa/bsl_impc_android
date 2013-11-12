package com.foreveross.chameleon.phone.activity;

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

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.store.model.SystemInfoModel;

public class MiltiSystemAdapter extends BaseAdapter {

	private Context context;
	private List<SystemInfoModel> infoModels;
	private String userName;
	private String passWord;
	private Boolean isremember;
	private Boolean isoutline;

	public MiltiSystemAdapter(Context context,
			List<SystemInfoModel> infoModels, String userName, String passWord,
			Boolean isremember , Boolean isoutline) {
		super();
		this.context = context;
		this.infoModels = infoModels;
		this.userName = userName;
		this.passWord = passWord;
		this.isremember = isremember;
		this.isoutline = isoutline;
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
		holder.alias.setText(infoModel.getAlias());
		holder.alias.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
