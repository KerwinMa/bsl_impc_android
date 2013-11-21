package com.foreveross.chameleon.phone.chat.search;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.phone.activity.BaseActivity;
import com.foreveross.chameleon.push.client.XmppManager.RosterManager;
import com.foreveross.chameleon.store.model.UserModel;

public class SearchFriendActivity extends BaseActivity {
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	// 搜索的内容
	private String app_search_content = "";
	private EditText app_search_edt;
	private ImageView app_search_close;
	private ListView searchListView;
	private SearchFriendAdapter adapter;
	private List<UserModel> searchFriendList;
	private Button searchBtn;
	private BroadcastReceiver broadcastReceiver;
	private RosterManager rosterManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_search);
		if (application.getNotificationService() == null ){
			return;
		}
		this.rosterManager = application.getNotificationService().getRosterManager();
		if(null!=getIntent()){
			String friendJid=getIntent().getStringExtra("jid");
			if (friendJid!=null&& !TextUtils.isEmpty(friendJid)) {
				String from = getIntent().getStringExtra("from");
				if(null!=from && from.equals("notify")){
					showAddFriendDialog(friendJid);
				}
			}
		}
		
		initValues();
		IntentFilter filter = new IntentFilter();
		filter.addAction("push.search.change");
		filter.addAction("push.model.addFriends");
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals("push.search.change")) {
					adapter.notifyDataSetChanged();
				}else if(intent.getAction().equals("push.model.addFriends")){
					final String from = intent.getStringExtra("jid");
					if(intent.getStringExtra("from").equals("SearchFriendActivity")){
						showAddFriendDialog(from);
					}
				 }
			}
		};
		registerReceiver(broadcastReceiver, filter);
	}
	@Override
	protected void onResume() {
		super.onResume();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	public void initValues() {
		searchFriendList=rosterManager.getSearchFriendList();
		titlebar_left = (Button) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("搜索好友");
		 // 搜索框
		app_search_edt = (EditText) findViewById(R.id.app_search_edt);
		//搜索按钮
		searchBtn = (Button) findViewById(R.id.app_search_btn);
		searchBtn.setOnClickListener(clickListener);
		// 搜索框交叉按钮
		app_search_close = (ImageView) findViewById(R.id.app_search_close);
		app_search_close.setOnClickListener(clickListener);
		searchListView = (ListView) findViewById(R.id.searchList);
		adapter = new SearchFriendAdapter(this,searchFriendList);
		searchListView.setAdapter(adapter);
		searchListView.setOnItemClickListener(itemClickListener);
	}


	 OnItemClickListener  itemClickListener =new  OnItemClickListener() {


		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			final String name = searchFriendList.get(position).getName();
			final String jid = searchFriendList.get(position).getJid();
			System.out.println(name);
			showAddFriendDialog(jid);
		}

	};

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_barleft:
				finish();
				break;
			case R.id.app_search_close:
				app_search_edt.setText("");
				break;
			case R.id.app_search_btn:
				app_search_content = app_search_edt.getText().toString();
				//当EidtText无焦点（focusable=false）时阻止输入法弹出  
				app_search_edt.clearFocus();
				InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);     
				imm.hideSoftInputFromWindow(app_search_edt.getWindowToken(), 0);  
				
				if (!TextUtils.isEmpty(app_search_content)) {
					searchFriendList.clear();
					try {
//						rosterManager.searchFriends(app_search_content);
//						List<UserModel> result=application.searchFriendList;
//						if(result!=null){
//							searchFriendList.addAll(result);
//						}
						adapter.notifyDataSetChanged();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(SearchFriendActivity.this, "搜索好友异常", Toast.LENGTH_SHORT).show();
					} 
				}
				break;
			}
		}
	};
	
	public void showAddFriendDialog(final String from){
		int atIndex = from.lastIndexOf("@");
		 final String name = from.substring(0, atIndex);
		 Dialog dialog= new AlertDialog.Builder(SearchFriendActivity.this)
			.setMessage(from+ "要加你为好友，同意吗?")
			.setTitle("提示")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface paramDialogInterface, int paramInt) {
					boolean result=rosterManager.addUser(from, name);
					if(result){
						rosterManager.addUser2Group(from, name,"好友列表");
						Toast.makeText(SearchFriendActivity.this, "添加好友成功", Toast.LENGTH_SHORT).show();
						try{
							finish();
						}catch(Exception e){
							e.printStackTrace();
						}

					}else{
						Toast.makeText(SearchFriendActivity.this, "添加好友失败", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
			})
			.setNegativeButton("取消", null).create();
		 dialog.show();
	}
}
