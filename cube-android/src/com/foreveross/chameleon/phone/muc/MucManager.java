package com.foreveross.chameleon.phone.muc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.Occupant;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.modules.task.HttpRequestAsynTask;
import com.foreveross.chameleon.push.client.Constants;
import com.foreveross.chameleon.push.client.Monitor;
import com.foreveross.chameleon.push.client.Notifier;
import com.foreveross.chameleon.push.client.XmppManager;
import com.foreveross.chameleon.store.model.ChatGroupModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;
import com.foreveross.chameleon.util.HttpUtil;
import com.foreveross.chameleon.util.Pool;
import com.foreveross.chameleon.util.Preferences;
import com.foreveross.chameleon.util.TimeUnit;
import com.google.gson.Gson;

/**
 * <BR>
 * [功能详细描述]
 * 
 * @author Amberlo
 * @version [CubeAndroid_istation , 2013-8-23]
 */
public class MucManager {
	XMPPConnection conn;
	private static Context context;

	/** handler 用的标识 */
	public static int MucInitMucRooms = 0x0000001;

	private static MucManager instanse;

	private boolean invitation = false;

	private String invitationRoomJid = null;

	private Application application;

	public static MucManager getInstanse(Context ctx) {
		context = ctx;
		if (instanse == null) {
			instanse = new MucManager(context);
		}
		return instanse;
	}

	public boolean isInstanse() {

		if (conn == null || context == null) {
			return false;
		}
		return true;
	}

	public MucManager(Context ctx) {
		context = ctx;
		this.application = Application.class.cast(context);
	}

	public void init(XMPPConnection conn) {
		mucMap.clear();
		this.conn = conn;
	}

