
package com.foreveross.chameleon.phone.modules.task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.CubeConstants;
import com.foreveross.chameleon.util.HttpUtil;

/**
 * @author chencao
 * 
 *         1、新建http异步任务
 * 
 *         HttpRequestAsynTask task = new HttpRequestAsynTask(HttpExample.this)
 *         {
 * @Override protected void doPostExecute(String result) {
 * 
 *           //2、获取响应result结果处理相关逻辑 // Log.d("cube", result); }
 * 
 *           };
 * 
 *           3、真正发起http请求
 * 
 *           task.execute("http://58.215.176.89:9000/m/apps/com.csair.appcs/7",
 *           "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);
 * 
 *           // =====execute中数组参数含义：======== params[0] = url; 请求URL
 * 
 *           params[1] = ""; 请求内容,如果是Form形式，参考：Form:key1=value1;key2=value2 |
 *           如果是upload模式，参考：UPLOAD:key1=value1;key2=value2
 *           UPLOAD约定：key1对应的value1值一定是需要上传的路径（/mnt/sdcard/my.txt）
 * 
 *           params[2] = HttpUtil.GB2312_ENCODING; 编码方式，默认UTF8_ENCODING
 * 
 *           params[3] = HttpUtil.HTTP_GET; 请求方式GET／POST，默认POST
 * 
 *           params[4] = "5000"; HTTP_CONNECTION_TIMEOUT，默认60000
 * 
 *           params[5] = "3000"; SOCKET_TIMEOUT，默认60000
 */
public abstract class HttpRequestAsynTask extends GeneralAsynTask {

	/**
	 * @param context
	 */
	protected Context context;
	

	public HttpRequestAsynTask(Context context) {
		super(context);
		this.context = context;
	}

	public HttpRequestAsynTask(Context context, boolean alertResultNull) {
		super(context, alertResultNull);
	}

	public HttpRequestAsynTask(Context context, String prompt) {
		super(context, prompt);
		this.context = context;
	}

	public HttpRequestAsynTask(Context context, ProgressDialog progressDialog) {
		super(context, progressDialog);
		this.context = context;
	}
	
	public int EXCEPTION_MESSAGE=0x0a;
	public Handler handler=new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
//			doHttpFail(e);
			if(msg.what==EXCEPTION_MESSAGE){
				Exception e =(Exception) msg.getData().getSerializable("exception");
				doHttpFail(e);
			}
		};
	};
	
	// 此方法在UI线程中执行
	// 当后台计算结束时，调用 UI线程。后台计算结果作为一个参数传递到这步
	@Override
	protected void onPostExecute(String result) { // 操作UI
		super.onPostExecute(result);
		ThreadPlatformUtils.finishTask(this);
		if (stopped) {
			return;
		}

        //由于设备注册返回为空,直接过滤了
//		if ((result == null || "".equals(result)) ) {
        if (result == null) {
			if(needProgressDialog){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("连接失败，请检查网络");
				builder.setPositiveButton("确定",null);
				Dialog dialog = builder.create();
				dialog.show();
			}
			return;
		}
		else if(result.equals("400")){
			if(needProgressDialog){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("账号已在别处登录，请注销");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Application.class.cast(context.getApplicationContext()).logOff();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
			return;
		}else if(result.equals("404")){
			if(needProgressDialog){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("远程服务器出错");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					//	Application.class.cast(context.getApplicationContext()).logOff();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
			return;
		}
		if (!stopped) {
			Log.i("AAAAA HttpRequest","result = "+result);
			doPostExecute(result);
		}
	}
	
	@Override
	public void doPreExecuteBeforeDialogShow() {
		super.doPreExecuteBeforeDialogShow();
	}

	@Override
	public void doPreExecuteWithoutDialog() {
		super.doPreExecuteWithoutDialog();
		ThreadPlatformUtils.addTask2List(this);
	}

	@Override
	protected void doPostExecute(String result) {
		
	}
	
	public void setDailogMessage(String message){
		
	}
	@Override
	protected String doInBackground(String... params) { // 后台执行
		String result = "";
		try{
			if (!CubeConstants.DEMO) {
				result = HttpUtil.doWrapedHttp(context, params);
			}
		}catch(Exception e){
			
			e.printStackTrace();
			Message msg =  new Message();
			msg.what=EXCEPTION_MESSAGE;
			Bundle bundle=new Bundle();
			bundle.putSerializable("exception", e);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		return result;
	}

	protected void doHttpFail(Exception e) {
		
	}
	
}
