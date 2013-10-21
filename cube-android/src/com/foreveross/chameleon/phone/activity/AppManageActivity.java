package com.foreveross.chameleon.phone.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.BroadcastConstans;
import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.phone.modules.ApplicationSyncListener;
import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.foreveross.chameleon.phone.modules.CubeGridViewAdapter;
import com.foreveross.chameleon.phone.modules.CubeListViewAdapter;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.modules.DataSourceAdapter;
import com.foreveross.chameleon.phone.modules.ModuleType;
import com.foreveross.chameleon.phone.view.DisplayManager;
import com.foreveross.chameleon.phone.view.NoScrollGridView;
import com.foreveross.chameleon.phone.view.NoScrollListView;
import com.foreveross.chameleon.phone.view.DisplayManager.DisplayStype;
import com.foreveross.chameleon.util.CheckNetworkUtil;
import com.foreveross.chameleon.util.UnkownUtil;

public class AppManageActivity extends BaseActivity {
	private CubeApplication app;
	private BroadcastReceiver broadcastReceiver;
	// titlebar
	private Button titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;

	private TextView installed;
	private TextView uninstalled;
	private TextView toupgrade;
	DisplayStype displayStype;
	ModuleType moduleType;

	private TextView list;
	private TextView grid;

	private LinearLayout scroll_content;

