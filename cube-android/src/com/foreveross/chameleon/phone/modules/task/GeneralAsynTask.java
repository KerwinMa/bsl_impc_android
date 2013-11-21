package com.foreveross.chameleon.phone.modules.task;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.csair.impc.R;
//import android.app.ProgressDialog;

public abstract class GeneralAsynTask extends
		AsyncTask<String, Integer, String> {
//	// 声明进度条对话框
//	public ProgressDialog progressDialog;
	public Dialog dialog;
	protected Context context;
	protected String prompt;
	protected String dialogContent  = "请稍候...";
	public  boolean setlandtab = false;
	
	protected boolean alertResultNull = false;
	protected boolean showProgressDialog = true;
	protected boolean lockScreen = true;
	protected boolean stopped = false;
	protected boolean needProgressDialog = true;
	public GeneralAsynTask(Context context) {
		super();
		
		this.context = context;
	}

	public GeneralAsynTask(Context context, boolean alertResultNull) {
		this(context);
		this.alertResultNull = alertResultNull;
	}

	public GeneralAsynTask(Context context, String prompt) {
		super();
		this.context = context;
		this.prompt = prompt;

	}

//	public GeneralAsynTask(Context context, ProgressDialog progressDialog) {
//		super();
//		this.progressDialog = progressDialog;
//		this.context = context;
//	}
	
	public GeneralAsynTask(Context context,Dialog dialog){
		super();
		this.dialog=dialog;
		this.context = context;
	}
	// 此方法在UI线程中执行
	// 任务被执行之后，立刻调用 UI线程。这步通常被用于设置任务，例如在用户界面显示一个进度条
	@Override
	protected void onPreExecute() {
		
		if (!needProgressDialog) {
			doPreExecuteWithoutDialog();
			return;
		} else {
//			// 创建ProgressDialog对象
//			if (showProgressDialog &&  progressDialog == null ) {
//				progressDialog = new ProgressDialog(context);
//				// 设置进度条风格，风格为圆形，旋转的
//				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//				// 设置ProgressDialog 标题
//				progressDialog.setTitle("提示");
//				// 设置ProgressDialog 提示信息
//				if (prompt == null) {
//					progressDialog.setMessage("请稍候...");
//				} else {
//					progressDialog.setMessage(prompt);
//				}
//				
//				progressDialog.setOnKeyListener(new OnKeyListener() {
//					@Override
//					public boolean onKey(DialogInterface dialog, int keyCode,
//							KeyEvent event) {
//						if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//							if (lockScreen) {
//								return true;
//							} else {
//								try {
//									stopped = true;
//									// NavigationActivity navigation =
//									// (NavigationActivity) context;
//									// navigation.popFragment();
//								} catch (Exception ex) {
//									ex.printStackTrace();
//								}
//							}
//						}
//						return false;
//					}
//				});
//
//				// 设置ProgressDialog 标题图标
//				// progressDialog.setIcon(R.drawable.wait);
//				// 设置ProgressDialog 的进度条是否不明确
//				progressDialog.setIndeterminate(false);
//
//				doPreExecuteBeforeDialogShow();
//
//				progressDialog.show();
				
				
				
				// 创建ProgressDialog对象
				if (showProgressDialog &&  dialog == null ) {
					dialog = new Dialog(context,R.style.common_dialog);
					LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View v = mInflater.inflate(R.layout.dialog_layout, null);
					TextView	tv = (TextView) v.findViewById(R.id.dialog_text);
					tv.setText(getDialogContent());
					dialog.setContentView(v);
					
					if(lockScreen){
						dialog.setCancelable(false);
					}else{
						dialog.setCancelable(true);
					}
//					TextView text = (TextView) v.findViewById(R.id.dialog_text);
//					if (prompt == null) {
////						progressDialog.setMessage("请稍候...");
//						text.setText("请稍后..");
//					} else {
//						text.setText(prompt);
//					}
					
					dialog.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode,
								KeyEvent event) {
							if ((keyCode == KeyEvent.KEYCODE_BACK)) {
								if (lockScreen) {
									return true;
								} else {
									stopped = true;
								}
							}
							return false;
						}
					});

					doPreExecuteBeforeDialogShow();
					if(!dialog.isShowing()){
						dialog.show();
					}
			}
		}
	}

	// 此方法在UI线程中执行
	// 当后台计算结束时，调用 UI线程。后台计算结果作为一个参数传递到这步
	@Override
	protected void onPostExecute(String result) {
		
		if(needProgressDialog){
			if (null != dialog && dialog.isShowing()) {
				try {
					dialog.cancel();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
		}
	}

	public void doPreExecuteBeforeDialogShow() {
	
	}
	
	public void doPreExecuteWithoutDialog() {

	}
	public void setShowProgressDialog(boolean show) {
		this.showProgressDialog = show;
	}

	public void setNeedProgressDialog(boolean needProgressDialog) {
		this.needProgressDialog = needProgressDialog;
	}
	
	
	
	public String getDialogContent() {
		return dialogContent;
	}
	/**
	 * 设置菊花字段
	 * 
	 * @param 
	 */
	public void setDialogContent(String dialogContent) {
		this.dialogContent = dialogContent;
	}

	/**
	 * 设置是否锁屏（默认锁屏）
	 * 
	 * @param lockScreen
	 */
	public void setLockScreen(boolean lockScreen) {
		this.lockScreen = lockScreen;
	}

	protected abstract void doPostExecute(String result);
	
//	protected abstract void doHttpFail(Exception e);
}
