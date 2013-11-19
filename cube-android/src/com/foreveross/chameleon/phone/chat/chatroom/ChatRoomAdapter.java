package com.foreveross.chameleon.phone.chat.chatroom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.imagemanager.AsyncImageManager;
import com.foreveross.chameleon.phone.chat.image.BitmapManager;
import com.foreveross.chameleon.phone.chat.voice.VoicePlayer;
import com.foreveross.chameleon.phone.chat.voice.VoicePlayer.CompletionListener;
import com.foreveross.chameleon.push.client.XmppManager;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.ExpressionUtil;
import com.foreveross.chameleon.util.PreferencesUtil;
import com.foreveross.chameleon.util.TimeUnit;

public class ChatRoomAdapter extends BaseAdapter {
	private Context context;
	private List<ConversationMessage> data;
	private Object object;
	private UserModel friend;
	private ChatGroupModel chatGroupModel;

	private boolean voiceDowning;

	public ChatRoomAdapter(Context context, List<ConversationMessage> data,
			Object object) {
		this.context = context;
		this.data = data;
		this.object = object;
		if (object instanceof UserModel) {
			friend = (UserModel) object;
		}
		if (object instanceof ChatGroupModel) {
			chatGroupModel = (ChatGroupModel) object;
		}
		player = new VoicePlayer();
		player.setListener(listener);
		isPlay = false;
		String userAccount = PreferencesUtil
				.getValue(context, "currentAccount");
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// Log.e("getView_2", "" + System.currentTimeMillis());
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_chat, null, false);

			holder.myLayout = (RelativeLayout) convertView
					.findViewById(R.id.chatroom_my_layout);
			holder.myContent = (TextView) convertView
					.findViewById(R.id.chatroom_my_content);
			holder.myIcon = (ImageView) convertView
					.findViewById(R.id.chatroom_my_icon);
			holder.myVoice = (ImageView) convertView
					.findViewById(R.id.chatroom_my_voice);
			holder.myTime = (TextView) convertView
					.findViewById(R.id.chatroom_my_time);

			holder.myName = (TextView) convertView
					.findViewById(R.id.chatroom_my_name);

			holder.friendLayout = (RelativeLayout) convertView
					.findViewById(R.id.chatroom_friend_layout);
			holder.friendIcon = (ImageView) convertView
					.findViewById(R.id.chatroom_friend_icon);
			holder.friendContent = (TextView) convertView
					.findViewById(R.id.chatroom_friend_content);
			holder.friendVoice = (ImageView) convertView
					.findViewById(R.id.chatroom_friend_voice);
			holder.friendTime = (TextView) convertView
					.findViewById(R.id.chatroom_friend_time);
			holder.friendName = (TextView) convertView
					.findViewById(R.id.chatroom_friend_name);
			holder.left_conversation_layout = (RelativeLayout) convertView
					.findViewById(R.id.left_conversation_layout);

			holder.left_image = (ImageView) convertView
					.findViewById(R.id.left_image);
			holder.image_left_layout = (RelativeLayout) convertView
					.findViewById(R.id.image_left_layout);
			holder.left_progressBar = (ProgressBar) convertView
					.findViewById(R.id.left_progressBar);

