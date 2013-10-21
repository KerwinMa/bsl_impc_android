/**
 * 
 */
package com.foreveross.chameleon.pad.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.chat.search.SearchFriendAdapter;
import com.foreveross.chameleon.phone.muc.MucAddFriendAdapter;
import com.foreveross.chameleon.phone.muc.MucBroadCastEvent;
import com.foreveross.chameleon.phone.muc.MucManagerFragment;
import com.foreveross.chameleon.push.client.XmppManager;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-17]
 */
public class MucAddFirendFragment extends Fragment {
	private MucAddFriendAdapter adapter;
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private ListView listView;
	private Map<String, UserModel> selectFriends = null;
	private List<UserModel> invitors = new ArrayList<UserModel>();
	private Application application;
	private ChatGroupModel chatGroupModel = null;
	
	// 搜索的内容
	private EditText app_search_edt;
	private ImageView app_search_close;
	private ListView searchListView;
	private SearchFriendAdapter searchFriendAdapter;

	private List<UserModel> searchFriendList;
	private Button searchBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = Application.class.cast(this.getAssocActivity()
				.getApplication());

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return 2013-9-17 下午2:21:04
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.chat_muc_addfriend, null);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param view
	 * @param savedInstanceState
	 *            2013-9-17 下午2:21:33
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initValues(view);
	}

	public void initValues(View view) {
		String roomJid = getAssocActivity().getIntent().getStringExtra(
				"roomJid");
		if (roomJid == null) {
			chatGroupModel = new ChatGroupModel();
		} else {
			chatGroupModel = IMModelManager.instance().getChatRoomContainer()
					.getStuff(roomJid);
		}
		
		if (chatGroupModel == null) {
			throw new IllegalStateException("不可能为空,传入的roomJid有错吗?");
		}

		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setOnClickListener(clickListener);
		titlebar_right.setText("邀请");
		selectFriends = new HashMap<String, UserModel>();
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("邀请成员");
		listView = (ListView) view.findViewById(R.id.mucAddFriendList);

		Map<String, UserModel> map = IMModelManager.instance().getUserMap();
		if (map != null) {
			List<UserModel> allUsers = new ArrayList<UserModel>(map.values());
			allUsers.removeAll(chatGroupModel.getList());
			String myJid = XmppManager.getMeJid();
			if (myJid != null){
				for (UserModel userModel : allUsers) {
					if(!myJid.equals(userModel.getJid())){
						invitors.add(userModel);
					}
				}
			}
		}
		// 搜索框
		// 搜索按钮
		searchBtn = (Button) view.findViewById(R.id.app_search_btn);
		searchBtn.setOnClickListener(clickListener);
		// 搜索框交叉按钮
		app_search_close = (ImageView) view
				.findViewById(R.id.app_search_close_chat);
		app_search_close.setOnClickListener(clickListener);
		app_search_close.setVisibility(View.GONE);

		searchListView = (ListView) view.findViewById(R.id.muc_addfriend_searchList);
		app_search_edt = (EditText) view.findViewById(R.id.app_search_edt);
		app_search_edt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@SuppressLint("NewApi")
			@Override
			public void afterTextChanged(Editable s) {
				String searchContent = s.toString().trim();
				if (searchContent.isEmpty()) {
					commonMode();
				} else {
					searchMode(searchContent);
				}
			}

		});

		searchFriendList = new ArrayList<UserModel>();
		searchFriendAdapter = new SearchFriendAdapter(getAssocActivity(),
				searchFriendList);
		searchListView.setAdapter(searchFriendAdapter);
		searchListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				UserModel user = searchFriendList.get(position);
				selectFriends.put(user.getJid(), user);
				invitors.remove(user);
				invitors.add(0, user);
				adapter.notifyDataSetChanged();
				commonMode();
				app_search_edt.setText("");
			}
		});
		
		adapter = new MucAddFriendAdapter(this.getAssocActivity(), invitors,
				selectFriends , new Filter() {
					@Override
					protected FilterResults performFiltering(CharSequence prefix) {
						// 持有过滤操作完成之后的数据。该数据包括过滤操作之后的数据的值以及数量。 count:数量
						// values包含过滤操作之后的数据的值

						FilterResults results = new FilterResults();
						List<UserModel> mOriginalValues = new ArrayList<UserModel>();
						// 做正式的筛选
						String prefixString = prefix.toString().toLowerCase(
								Locale.CHINESE);

							List<UserModel> models = search(prefixString);
							for(UserModel userModel : models){
								if (!mOriginalValues.contains(userModel)){
									mOriginalValues.add(userModel);
								}
							}
						// 然后将这个新的集合数据赋给FilterResults对象
						results.values = mOriginalValues;
						results.count = mOriginalValues.size();
						return results;
					}

					@SuppressWarnings("unchecked")
					@Override
					protected void publishResults(CharSequence constraint,
							FilterResults results) {
						// 重新将与适配器相关联的List重赋值一下
						searchFriendList.clear();
						searchFriendList
								.addAll((List<UserModel>) results.values);
						searchFriendAdapter.notifyDataSetChanged();
					};
				});
		listView.setAdapter(adapter);
	}

	/**
	 * [搜索模式]<BR>
	 * [功能详细描述] 
	 */
	public void searchMode(String content) {
		searchListView.setVisibility(View.VISIBLE);
		app_search_close.setVisibility(View.VISIBLE);
		adapter.getFilter().filter(content);
		listView.setVisibility(View.GONE);
	}

	/**
	 * [普通模式]<BR>
	 * [功能详细描述]
	 */
	public void commonMode() {
		searchListView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		app_search_close.setVisibility(View.GONE);
	}
	
	public List<UserModel> search(String prefix) {
		List<UserModel> filteredList = new ArrayList<UserModel>();
		for (UserModel user : invitors) {
			if ((user.getJid() != null && user.getJid().contains(prefix))
					|| (user.getName() != null && user.getName().contains(
							prefix))) {
				filteredList.add(user);
			}
		}
		return filteredList;
	}
	
	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.title_barleft: {

				InputMethodManager imm = (InputMethodManager) getAssocActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE); 
			    imm.hideSoftInputFromWindow(app_search_edt.getWindowToken(), 0); 
				if (getAssocActivity() instanceof FacadeActivity) {
					FacadeActivity.class.cast(getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
				}
				break;
			}
			
			case R.id.app_search_close_chat:
				app_search_edt.setText("");
				break;
			case R.id.title_barright: {
				if (selectFriends.size() == 0){
					Toast.makeText(MucAddFirendFragment.this.getAssocActivity(),
							"请选择需要添加的成员", Toast.LENGTH_SHORT).show();
					return;
				}
				// 创建
				String roomName = "";
				PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(
						MucAddFirendFragment.this.getAssocActivity(), R.raw.cube);
				
				final String roomJid = UUID.randomUUID().toString() + "@"
						+ propertiesUtil.getString("MucServiceName", "conference.snda-192-168-2-32");
				UserModel me = IMModelManager.instance().getMe();
				if (chatGroupModel.getRoomJid() == null) {
					View dialogView= LayoutInflater.from(getAssocActivity()).inflate(R.layout.dialog_muc_createroom,null);
					final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_muc_edt);
					new AlertDialog.Builder(getAssocActivity())
					.setTitle("修改群组名称")
					.setView(dialogView)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String roomName = edt.getText().toString();
							if(roomName.equals("")){
								return;
							}
							else {
								chatGroupModel.setGroupName(roomName);
								chatGroupModel.setGroupCode(roomJid);
								chatGroupModel.setRoomJid(roomJid);
								IMModelManager
										.instance()
										.getChatRoomContainer()
										.createChatRoom(MucAddFirendFragment.this.getAssocActivity(),chatGroupModel,
												selectFriends.values());
								Toast.makeText(MucAddFirendFragment.this.getAssocActivity(), "新建群组成功", Toast.LENGTH_SHORT).show();
								if (getAssocActivity() instanceof FacadeActivity) {
									FacadeActivity.class.cast(getAssocActivity()).popRight();
								} else {
									getAssocActivity().finish();
								}
							}
						}	
					})
					.setNegativeButton("取消", null)
					.show();
				}
				// 更新
				else {
					UserModel[] us = selectFriends.values().toArray(
							new UserModel[selectFriends.values().size()]);
					chatGroupModel.addMembers(application, us);
					HashMap<String , Object> hashMap = new HashMap<String, Object>();
					hashMap.put(MucBroadCastEvent.PUSH_MUC_ADDFRIEND, us);
					//发消息刷新界面
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST).post(
							hashMap);
					Toast.makeText(MucAddFirendFragment.this.getAssocActivity(), "新建成员成功", Toast.LENGTH_SHORT).show();
					if (getAssocActivity() instanceof FacadeActivity) {
						FacadeActivity.class.cast(getAssocActivity()).popRight();
					} else {
						getAssocActivity().finish();
					}
				}
				InputMethodManager imm = (InputMethodManager) getAssocActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE); 
			    imm.hideSoftInputFromWindow(app_search_edt.getWindowToken(), 0); 
			}
				break;
			}
		}
	};

}
