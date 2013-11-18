package com.foreveross.chameleon.pad.fragment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.format.DateFormat;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.CubeConstants;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.activity.FacadeActivity;
import com.foreveross.chameleon.event.ConnectStatusChangeEvent;
import com.foreveross.chameleon.event.ConversationChangedEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.activity.Player;
import com.foreveross.chameleon.phone.activity.PushSettingActivity;
import com.foreveross.chameleon.phone.chat.chatroom.ChatRoomAdapter;
import com.foreveross.chameleon.phone.chat.chatroom.LocalModule;
import com.foreveross.chameleon.phone.chat.chatroom.MyPagerAdapter;
import com.foreveross.chameleon.phone.chat.chatroom.PicutureDetailActivity;
import com.foreveross.chameleon.phone.chat.image.CropImage;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.phone.muc.MucBroadCastEvent;
import com.foreveross.chameleon.phone.muc.MucManagerActivity;
import com.foreveross.chameleon.phone.view.ChatroomLayout;
import com.foreveross.chameleon.push.client.NotificationService;
import com.foreveross.chameleon.push.client.XmppManager.RosterManager;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.store.core.StaticReference;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.ExpressionUtil;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.PadUtils;
import com.foreveross.chameleon.util.Preferences;
import com.foreveross.chameleon.util.PreferencesUtil;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

/**
 * <BR>
 * [功能详细描述] 即时通信好友聊天界面 Activity迁移至Fragment中
 * 
 * @author Amberlo
 * @version [CubeAndroid , 2013-6-13]
 */
public class ChatRoomFragment extends Fragment {

	private final static Logger log = LoggerFactory
			.getLogger(ChatRoomFragment.class);

	private Button titlebar_left;
	private Button titlebar_right;
	private Button collect_friend;
	private TextView titlebar_content;
	private ListView listview;
	private EditText edittext;
	private Button postButton;
	private Button chat_change_btn;
	private Button chat_emotion_btn;
	private Button chat_voice;
	/** 加号--表情、照相按钮切换 */
	private Button chat_plus_btn;
	/** 语音键盘切换按钮状态 */
	private int changeButtonStatus = CHANGE_KEYBOARD;
	private static int CHANGE_KEYBOARD = 0x001;
	private static int CHANGE_VOICE = 0x002;
	/** 语音键盘布局 */
	private RelativeLayout layout_voice;
	private RelativeLayout layout_keyboard;
	private ProgressDialog progressDialog;
	private RelativeLayout flowview;
	private RelativeLayout chat_send_layout;
	private LinearLayout chat_net_exception;
	private TextView chat_error_message;
	private Dialog dialog;
	public static UserModel userModel = null;
	private ChatRoomAdapter adapter;
	private List<ConversationMessage> conversations = new ArrayList<ConversationMessage>();
	private BroadcastReceiver broadcastReceiver;
	private String currentAccount;
	/** 加载更多历史 */
	private static final int RECORD_START = 0xb;
	private static final int RECORD_END = 0xc;
	private static final int RECORD_ING = 0xd;
	private static final int RECORD_SHORT = 0xe;

	/** 录音器 */
	private MediaRecorder mr;
	private Application application;
	private RosterManager rosterManager;
	/** 面板，点击面板，键盘表情框关闭 */
	private ChatroomLayout chatroom_layout_content;
	
	private RelativeLayout chat_popwindows;
	
	private LinearLayout chat_pop_transparent;
	/**  ------------------------------------------ 表情部分  ------------------------------------- */
	/** 点击加号后出现的框框 */
	private LinearLayout local_module_layout;
	/** 表情框 */
	private RelativeLayout local_ex_layout;
	private ViewPager viewPager;
	private final int EXPRESSION_LENGTH = 107;
	private int[] imageIds = new int[EXPRESSION_LENGTH];
	private List<View> mListViews;
	private MyPagerAdapter pagerAdapter;
	private View[] pageviews;
	private ViewGroup emotionViewGroup;
	private List<LocalModule> modulelList = new ArrayList<LocalModule>();
	private GridView grideView;
	
	private Button chat_opengallery;
	private Button chat_takepicture;
	private Button chat_cancle;
	/** -------------------第三方裁剪图片包 --------------------------------------*/
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";// 拍照缓存图片

	public static final int REQUEST_CODE_GALLERY = 0xf1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0xf2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0xf4;
	private File mFileTemp;// 缓存图片
	private File imageFile;
	
	private String roomId;
	
	private ChatGroupModel chatGroupModel;
	
	private long start = 0;
	
	private boolean voiceStatus;
	
