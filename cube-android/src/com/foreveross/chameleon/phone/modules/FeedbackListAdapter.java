package com.foreveross.chameleon.phone.modules;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csair.impc.R;

public class FeedbackListAdapter extends BaseAdapter {
	private Context context;
	private List<FeedbackModule> feedbackModules;
	
	public FeedbackListAdapter(Context context,List<FeedbackModule> feedbackModules){
		this.context=context;
		this.feedbackModules=feedbackModules;
	}

	@Override
	public int getCount() {
		return feedbackModules.size();
	}

	@Override
	public Object getItem(int position) {
		return feedbackModules.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FeedbackItem feedbackItem=null;
		if(convertView==null){
          convertView=LayoutInflater.from(context).inflate(R.layout.feedback_content, null);
          feedbackItem=new FeedbackItem();
          feedbackItem.date=(TextView) convertView.findViewById(R.id.feedback_date);
          feedbackItem.content=(TextView) convertView.findViewById(R.id.feedback_msg);
          feedbackItem.deal=(TextView) convertView.findViewById(R.id.feedback_deal);
          feedbackItem.commonLayout=(LinearLayout) convertView.findViewById(R.id.common);
          feedbackItem.common=(TextView) convertView.findViewById(R.id.feedback_common);
          feedbackItem.modifiedAt=(TextView) convertView.findViewById(R.id.feedback_modifiedAt);
          
		  convertView.setTag(feedbackItem);
		}else{
			feedbackItem=(FeedbackItem) convertView.getTag();
		}
		FeedbackModule each=feedbackModules.get(position);
		feedbackItem.date.setText(each.getCreatedAt());
		feedbackItem.deal.setText(each.getStatus());
		feedbackItem.content.setText(each.getContent());
		if(each.getCommon()!=null){
			feedbackItem.commonLayout.setVisibility(View.VISIBLE);
			feedbackItem.modifiedAt.setText(each.getModifiedAt());
			feedbackItem.common.setText(each.getCommon());
			
		}else{
			feedbackItem.commonLayout.setVisibility(View.GONE);
		}
		
		
		return convertView;
	}
	public class FeedbackItem{
		public TextView deal;
		public TextView date;
		public TextView content;
		public LinearLayout commonLayout;
		public TextView modifiedAt;
		public TextView common;
		
	}

}
