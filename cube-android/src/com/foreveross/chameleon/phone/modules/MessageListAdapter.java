package com.foreveross.chameleon.phone.modules;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.csair.impc.R;

public class MessageListAdapter extends BaseAdapter {
	public static final int TYPE_TITLE= 0;
    public static final int TYPE_CONTENT= 1;
    public static final int TYPE_COUNT=TYPE_CONTENT+1;
	private Context context;

    private List<ListElement> resultList=new ArrayList<ListElement>();

    private LayoutInflater layoutInflater;
    
    public MessageListAdapter(Context context,List<ListElement> resultList){
    	this.context=context;
    	this.resultList=resultList;
    	layoutInflater = (LayoutInflater) context .getSystemService("layout_inflater"); 
    
    }
	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public Object getItem(int position) {
		return resultList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		return this.resultList.get(position).getViewForListElement(layoutInflater, context,convertView,parent,type); 

	}
	@Override
	public boolean isEnabled(int position) {
		return this.resultList.get(position).isClickable();
	}
	@Override
	public int getItemViewType(int position) {
		if(resultList.get(position).getLayoutId()==R.layout.msg_title){
			return TYPE_TITLE;
		}
		return TYPE_CONTENT;
	}
	@Override
	public int getViewTypeCount() {
		return TYPE_COUNT;
	}
	

}