	private boolean roomIsNoExit;
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param outState
	 *            2013-9-2 下午3:16:27
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case RECORD_START:
				if (dialog != null && !dialog.isShowing()) {
					dialog.show();
				}
				break;
			case RECORD_END:
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
				break;
			case RECORD_ING:
				int ampValue = msg.arg1;
				if (ampValue > 28000) {
					wave.getDrawable().setLevel(11);
				} else if (ampValue > 23000) {
					wave.getDrawable().setLevel(10);
				} else if (ampValue > 17000) {
					wave.getDrawable().setLevel(9);
				} else if (ampValue > 13000) {
					wave.getDrawable().setLevel(8);
				} else if (ampValue > 8000) {
					wave.getDrawable().setLevel(7);
				} else if (ampValue > 5000) {
					wave.getDrawable().setLevel(6);
				} else if (ampValue > 3500) {
					wave.getDrawable().setLevel(5);
				} else if (ampValue > 2000) {
					wave.getDrawable().setLevel(4);
				} else if (ampValue > 1000) {
					wave.getDrawable().setLevel(3);
				} else if (ampValue > 400) {
					wave.getDrawable().setLevel(2);
				} else {
					wave.getDrawable().setLevel(1);
				}
				break;
			case RECORD_SHORT:
				break;
			}
		}

	};

	@Subscribe
	public void onConversationsChanged(
			ConversationChangedEvent conversationChangedEvent) {
		
		application.getUIHandler().post(new Runnable() {

			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		super.onCreateView(inflater, container, savedInstanceState);
		application = Application.class.cast(getAssocActivity()
				.getApplicationContext());
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON, ThreadEnforcer.MAIN).register(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_CHAT, ThreadEnforcer.MAIN).register(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_MESSAGE_CONTENT,ThreadEnforcer.MAIN).register(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST,
				ThreadEnforcer.MAIN).register(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH,
				ThreadEnforcer.MAIN).register(this);
		return inflater.inflate(R.layout.chat_chatroom, container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initValues(view);
	}

	private void initValues(View view) {
		voiceStatus = false;
		if (userModel != null){
			userModel = null;
		}
		if (chatGroupModel != null){
			chatGroupModel = null;
		}
		
		chatroom_layout_content = (ChatroomLayout)view.findViewById(R.id.chatroom_layout_content);
		chatroom_layout_content.setTouchCallback(new ChatroomLayout.TouchCallback() {
			
			@Override
			public void onTouchCallbackEvent() {
				closeOtherWindow();
			}
		});
		
		//清除当前聊天的jid
		Preferences.saveChatJid("", Application.sharePref);
		Intent i = getAssocActivity().getIntent();
		String chat = i.getStringExtra("chat");
		chat_popwindows = (RelativeLayout) view.findViewById(R.id.chat_popwindows);
		chat_opengallery = (Button) view.findViewById(R.id.chat_opengallery);
		chat_opengallery.setOnClickListener(mClickListener);
		chat_takepicture = (Button) view.findViewById(R.id.chat_takepicture);
		chat_takepicture.setOnClickListener(mClickListener);
		chat_cancle = (Button) view.findViewById(R.id.chat_cancle);
		chat_cancle.setOnClickListener(mClickListener);
		chat_pop_transparent = (LinearLayout) view.findViewById(R.id.chat_pop_transparent);
		chat_pop_transparent.setOnClickListener(mClickListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setText("管理");
		titlebar_right.setOnClickListener(mClickListener);
		chat_change_btn = (Button) view.findViewById(R.id.chat_change_btn);
		chat_change_btn.setOnClickListener(mClickListener);
		chat_emotion_btn = (Button) view.findViewById(R.id.chat_emotion_btn);
		chat_emotion_btn.setOnClickListener(mClickListener);
		collect_friend = (Button) view.findViewById(R.id.chatroom_collect_friend_icon);
		collect_friend.setOnClickListener(mClickListener);
		chat_error_message = (TextView) view.findViewById(R.id.chat_error_message);
		chat_net_exception = (LinearLayout) view.findViewById(R.id.chat_net_exception);
		chat_send_layout = (RelativeLayout) view.findViewById(R.id.chat_send_layout);
		if ("room".equals(chat)) {
//			changeButton.setVisibility(View.GONE);
			roomId = i.getStringExtra("jid");
			
			//保存当前聊天用户
			Preferences.saveChatJid(roomId, Application.sharePref);
			chatGroupModel = IMModelManager.instance()
					.getChatRoomContainer().getStuff(roomId);
			
			if (chatGroupModel != null) {
				roomIsNoExit = false;
				titlebar_right.setVisibility(View.VISIBLE);
				CubeModule module = CubeModuleManager.getInstance()
						.getCubeModuleByIdentifier(TmpConstants.CHAT_RECORD_IDENTIFIER);
				if (module != null) {
					module.decreaseMsgCountBy(chatGroupModel.getUnreadMessageCount());
				}
				chatGroupModel.clearNewMessageCount();
				conversations = chatGroupModel.getConversations();
			} else {
				roomIsNoExit = true;
				titlebar_right.setVisibility(View.GONE);
				// 用戶群为已解散用户群
				// 构建一个假的chatGroupModel
				chatGroupModel = new ChatGroupModel();
				SessionModel model = IMModelManager.instance()
						.getSessionContainer().getStuff(roomId);
				chatGroupModel.setGroupName(model.getRoomName());
				chatGroupModel.setGroupCode(roomId);
				chatGroupModel.setRoomJid(roomId);
				conversations = chatGroupModel.findLastHistory(-1);
				Log.i("conversations" , conversations.toString());
				chat_net_exception.setVisibility(View.VISIBLE);
				chat_send_layout.setVisibility(View.GONE);
				chat_error_message.setText("你已退出用户组");
			}
			
		} else {
			titlebar_right.setVisibility(View.GONE);
			collect_friend.setVisibility(View.VISIBLE);
			String jid = i.getStringExtra("jid");
			Preferences.saveChatJid(jid, Application.sharePref);
			userModel = IMModelManager.instance().getUserModel(jid);
			if (userModel != null) {
				if (userModel.isFavor()){
					collect_friend.setText("取消关注");

				} else {
					collect_friend.setText("关注");
				}
				CubeModule module = CubeModuleManager.getInstance()
						.getCubeModuleByIdentifier(TmpConstants.CHAT_RECORD_IDENTIFIER);
				if (module != null) {
					module.decreaseMsgCountBy(userModel.getUnreadMessageCount());
				}
				userModel.clearNewMessageCount();
				conversations = userModel.getConversations();
			}
		}
		
		flowview = (RelativeLayout) view.findViewById(R.id.chat_room_flowview);
		flowview.setOnClickListener(mClickListener);
		if (application.getNotificationService() != null){
			currentAccount = Preferences.getUserName(Application.sharePref) + "@"
					+ application.getNotificationService().getXmppServiceName();
		}

		PreferencesUtil.setValue(getAssocActivity(), "currentAccount",
				currentAccount);

		titlebar_left = (Button) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(mClickListener);
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		if (chatGroupModel != null){
			titlebar_content.setText(chatGroupModel.getGroupName());
		} 
		if (userModel != null){
			titlebar_content.setText(userModel.getName());
		}
		
		titlebar_content.setEllipsize(TruncateAt.END);
		titlebar_content.setSingleLine(true);
		listview = (ListView) view.findViewById(R.id.chat_history_lv);
		listview.setSelected(true);

		edittext = (EditText) view.findViewById(R.id.chat_content_et);
		edittext.setOnFocusChangeListener(focusChangeListener);
		edittext.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
/*				if (local_module_layout.isShown()) {
					local_module_layout.setVisibility(View.GONE);
				}*/
				chat_change_btn.setBackgroundResource(R.drawable.voice_button_selector);
				local_ex_layout.setVisibility(View.GONE);
				return false;
			}
		});

		postButton = (Button) view.findViewById(R.id.chat_btn_sendcontent);
		postButton.setOnClickListener(mClickListener);
		chat_plus_btn = (Button) view.findViewById(R.id.chat_plus_btn);
		chat_plus_btn.setOnClickListener(mClickListener);
		chat_voice = (Button) view.findViewById(R.id.chat_voice);
		chat_voice.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (application.getNotificationService() != null 
						&& !application.getNotificationService().isOnline()) {
					
					if (!roomIsNoExit){
						flowview.setVisibility(View.VISIBLE);
						chat_net_exception.setVisibility(View.VISIBLE);
						chat_send_layout.setVisibility(View.GONE);
					}
					return true;
				}

				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					mHandler.sendEmptyMessage(RECORD_START);
					start = 0;
					start = System.currentTimeMillis();
					if (userModel != null){
						longimplement(userModel.getName());
					}
					if (chatGroupModel != null){
						longimplement(chatGroupModel.getGroupName());
					}
//					voiceButton
//							.setBackgroundResource(R.drawable.chatroom_voice_button_click);

					isStart.set(true);
					Thread ampThread = new Thread(ampTask);
					ampThread.start();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// 延时一秒
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					mHandler.sendEmptyMessage(RECORD_END);
					isStart.set(false);
//					voiceButton
//							.setBackgroundResource(R.drawable.chatroom_voice_button);
					// 此处触发停止事件
					if (mr != null) {
						// 停止录音
						 mr.stop();
						// 释放录音
						try {
							mr.reset();
							mr.release();
						} catch (Exception e) {
							e.printStackTrace();
						}

						
						// 翻译资源
						mr = null;
					}
					if (System.currentTimeMillis() - start > 1500) {
						Toast.makeText(getAssocActivity(), "录制成功",
								Toast.LENGTH_SHORT).show();
						String fromWho = currentAccount;
						String toWho = null;
						if (userModel != null){
							toWho = userModel.getJid();
						}
						if (chatGroupModel != null){
							toWho = chatGroupModel.getRoomJid();
						}
						
						final ConversationMessage conversation = createConversation(
								myRecAudioFile.getPath(), fromWho, toWho, "voice");
						// 发送消息至服务器
						// ====================MARK
						HttpRequestAsynTask uploadTask = new HttpRequestAsynTask(ChatRoomFragment.this.getAssocActivity()) {

							@Override
							protected void doPostExecute(String result) {

								if("error".equals(result)){
									Toast.makeText(ChatRoomFragment.this.getAssocActivity(), "发送失败", Toast.LENGTH_SHORT).show();
									return;
								}
								try {
									JSONObject json = new JSONObject(result);
									String id = json.getString("id");
									conversation.setPicId(id);
									sendMessage(conversation);
								} catch (JSONException e) {
									e.printStackTrace();
									Toast.makeText(ChatRoomFragment.this.getAssocActivity(), "发送失败", Toast.LENGTH_SHORT).show();
								}
							}
						};
						uploadTask.setNeedProgressDialog(false);
						uploadTask.setShowProgressDialog(false);
						StringBuilder sb = new StringBuilder();
						sb.append("UPLOAD:file=").append(new File(myRecAudioFile.getPath()))
							.append(";enctype=").append("multipart/form-data")
							.append(";sessionKey=").append(URL.getSessionKey())
							.append(";appKey=").append(URL.getAppKey());
						uploadTask.execute(URL.UPLOAD_URL, sb.toString(),HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);
					} else {
						if (myRecAudioFile.exists()) {
							myRecAudioFile.delete();
						}
						Toast.makeText(getAssocActivity(), "您讲话的时间太短了",
								Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});

//		layout_voice = (RelativeLayout) view.findViewById(R.id.chat_layout_voice);
//		layout_keyboard = (RelativeLayout) view.findViewById(R.id.chat_layout_keyboard);

		if (userModel != null){
			adapter = new ChatRoomAdapter(getAssocActivity(), conversations, userModel);
		}
		if (chatGroupModel != null){
			adapter = new ChatRoomAdapter(getAssocActivity(), conversations, chatGroupModel);
		}
		listview.setAdapter(adapter);
		listview.setSelection(conversations.size());

		if (chatGroupModel != null){
			if (!roomIsNoExit){
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						chatGroupModel.findHistory(-1);
						return null;
					}
					protected void onPostExecute(Void result) {
						listview.setSelection(adapter.getCount());
						adapter.notifyDataSetChanged();
					};
				}.execute();
			}
		}
		
		progressDialog = new ProgressDialog(getAssocActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("提示");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setMessage("正在录音...");

		dialog = createDialog();
		NotificationService notificationService = Application.class.cast(
				getAssocActivity().getApplication()).getNotificationService();
		if (notificationService != null && notificationService.isOnline()) {
			if (!roomIsNoExit){
				flowview.setVisibility(View.GONE);
				chat_net_exception.setVisibility(View.GONE);
				chat_send_layout.setVisibility(View.VISIBLE);
			}

		} else {
			if (!roomIsNoExit){
				flowview.setVisibility(View.VISIBLE);
				chat_net_exception.setVisibility(View.VISIBLE);
				chat_send_layout.setVisibility(View.GONE);
			}
			
		}

		/** ----------------------- 表情部分 ----------------------- */
//		local_module_layout = (LinearLayout) view.findViewById(R.id.local_module_layout);
		local_ex_layout = (RelativeLayout) view.findViewById(R.id.local_expression_layout);
		viewPager = (ViewPager) view.findViewById(R.id.viewpagerLayout);
		emotionViewGroup = (ViewGroup) view.findViewById(R.id.viewGroup);
		ExpressionUtil.getInstance().ParseExpressionFileData(this.getAssocActivity(), 26);
		createExpressionPageView();
		LocalModule emotions = new LocalModule();
		emotions.setModuleName("表情");
		emotions.setLocalClass("emotion");
		emotions.setIcon(getResources().getDrawable(R.drawable.emotions_big));
		emotions.setSortNum(2);
		modulelList.add(emotions);
		LocalModule photographer = new LocalModule();
		photographer.setModuleName("拍照");
		photographer.setLocalClass("photographer");
		photographer.setIcon(getResources().getDrawable(R.drawable.photo_icon_big));
		photographer.setSortNum(1);
		modulelList.add(photographer);
		LocalModule localPic = new LocalModule();
		localPic.setModuleName("图片");
		localPic.setLocalClass("picture");
		localPic.setIcon(getResources().getDrawable(R.drawable.local_pic_big));
		localPic.setSortNum(0);
		modulelList.add(localPic);
/*		grideView = (GridView) view.findViewById(R.id.grideView);
		grideView.setAdapter(new GridVeiwListAdapter(this.getAssocActivity(),modulelList));
		grideView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int index, long arg3) {
				if (modulelList.get(index).getLocalClass().equals("emotion")) {
					// 表情
					if (local_module_layout.isShown()) {
						local_module_layout.setVisibility(View.GONE);
						local_ex_layout.setVisibility(View.VISIBLE);
						// viewPager.setVisibility(View.VISIBLE);
					}
				} else if (modulelList.get(index).getLocalClass()
						.equals("picture")) {
					// 关闭键盘
//					InputMethodManager imm2 = (InputMethodManager) getAssocActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm2.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
					closeKeyboard();
					 openGallery();
				} else if (modulelList.get(index).getLocalClass()
						.equals("photographer")) {
					// 关闭键盘
//					InputMethodManager imm2 = (InputMethodManager) getAssocActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm2.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
					closeKeyboard();
					 takePicture();

				}
			}
		});*/
		String path = Environment.getExternalStorageDirectory()+ "/CubeImageCache/sendFiles/";
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		path += TEMP_PHOTO_FILE_NAME;
		mFileTemp = new File(path);
		if (!mFileTemp.exists()) {
			try {
				mFileTemp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
//		Log.e("------------------------", "文件路径：" + mFileTemp.getPath());
	}

	ImageView wave;

	public Dialog createDialog() {

		View v = getAssocActivity().getLayoutInflater().inflate(
				R.layout.chat_voice_record, null);
		Dialog dialog = new Dialog(getAssocActivity(), R.style.voice_dialog);
		dialog.setContentView(v);

		wave = (ImageView) v.findViewById(R.id.chat_wave);

		return dialog;
	}

	private View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_barleft:
				if (getAssocActivity() instanceof FacadeActivity) {
					FacadeActivity.class.cast(getAssocActivity()).popRight();
				} else {
					getAssocActivity().finish();
					application.getActivityManager().pushActivity(getAssocActivity());
				}

				break;
			case R.id.chat_btn_sendcontent:
				if (application.getNotificationService() != null &&
				!application.getNotificationService().isOnline()) {
					if (!roomIsNoExit){
						flowview.setVisibility(View.VISIBLE);
						chat_net_exception.setVisibility(View.VISIBLE);
						chat_send_layout.setVisibility(View.GONE);
					}
					return;
				}
				String content = edittext.getText().toString();
				String fromWho = currentAccount;
				String toWho = null;
				if (userModel != null){
					toWho = userModel.getJid();
				}
				if (chatGroupModel != null){
					toWho = chatGroupModel.getRoomJid();
				}
				if (!TextUtils.isEmpty(content)) {
					edittext.setText("");
					ConversationMessage newConversation = createConversation(
							content, fromWho, toWho, "text");
					sendMessage(newConversation);
				}
				break;
			case R.id.chat_change_btn:
				if (local_ex_layout.getVisibility() == View.VISIBLE){
					local_ex_layout.setVisibility(View.GONE);
				}
				closeKeyboard();
				if (voiceStatus){
					chat_change_btn.setBackgroundResource(R.drawable.inputtext_button_selector);
					voiceStatus = false;
					chat_voice.setVisibility(View.VISIBLE);
					edittext.setVisibility(View.GONE);
				} else {
					chat_change_btn.setBackgroundResource(R.drawable.voice_button_selector);
					voiceStatus = true;
					chat_voice.setVisibility(View.GONE);
					edittext.setVisibility(View.VISIBLE);
				}
/*				if (changeButtonStatus == CHANGE_KEYBOARD) {
					// changeButton.setText("键盘");
					changeButton.setBackgroundResource(R.drawable.xmpp_text);
					changeButtonStatus = CHANGE_VOICE;
					layout_voice.setVisibility(View.VISIBLE);
					layout_keyboard.setVisibility(View.GONE);
					// 当EidtText无焦点（focusable=false）时阻止输入法弹出
					edittext.clearFocus();
//					InputMethodManager imm = (InputMethodManager) getAssocActivity()
//							.getSystemService(Context.INPUT_METHOD_SERVICE);
//					imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
					closeKeyboard();
					// 切换成语音时把表情窗口关掉
					local_ex_layout.setVisibility(View.GONE);
					// 切换到选择表情窗口
					if (local_module_layout.isShown()) {
						local_module_layout.setVisibility(View.GONE);
					}
					hideFaceView();
				} else if (changeButtonStatus == CHANGE_VOICE) {
					// changeButton.setText("声音");
					changeButton.setBackgroundResource(R.drawable.xmpp_voice);
					changeButtonStatus = CHANGE_KEYBOARD;
					layout_voice.setVisibility(View.GONE);
					layout_keyboard.setVisibility(View.VISIBLE);
				}*/
				break;
			case R.id.chat_emotion_btn:
//				if (!voiceStatus){
//					break;
//				}
				chat_change_btn.setBackgroundResource(R.drawable.voice_button_selector);
				voiceStatus = true;
				chat_voice.setVisibility(View.GONE);
				edittext.setVisibility(View.VISIBLE);
				if (local_ex_layout.getVisibility() == View.VISIBLE){
					local_ex_layout.setVisibility(View.GONE);
				} else {
					local_ex_layout.setVisibility(View.VISIBLE);
				}
				closeKeyboard();
				break;
//			case R.id.chatroom_layout_content:
//				closeOtherWindow();
//				break;
			case R.id.chat_room_flowview:
				Intent i = new Intent();
				i.setClass(getAssocActivity(), PushSettingActivity.class);
				startActivity(i);
				break;
			case R.id.chat_plus_btn:
				chat_popwindows.setVisibility(View.VISIBLE);
				closeOtherWindow();
				break;
			case R.id.chat_takepicture:
				chat_popwindows.setVisibility(View.GONE);
				takePicture();
				break;
			case R.id.chat_opengallery:
				chat_popwindows.setVisibility(View.GONE);
				openGallery();
				break;

			case R.id.chat_cancle:
				chat_popwindows.setVisibility(View.GONE);
				break;
				
		    case R.id.chat_pop_transparent:
		    	chat_popwindows.setVisibility(View.GONE);
		    	break;
			case R.id.title_barright:
				if (application.getNotificationService() != null && 
						!application.getNotificationService().isOnline()) {
					if (!roomIsNoExit){
						flowview.setVisibility(View.VISIBLE);
						chat_net_exception.setVisibility(View.VISIBLE);
						chat_send_layout.setVisibility(View.GONE);
					}
					return;
				}
				if (PadUtils.isPad(getAssocActivity())) {
					PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(ChatRoomFragment.this.getAssocActivity(), CubeConstants.CUBE_CONFIG);
					String mucmanager = propertiesUtil.getString("mucmanager", "");
					Intent intent = new Intent();
					intent.putExtra("direction", 2);
					intent.putExtra("type", "fragment");
					intent.putExtra("roomJid", roomId);
					intent.putExtra("value", mucmanager);
					intent.setClass(getAssocActivity(), FacadeActivity.class);
					getAssocActivity().startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(getAssocActivity(), MucManagerActivity.class);
					intent.putExtra("roomJid", roomId);
					getAssocActivity().startActivity(intent);
				}
				break;
			case R.id.chatroom_collect_friend_icon:
				if (application.getNotificationService() != null && 
						!application.getNotificationService().isOnline()) {
					if (!roomIsNoExit){
						flowview.setVisibility(View.VISIBLE);
						chat_net_exception.setVisibility(View.VISIBLE);
						chat_send_layout.setVisibility(View.GONE);
					}
					return;
				}
				Application application = Application.class.cast(getAssocActivity()
						.getApplicationContext());
				HttpRequestAsynTask collectedFriendTask = new HttpRequestAsynTask(
						getAssocActivity()) {

					@Override
					protected void doPostExecute(String result) {
						Log.e("collectedFriendTask", result);
						super.doPostExecute(result);

						if (result != null) {
							if (userModel.isFavor()){
								collect_friend.setText("取消关注");
								Toast.makeText(getAssocActivity(), 
										"收藏成功", Toast.LENGTH_SHORT).show();
							} else {
								collect_friend.setText("关注");
								Toast.makeText(getAssocActivity(), 
										"删除收藏成功", Toast.LENGTH_SHORT).show();
							}
						}
					}

				};
				if (userModel.isFavor()){
					userModel.setFavor(false);
					userModel.update();
					String url = URL.CHATDELETE
							+ "/"
							+ Preferences.getUserName(Application.sharePref)
							+ "@"
							+ application.getChatManager().getConnection()
									.getServiceName() + "/" + userModel.getJid() + URL.getSessionKeyappKey();
					collectedFriendTask.execute(url, "",
							HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
					IMModelManager.instance().getFavorContainer()
					.notifyContentChange();
				} else {
					userModel.setFavor(true);
					userModel.update();
					String url = URL.CHATSAVE;
					StringBuilder sb = new StringBuilder();
					Log.e("添加删除好友", userModel.getName());
					sb = sb.append("Form:jid=")
							.append(userModel.getJid())
							.append(";username=")
							.append(userModel.getName())
							.append(";sex=")
							.append(userModel.getSex())
							.append(";status=")
							.append(userModel.getStatus())
							.append(";userId=")
							.append(Preferences
									.getUserName(Application.sharePref)
									+ "@"
									+ application.getChatManager()
											.getConnection().getServiceName())
							.append(";sessionKey=").append(URL.getSessionKey())
							.append(";appKey=").append(URL.getAppKey());;
					collectedFriendTask.execute(url, sb.toString().trim(),
							HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);
					IMModelManager.instance().getFavorContainer()
					.notifyContentChange();
				}
				break;
				
			default:
				break;
			}
		}
	};

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

	private void sendMessage(ConversationMessage conversation) {

		if (application.getNotificationService() != null && 
				application.getNotificationService().isOnline()) {
			
			if (conversation.getType().equals("voice")) {
				String filePath = conversation.getContent();
				String fileId = conversation.getPicId();
				
				conversation.setContent(fileId);
				if (roomId != null && !"".equals(roomId)){
					chatGroupModel.sendMessage(getAssocActivity(),conversation);
//					Toast.makeText(getAssocActivity(), "群组功能不支持语音聊天", Toast.LENGTH_SHORT).show();
				} else {
					application.getNotificationService().sendMessage(conversation);
				}
				conversation.setContent(filePath);
			} else if (conversation.getType().equals("text")) {

				try {
					conversation.setContent(new String(conversation.getContent()
							.getBytes(), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (roomId != null && !"".equals(roomId)){
					chatGroupModel.sendMessage(getAssocActivity(),conversation);
				} else {
					application.getNotificationService().sendMessage(conversation);
				}
			}  else if (conversation.getType().equals("image")) {
				
				String filePath = conversation.getContent();
				String fileId = conversation.getPicId();
				
				conversation.setContent(fileId);
				if (roomId != null && !"".equals(roomId)){
					chatGroupModel.sendMessage(getAssocActivity(),conversation);
//					Toast.makeText(getAssocActivity(), "群组功能不支发送图片", Toast.LENGTH_SHORT).show();
				} else {
					application.getNotificationService().sendMessage(conversation);
				}
				conversation.setContent(filePath);
				
			}else {
				throw new UnsupportedOperationException("未知消息类型!");
			}
			conversations.add(conversation);
			SessionModel sessionModel = IMModelManager.instance().getSessionContainer().getSessionModel(conversation.getChater(), true);
			if (roomId != null && !"".equals(roomId)){
				sessionModel.setFromType(SessionModel.SESSION_ROOM);
				sessionModel.setRoomName(chatGroupModel.getGroupName());
			} else {
				sessionModel.setFromType(SessionModel.SESSION_SINGLE);
			}
			sessionModel.setFromWhich(conversation.getFromWho());
			sessionModel.setToWhich(conversation.getToWho());
			sessionModel.setChatter(conversation.getChater());
			sessionModel.setSendTime(System.currentTimeMillis());
			if (conversation.getType().equals("voice")){
				sessionModel.setLastContent("[声音]");
			} else if (conversation.getType().equals("image")){
				sessionModel.setLastContent("[图片]");
			} else if (conversation.getType().equals("text")){
				sessionModel.setLastContent(conversation.getContent());
			}
			if (userModel != null){
				sessionModel.setStatus(userModel.getStatus());
				userModel.setLastMessage(conversation);
			}
			StaticReference.userMf.createOrUpdate(sessionModel);
			if (chatGroupModel != null){
				chatGroupModel.setLastMessage(conversation);
			
			}
			listview.setSelection(conversations.size());
			StaticReference.userMf.createOrUpdate(conversation);
		} else {
			Toast.makeText(getAssocActivity(), "连接服务器失败", Toast.LENGTH_SHORT)
					.show();
			if (!roomIsNoExit){
				flowview.setVisibility(View.VISIBLE);
				chat_net_exception.setVisibility(View.VISIBLE);
				chat_send_layout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON, ThreadEnforcer.MAIN).unregister(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_CHAT, ThreadEnforcer.MAIN).unregister(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_MESSAGE_CONTENT,ThreadEnforcer.MAIN).unregister(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH,ThreadEnforcer.MAIN).unregister(this);
		EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST,
				ThreadEnforcer.MAIN).unregister(this);
		CubeModule messageModule = CubeModuleManager.getInstance()
				.getCubeModuleByIdentifier(TmpConstants.CHAT_RECORD_IDENTIFIER);
		if (userModel != null){
			messageModule.decreaseMsgCountBy(userModel.getUnreadMessageCount());
			userModel.clearNewMessageCount();
		}
		if (chatGroupModel != null){
			messageModule.decreaseMsgCountBy(chatGroupModel.getUnreadMessageCount());
			chatGroupModel.clearNewMessageCount();
		}
	}

	/** 录音文件路径 */
	private File myRecAudioFile = null;

	/**
	 * 长按按钮时要触发的事件实现
	 */
	private synchronized void longimplement(String chater) {

		File recordDir = new File(Environment.getExternalStorageDirectory()
				.getPath() + "/" + TmpConstants.RECORDER_SEND_PATH);
		/*
		 * 取得SD Card路径做为录音的文件位置
		 */
		try {
			if (!recordDir.exists()) {
				recordDir.mkdirs();
			}
			try {
				// 获取录音文件时间以此为文件名，唯一性
				long time = System.currentTimeMillis();
				if (time != 0) {
					String datetime = DateFormat.format("yyyyMMddhhmmssfff",
							time).toString();
					myRecAudioFile = new File(recordDir + "/" + chater + " "
							+ datetime + ".aac");
				}

				/*
				 * 建立录音文件
				 */
				if (mr == null) {
					mr = new MediaRecorder();
					// 设置资源来源于麦克风
					mr.setAudioSource(MediaRecorder.AudioSource.MIC);
					// 设置输出格式
					mr.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
					// 设置编码方式
					 mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
					// 设置输出路径
					mr.setOutputFile(myRecAudioFile.getAbsolutePath());
					// 设置录制的音频通道数
					mr.setAudioChannels(2);
					// 设置录制的音频编码比特率
					mr.setAudioEncodingBitRate(44100);
					// 准备录音
					mr.prepare();
					// 开始录音
					mr.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-9-3 上午11:30:58
	 */
	@Override
	public void onPause() {
		super.onPause();
		application.setShouldSendChatNotification(true);
	}

	/* 抬起事件监听* */

	@Override
	public void onStop() {
		super.onStop();
		Log.i("KKK", "调用onStop()方法，关闭播放并释放资源");
		Player.handler.sendEmptyMessage(1);
		if (mr != null) {
			mr.stop();
			mr.release();
			mr = null;
			Log.i("KKK", "调用onStop()方法,释放资源完毕");
		}
		if (adapter != null){
			adapter.stopPlay();
		}
		//清除当前聊天的jid
		Preferences.saveChatJid("", Application.sharePref);
		application.setIsInChatRoomFragment(false);
	}

	public int getAmplitude() {
		if (null != mr) {
			return mr.getMaxAmplitude();
		}
		return 0;
	}

	private AtomicBoolean isStart = new AtomicBoolean(false);
	private Runnable ampTask = new Runnable() {

		@Override
		public void run() {
			try {
				while (isStart.get()) {
					if (isStart.get()) {
						android.os.Message msg = mHandler.obtainMessage();
						msg.arg1 = getAmplitude();
						msg.what = RECORD_ING;
						mHandler.sendMessage(msg);
					}
					Thread.sleep(150);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**********************************************************************************
	 * 
	 *
	 * 
	 **********************************************************************************/

	public void onResume() {
		super.onResume();
		application.setShouldSendChatNotification(false);
		application.setIsInChatRoomFragment(true);
	};

	@Subscribe
	public void onConnectStatusChnageEvent(
			ConnectStatusChangeEvent connectStatusChnageEvent) {
		String status = connectStatusChnageEvent.getStatus();
		if (ConnectStatusChangeEvent.CONN_STATUS_ONLINE.equals(status)) {
			if (!roomIsNoExit){
				flowview.setVisibility(View.GONE);
				chat_net_exception.setVisibility(View.GONE);
				chat_send_layout.setVisibility(View.VISIBLE);
			}
		} else {
			if (!roomIsNoExit){
				flowview.setVisibility(View.VISIBLE);
				chat_net_exception.setVisibility(View.VISIBLE);
				chat_send_layout.setVisibility(View.GONE);
			}
		}
	}

	/** ----------------------------- 表情部分 -------------------------------- */

	/**
	 * 创建一个表情选择对话框
	 */
	private void createExpressionPageView() {
		mListViews = new ArrayList<View>();
		pagerAdapter = new MyPagerAdapter(mListViews);

		View view;
		int page = ExpressionUtil.getInstance().getPage();
		pageviews = new View[page];
		for (int i = 0; i < page; i++) {
			view = new View(this.getAssocActivity());
			view.setLayoutParams(new LayoutParams(20, 20));

			pageviews[i] = view;
			if (i == 0) {
				pageviews[i].setBackgroundResource(R.drawable.page_current);
			} else {
				pageviews[i].setBackgroundResource(R.drawable.page_others);
			}
			emotionViewGroup.addView(pageviews[i]);
		}
		for (int i = 0; i < page; i++) {
			GridView gridView = createGridView(i);
			final int start = i * ExpressionUtil.getInstance().getLength();
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
//					closeKeyboard();
					int selection = edittext.getSelectionStart();
					if (arg2 > ExpressionUtil.getInstance().getLength() - 1) {

						String text = edittext.getText().toString();
						if (selection > 0) {
							String text2 = text.substring(selection - 1,
									selection);

							if ("]".equals(text2)) {
								String zhengze = "\\[/:[^\\]]+\\]";
								String text3 = text.substring(0, selection);
								Pattern sinaPatten = Pattern.compile(zhengze,
										Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
								Matcher matcher = sinaPatten.matcher(text3);
								String a = null;
								while (!matcher.hitEnd()) {
									if (matcher.find()) {
										a = matcher.group();
									}
								}
								if (a != null) {
									int start = selection - a.length();
									int end = selection;
									edittext.getText().delete(start, end);
								}
								return;
							}
							edittext.getText().delete(selection - 1, selection);
						}
						return;
					}
					ExpressionUtil.ExpressionElement ee = ExpressionUtil
							.getInstance().getExpressionElement(arg2 + start);
					Bitmap bitmap = null;
					bitmap = BitmapFactory.decodeResource(getResources(),
							ee.getId());// 这里是被选中的表情ID,根据页数确认;
					ImageSpan imageSpan = new ImageSpan(ChatRoomFragment.this
							.getAssocActivity(), bitmap);

					SpannableString spannableString = new SpannableString(ee
							.getCode());
					spannableString.setSpan(imageSpan, 0,
							ee.getCode().length(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (selection < edittext.length()) {
						edittext.getText().insert(selection, spannableString);
					} else {
						edittext.append(spannableString);

					}

				}
			});
			mListViews.add(gridView);
		}
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}

	class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			pageviews[arg0].setBackgroundResource(R.drawable.page_current);
			for (int i = 0; i < pageviews.length; i++) {
				if (arg0 != i) {
					pageviews[i].setBackgroundResource(R.drawable.page_others);
				}
			}

		}

	}

	/**
	 * 生成一个表情对话框中的gridview
	 * 
	 * @return
	 */
	private GridView createGridView(int currentPage) {
		GridView view = new GridView(this.getAssocActivity());
		List<Map<String, Object>> listItems = ExpressionUtil.getInstance()
				.getImageIdList(currentPage);

		SimpleAdapter simpleAdapter = new SimpleAdapter(
				this.getAssocActivity(), listItems,
				R.layout.team_layout_single_expression_cell,
				new String[] { "image" }, new int[] { R.id.image });
		view.setAdapter(simpleAdapter);
		view.setNumColumns(9);
		// view.setBackgroundColor(Color.rgb(214, 211, 214));
		view.setHorizontalSpacing(1);
		view.setVerticalSpacing(1);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		view.setGravity(Gravity.CENTER);
		return view;
	}

	/**
	 * 隐藏表情选择框
	 * 
	 * @return
	 */
	public boolean hideFaceView() {
		// 隐藏表情选择框
		if (local_ex_layout != null
				&& local_ex_layout.getVisibility() == View.VISIBLE) {
			local_ex_layout.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	OnFocusChangeListener focusChangeListener =  new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			hideFaceView();
/*			if (local_module_layout.isShown()) {
				local_module_layout.setVisibility(View.GONE);
			}*/
		}
	};
	/** 拍照部分 */
	private void openGallery() {

		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != android.app.Activity.RESULT_OK) {

			return;
		}

		switch (requestCode) {
		case REQUEST_CODE_GALLERY:
			Log.d("回到聊天", "REQUEST_CODE_TAKE_PICTURE");
			Uri uri = data.getData();
			String[] proj = { MediaStore.Images.Media.DATA };
			// 好像是android多媒体数据库的封装接口，具体的看Android文档
			Cursor cursor = getAssocActivity().managedQuery(uri, proj, null, null, null);
			String selectpath = null;
			if (cursor != null) {
				// 是获得用户选择的图片的索引值
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// 将光标移至开头 ，这个很重要，不小心很容易引起越界
				cursor.moveToFirst();
				// 最后根据索引值获取图片路径
				selectpath = cursor.getString(column_index);
				// 4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
				if (Integer.parseInt(Build.VERSION.SDK) < 14) {
					cursor.close();
				}
			}
			Intent detailIntent = new Intent();
			detailIntent.putExtra("imagePath", selectpath);
			detailIntent.putExtra("showFlag", true);
			detailIntent.setClass(getAssocActivity(),
					PicutureDetailActivity.class);
			startActivityForResult(detailIntent, REQUEST_CODE_CROP_IMAGE);
			break;
		case REQUEST_CODE_TAKE_PICTURE:
			Log.d("回到聊天", "REQUEST_CODE_TAKE_PICTURE");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Uri imguri = Uri.fromFile(mFileTemp);
					ChatRoomFragment.this.getAssocActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
							imguri));
					startCropImage();
					// 清除缓存
				}
			}).start();
			break;
		case REQUEST_CODE_CROP_IMAGE:

			String path = data.getStringExtra(CropImage.IMAGE_PATH);
			Log.e("REQUEST_CODE_CROP_IMAGE",
					"data.getStringExtra(CropImage.IMAGE_PATH)=" + path);
			if (path == null) {

				return;
			}
			// mImageView.setImageBitmap(bitmap);
			try {
				String fromWho = currentAccount;
				String toWho = null;
				if (userModel != null){
					toWho = userModel.getJid();
				}
				if (chatGroupModel != null){
					toWho = chatGroupModel.getRoomJid();
				}
				Log.d("HttpRequestAsynTask", "fromWho=" + fromWho + "   toWho="+ toWho);
				final ConversationMessage conversation = createConversation(path,
						fromWho, toWho, "image");
				if (application.getNotificationService() != null && 
						!application.getNotificationService().isOnline()) {
					Toast.makeText(ChatRoomFragment.this.getAssocActivity(), "网络连接出现错误", Toast.LENGTH_SHORT).show();
					if (!roomIsNoExit){
						flowview.setVisibility(View.VISIBLE);
						chat_net_exception.setVisibility(View.VISIBLE);
						chat_send_layout.setVisibility(View.GONE);
					}
					return;
				}
				// ====================直接显示
//				conversation.create();
//				userModel.setLastMessage(conversation);
//				Intent intent = new Intent(BroadcastConstans.PUSH_USER_CHAT_ACTION);
//				intent.putExtra("from", conversation.getToWho());
//				sendBroadcast(intent);

				// ====================MARK
				HttpRequestAsynTask uploadTask = new HttpRequestAsynTask(ChatRoomFragment.this.getAssocActivity()) {

					@Override
					protected void doPostExecute(String result) {
						if("error".equals(result)){
							Toast.makeText(ChatRoomFragment.this.getAssocActivity(), "发送失败", Toast.LENGTH_SHORT).show();
							return;
						}
						try {
							JSONObject json = new JSONObject(result);
							String id = json.getString("id");
							conversation.setPicId(id);
							sendMessage(conversation);
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(ChatRoomFragment.this.getAssocActivity(), "发送失败", Toast.LENGTH_SHORT).show();
						}
					}
				};
				uploadTask.setNeedProgressDialog(false);
				uploadTask.setShowProgressDialog(false);
				StringBuilder sb = new StringBuilder();
				sb.append("UPLOAD:file=").append(new File(path))
					
					.append(";sessionKey=").append(URL.getSessionKey())
					.append(";appKey=").append(URL.getAppKey());
				uploadTask.execute(URL.UPLOAD_URL, sb.toString(),HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}
	/**
	 * 
	 * ----------------------------------------一下为引入第三方包
	 * 
	 */
	private void takePicture() {

//		Intent intent = new Intent(ChatRoomFragment.this.getAssocActivity(), CameraActivity.class);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();

			mImageCaptureUri = Uri.fromFile(mFileTemp);
		
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
			startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {
			Log.d("CORPIMAGE", "cannot take picture", e);
		}
		
	}
	private void startCropImage() {

//		Intent intent = new Intent(ChatRoomFragment.this.getAssocActivity(), CropImage.class);
		if (null == mFileTemp) {
			String path = Environment.getExternalStorageDirectory()
					+ "/CubeImageCache/sendFiles/";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			path += TEMP_PHOTO_FILE_NAME;
			mFileTemp = new File(path);
		}
//		intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
//		intent.putExtra(CropImage.SCALE, true);
//
//		intent.putExtra(CropImage.ASPECT_X, 3);
//		intent.putExtra(CropImage.ASPECT_Y, 2);
//		Log.e("CORPIMAGE", "启动裁剪");
//		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
		
		Intent detailIntent = new Intent();
		detailIntent.putExtra("imagePath", mFileTemp.getPath());
		detailIntent.putExtra("showFlag", true);
		detailIntent.setClass(getAssocActivity(),
				PicutureDetailActivity.class);
		startActivityForResult(detailIntent, REQUEST_CODE_CROP_IMAGE);
	}
	
	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}
	
	@Subscribe
	public void onMucManagerEvent(String mucBroadCastEvent) {
		if (MucBroadCastEvent.PUSH_MUC_LEAVE.equals(mucBroadCastEvent)) {
			//退出房间
			if (getAssocActivity() instanceof FacadeActivity) {
				FacadeActivity.class.cast(getAssocActivity()).popRight();
			} else {
				getAssocActivity().finish();
				application.getActivityManager().popActivity(getAssocActivity());
			}
			Log.i("test", "mucBroadCastEvent" + mucBroadCastEvent);
		}
	}
	
	@Subscribe
	public void onMucManagerKillEvent(HashMap<String, String> map) {
		String rename = map.get("rename");
		String roomName = map.get("roomname");
		if (MucBroadCastEvent.PUSH_MUC_REROOMNAME.equals(rename)){
			if(roomName!=null){
				titlebar_content.setText(roomName);
			}
		}
	}
		
	
	private void closeOtherWindow(){
		closeKeyboard();
		// 切换到选择表情窗口
//		if (local_module_layout.isShown()) {
//			local_module_layout.setVisibility(View.GONE);
//		}
		local_ex_layout.setVisibility(View.GONE);
	}
	
	
	public void closeKeyboard(){
		InputMethodManager imm2 = (InputMethodManager) getAssocActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm2.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
	}
	
	
}
