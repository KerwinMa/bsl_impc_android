package com.foreveross.chameleon.pad.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.NoticesDeletedInMessageEvent;
import com.foreveross.chameleon.event.PatchNoticeModelEvent;
import com.foreveross.chameleon.event.PresenceEvent;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.modules.MessageFragmentModel;
import com.foreveross.chameleon.phone.modules.NoticeListAdapter;
import com.foreveross.chameleon.push.client.Constants;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.Pool;
import com.foreveross.chameleon.util.Preferences;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

/**
 * [公告界面]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-10-11]
 */
public class NoticeFragment extends Fragment {
	private NotificationManager notificationManager;

	private Button titlebar_left;
	private Button titlebar_right;

	private TextView titlebar_content;
	private ListView noticelist;
	private CheckBox allselected;
	private RelativeLayout editcheckbox;
	private Button delete;
	private Button mark;
	private NoticeListAdapter noticeListAdapter;
	private List<NoticeModuleMessage> noticeModules = new ArrayList<NoticeModuleMessage>();
	private CubeModule cubeModule = null;
	private com.foreveross.chameleon.Application application;
	private Map<String, Integer> idMap = Collections
			.synchronizedMap(new HashMap<String, Integer>());

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = com.foreveross.chameleon.Application.class.cast(this
				.getAssocActivity().getApplication());
		notificationManager = (NotificationManager) getAssocActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// 1.得到当前公告模块
		cubeModule = CubeModuleManager.getInstance().getModuleByIdentify(
				TmpConstants.ANNOUCE_RECORD_IDENTIFIER);
		return inflater.inflate(R.layout.notice, null);
	}

	/**
	 * [批量处理]<BR>
	 * [功能详细描述]
	 * 
	 * @param patchNoticeModelEvent
	 *            2013-10-11 上午9:48:55
	 */
	@Subscribe
	public void onPatchNoticeModelEvent(
			final PatchNoticeModelEvent patchNoticeModelEvent) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				List<NoticeModuleMessage> list = patchNoticeModelEvent
						.getPatch();
				if (list == null || list.isEmpty()) {
					return null;
				}

				for (NoticeModuleMessage noticeModuleMessage : list) {
					noticeModuleMessage.setEditable(isEditing);
					noticeModuleMessage.setSelected(allselected.isChecked());
					noticeModules.add(0, noticeModuleMessage);

				}
				return null;
			}

			protected void onPostExecute(Void result) {
				noticeListAdapter.notifyDataSetChanged();
			};
		}.execute();

	}

	/**
	 * [读取多个公告]<BR>
	 * [功能详细描述]
	 * 
	 * @param unreadIds
	 *            2013-10-11 上午10:54:29
	 */
	public void readMessages(Set<String> unreadIds) {
		MessageFragmentModel.instance().readNotices(unreadIds);
	}

	public void markRead() {
		synchronized (NoticeFragment.this) {
			int unreadCount = 0;
			final List<NoticeModuleMessage> unreadModels = new ArrayList<NoticeModuleMessage>();
			final Set<String> unreadIds = new HashSet<String>();
			for (NoticeModuleMessage model : noticeModules) {
				if (model.isSelected() && !model.isHasRead()) {

					unreadIds.add(model.getMesssageId());
					model.setHasRead(true);
					unreadModels.add(model);
					++unreadCount;

				}
			}
			if (unreadCount != 0) {
				cubeModule.decreaseMsgCountBy(unreadCount);
			}
			noticeListAdapter.notifyDataSetChanged();
			Pool.getPool().execute(new Runnable() {

				@Override
				public void run() {
					readMessages(unreadIds);
					for (NoticeModuleMessage noticeModuleMessage : unreadModels) {
						noticeModuleMessage.update();
					}
				}
			});
		}
	}

	private boolean isEditing = false;

	public void doEdit() {
		isEditing = true;
		titlebar_right.setText("取消");
		titlebar_right.setVisibility(View.VISIBLE);
		editcheckbox.setVisibility(View.VISIBLE);
		for (NoticeModuleMessage noticeModuleMessage : noticeModules) {
			noticeModuleMessage.setEditable(isEditing);
		}
		noticeListAdapter.notifyDataSetChanged();
	}

	public void doUnEdit() {

		isEditing = false;
		titlebar_right.setText("编辑");
		editcheckbox.setVisibility(View.GONE);
		for (NoticeModuleMessage noticeModuleMessage : noticeModules) {
			noticeModuleMessage.setEditable(isEditing);
		}
		noticeListAdapter.notifyDataSetChanged();
	}

	public void deleteSelected() {
		boolean hasSelected = false;
		for (NoticeModuleMessage noticeModuleMessage : noticeModules) {
			if (noticeModuleMessage.isSelected()) {
				hasSelected = true;
				break;
			}
		}
		if (!hasSelected) {
			new AlertDialog.Builder(getAssocActivity()).setTitle("提示")
					.setMessage("暂无删除项").setNegativeButton("确定", null).show();
		} else {
			new AlertDialog.Builder(getAssocActivity())
					.setTitle("提示")
					.setMessage("确定删除？")
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									synchronized (NoticeFragment.this) {
										final NoticesDeletedInMessageEvent noticesDeletedInMessageEvent = new NoticesDeletedInMessageEvent();
										final Set<String> deleteNoticeIds = new HashSet<String>();
										final List<NoticeModuleMessage> deletedModles = new ArrayList<NoticeModuleMessage>();
										int unReadCount = 0;
										for (NoticeModuleMessage noticeModuleMessage : noticeModules) {
											if (noticeModuleMessage
													.isSelected()) {
												deletedModles
														.add(noticeModuleMessage);
												if (!noticeModuleMessage
														.isHasRead()) {
													++unReadCount;
												}
												deleteNoticeIds.add(noticeModuleMessage
														.getMesssageId());
											}

										}

										MessageFragmentModel.instance().removeNotices(deleteNoticeIds);
										if (unReadCount != 0) {
											cubeModule
													.decreaseMsgCountBy(unReadCount);
										}
										noticeModules.removeAll(deletedModles);
										noticeListAdapter
												.notifyDataSetChanged();
										Pool.getPool().execute(new Runnable() {

											@Override
											public void run() {
												for (NoticeModuleMessage moduleMessage : deletedModles) {
													moduleMessage.delete();
												}
											}
										});
									}
								}
							}).show();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		EventBus.getEventBus(TmpConstants.EVENTBUS_ANNOUNCE_CONTENT,
				ThreadEnforcer.MAIN).register(this);
		// titlebar
		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PadUtils.isPad(getAssocActivity())) {
					((FacadeActivity) getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
				}

			}
		});
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isEditing) {
					doUnEdit();
				} else {
					doEdit();
				}
			}
		});
		titlebar_right.setBackgroundResource(R.drawable.normal_button_clickbg);
		titlebar_right.setText("编辑");
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("公告");

		noticelist = (ListView) view.findViewById(R.id.noticelist);
		allselected = (CheckBox) view.findViewById(R.id.allselected);
		allselected.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (int i = 0; i < noticeModules.size(); i++) {
					noticeModules.get(i).setSelected(isChecked);
				}
				noticeListAdapter.notifyDataSetChanged();
			}
		});
		delete = (Button) view.findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteSelected();
			}
		});
		mark = (Button) view.findViewById(R.id.mark);
		mark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				markRead();
			}
		});
		editcheckbox = (RelativeLayout) view.findViewById(R.id.editcheckbox);

		notificationManager.cancel(Constants.NOTICE_NOTIFY_ID);
		initData();
	}

	public void readNotice(int index) {
		final NoticeModuleMessage noticeModuleMessage = noticeModules
				.get(index);
		noticeModuleMessage.setHasRead(true);
		CubeModule cubeModule = CubeModuleManager.getInstance()
				.getModuleByIdentify(TmpConstants.ANNOUCE_RECORD_IDENTIFIER);
		cubeModule.decreaseMsgCount();
		Pool.getPool().execute(new Runnable() {

			@Override
			public void run() {

				noticeModuleMessage.update();
			}
		});
		noticeListAdapter.notifyDataSetChanged();
	}

	public void initData() {
		noticeListAdapter = new NoticeListAdapter(getAssocActivity(),
				noticeModules);
		noticeListAdapter.registerDataSetObserver(new DataSetObserver() {
			/**
			 * [一句话功能简述]<BR>
			 * [功能详细描述] 2013-8-21 下午1:15:55
			 */
			@Override
			public void onChanged() {
				super.onChanged();
				if (noticeModules.isEmpty()) {
					if (isEditing) {
						isEditing = false;
						titlebar_right.setText("编辑");
					}
					titlebar_right.setVisibility(View.GONE);
					editcheckbox.setVisibility(View.GONE);
				} else if (titlebar_right.getVisibility() == View.GONE) {
					titlebar_right.setVisibility(View.VISIBLE);
				}
				new AsyncTask<Void, Void, Integer>() {

					@Override
					protected Integer doInBackground(Void... params) {
						idMap.clear();
						for (int i = 0; i < noticeModules.size(); i++) {
							idMap.put(noticeModules.get(i).getMesssageId(), i);
						}
						if (getAssocActivity() == null) {
							return null;
						}
						Intent intent = getAssocActivity().getIntent();
						if (intent == null) {
							return null;
						}
						String messageId = intent.getStringExtra("messageId");
						Integer position = idMap.get(messageId);
						return position;
					}

					protected void onPostExecute(Integer result) {
						if (result != null) {
							noticelist.setSelection(result);
							readNotice(result);
							getAssocActivity().getIntent().removeExtra(
									"messageId");
						}
					};
				}.execute();
			}

		});
		noticelist.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		noticelist.setAdapter(noticeListAdapter);
		new AsyncTask<String, Integer, String>() {

			protected void onPreExecute() {
				showCustomDialog(true);
			};

			@Override
			protected String doInBackground(String... params) {
				noticeModules.clear();
				List<NoticeModuleMessage> queryData = new ArrayList<NoticeModuleMessage>();
				try {
					String userName = Preferences.getUserName(Application.sharePref);
					queryData.addAll(StaticReference.defMf
							.queryBuilder(NoticeModuleMessage.class)
							.orderBy("sendTime", false).where().eq("userName", userName).query());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				int unreadCount = 0;
				for (NoticeModuleMessage noticeModuleMessage : queryData) {
					if (!noticeModuleMessage.isHasRead()) {
						++unreadCount;
					}
				}
				CubeModule cubeModule = CubeModuleManager.getInstance()
						.getModuleByIdentify(
								TmpConstants.ANNOUCE_RECORD_IDENTIFIER);
				if (cubeModule != null) {
					cubeModule.setMsgCount(unreadCount);
				}
				noticeModules.addAll(queryData);
				return null;
			}

			protected void onPostExecute(String result) {
				cancelDialog();
				noticeListAdapter.notifyDataSetChanged();
			}
		}.execute();

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-8-20 下午6:54:47
	 */
	@Override
	public void onResume() {
		super.onResume();
		application.setShouldSendNoticeNotification(false);
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
				new PresenceEvent(TmpConstants.VIEW_ANNOUNCE_PRESENCE, true));

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-9-3 上午11:30:11
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		application.setShouldSendNoticeNotification(true);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-8-20 下午6:56:11
	 */
	@Override
	public void onStop() {
		super.onStop();
		doUnEdit();
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
				new PresenceEvent(TmpConstants.VIEW_ANNOUNCE_PRESENCE, false));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getEventBus(TmpConstants.EVENTBUS_ANNOUNCE_CONTENT,
				ThreadEnforcer.MAIN).unregister(this);
	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName();
	}

	public Dialog progressDialog;

	public void showCustomDialog(boolean cancelable) {
		if (progressDialog == null) {
			progressDialog = new Dialog(getAssocActivity(), R.style.dialog);
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

}
