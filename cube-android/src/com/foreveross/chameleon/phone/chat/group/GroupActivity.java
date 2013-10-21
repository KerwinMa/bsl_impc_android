package com.foreveross.chameleon.phone.chat.group;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.phone.activity.BaseActivity;
import com.foreveross.chameleon.phone.activity.ChatRoomActivity;
import com.foreveross.chameleon.phone.activity.PushSettingActivity;
import com.foreveross.chameleon.phone.chat.search.SearchFriendAdapter;
import com.foreveross.chameleon.push.client.XmppManager.RosterManager;
import com.foreveross.chameleon.store.model.AbstractContainerModel;
import com.foreveross.chameleon.store.model.IMModelManager;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.util.UnkownUtil;
import com.squareup.otto.ThreadEnforcer;

public class GroupActivity extends FragmentActivity {
	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param savedInstanceState
	 *            2013-8-27 上午9:48:21
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_activity_layout);
	}

}