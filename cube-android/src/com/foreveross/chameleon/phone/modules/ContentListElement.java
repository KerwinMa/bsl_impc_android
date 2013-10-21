package com.foreveross.chameleon.phone.modules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.store.model.MessageModule;

public class ContentListElement implements ListElement{
	
	private MessageModule messageModule;
	public void setTitle(MessageModule messageModule) { 
      this.messageModule=messageModule;
    }

	@Override
	public int getLayoutId() {
		return R.layout.msg_content;
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public View getViewForListElement(LayoutInflater layoutInflater,
			Context context, View view,ViewGroup parent,int type) {
		MsgContentItem msgContentItem=null;
		if(view==null){
			if(type==MessageListAdapter.TYPE_CONTENT){
				view =layoutInflater.inflate(getLayoutId(), null); 
				msgContentItem=new MsgContentItem();
				msgContentItem.msg_title=(TextView) view.findViewById(R.id.msg_title);
				msgContentItem.msg_content=(TextView) view.findViewById(R.id.msg_content);
				msgContentItem.msg_time=(TextView) view.findViewById(R.id.msg_time);
				msgContentItem.msg_checkbox=(CheckBox) view.findViewById(R.id.msgcheckbox);
				view.setTag(msgContentItem);
			}
			
		}else{
			msgContentItem=(MsgContentItem) view.getTag();
		}
		String identify=messageModule.getMsgSort();
		if(null == CubeModuleManager.getInstance().getCubeModuleByIdentifier(identify))
		{
			msgContentItem.msg_title.setText("系统消息");
		}
		else
		{
			msgContentItem.msg_title.setText(CubeModuleManager.getInstance().getCubeModuleByIdentifier(identify).getName());
		}
		msgContentItem.msg_content.setText(messageModule.getMsgContent());
		msgContentItem.msg_time.setText(messageModule.getMsgTime());
		if(!messageModule.getMsgEditable()){
			msgContentItem.msg_checkbox.setVisibility(View.GONE);
		}else{
			msgContentItem.msg_checkbox.setVisibility(View.VISIBLE);
			msgContentItem.msg_checkbox.setChecked(messageModule.getMsgSelected());
		}
		return view;
	}


	public class MsgContentItem{
		public TextView msg_title;
		public TextView msg_content;
		public TextView msg_time;
		public CheckBox msg_checkbox;
	}


	@Override
	public Object getTitle() {
		return messageModule;
	}


}