	/**
	 * 创建群组
	 * 
	 * @param nickName
	 *            用户昵称
	 * @param inviteJids
	 *            被邀请进群的用户
	 */
	public void createMutiUserChatroom(final ChatGroupModel room,
			final UserModel creator, final List<UserModel> inviteUsers) {
		if (!isInstanse())
			return;
		final String roomName = room.getGroupName();
		final String roomJid = room.getRoomJid();
		// final String roomJid = UUID.randomUUID().toString() + "@"
		// + MucManager.MucServiceName;
		final MultiUserChat muc = getMuc(roomJid);
		new AsyncTask<String, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(String... params) {
				try {
					// 创建聊天室
					muc.create(creator.getJid());
					// // 获得聊天室的配置表单
					Form form = muc.getConfigurationForm();
					// 根据原始表单创建一个要提交的新表单。
					Form submitForm = form.createAnswerForm();
					// 向要提交的表单添加默认答复
					for (Iterator<FormField> fields = form.getFields(); fields
							.hasNext();) {
						FormField field = fields.next();
						if (!FormField.TYPE_HIDDEN.equals(field.getType())
								&& field.getVariable() != null) {
							// 设置默认值作为答复
							submitForm.setDefaultAnswer(field.getVariable());
						}
					}
					// 设置聊天室的新拥有者
					// List owners = new ArrayList();
					// owners.add("liaonaibo2\\40slook.cc");
					// owners.add("liaonaibo1\\40slook.cc");
					// submitForm.setAnswer("muc#roomconfig_roomowners",
					// owners);
					// 设置聊天室是持久聊天室，即将要被保存下来
					submitForm.setAnswer("muc#roomconfig_persistentroom", true);
					// 群组仅对成员开放
					submitForm.setAnswer("muc#roomconfig_membersonly", false);
					// 允许占有者邀请其他人
					submitForm.setAnswer("muc#roomconfig_allowinvites", true);
					// 能够发现占有者真实 JID 的角色
					// submitForm.setAnswer("muc#roomconfig_whois", "anyone");
					// 登录群组对话
					submitForm.setAnswer("muc#roomconfig_enablelogging", true);
					// 仅允许注册的昵称登录
					submitForm.setAnswer("x-muc#roomconfig_reservednick", true);
					// 允许使用者修改昵称
					submitForm.setAnswer("x-muc#roomconfig_canchangenick",
							false);
					// 允许用户注册群组
					submitForm
							.setAnswer("x-muc#roomconfig_registration", false);

					submitForm.setAnswer("muc#roomconfig_roomname", roomName);
					// 发送已完成的表单（有默认值）到服务器来配置聊天室
					muc.sendConfigurationForm(submitForm);
					// 把自己加进群组
					if (!joinRoom(roomJid, creator.getJid(), new Date())) {
						Log.e("muc create room", "创建群组失败");
						return false;
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					Log.e("muc create room", "创建群组失败");
					return false;
				}
			};

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					// 加进容器中
					// MucRoomModel room = new MucRoomModel();
					// room.setRoomJid(muc.getRoom());
					// room.setName(roomName);
					// room.setUsers(inviteUsers);

					// mucRoomList.add(room);
					// getAllMucRoomMap().put(room.getRoomJid(), room);
					new Thread() {
						@Override
						public void run() {
							super.run();
							// 批量提交用户至服务器
							inviteUsers.add(creator);
							createinvite(room, inviteUsers, creator.getJid());
						}
					}.start();
					Toast.makeText(context, "创建群组成功", Toast.LENGTH_SHORT)
							.show();
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_MANAGER_MEMBER);
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_INITROOMS);

				} else {
					Toast.makeText(context, "创建群组失败", Toast.LENGTH_SHORT)
							.show();
				}
			};
		}.execute();

	}

	/**
	 * 踢人出群组
	 * 
	 * @param user
	 */
	public void kick(String roomJid, final String userJid) {
		if (!isInstanse())
			return;
		final MultiUserChat muc = getMuc(roomJid);
		try {
			muc.kickParticipant(userJid, "出去吧");
			// final MucRoomModel room = getAllMucRoomMap().get(muc.getRoom());
			HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
				@Override
				protected void doPostExecute(String result) {
					super.doPostExecute(result);
					// Log.i("MucManager", "kick member " + user.getName() +
					// "success");
					// room.deleteMember(user);
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_MANAGER_MEMBER);

				}
			};
			task.setLockScreen(false);
			task.setShowProgressDialog(false);
			task.setNeedProgressDialog(false);
			String url = URL.MUC_DeleteMember + muc.getRoom() + "/" + userJid
					+ URL.getSessionKeyappKey();
			task.execute(url, "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 踢人出群组
	 * 
	 * @param users
	 */
	public void kick(String roomJid, List<UserModel> users) {
		if (!isInstanse())
			return;
		final MultiUserChat muc = getMuc(roomJid);
		List<Map<String, Object>> requestData = new ArrayList<Map<String, Object>>();
		for (UserModel user : users) {
			try {
				muc.kickParticipant(user.getJid(), "出去吧");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Map<String, Object> userM = new HashMap<String, Object>();
			userM.put("jid", user.getJid());
			userM.put("roomId", muc.getRoom());
			requestData.add(userM);
		}
		HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
			@Override
			protected void doPostExecute(String result) {
				super.doPostExecute(result);
				Log.i("MucManager", "kick members success");
			}
		};
		task.setLockScreen(false);
		task.setShowProgressDialog(false);
		task.setNeedProgressDialog(false);

		StringBuffer sb = new StringBuffer();

		sb = sb.append("Form:deleteJson=")
				.append(new Gson().toJson(requestData)).append(";sessionKey=")
				.append(URL.getSessionKey()).append(";appKey=")
				.append(URL.getAppKey());
		;
		String s = sb.toString();
		task.execute(URL.MUC_DeleteMembers, s, HttpUtil.UTF8_ENCODING,
				HttpUtil.HTTP_POST);

	}

	/**
	 * 邀请多个用户进群组
	 * 
	 * @param inviteUsers
	 */
	public boolean invite(ChatGroupModel room, Collection<UserModel> inviteUsers) {
		if (!isInstanse())
			return false;
		final MultiUserChat muc = getMuc(room.getRoomJid());
		List<Map<String, Object>> requestData = new ArrayList<Map<String, Object>>();
		for (UserModel user : inviteUsers) {
			muc.invite(user.getJid(), "进来吧");
			Map<String, Object> userM = new HashMap<String, Object>();
			userM.put("sex", user.getSex());
			userM.put("jid", user.getJid());
			userM.put("username", user.getName());

			if (user.getStatus() == UserStatus.USER_STATE_OFFLINE) {
				userM.put("statue", "OFFLINE");
			} else {
				userM.put("statue", "ONLINE");
			}
			userM.put("roomId", room.getRoomJid());

			if (room.getGroupName() == null) {
				userM.put("roomName", room.getRoomJid());
			} else {
				userM.put("roomName", room.getGroupName());
			}
			requestData.add(userM);
		}

		try {
			StringBuffer sb = new StringBuffer();

			sb = sb.append("Form:members=")
					.append(new Gson().toJson(requestData))
					.append(";sessionKey=").append(URL.getSessionKey())
					.append(";appKey=").append(URL.getAppKey());
			;
			String s = sb.toString();
			HttpUtil.doWrapedHttp(context, new String[] { URL.MUC_AddMembers,
					s, HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 邀请多个用户进群组
	 * 
	 * @param inviteUsers
	 */
	public boolean createinvite(ChatGroupModel room,
			Collection<UserModel> inviteUsers, String creator) {
		if (!isInstanse())
			return false;
		final MultiUserChat muc = getMuc(room.getRoomJid());
		String creatorinvite = creator;
		List<Map<String, Object>> requestData = new ArrayList<Map<String, Object>>();
		for (UserModel user : inviteUsers) {
			muc.invite(user.getJid(), "进来吧");
			Map<String, Object> userM = new HashMap<String, Object>();
			userM.put("sex", user.getSex());
			userM.put("jid", user.getJid());
			userM.put("username", user.getName());

			if (user.getStatus() == UserStatus.USER_STATE_OFFLINE) {
				userM.put("statue", "OFFLINE");
			} else {
				userM.put("statue", "ONLINE");
			}
			userM.put("roomId", room.getRoomJid());

			if (room.getGroupName() == null) {
				userM.put("roomName", room.getRoomJid());
			} else {
				userM.put("roomName", room.getGroupName());
			}
			requestData.add(userM);
		}

		try {
			StringBuffer sb = new StringBuffer();

			sb = sb.append("Form:members=")
					.append(new Gson().toJson(requestData)).append(";creator=")
					.append(creatorinvite).append(";sessionKey=")
					.append(URL.getSessionKey()).append(";appKey=")
					.append(URL.getAppKey());
			String s = sb.toString();
			HttpUtil.doWrapedHttp(context, new String[] { URL.MUC_AddMembers,
					s, HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 修改群组名称
	 */
	public boolean reNameRoom(String roomJid, final String roomName) {
		if (!isInstanse())
			return false;
		final MultiUserChat muc = getMuc(roomJid);
		// 根据原始表单创建一个要提交的新表单。
		try {
			Form form = muc.getConfigurationForm();
			Form submitForm = form.createAnswerForm();
			// 向要提交的表单添加默认答复
			for (Iterator<FormField> fields = form.getFields(); fields
					.hasNext();) {
				FormField field = fields.next();
				if (!FormField.TYPE_HIDDEN.equals(field.getType())
						&& field.getVariable() != null) {
					// 设置默认值作为答复
					submitForm.setDefaultAnswer(field.getVariable());
				}
			}
			submitForm.setAnswer("muc#roomconfig_roomname", roomName);
			// 发送已完成的表单（有默认值）到服务器来配置聊天室
			muc.sendConfigurationForm(submitForm);
			// MucRoomModel room = getAllMucRoomMap().get(muc.getRoom());
			// if (room != null) {
			// room.setName(roomName);
			// }
			new Thread() {
				public void run() {
					try {
						StringBuffer sb = new StringBuffer();
						sb = sb.append("Form:").append("roomId=")
								.append(muc.getRoom()).append(";roomName=")
								.append(roomName).append(";sessionKey=")
								.append(URL.getSessionKey()).append(";appKey=")
								.append(URL.getAppKey());
						;
						String s = sb.toString();
						HttpUtil.doWrapedHttp(context, new String[] {
								URL.MUC_ReRoomName, s, HttpUtil.UTF8_ENCODING,
								HttpUtil.HTTP_POST });
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("rename", MucBroadCastEvent.PUSH_MUC_REROOMNAME);
			map.put("roomname", roomName);
			EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST).post(map);
			EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST).post(
					MucBroadCastEvent.PUSH_MUC_MANAGER_MEMBER);

			// Intent i = new Intent(BroadCastConstants.PUSH_MUC_REROOMNAME);
			// i.putExtra("roomName", roomName);
			// context.sendBroadcast(i);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 离开群组
	 * 
	 * @param muc
	 */
	public void destroyRoom(String roomJid) {
		if (!isInstanse())
			return;
		final MultiUserChat muc = getMuc(roomJid);
		// String account =
		// Preferences.getUserName(Application.sharePref)+"@"+conn.getServiceName();
		try {
			muc.destroy("解散吧", null);
			// 离开群组，通知服务器
			HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
				@Override
				protected void doPostExecute(String result) {
					super.doPostExecute(result);
					Log.i("MucManager", "leave success");

					// Iterator<MucRoomModel> i = mucRoomList.iterator();
					// while (i.hasNext()) {
					// MucRoomModel room = i.next();
					// if (room.getRoomJid().equals(muc.getRoom())) {
					// i.remove();
					// }
					// }

					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_LEAVE);
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_INITROOMS);
				}
			};
			task.setLockScreen(false);
			task.setShowProgressDialog(false);
			task.setNeedProgressDialog(false);
			String url = URL.MUC_DeleteRoom + muc.getRoom()
					+ URL.getSessionKeyappKey();
			task.execute(url, "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// public MucRoomModel getRoomById(String roomId) {
	//
	// MucRoomModel room = getAllMucRoomMap().get(roomId);
	// return room;
	// }

	public void leaveRoom(String roomJid) {
		if (!isInstanse())
			return;
		final MultiUserChat muc = getMuc(roomJid);
		String account = Preferences.getUserName(Application.sharePref) + "@"
				+ conn.getServiceName();
		try {
			muc.leave();
			// 离开群组，通知服务器
			HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
				@Override
				protected void doPostExecute(String result) {
					super.doPostExecute(result);
					Log.i("MucManager", "leave success");

					// Iterator<MucRoomModel> i = mucRoomList.iterator();
					// while (i.hasNext()) {
					// MucRoomModel room = i.next();
					// if (room.getRoomJid().equals(muc.getRoom())) {
					// i.remove();
					// EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
					// .post(MucBroadCastEvent.PUSH_MUC_LEAVE);
					// EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
					// .post(MucBroadCastEvent.PUSH_MUC_INITROOMS);
					// }
					// }
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_LEAVE);
					EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
							.post(MucBroadCastEvent.PUSH_MUC_INITROOMS);
				}
			};
			task.setLockScreen(false);
			task.setShowProgressDialog(false);
			task.setNeedProgressDialog(false);
			String url = URL.MUC_DeleteMember + muc.getRoom() + "/" + account
					+ URL.getSessionKeyappKey();
			task.execute(url, "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 初始化群组列表数据
	// */
	// public synchronized void initMucRoomList(final UserModel account) {
	// if (!isInstanse())
	// return;
	// new AsyncTask<String, Integer, String>() {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// // getAllMucRoomMap().clear();
	// // mucRoomList.clear();
	// // 重置muc对象
	// mucMap.clear();
	// };
	//
	// @Override
	// protected String doInBackground(String... params) {
	// String result = null;
	// try {
	// String url = URL.MUC_ALLROOM + account.getJid();
	// result = HttpUtil.doWrapedHttp(context, new String[] { url,
	// "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET });
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return result;
	// }
	//
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	// if (result != null) {
	// try {
	// // 通过服务器提供的群组id，构建群组对象
	// JSONArray jay = new JSONArray(result);
	// for (int i = 0; i < jay.length(); i++) {
	// JSONObject jb = (JSONObject) jay.get(i);
	// String roomId = (String) jb.get("roomId");
	// String roomName = "";
	// try {
	// roomName = (String) jb.get("roomName");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// MucRoomModel room = new MucRoomModel();
	// room.setName(roomName);
	// room.setRoomJid(roomId);
	// MultiUserChat muc = getMuc(room.getRoomJid());
	// // if (joinRoom(muc.getRoom(), account.getJid())) {
	// // mucRoomList.add(room);
	// // getAllMucRoomMap().put(room.getRoomJid(), room);
	// // initRoomMembers(room, account);
	// // }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	// EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
	// .post(MucBroadCastEvent.PUSH_MUC_INITROOMS);
	// };
	//
	// }.execute();
	//
	// }

	public boolean joinRoom(String roomJid, String nickname, Date date) {
		if (!isInstanse())
			return false;
		MultiUserChat muc = getMuc(roomJid);
		DiscussionHistory history = new DiscussionHistory();
		history.setMaxChars(100);
		history.setSince(date);
		try {
			muc.join(nickname, null, history, 60000);
			// muc.addUserStatusListener(new MucUserStatusListener(context, muc
			// .getRoom()));
			// muc.addParticipantStatusListener(new
			// MucParticipantStatusListener(
			// context, muc.getRoom()));
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}

	}

	// /**
	// * 构建群组成员
	// *
	// * @param room
	// * @param account
	// */
	// public void initRoomMembers(final MucRoomModel room, final UserModel
	// account) {
	// if (!isInstanse())
	// return;
	// // 获取群组里面的用户
	// new AsyncTask<String, Integer, String>() {
	//
	// @Override
	// protected String doInBackground(String... params) {
	// String url = URL.MUC_QueryMembers + room.getRoomJid();
	// String result;
	// try {
	// result = HttpUtil.doWrapedHttp(context, new String[] { url,
	// "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET });
	// if (result != null && result.length() != 0) {
	// JSONArray jay = new JSONArray(result);
	// List<UserModel> users = new ArrayList<UserModel>();
	// for (int i = 0; i < jay.length(); i++) {
	// JSONObject jb = jay.getJSONObject(i);
	// String userJid = jb.getString("jid");
	// if (account.getJid().equals(userJid)) {
	// users.add(account);
	// } else {
	// UserModel user = IMModelManager.instance()
	// .getUserModel(userJid);
	// if (user != null) {
	// users.add(user);
	// }
	// }
	// }
	// room.setUsers(users);
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// }.execute();
	//
	// }

	public List<Affiliate> getOwners(MultiUserChat muc) {
		List<Affiliate> list = new ArrayList<Affiliate>();
		try {
			Collection<Affiliate> owners = muc.getOwners();
			list.addAll(owners);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

	/**
	 * 判断是否为群组创建者
	 * 
	 * @param roomJid
	 * @param user
	 * @return
	 */
	public boolean isOwners(String roomJid, String userJid) {
		if (!isInstanse())
			return false;
		MultiUserChat muc = getMuc(roomJid);
		boolean result = false;
		try {
			Collection<Affiliate> owners = muc.getOwners();
			Iterator<Affiliate> i = owners.iterator();
			while (i.hasNext()) {
				Affiliate a = i.next();
				if (userJid.equals(a.getJid())) {
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public List<Occupant> getAccountRole(MultiUserChat muc) {
		List<Occupant> list = new ArrayList<Occupant>();
		try {
			Collection<Occupant> owners = muc.getModerators();
			list.addAll(owners);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}

	public void sendMucMessage(String roomJid, String msg) {
		if (!isInstanse())
			return;
		MultiUserChat muc = getMuc(roomJid);
		try {
			muc.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendMucMessage(ConversationMessage conversation) {
		if (!isInstanse())
			return;
		MultiUserChat muc = getMuc(conversation.getChater());
		org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
		message.setFrom(conversation.getFromWho());
		message.setType(Type.groupchat);
		message.setTo(conversation.getToWho());
		message.setBody(conversation.getContent());
		message.setSubject(conversation.getType());
		message.setProperty("sendDate", TimeUnit.LongToStr(
				conversation.getLocalTime(), TimeUnit.LONG_FORMAT));
		message.setProperty("uqID", conversation.getLocalTime());
		try {
			muc.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}

	// public List<MucRoomModel> getMucRoomList() {
	// return mucRoomList;
	// }
	//
	// public void setMucRoomList(List<MucRoomModel> mucRoomList) {
	// this.mucRoomList = mucRoomList;
	// }

	public MultiUserChat getMuc(String roomJid) {

		MultiUserChat muc = mucMap.get(roomJid);
		if (muc == null) {
			// Log.e("创建MultiUserChat", "conn="+conn);
			// Log.e("创建MultiUserChat", "roomJid="+roomJid);
			muc = new MultiUserChat(conn, roomJid);
			muc.addUserStatusListener(new MucUserStatusListener(context, muc
					.getRoom()));
			muc.addParticipantStatusListener(new MucParticipantStatusListener(
					context, muc.getRoom()));
			mucMap.put(roomJid, muc);
		}
		return muc;
	}

	// public void cleanRoomIsRead(String roomJid) {
	// MucRoomModel room = getRoomById(roomJid);
	// if (room != null) {
	// room.setIsRead(0);
	// }
	// }
	//
	// public void addRoomIsRead(String roomJid) {
	// MucRoomModel room = getRoomById(roomJid);
	// if (room != null) {
	// int count = room.getIsRead();
	// room.setIsRead(count + 1);
	// }
	// }
	//
	// public int getRoomIsRead(String roomJid) {
	// MucRoomModel room = getRoomById(roomJid);
	// if (room != null) {
	// return room.getIsRead();
	// } else
	// return 0;
	// }
	// public void cleanAll() {
	// mucRoomList.clear();
	// getAllMucRoomMap().clear();
	// EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
	// .post(MucBroadCastEvent.PUSH_MUC_INITROOMS);
	//
	// EventBus.getEventBus(TmpConstants.EVENTBUS_MUC_BROADCAST)
	// .post(MucBroadCastEvent.PUSH_MUC_MANAGER_MEMBER);
	// }

	// public Map<String, MucRoomModel> getAllMucRoomMap() {
	// return allMucRoomMap;
	// }

	public void offLine() {
		new Thread() {
			@Override
			public void run() {
				super.run();
				String meJid = XmppManager.getMeJid();

				try {
					StringBuffer sb = new StringBuffer();
					sb = sb.append("Form:").append("statue=").append("OFFLINE")
							.append(";jid=").append(meJid)
							.append(";sessionKey=").append(URL.getSessionKey())
							.append(";appKey=").append(URL.getAppKey());
					;
					;
					String s = sb.toString();
					HttpUtil.doWrapedHttp(context, new String[] {
							URL.MUC_UpdateStatue, s, HttpUtil.UTF8_ENCODING,
							HttpUtil.HTTP_POST });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/** 数据容器 */
	// private List<MucRoomModel> mucRoomList = new ArrayList<MucRoomModel>();
	// private Map<String, MucRoomModel> allMucRoomMap = new HashMap<String,
	// MucRoomModel>();
	private Map<String, MultiUserChat> mucMap = new HashMap<String, MultiUserChat>();

	public void obtainRooms(String userJid) {
		obtainRooms(userJid, null);
	}

	public void obtainRooms(String userJid, final Monitor monitor) {
		Log.i("test", "obtainRooms");
		try {
			List<ChatGroupModel> chatGroupModels = asemblyRooms(userJid);
			IMModelManager.instance().getChatRoomContainer().clear();
			IMModelManager.instance().getChatRoomContainer()
					.addStuffs(chatGroupModels);
			final String currentUserJid = userJid;
			final CountDownLatch countDownLatch = new CountDownLatch(
					chatGroupModels.size());

			for (final ChatGroupModel chatGroupModel : chatGroupModels) {
				if (chatGroupModel != null && !IMModelManager.instance().containGroup(
						chatGroupModel.getGroupCode())) {
					IMModelManager.instance().addUserGroupModel(chatGroupModel);
				}

				Pool.getPool().execute(new Runnable() {

					@Override
					public void run() {
						obtainUsers(currentUserJid, chatGroupModel);

						if (monitor != null)
							countDownLatch.countDown();
					}
				});
			}

			if (monitor != null) {
				countDownLatch.await();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (monitor != null) {
				synchronized (monitor) {
					monitor.notify();
					monitor.setNotified(true);
				}
			}
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param jid
	 * @return 2013-9-16 下午8:30:56
	 */
	private List<ChatGroupModel> asemblyRooms(String jid) {
		List<ChatGroupModel> chatGroupModels = new ArrayList<ChatGroupModel>();
		String url = URL.MUC_ALLROOM + jid + URL.getSessionKeyappKey();
		String result = null;
		try {
			result = HttpUtil.doWrapedHttp(context, new String[] { url, "",
					HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (result != null) {
			try {
				// 通过服务器提供的群组id，构建群组对象
				JSONArray jay = new JSONArray(result);
				for (int i = 0; i < jay.length(); i++) {
					JSONObject jb = (JSONObject) jay.get(i);
					String roomId = (String) jb.get("roomId");
					String creator = (String) jb.get("creator");
					String roomName = "";
					try {
						roomName = (String) jb.get("roomName");
						if (invitation) {
							if (roomId != null
									&& roomId.equals(invitationRoomJid)
									&& !creator.equals(XmppManager.getMeJid())) {
								invitation = false;
								// 发通知栏通知
								final String invitationroomname = roomName;
								String s[] = creator.split("@");
								final String invitationcreator = s[0];
								application.getUIHandler().post(new Runnable() {

									@Override
									public void run() {
										Intent intent = new Intent();
										Notifier.notifyInfo(context,
												R.drawable.appicon,
												Constants.ID_CHAT_NOTIFICATION,
												invitationcreator,
												invitationcreator + "邀请你加入群组"
														+ invitationroomname,
												intent);
									}
								});
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					ChatGroupModel chatGroupModel = null;
					if ((chatGroupModel = (ChatGroupModel) IMModelManager
							.instance().getUserGroupModel(roomId)) == null) {
						chatGroupModel = new ChatGroupModel();
						chatGroupModel.setGroupCode(roomId);
						chatGroupModel.setGroupName(roomName);
						chatGroupModel.setCreatorJid(creator);
						if (!IMModelManager.instance().containGroup(
								chatGroupModel.getGroupCode())) {
							IMModelManager.instance().addUserGroupModel(
									chatGroupModel);
						}
					} else {
						chatGroupModel = (ChatGroupModel) IMModelManager
								.instance().getUserGroupModel(roomId);
						chatGroupModel.setCreatorJid(creator);
					}
					chatGroupModels.add((ChatGroupModel) chatGroupModel);
				}
			} catch (Exception e) {
				Log.e("asemblyRooms error!", e.getMessage());
			}

		}
		return chatGroupModels;
	}

	private void obtainUsers(String currentUserJid,
			ChatGroupModel chatGroupModel) {
		if (chatGroupModel == null){
			return;
		}
		Log.i("test", "obtainUsers");
		chatGroupModel.findHistory(1);
		List<String> jids = new ArrayList<String>();
		String url = URL.MUC_QueryMembers + chatGroupModel.getGroupCode()
				+ URL.getSessionKeyappKey();
		String result = null;
		try {
			result = HttpUtil.doWrapedHttp(context, new String[] { url, "",
					HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET });
			if (result != null && result.length() != 0) {
				JSONArray jay = new JSONArray(result);
				for (int i = 0; i < jay.length(); i++) {
					JSONObject jb = jay.getJSONObject(i);
					String userJid = jb.getString("jid");
					String offLineTime = jb.getString("offLineTime");
					if (userJid != null) {
						jids.add(userJid);
						if (userJid.equals(currentUserJid)) {
							Log.i("test", "obtainUsers" + userJid);
							Date date = null;
							if (chatGroupModel.getLastMessage() != null) {
								date = new Date(chatGroupModel.getLastMessage()
										.getLocalTime());
							} else {
								date = TimeUnit.strToLongDate(offLineTime);
							}
							joinRoom(chatGroupModel.getRoomJid(), userJid, date);
						}
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
			IMModelManager.instance().addChatgroup(jid, chatGroupModel);
		}
	}

	public boolean isInvitation() {
		return invitation;
	}

	public void setInvitation(boolean invitation) {
		this.invitation = invitation;
	}

	public String getInvitationRoomJid() {
		return invitationRoomJid;
	}

	public void setInvitationRoomJid(String invitationRoomJid) {
		this.invitationRoomJid = invitationRoomJid;
	}

}
