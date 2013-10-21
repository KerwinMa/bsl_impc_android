/**
 * 
 */
package com.foreveross.chameleon.phone.chat.chatroom;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.csair.impc.R;

/**
 * @author zhoujun
 *
 */
public class GridVeiwListAdapter extends BaseAdapter {
	private List<LocalModule> modules;
	private Context mContext;

	public GridVeiwListAdapter(Context context,List<LocalModule> modules){
		this.modules = modules;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return modules.size();
	}

	@Override
	public Object getItem(int position) {
		return modules.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GridViewItem gridItem=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.app_item,null);
			gridItem = new GridViewItem();
			gridItem.mAppIcon = (ImageView) convertView.findViewById(R.id.ivAppIcon);
			gridItem.mAppName = (TextView) convertView.findViewById(R.id.tvAppName);
			gridItem.mAppbar = (ProgressBar) convertView.findViewById(R.id.progressBar_download);
			gridItem.mAppupdata = (ImageView) convertView.findViewById(R.id.imageView_updata);
			convertView.setTag(gridItem);
		} else {
		    gridItem = (GridViewItem) convertView.getTag();
		}
		LocalModule localModule=modules.get(position);
		gridItem.mAppName.setText(localModule.getModuleName());
		gridItem.mAppIcon.setImageDrawable(localModule.getIcon());
		gridItem.mAppupdata.setVisibility(View.GONE);
		gridItem.mAppbar.setVisibility(View.GONE);
		

		return convertView;
	}
	class GridViewItem{
		public ImageView mAppIcon;
		public TextView mAppName;
		public ProgressBar mAppbar;
		public ImageView mAppupdata;
	}
	

}