			holder.right_conversation_layout = (LinearLayout) convertView
					.findViewById(R.id.right_conversation_layout);
			holder.right_image = (ImageView) convertView
					.findViewById(R.id.right_image);
			holder.image_right_layout = (LinearLayout) convertView
					.findViewById(R.id.image_right_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ConversationMessage conversation = data.get(position);

		if (null != conversation.getType()
				&& conversation.getType().equals("voice")) {
			holder.myContent.setVisibility(View.GONE);
			holder.myVoice.setVisibility(View.VISIBLE);

			holder.friendContent.setVisibility(View.GONE);
			holder.friendVoice.setVisibility(View.VISIBLE);

			holder.left_conversation_layout
					.setOnClickListener(new VoiceOnClickListener(conversation,
							"friend"));
			holder.right_conversation_layout
					.setOnClickListener(new VoiceOnClickListener(conversation,
							"my"));
			holder.left_conversation_layout.setVisibility(View.VISIBLE);
			holder.right_conversation_layout.setVisibility(View.VISIBLE);
			holder.image_left_layout.setVisibility(View.GONE);
			holder.image_right_layout.setVisibility(View.GONE);

			// holder.myVoice.setOnClickListener(new ClickListener(conversation
			// .getContent()));
			// holder.friendVoice.setOnClickListener(new ClickListener(
			// conversation.getContent()));
		} else if (null != conversation.getType()
				&& conversation.getType().equals("image")) {
			holder.left_conversation_layout.setVisibility(View.GONE);
			holder.image_left_layout.setVisibility(View.VISIBLE);

			holder.right_conversation_layout.setVisibility(View.GONE);
			holder.image_right_layout.setVisibility(View.VISIBLE);
		} else {
			holder.left_conversation_layout.setVisibility(View.VISIBLE);
			holder.right_conversation_layout.setVisibility(View.VISIBLE);
			holder.image_left_layout.setVisibility(View.GONE);
			holder.image_right_layout.setVisibility(View.GONE);
			holder.myContent.setVisibility(View.VISIBLE);
			holder.myVoice.setVisibility(View.GONE);
			holder.friendContent.setVisibility(View.VISIBLE);
			holder.friendVoice.setVisibility(View.GONE);
		}
		// 这里做正则判断聊天信息里面有没有表情~_MARK_
		String zhengze = "\\[/:[^\\]]+\\]";

		// String zhengze = "f0[0-9]{2}|f10[0-7]"; //正则表达式，用来判断消息内是否有表情
		try {
			if (conversation.getContent() != null) {
				SpannableString spannableString;
				spannableString = ExpressionUtil.getInstance()
						.getExpressionString(
								context,
								new String(
										conversation.getContent().getBytes(),
										"utf-8"), zhengze);
				holder.myContent.setText(spannableString);
				holder.friendContent.setText(spannableString);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (conversation.getType().equals("image")) {
			// 个人聊天
			if (friend != null) {
				String myJid = XmppManager.getMeJid();
				String chaterJid = conversation.getFromWho();
				// 如果发送者是自己
				if (chaterJid.equals(myJid)) {
					holder.myLayout.setVisibility(View.VISIBLE);
					holder.friendLayout.setVisibility(View.GONE);
					holder.image_right_layout.setVisibility(View.VISIBLE);
					holder.image_left_layout.setVisibility(View.GONE);
					if (conversation.getLocalTime() == 0) {
						holder.myTime.setText(TimeUnit.getStringDate());
					} else {
						holder.myTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					String myName = IMModelManager.instance().getMe().getName();
					if (myName != null && myName.contains("@")) {
						String s1[] = myName.split("@");
						myName = s1[0];
					}
					holder.myName.setText(myName);
					holder.right_image
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (null == v.getTag()) {

									} else {
										String path = (String) v.getTag();
										Intent detailIntent = new Intent();
										detailIntent
												.putExtra("imagePath", path);
										detailIntent
												.putExtra("showFlag", false);
										detailIntent
										.putExtra("showTitle", false);
										detailIntent.setClass(context,
												PicutureDetailActivity.class);
										context.startActivity(detailIntent);

									}
								}
							});

					// holder.right_image.setImageDrawable(context.getResources().getDrawable(
					// R.drawable.pic_bg_02));
					String tag = (String) holder.right_image.getTag();

					String path = conversation.getContent();
					Bitmap bitmap = AsyncImageManager.getInstance()
							.loadImgFromMemery(path);
					if (bitmap != null) {
						holder.right_image.setImageBitmap(bitmap);
						holder.right_image.setTag(conversation.getContent());
					} else {
						if (tag == null) {
							new ImageTask(holder.right_image, null,
									conversation).execute(
									conversation.getContent(),
									conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new ImageTask(holder.right_image, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}
				}
				// 如果发送者不是自己
				else {
					holder.myLayout.setVisibility(View.GONE);
					holder.friendLayout.setVisibility(View.VISIBLE);
					holder.image_right_layout.setVisibility(View.GONE);
					holder.image_left_layout.setVisibility(View.VISIBLE);
					String friendName = null;
					UserModel model = IMModelManager.instance().getUserModel(
							chaterJid);
					if (model == null) {
						friendName = conversation.getFromWho();
					} else {
						friendName = model.getName();
					}
					if (friendName == null || "".equals(friendName)) {
						friendName = conversation.getFromWho();
					}
					if (friendName != null && friendName.contains("@")) {
						String s1[] = friendName.split("@");
						friendName = s1[0];
					}
					holder.friendName.setText(friendName);
					if (conversation.getLocalTime() == 0) {
						holder.friendTime.setText(TimeUnit.getStringDate());
					} else {
						holder.friendTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					// friend.setIcon(holder.friendIcon);
					String tag = (String) holder.left_image.getTag();

					String path = conversation.getContent();
					Bitmap bitmap = AsyncImageManager.getInstance()
							.loadImgFromMemery(path);
					if (bitmap != null) {
						holder.left_image.setImageBitmap(bitmap);
						holder.left_image.setTag(conversation.getContent());
					} else {
						if (tag == null) {
							new ImageTask(holder.left_image, null, conversation)
									.execute(conversation.getContent(),
											conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new ImageTask(holder.left_image, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}

					holder.left_image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (null == v.getTag()) {

							} else {
								String path = (String) v.getTag();
								Intent detailIntent = new Intent();
								detailIntent.putExtra("imagePath", path);
								detailIntent.putExtra("showFlag", false);
								detailIntent
								.putExtra("showTitle", false);
								detailIntent.setClass(context,
										PicutureDetailActivity.class);
								context.startActivity(detailIntent);

							}
						}
					});
				}

			}
			// 用户组聊天
			else if (chatGroupModel != null) {
				String myJid = XmppManager.getMeJid();
				String chaterJid = conversation.getFromWho();
				if (chaterJid.equals(myJid)) {
					holder.myLayout.setVisibility(View.VISIBLE);
					holder.friendLayout.setVisibility(View.GONE);
					holder.image_right_layout.setVisibility(View.VISIBLE);
					holder.image_left_layout.setVisibility(View.GONE);
					String myName = IMModelManager.instance().getMe().getName();
					if (myName != null && myName.contains("@")) {
						String s1[] = myName.split("@");
						myName = s1[0];
					}
					holder.myName.setText(myName);
					if (conversation.getLocalTime() == 0) {
						holder.myTime.setText(TimeUnit.getStringDate());
					} else {
						holder.myTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					holder.right_image
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (null == v.getTag()) {

									} else {
										String path = (String) v.getTag();
										Intent detailIntent = new Intent();
										detailIntent
												.putExtra("imagePath", path);
										detailIntent
												.putExtra("showFlag", false);
										detailIntent
										.putExtra("showTitle", false);
										detailIntent.setClass(context,
												PicutureDetailActivity.class);
										context.startActivity(detailIntent);

									}
								}
							});

					String tag = (String) holder.right_image.getTag();

					String path = conversation.getContent();
					Bitmap bitmap = AsyncImageManager.getInstance()
							.loadImgFromMemery(path);
					if (bitmap != null) {
						holder.right_image.setImageBitmap(bitmap);
						holder.right_image.setTag(conversation.getContent());
					} else {
						if (tag == null) {
							new ImageTask(holder.right_image, null,
									conversation).execute(
									conversation.getContent(),
									conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new ImageTask(holder.right_image, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}
				}
				// 如果发送者不是自己
				else {

					holder.myLayout.setVisibility(View.GONE);
					holder.friendLayout.setVisibility(View.VISIBLE);
					holder.image_right_layout.setVisibility(View.GONE);
					holder.image_left_layout.setVisibility(View.VISIBLE);
					String friendName = null;
					UserModel model = IMModelManager.instance().getUserModel(
							chaterJid);
					if (model == null) {
						friendName = conversation.getFromWho();
					} else {
						friendName = model.getName();
					}
					if (friendName == null || "".equals(friendName)) {
						friendName = conversation.getFromWho();
					}
					if (friendName != null && friendName.contains("@")) {
						String s1[] = friendName.split("@");
						friendName = s1[0];
					}
					holder.friendName.setText(friendName);
					// friend.setIcon(holder.friendIcon);
					if (conversation.getLocalTime() == 0) {
						holder.friendTime.setText(TimeUnit.getStringDate());
					} else {
						holder.friendTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					String tag = (String) holder.left_image.getTag();

					String path = conversation.getContent();
					Bitmap bitmap = AsyncImageManager.getInstance()
							.loadImgFromMemery(path);
					if (bitmap != null) {
						holder.left_image.setImageBitmap(bitmap);
						holder.left_image.setTag(conversation.getContent());
					} else {
						if (tag == null) {
							new ImageTask(holder.left_image, null, conversation)
									.execute(conversation.getContent(),
											conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new ImageTask(holder.left_image, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}

					holder.left_image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (null == v.getTag()) {

							} else {
								String path = (String) v.getTag();
								Intent detailIntent = new Intent();
								detailIntent.putExtra("imagePath", path);
								detailIntent.putExtra("showFlag", false);
								detailIntent
								.putExtra("showTitle", false);
								detailIntent.setClass(context,
										PicutureDetailActivity.class);
								context.startActivity(detailIntent);

							}
						}
					});

				}

			}
		} else {
			// 个人聊天
			if (friend != null) {
				String myJid = XmppManager.getMeJid();
				String chaterJid = conversation.getFromWho();
				if (chaterJid.equals(myJid)) {
					// 如果发送者是自己
					holder.myLayout.setVisibility(View.VISIBLE);
					holder.friendLayout.setVisibility(View.GONE);
					if (conversation.getLocalTime() == 0) {
						holder.myTime.setText(TimeUnit.getStringDate());
					} else {
						holder.myTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					String myName = IMModelManager.instance().getMe().getName();
					if (myName != null && myName.contains("@")) {
						String s1[] = myName.split("@");
						myName = s1[0];
					}
					holder.myName.setText(myName);
					if ("voice".equals(conversation.getType())) {
						String tag = (String) holder.right_conversation_layout
								.getTag();
						// holder.right_conversation_layout.setVisibility(View.GONE);
						if (tag == null) {
							new VoiceTask(holder.myLayout,
									holder.right_conversation_layout, null,
									conversation).execute(
									conversation.getContent(),
									conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new VoiceTask(holder.myLayout,
										holder.right_conversation_layout, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}
				}
				// 如果发送者不是自己
				else {
					holder.myLayout.setVisibility(View.GONE);
					holder.friendLayout.setVisibility(View.VISIBLE);
					if (conversation.getLocalTime() == 0) {
						holder.friendTime.setText(TimeUnit.getStringDate());
					} else {
						holder.friendTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					String friendName = null;
					UserModel model = IMModelManager.instance().getUserModel(
							chaterJid);
					if (model == null) {
						friendName = conversation.getFromWho();
					} else {
						friendName = model.getName();
					}
					if (friendName == null || "".equals(friendName)) {
						friendName = conversation.getFromWho();
					}
					if (friendName != null && friendName.contains("@")) {
						String s1[] = friendName.split("@");
						friendName = s1[0];
					}
					holder.friendName.setText(friendName);
					if ("voice".equals(conversation.getType())) {
						String tag = (String) holder.left_conversation_layout
								.getTag();
						// holder.left_conversation_layout.setVisibility(View.GONE);
						if (tag == null) {
							new VoiceTask(holder.friendLayout,
									holder.left_conversation_layout, null,
									conversation).execute(
									conversation.getContent(),
									conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new VoiceTask(holder.friendLayout,
										holder.left_conversation_layout, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}
				}

			}
			// 用户组聊天
			else if (chatGroupModel != null) {
				String myJid = XmppManager.getMeJid();
				String chaterJid = conversation.getFromWho();
				if (chaterJid.equals(myJid)) {
					// 如果发送者是自己
					holder.myLayout.setVisibility(View.VISIBLE);
					holder.friendLayout.setVisibility(View.GONE);
					if (conversation.getLocalTime() == 0) {
						holder.myTime.setText(TimeUnit.getStringDate());
					} else {
						holder.myTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					String myName = IMModelManager.instance().getMe().getName();
					if (myName != null && myName.contains("@")) {
						String s1[] = myName.split("@");
						myName = s1[0];
					}
					holder.myName.setText(myName);
					if ("voice".equals(conversation.getType())) {
						String tag = (String) holder.right_conversation_layout
								.getTag();
						// holder.right_conversation_layout.setVisibility(View.GONE);
						if (tag == null) {
							new VoiceTask(holder.myLayout,
									holder.right_conversation_layout, null,
									conversation).execute(
									conversation.getContent(),
									conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new VoiceTask(holder.myLayout,
										holder.right_conversation_layout, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}

				}
				// 如果发送者不是自己
				else {
					holder.myLayout.setVisibility(View.GONE);
					holder.friendLayout.setVisibility(View.VISIBLE);
					if (conversation.getLocalTime() == 0) {
						holder.friendTime.setText(TimeUnit.getStringDate());
					} else {
						holder.friendTime.setText(TimeUnit.LongToStr(
								conversation.getLocalTime(),
								TimeUnit.LONG_FORMAT));
					}
					String friendName = null;
					UserModel model = IMModelManager.instance().getUserModel(
							chaterJid);
					if (model == null) {
						friendName = conversation.getFromWho();
					} else {
						friendName = model.getName();
					}
					if (friendName == null || "".equals(friendName)) {
						friendName = conversation.getFromWho();
					}
					if (friendName != null && friendName.contains("@")) {
						String s1[] = friendName.split("@");
						friendName = s1[0];
					}
					holder.friendName.setText(friendName);
					if ("voice".equals(conversation.getType())) {
						String tag = (String) holder.left_conversation_layout
								.getTag();
						// holder.left_conversation_layout.setVisibility(View.GONE);
						if (tag == null) {
							new VoiceTask(holder.friendLayout,
									holder.left_conversation_layout, null,
									conversation).execute(
									conversation.getContent(),
									conversation.getPicId());
						} else {
							if (!tag.equals(conversation.getContent())) {
								new VoiceTask(holder.friendLayout,
										holder.left_conversation_layout, null,
										conversation).execute(
										conversation.getContent(),
										conversation.getPicId());
							}
						}
					}
				}
			}
		}
		return convertView;
	}

	class ViewHolder {
		/** 当前账号发消息的布局 */
		RelativeLayout myLayout;
		/** 文本内容控件 */
		TextView myContent;
		ImageView myIcon;
		ImageView myVoice;
		TextView myTime;
		TextView myName;
		/** 显示对方消息最外的布局 */
		RelativeLayout friendLayout;
		/** 文本内容控件 */
		TextView friendContent;
		ImageView friendIcon;
		ImageView friendVoice;
		TextView friendTime;
		TextView friendName;

		LinearLayout image_right_layout;
		LinearLayout right_conversation_layout;
		ImageView right_image;

		RelativeLayout image_left_layout;
		RelativeLayout left_conversation_layout;
		ProgressBar left_progressBar;// 左边进度条
		ImageView left_image;

	}

	// class TouchListener implements View.OnTouchListener{
	//
	// @Override
	// public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
	// return false;
	// }
	// };

	// class ClickListener implements View.OnClickListener {
	// String path;
	//
	// public ClickListener(String path) {
	// this.path = path;
	// }
	//
	// @Override
	// public void onClick(View view) {
	// new Player().playMusic(path);
	// }
	//
	// }

	class VoiceOnClickListener implements OnClickListener {
		String who;
		ConversationMessage conversation;

		public VoiceOnClickListener(ConversationMessage conversation, String who) {
			this.who = who;
			this.conversation = conversation;
		}

		@Override
		public void onClick(View v) {
			String tag = (String) v.getTag();
			if (tag == null) {
				if (!voiceDowning) {
					new VoiceTask(null, v, null, conversation).execute(null,
							conversation.getPicId());
				} else {
				}
				return;
			}

			if (!isPlay) {
				String path = conversation.getContent();
				if (path == null) {
					return;
				}
				if (null != mLastPlayIv && mAnim.isRunning()) {
					mAnim.stop();
					mLastPlayIv
							.setBackgroundResource(R.drawable.chat_record_m_3);
				}
				if (null != fLastPlayIv && fAnim.isRunning()) {
					fAnim.stop();
					fLastPlayIv
							.setBackgroundResource(R.drawable.chat_record_f_3);
				}
				File file = new File(path);
				if (!file.exists()) {
					Toast.makeText(context, "该文件已经不存在", Toast.LENGTH_SHORT)
							.show();
				} else {
					boolean playable = player.playByPath(path);
					if (!playable) {
						if (null != mLastPlayIv && mAnim.isRunning()) {
							mAnim.stop();
							mLastPlayIv
									.setBackgroundResource(R.drawable.chat_record_m_3);
						}
						if (null != fLastPlayIv && fAnim.isRunning()) {
							fAnim.stop();
							fLastPlayIv
									.setBackgroundResource(R.drawable.chat_record_f_3);
						}
						Toast.makeText(context, "音频文件不能播放，重新下载音频",
								Toast.LENGTH_SHORT).show();
						new VoiceTask(null, v, null, conversation).execute(
								null, conversation.getPicId());
						return;
					}
					if (who.equals("friend")) {
						fLastPlayIv = (ImageView) v
								.findViewById(R.id.chatroom_friend_voice);
						fLastPlayIv
								.setBackgroundResource(R.drawable.chat_record_friend);
						fAnim = (AnimationDrawable) fLastPlayIv.getBackground();
						fAnim.start();

					} else {
						mLastPlayIv = (ImageView) v
								.findViewById(R.id.chatroom_my_voice);
						mLastPlayIv
								.setBackgroundResource(R.drawable.chat_record_my);
						mAnim = (AnimationDrawable) mLastPlayIv.getBackground();
						mAnim.start();
					}
					isPlay = true;
				}

			} else {
				isPlay = false;
				stopPlay();
			}
		};
	};

	private VoicePlayer player;
	private ImageView mLastPlayIv;
	private AnimationDrawable mAnim;
	private ImageView fLastPlayIv;
	private AnimationDrawable fAnim;
	private boolean isPlay = false;

	public void stopPlay() {
		if (null != player) {
			player.getMediaPlayer().stop();
			listener.onCompletion();
		}
	}

	private CompletionListener listener = new CompletionListener() {

		@Override
		public void onCompletion() {

			if (null != mAnim) {
				mAnim.stop();
				mLastPlayIv.setBackgroundResource(R.drawable.chat_record_m_3);

			}
			if (null != fAnim) {
				fAnim.stop();
				fLastPlayIv.setBackgroundResource(R.drawable.chat_record_f_3);
			}
			isPlay = false;
		}
	};

	class ImageTask extends AsyncTask<String, Integer, Bitmap> {
		// 刷新进度条
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (progressBar == null) {
				return;
			}
			if (progressBar.getVisibility() == View.GONE) {
				progressBar.setVisibility(View.VISIBLE);
			}
			if (values[0] == 100) {
				progressBar.setVisibility(View.GONE);
				return;
			}
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		private ImageView imageView;

		private ProgressBar progressBar;

		private ConversationMessage conversation;

		public ImageTask(ImageView imageView, ProgressBar progressBar) {
			this.imageView = imageView;
			this.progressBar = progressBar;
		}

		public ImageTask(ImageView imageView, ProgressBar progressBar,
				ConversationMessage conversation) {
			this.imageView = imageView;
			this.progressBar = progressBar;
			this.conversation = conversation;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			imageView.setImageDrawable(context.getResources().getDrawable(
					R.drawable.pic_bg_02));
		}

		@SuppressWarnings("resource")
		@Override
		protected Bitmap doInBackground(String... params) {
			String path = params[0];
			final String picId = params[1];
			String dirPath = Environment.getExternalStorageDirectory()
					+ "/CubeImageCache/";
			Bitmap bitmap = null;
			if (path == null || "".equals(path)) {

				String url = URL.getDownloadUrl(context, picId);
				try {
					FileOutputStream output = null;
					InputStream is = null;
					try {
						Log.i("test", "url" + url);
						java.net.URL imgURL = new java.net.URL(url);
						HttpURLConnection connect = (HttpURLConnection) imgURL
								.openConnection();
						int voiceSize = connect.getContentLength();
						if (voiceSize == 0) {
							return null;
						}
						is = connect.getInputStream();

						File file = new File(dirPath);
						if (!file.exists()) {
							file.mkdirs();
						}
						output = new FileOutputStream(dirPath + picId);
						int ch = 0;
						while ((ch = is.read()) != -1) {
							output.write(ch);
						}
						output.flush();

						imageView.setTag(dirPath + picId);
						conversation.setContent(dirPath + picId);
						conversation.update();
						// bitmap = dealwithImage(dirPath + picId);
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inSampleSize = 4;
						FileInputStream fis = new FileInputStream(dirPath
								+ picId);
						bitmap = BitmapManager.instance().decodeFileDescriptor(
								fis.getFD(), opts);
						AsyncImageManager.getInstance().setImageCache(bitmap,
								dirPath + picId);
						return bitmap;
					} catch (MalformedURLException e1) {
						Log.e("URL_TAG", "url 出错");
						return null;
					} catch (IOException e) {
						return null;
					} finally {
						// 关闭输入流等（略）
						if (output != null) {

							try {
								output.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (is != null) {
							try {
								is.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (Exception e1) {
					Log.e("URL_TAG", "url 出错");
					return null;
				}

			} else {
				imageView.setTag(path);
				try {
					bitmap = AsyncImageManager.getInstance().loadImgFromMemery(
							path);
					if (bitmap == null) {
						// bitmap = dealwithImage(path);
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inSampleSize = 4;
						FileInputStream fis = new FileInputStream(path);
						bitmap = BitmapManager.instance().decodeFileDescriptor(
								fis.getFD(), opts);
						AsyncImageManager.getInstance().setImageCache(bitmap,
								path);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return bitmap;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (null != result) {
				imageView.setImageBitmap(result);
			}
		}

	}

	class VoiceTask extends AsyncTask<String, Integer, Integer> {
		// 刷新进度条
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (progressBar == null) {
				return;
			}
			if (progressBar.getVisibility() == View.GONE) {
				progressBar.setVisibility(View.VISIBLE);
			}
			if (values[0] == 100) {
				progressBar.setVisibility(View.GONE);
				return;
			}
			progressBar.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		private View voiceView;

		private RelativeLayout voiceLayout;

		private ProgressBar progressBar;

		private ConversationMessage conversation;

		public VoiceTask(View voiceView, ProgressBar progressBar) {
			this.voiceView = voiceView;
			this.progressBar = progressBar;
		}

		public VoiceTask(RelativeLayout voiceLayout, View voiceView,
				ProgressBar progressBar, ConversationMessage conversation) {
			this.voiceView = voiceView;
			this.progressBar = progressBar;
			this.conversation = conversation;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (voiceLayout != null) {
				voiceLayout.setVisibility(View.GONE);
			}
			if (voiceView != null) {
				voiceView.setTag(null);
			}
		}

		@Override
		protected Integer doInBackground(String... params) {
			voiceDowning = true;
			String path = params[0];
			final String picId = params[1];
			if (path == null || "".equals(path)) {
				String url = URL.getDownloadUrl(context, picId);
				FileOutputStream output = null;
				InputStream is = null;
				try {
					Log.i("test", "url" + url);
					java.net.URL imgURL = new java.net.URL(url);
					HttpURLConnection connect = (HttpURLConnection) imgURL
							.openConnection();
					int voiceSize = connect.getContentLength();
					if (voiceSize == 0) {
						return -1;
					}
					is = connect.getInputStream();
					String voicePath = makeVoiceReceiveDir(conversation
							.getChater());
					output = new FileOutputStream(voicePath);
					int ch = 0;
					while ((ch = is.read()) != -1) {
						output.write(ch);
					}
					output.flush();

					voiceView.setTag(voicePath + picId);
					conversation.setContent(voicePath);
					conversation.update();
					return 1;
				} catch (MalformedURLException e1) {
					Log.e("URL_TAG", "url 出错");
					return -1;
				} catch (IOException e) {
					return -1;
				} finally {
					// 关闭输入流等（略）
					if (output != null) {

						try {
							output.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} else {
				voiceView.setTag(path);
				return 1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result != 1) {
				voiceView.setTag(null);
			}
			voiceDowning = false;
			if (result == 1) {
				if (voiceLayout != null) {
					voiceLayout.setVisibility(View.VISIBLE);
				}
			}
		}

	}

	/**
	 * @return 创建并音频文件路径
	 **/
	private String makeVoiceReceiveDir(String chater) {
		long time = System.currentTimeMillis();
		String datetime = DateFormat.format("yyyyMMddhhmmssfff", time)
				.toString();
		String dir = Environment.getExternalStorageDirectory().getPath() + "/"
				+ TmpConstants.RECORDER_RECEIVE_PATH;
		File recordDir = new File(dir);
		if (!recordDir.exists()) {
			recordDir.mkdirs();
		}
		String voicePath = dir + "/" + chater + " " + datetime + ".aac";
		return voicePath;
	}

	public Bitmap dealwithImage(String dirPath) throws FileNotFoundException {
		File f = new File(dirPath);
		Bitmap bitmap = null;
		BitmapFactory.Options newOpts = null;
		if (f.exists()) {
			newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			newOpts.inPurgeable = true;
			newOpts.inSampleSize = 4;
			newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
			bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null,
					newOpts);// 此时返回bm为空
			newOpts.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(new FileInputStream(f), null,
					newOpts);
		}
		return bitmap;

	}

}
