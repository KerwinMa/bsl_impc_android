package com.foreveross.chameleon.phone.chat.group;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.foreveross.chameleon.store.model.AbstractContainerModel;
import com.foreveross.chameleon.store.model.UserModel;

public class FriendQueryTask extends
		AsyncTask<String, Integer, List<AbstractContainerModel>> {
	// 声明进度条对话框
	private ProgressBar progressBar;
	private Context context;
	private Map<String, List<UserModel>> userListMap = new HashMap<String, List<UserModel>>();

	public FriendQueryTask(Context context) {
		super();
		this.context = context;
	}

	public FriendQueryTask(Context context, ProgressBar progressBar) {
		super();
		this.progressBar = progressBar;
		this.context = context;
	}

	// 此方法在UI线程中执行
	// 任务被执行之后，立刻调用 UI线程。这步通常被用于设置任务，例如在用户界面显示一个进度条
	@Override
	protected void onPreExecute() {

		if (progressBar != null) {
			progressBar.setVisibility(View.VISIBLE);
		}
	}

	// 此方法在UI线程中执行
	// 当后台计算结束时，调用 UI线程。后台计算结果作为一个参数传递到这步
	@Override
	protected void onPostExecute(List<AbstractContainerModel> result) {
		super.onPostExecute(result);
		if (null != progressBar) {
			progressBar.setVisibility(View.GONE);
		}
		if (result == null) {
			Toast.makeText(context, "获取好友列表失败", Toast.LENGTH_SHORT).show();
			return;
		}
		if (result.size() != 0) {
			Log.v("queryFriends", "获取好友列表成功");
			// 按拼音首字母排序
			Collections.sort(result, new SortByName());
			// IMModelManager.instance().init(result,new
			// IMModelManager.CallBack() {
			// @Override
			// public void call(final List<AbstractGroupModel> groupModels) {
			// EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON,ThreadEnforcer.MAIN).post(new
			// ModelChangeEvent());
			// IMModelManager.instance().sortAllOnlineUser();
			// new AsyncTask<String, Integer, String>() {
			//
			// @Override
			// protected String doInBackground(String... arg0) {
			// final Map<String, List<UserModel>> map = new HashMap<String,
			// List<UserModel>>();
			// try {
			// //批处理数据库信息
			// userMf.callBatchTasks(new Callable<Void>() {
			// @Override
			// public Void call() throws Exception {
			// for (AbstractGroupModel group : groupModels) {
			// map.put(group.getGroupCode(),group.getList());
			// StaticReference.userMf.createOrUpdate(group);
			// }
			// for (final Entry<String, List<UserModel>> entry : map.entrySet())
			// {
			// synchronized (entry.getValue()) {
			// for (int i = 0; i < entry.getValue().size(); i++) {
			// UserModel user = entry.getValue().get(i);
			// StaticReference.userMf.createOrUpdate(user);
			// }
			// }
			// }
			// return null;
			// }
			//
			// });
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// return null;
			// }
			// }.execute();

			// }
			// });
		}

	}

	@Override
	protected List<AbstractContainerModel> doInBackground(
			String... paramArrayOfParams) {
		// return
		// Application.class.cast(context.getApplicationContext()).getNotificationService()
		// .getRosterManager().queryAllGroup();
		return null;
	}

	class SortByName implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			Collator cmp = Collator.getInstance(Locale.CHINA);
			AbstractContainerModel g1 = (AbstractContainerModel) o1;
			AbstractContainerModel g2 = (AbstractContainerModel) o2;
			// g1.getGroupName().compareTo(g2.getGroupName());
			return cmp.compare(g1.getGroupName(), g2.getGroupName());
		}
	}

}
