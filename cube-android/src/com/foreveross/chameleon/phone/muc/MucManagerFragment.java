package com.foreveross.chameleon.phone.muc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.view.NoScrollListView;
import com.foreveross.chameleon.push.client.XmppManager;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.PadUtils;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class MucManagerFragment extends Fragment {

	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;

	private Button btn_leave;
	private RelativeLayout btn_rename;
	private NoScrollListView memberListView;

	private TextView muc_chatroomname;

	private String roomJid;
	private MucManagerAdapter adapter;

	private ChatGroupModel chatGroupModel;
	// 角色是否有kick权限
	boolean kickable;
	private List<UserModel> list;
	private Application application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = Application.class.cast(getAssocActivity()
				.getApplicationContext());
		EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST,
				ThreadEnforcer.MAIN).register(this);
		return inflater.inflate(R.layout.chat_muc_manager, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initValues(view);
	}

	public void initValues(View view) {
		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);

		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setOnClickListener(clickListener);
		titlebar_right.setText("新增");

		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("群组管理");

		btn_leave = (Button) view.findViewById(R.id.muc_btn_leave);
		btn_leave.setOnClickListener(clickListener);
		btn_rename = (RelativeLayout) view.findViewById(R.id.muc_btn_rename);
		btn_rename.setOnClickListener(clickListener);
		muc_chatroomname = (TextView) view.findViewById(R.id.muc_chatroomname);

		memberListView = (NoScrollListView) view
				.findViewById(R.id.muc_listview_members);
		roomJid = getAssocActivity().getIntent().getStringExtra("roomJid");
		chatGroupModel = IMModelManager.instance().getChatRoomContainer()
				.getStuff(roomJid);

		if (chatGroupModel != null && roomJid != null) {
			String chatroomname = chatGroupModel.getGroupName();
			if (chatroomname != null) {
				muc_chatroomname.setText(chatroomname);
			}
			String myJid = XmppManager.getMeJid();
			if (!chatGroupModel.isCreator(getAssocActivity(), myJid)) {
				titlebar_right.setVisibility(View.INVISIBLE);
			}
			new AsyncTask<String, Integer, Boolean>() {

				protected void onPreExecute() {
					showCustomDialog(false);
				};

				@Override
				protected Boolean doInBackground(String... params) {
					List<String> jids = new ArrayList<String>();
					String url = URL.MUC_QueryMembers
							+ chatGroupModel.getGroupCode()
							+ URL.getSessionKeyappKey();
					String result = null;
					try {
						result = HttpUtil.doWrapedHttp(getAssocActivity(),
								new String[] { url, "", HttpUtil.UTF8_ENCODING,
										HttpUtil.HTTP_GET });
						if (result != null && result.length() != 0) {
							JSONArray jay = new JSONArray(result);
							for (int i = 0; i < jay.length(); i++) {
								JSONObject jb = jay.getJSONObject(i);
								String userJid = jb.getString("jid");
								if (userJid != null) {
									jids.add(userJid);
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (String jid : jids) {
						UserModel userModel = IMModelManager.instance()
								.getUserModel(jid);
						chatGroupModel.clear();
						if (userModel != null) {
							chatGroupModel.addStuff(userModel);
						}
					}
					UserModel me = IMModelManager.instance().getMe();
					if (me != null) {
						chatGroupModel.addStuff(me);
					}
					return chatGroupModel.isCreator(getAssocActivity(),
							XmppManager.getMeJid());
				}

				protected void onPostExecute(Boolean result) {
					cancelDialog();
					list = getList();
					kickable = result;
					adapter = new MucManagerAdapter(getAssocActivity(), list,
							chatGroupModel, kickable);
					memberListView.setAdapter(adapter);
					if (kickable) {
						btn_leave.setText("退出并解散群组");
						btn_rename.setVisibility(View.VISIBLE);
					} else {
						btn_leave.setText("退出群组");
						btn_rename.setVisibility(View.GONE);
					}
				};
			}.execute();
		}

	}

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.title_barleft:
				System.gc();
				if (getAssocActivity() instanceof FacadeActivity) {
					FacadeActivity.class.cast(getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
				}
				break;

			case R.id.title_barright:

				if (PadUtils.isPad(getAssocActivity())) {
					PropertiesUtil propertiesUtil = PropertiesUtil
							.readProperties(getAssocActivity(), R.raw.cube1);
					String addfirend = propertiesUtil
							.getString("addfirend", "");
					Intent intent = new Intent();
					intent.putExtra("direction", 2);
					intent.putExtra("type", "fragment");
					intent.putExtra("value", addfirend);
					intent.putExtra("inviteType", "more");
					intent.putExtra("roomJid", roomJid);
					intent.setClass(getAssocActivity(), FacadeActivity.class);
					getAssocActivity().startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("inviteType", "more");
					intent.putExtra("roomJid", roomJid);
					intent.setClass(getAssocActivity(),
							MucAddFirendActivity.class);
					getAssocActivity().startActivity(intent);
				}

				break;
			case R.id.muc_btn_rename:
				View dialogView = LayoutInflater.from(getAssocActivity())
						.inflate(R.layout.dialog_muc_createroom, null);
				final EditText edt = (EditText) dialogView
						.findViewById(R.id.dialog_muc_edt);
				new AlertDialog.Builder(getAssocActivity())
						.setTitle("修改群组名称")
						.setView(dialogView)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String roomName = edt.getText()
												.toString();
										if (roomName.equals("")) {
											return;
										} else {
											chatGroupModel.rename(
													MucManagerFragment.this
															.getAssocActivity(),
													roomName);
											if (getAssocActivity() instanceof FacadeActivity) {
												FacadeActivity.class.cast(
														getAssocActivity())
														.popRight();
											} else {
												getAssocActivity().finish();
											}
										}
									}
								}).setNegativeButton("取消", null).show();

				break;
			case R.id.muc_btn_leave:
				if (chatGroupModel != null) {
					if (kickable) {
						// chatGroupModel.free(getAssocActivity());
						// 解散群组
						String fromWho = XmppManager.getMeJid();
						String toWho = roomJid;
						String content = fromWho;
						ConversationMessage conversation = createConversation(
								content, fromWho, toWho, "quitgroup");
						MucManager.getInstanse(getAssocActivity())
								.sendMucMessage(conversation);
						IMModelManager
								.instance()
								.getChatRoomContainer()
								.free(getAssocActivity(),
										chatGroupModel.getRoomJid());
						conversation.setContent("用户群组被解散");
						StaticReference.userMf.createOrUpdate(conversation);
						Toast.makeText(getAssocActivity(), "解散群组成功",
								Toast.LENGTH_SHORT).show();
						// IMModelManager.instance().getSessionContainer().removeSession(chatGroupModel.getRoomJid());
					} else {
						// 个人主动离开房间
						String fromWho = XmppManager.getMeJid();
						String toWho = roomJid;
						String content = fromWho;
						ConversationMessage conversation = createConversation(
								content, fromWho, toWho, "quitperson");
						MucManager.getInstanse(getAssocActivity())
								.sendMucMessage(conversation);
						if (fromWho.contains("@")){
							String s1[] = fromWho.split("@");
							fromWho = s1[0];
						}
						String contentString = fromWho + "离开用户组";
						conversation.setContent(contentString);
						StaticReference.userMf.createOrUpdate(conversation);
						// chatGroupModel.leave(getAssocActivity());
						IMModelManager
								.instance()
								.getChatRoomContainer()
								.leave(getAssocActivity(),
										chatGroupModel.getRoomJid());
						Toast.makeText(getAssocActivity(), "退出群组成功",
								Toast.LENGTH_SHORT).show();
						// IMModelManager.instance().getSessionContainer().removeSession(chatGroupModel.getRoomJid());
					}
					if (getAssocActivity() instanceof FacadeActivity) {
						FacadeActivity.class.cast(getAssocActivity())
								.popRight();
					} else {
						getAssocActivity().finish();
					}
				}
				// System.gc();
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onDestroyView() {
		super.onDestroy();

		EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST,
				ThreadEnforcer.MAIN).unregister(this);
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Subscribe
	public void onMucManagerEvent(String mucBroadCastEvent) {
		if (MucBroadCastEvent.PUSH_MUC_MANAGER_MEMBER.equals(mucBroadCastEvent)) {
			getAssocActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (adapter != null) {
						adapter.notifyDataSetChanged();
					}
				}
			});
		}

		if (MucBroadCastEvent.PUSH_MUC_ADDFRIEND.equals(mucBroadCastEvent)) {
			getAssocActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (adapter != null) {
						list = getList();
						adapter.notifyDataSetChanged();
					}
				}
			});
		}
	}

	@Subscribe
	public void onMucAddFreindEvent(HashMap<String, Object> hashMap) {
		try {
			Object key = hashMap.get(MucBroadCastEvent.PUSH_MUC_ADDFRIEND);
			UserModel[] us = (UserModel[]) key;
			for (UserModel userModel : us) {
				list.add(userModel);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		getAssocActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	@Subscribe
	public void onMucManagerKillEvent(HashMap<String, String> map) {
		String muckill = map.get("muckill");
		String roomJid = map.get("roomJid");
		if (MucBroadCastEvent.PUSH_MUC_KICKED.equals(muckill)) {
			// 被当前房主踢出群组了
			if (roomJid.equals(roomJid)) {
				Toast.makeText(getAssocActivity(), "你已经被提出群组了",
						Toast.LENGTH_SHORT).show();
				if (getAssocActivity() instanceof FacadeActivity) {
					FacadeActivity.class.cast(getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
				}
			}

		}
	}

	public Dialog progressDialog;

	public void showCustomDialog(boolean cancelable) {
		if (progressDialog == null) {
			progressDialog = new Dialog(
					MucManagerFragment.this.getAssocActivity(), R.style.dialog);
			progressDialog.setContentView(R.layout.dialog_layout);
		}

		if (progressDialog.isShowing()) {
			return;
		}
		progressDialog.setCancelable(cancelable);
		progressDialog.show();
	}

	public void cancelDialog() {
		if (progressDialog == null) {
			return;
		}
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}

	/**
	 * 构建一个会话对象
	 **/
	private ConversationMessage createConversation(String content,
			String fromWho, String toWho, String type) {
		ConversationMessage conversation = new ConversationMessage();
		conversation.setContent(content);
		conversation.setFromWho(fromWho);
		conversation.setToWho(toWho);
		conversation.setUser(fromWho);
		conversation.setChater(toWho);
		conversation.setLocalTime(System.currentTimeMillis());
		conversation.setType(type);
		return conversation;
	}

	public List<UserModel> getList() {
		List<UserModel> members = chatGroupModel.getList();
		HashMap<String, UserModel> map = new HashMap<String, UserModel>();
		for (UserModel userModel : members) {
			if (!map.containsKey(userModel.getName())) {
				map.put(userModel.getName(), userModel);
			}
		}
		ArrayList<UserModel> list = new ArrayList<UserModel>();
		Iterator iter = map.entrySet().iterator(); // 获得map的Iterator
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			list.add((UserModel) entry.getValue());
		}
		map.clear();
		return list;
	}
}