	// 搜索
	private EditText app_search_edt;
	private ImageView app_search_close;
	// 搜索的内容
	private String app_search_content;
	private Map<String, List<CubeModule>> searchgroup = new HashMap<String, List<CubeModule>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		app=super.application.getCubeApplication();
		if(null==app){
			app=CubeApplication.getInstance(this);
			app.loadApplication();
			super.application.setCubeApplication(app);
		}
		initValue();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BroadcastConstans.MODULE_CHANGE);
		broadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent.getAction().equals(BroadcastConstans.MODULE_CHANGE)) {
					if(!UnkownUtil.isTopActivity(context, AppManageActivity.class)){
						return;
					}
					String identifier = intent.getStringExtra("identifier");
					CubeModule cubeModule = CubeModuleManager.getInstance()
							.getCubeModuleByIdentifier(identifier);
					if (cubeModule == null) {
						showDifferentLayout();
						return;
					}
					DataSourceAdapter baseAdapter = adapterMap.get(cubeModule
							.getCategory());
					if (baseAdapter != null) {
						List<CubeModule> m = null;
						if (ModuleType.INSTALLED.equals(moduleType)) {
							m = CubeModuleManager.getInstance().getInstalled_map().get(
									cubeModule.getCategory());
						} else if (ModuleType.UNINSTALLED.equals(moduleType)) {
							m = CubeModuleManager.getInstance().getUninstalled_map().get(
									cubeModule.getCategory());
						} else if (ModuleType.UPDATABLE.equals(moduleType)) {
							m = CubeModuleManager.getInstance().getUpdatable_map().get(
									cubeModule.getCategory());
						}
						if (null == m || m.size() == 0) {
							textMap.get(cubeModule.getCategory())
									.setVisibility(View.GONE);
						}
							
							baseAdapter.notifyDataSetChanged();
					} else {
						showDifferentLayout();
					}
				}
			}
		};
		registerReceiver(broadcastReceiver, filter);
		
	}
	
	public void initValue() {
		setContentView(R.layout.app_manage);
		// titlebar
		titlebar_left = (Button) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(onTouchListener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("模块管理");

		list = (TextView) findViewById(R.id.app_list);
		grid = (TextView) findViewById(R.id.app_grid);
		list.setOnClickListener(onTouchListener);
		grid.setOnClickListener(onTouchListener);
		grid.setBackgroundResource(R.drawable.segment_clickleft);
		list.setBackgroundResource(R.drawable.segment_right);
		installed = (TextView) findViewById(R.id.installed);
		uninstalled = (TextView) findViewById(R.id.uninstalled);
		toupgrade = (TextView) findViewById(R.id.toupgrade);
		installed.setOnClickListener(onTouchListener);
		uninstalled.setOnClickListener(onTouchListener);
		toupgrade.setOnClickListener(onTouchListener);

		scroll_content = (LinearLayout) findViewById(R.id.scroll_content);

		app_search_edt = (EditText) findViewById(R.id.app_search_edt);
		app_search_close = (ImageView) findViewById(R.id.app_search_close);
		app_search_close.setOnClickListener(onTouchListener);
		app_search_edt.addTextChangedListener(textWatcher);

		moduleType = ModuleType.UNINSTALLED;
		displayStype = DisplayStype.LISTSHOW;
		grid.setBackgroundResource(R.drawable.segment_left);
		list.setBackgroundResource(R.drawable.segment_clickright);
		installed.setTextColor(Color.parseColor("#161616"));
		installed.setBackgroundResource(R.drawable.bottom_cellcenter);
		uninstalled.setTextColor(Color.parseColor("#dcdcdc"));
		uninstalled.setBackgroundResource(R.drawable.click_cellleft);
		toupgrade.setTextColor(Color.parseColor("#161616"));
		toupgrade.setBackgroundResource(R.drawable.bottom_cellright);

		// 同步
		try {
			if(application.getLoginType()==TmpConstants.LOGIN_ONLINE){
				if (CheckNetworkUtil.checkNetWork(AppManageActivity.this)) {
					app.sync(syncListener, app, AppManageActivity.this,true);
				} else {
					Toast.makeText(AppManageActivity.this, "网络故障，无法同步数据",
							Toast.LENGTH_SHORT).show();
					showDifferentLayout();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			showDifferentLayout();
		}

	}
	
	ApplicationSyncListener syncListener = new ApplicationSyncListener() {

		@Override
		public void syncStart() {
		}

		@Override
		public void syncFinish() {
			showDifferentLayout();
		}

		@Override
		public void syncFail() {

		}
	};

	private Map<String, List<CubeModule>> groupModels = new HashMap<String, List<CubeModule>>();

	public void showDifferentLayout() {
		new AsyncTask<String, Integer, List<String>>(){
			
			@Override
			protected void onPreExecute() {
				scroll_content.setVisibility(View.INVISIBLE);
			};
			
			@Override
			protected List<String> doInBackground(String... paramArrayOfParams) {
				List<String> sort = null;
				if (ModuleType.INSTALLED.equals(moduleType)) {
					groupModels = CubeModuleManager.getInstance().getInstalled_map();
					sort = UnkownUtil.getSort(CubeModuleManager.getInstance().IN_INSTALLED, null);
				} else if (ModuleType.UNINSTALLED.equals(moduleType)) {
					groupModels = CubeModuleManager.getInstance().getUninstalled_map();
					sort = UnkownUtil.getSort(CubeModuleManager.getInstance().IN_UNINSTALLED, null);
				} else if (ModuleType.UPDATABLE.equals(moduleType)) {
					groupModels = CubeModuleManager.getInstance().getUpdatable_map();
					sort = UnkownUtil.getSort(CubeModuleManager.getInstance().IN_UPDATABLE, null);
				}
				return sort;
			}
			
			protected void onPostExecute(List<String> result) {
				if(result!=null){
					initView(result, displayStype, groupModels);
				}
				scroll_content.setVisibility(View.VISIBLE);
			};
		}.execute();
		
	}

	private Map<String, DataSourceAdapter> adapterMap = new HashMap<String, DataSourceAdapter>();
	private Map<String, LinearLayout> textMap = new HashMap<String, LinearLayout>();


	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d("AppManage",
					((CubeModule) parent.getItemAtPosition(position)).getName()
							+ "--->"
							+ ((CubeModule) parent.getItemAtPosition(position))
									.getBuild()
							+ "-->"
							+ ((CubeModule) parent.getItemAtPosition(position))
									.getModuleType());
			CubeModule cubeModule = (CubeModule) parent
					.getItemAtPosition(position);
			Intent intent = new Intent();
			intent.putExtra("identifier", cubeModule.getIdentifier());
			if (CubeModule.UPGRADABLE == cubeModule.getModuleType()
					|| CubeModule.UPGRADING == cubeModule.getModuleType()) {
				intent.putExtra("FROM_UPGRAGE", "FROM_UPGRAGE");
			}
			intent.setClass(AppManageActivity.this, AppDetailActivity.class);
			startActivity(intent);

		}
	};
	
	
	OnClickListener onTouchListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.title_barleft:
				AppManageActivity.this.finish();
				break;
			case R.id.app_search_close:
				app_search_edt.setText("");
				showDifferentLayout();
				break;
			case R.id.installed:
				app_search_edt.setText("");
				installed.setTextColor(Color.parseColor("#dcdcdc"));
				installed.setBackgroundResource(R.drawable.click_cellcenter);
				uninstalled.setTextColor(Color.parseColor("#161616"));
				uninstalled.setBackgroundResource(R.drawable.bottom_cellleft);
				toupgrade.setTextColor(Color.parseColor("#161616"));
				toupgrade.setBackgroundResource(R.drawable.bottom_cellright);
				moduleType = ModuleType.INSTALLED;
				showDifferentLayout();
				break;
			case R.id.uninstalled:
				app_search_edt.setText("");
				installed.setTextColor(Color.parseColor("#161616"));
				installed.setBackgroundResource(R.drawable.bottom_cellcenter);
				uninstalled.setTextColor(Color.parseColor("#dcdcdc"));
				uninstalled.setBackgroundResource(R.drawable.click_cellleft);
				toupgrade.setTextColor(Color.parseColor("#161616"));
				toupgrade.setBackgroundResource(R.drawable.bottom_cellright);
				moduleType = ModuleType.UNINSTALLED;
				showDifferentLayout();
				break;
			case R.id.toupgrade:
				app_search_edt.setText("");
				installed.setTextColor(Color.parseColor("#161616"));
				installed.setBackgroundResource(R.drawable.bottom_cellcenter);
				uninstalled.setTextColor(Color.parseColor("#161616"));
				uninstalled.setBackgroundResource(R.drawable.bottom_cellleft);
				toupgrade.setTextColor(Color.parseColor("#dcdcdc"));
				toupgrade.setBackgroundResource(R.drawable.click_cellright);
				moduleType = ModuleType.UPDATABLE;
				showDifferentLayout();
				break;
			case R.id.app_list:
				app_search_edt.setText("");
				displayStype = DisplayStype.LISTSHOW;
				grid.setBackgroundResource(R.drawable.segment_left);
				list.setBackgroundResource(R.drawable.segment_clickright);
				showDifferentLayout();
				break;
			case R.id.app_grid:
				app_search_edt.setText("");
				displayStype = DisplayStype.GRIDSHOW;
				grid.setBackgroundResource(R.drawable.segment_clickleft);
				list.setBackgroundResource(R.drawable.segment_right);
				showDifferentLayout();
				break;
			default:
				break;
			}
		}
	};
	TextWatcher textWatcher = new TextWatcher() {

		// @Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (0 < count || 0 < before) {
				app_search_close.setVisibility(View.VISIBLE);
				Message message = new Message();
				message.what = 0xc;
				handler.sendMessage(message);

			}
			if (start == 0 && count == 0) {
				app_search_close.setVisibility(View.GONE);
				showDifferentLayout();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	};
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0xc) {
				app_search_content = app_search_edt.getText().toString();
				if (!TextUtils.isEmpty(app_search_content)) {
					// showProgress();
					new Thread() {

						@Override
						public void run() {
							super.run();
							searchgroup.clear();
							Iterator<Entry<String, List<CubeModule>>> iter = groupModels
									.entrySet().iterator();
							while (iter.hasNext()) {
								List<CubeModule> search_result = new ArrayList<CubeModule>();
								Map.Entry<String, List<CubeModule>> entry = (Map.Entry<String, List<CubeModule>>) iter
										.next();
								String key = (String) entry.getKey();
								List<CubeModule> val = (List<CubeModule>) entry
										.getValue();
								for (CubeModule module : val) {
									if (module.getName().contains(
											app_search_content)) {
										search_result.add(module);
									}
								}
								if (search_result.size() != 0) {
									searchgroup.put(key, search_result);
								}

							}
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									List<String> sort = UnkownUtil.getSort(CubeModuleManager.getInstance().IN_INSTALLED, searchgroup);
									initView(sort,displayStype, searchgroup);
								}
							});

						};

					}.start();

				}

			}
		}
	};


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}
	
	
	
	
	private void initView(List<String> sort,DisplayStype type,Map<String,List<CubeModule>> groups){
		scroll_content.removeAllViews();
		for(String key:sort){
			List<CubeModule> values =  groups.get(key);
			if(values==null)continue;
			View view = LayoutInflater.from(AppManageActivity.this).inflate(
					R.layout.app_main_list, null);
			TextView title = (TextView) view.findViewById(R.id.list_title);
			LinearLayout linear = (LinearLayout) view.findViewById(R.id.linear);
			title.setText(key);
			NoScrollListView list_content = (NoScrollListView) view.findViewById(R.id.list_content);
			NoScrollGridView grid_content = (NoScrollGridView) view.findViewById(R.id.grid_content);
			if(type.equals(DisplayStype.LISTSHOW) ){
				
				list_content.setVisibility(View.VISIBLE);
				grid_content.setVisibility(View.GONE);
//				CubeListViewAdapter listAdapter = new CubeListViewAdapter(AppManageActivity.this, values , CubeModuleManager.getInstance().IN_MAIN);
				CubeListViewAdapter listAdapter = null;
				if (ModuleType.INSTALLED.equals(moduleType)) {
					listAdapter = new CubeListViewAdapter(AppManageActivity.this,
							values, CubeModuleManager.getInstance().IN_INSTALLED);
				} else if (ModuleType.UNINSTALLED.equals(moduleType)) {
					listAdapter = new CubeListViewAdapter(AppManageActivity.this,
							values, CubeModuleManager.getInstance().IN_UNINSTALLED);
				} else if (ModuleType.UPDATABLE.equals(moduleType)) {
					listAdapter = new CubeListViewAdapter(AppManageActivity.this,
							values, CubeModuleManager.getInstance().IN_UPDATABLE);
				}
				
				list_content.setAdapter(listAdapter);
				adapterMap.put(key, listAdapter);
				list_content.setOnItemClickListener(itemClickListener);
			}else if(type.equals(DisplayStype.GRIDSHOW)){
				list_content.setVisibility(View.GONE);
				grid_content.setVisibility(View.VISIBLE);
				CubeGridViewAdapter gridAdapter = null;
				if (ModuleType.INSTALLED.equals(moduleType)) {
					gridAdapter = new CubeGridViewAdapter(AppManageActivity.this,
							values, CubeModuleManager.getInstance().IN_INSTALLED);
				} else if (ModuleType.UNINSTALLED.equals(moduleType)) {
					gridAdapter = new CubeGridViewAdapter(AppManageActivity.this,
							values, CubeModuleManager.getInstance().IN_UNINSTALLED);
				} else if (ModuleType.UPDATABLE.equals(moduleType)) {
					gridAdapter = new CubeGridViewAdapter(AppManageActivity.this,
							values, CubeModuleManager.getInstance().IN_UPDATABLE);
				}
				
				grid_content.setAdapter(gridAdapter);
				adapterMap.put(key, gridAdapter);
				grid_content.setOnItemClickListener(itemClickListener);
			}
			textMap.put(key == null ? "基本功能" : key, linear);
			scroll_content.addView(view);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showDifferentLayout();
	}
}
