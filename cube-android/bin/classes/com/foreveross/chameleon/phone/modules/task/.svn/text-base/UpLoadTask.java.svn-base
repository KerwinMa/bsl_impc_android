package com.foreveross.chameleon.phone.modules.task;

import java.io.File;

import android.content.Context;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.util.HttpUtil;

/**
 * <BR>
 * 上传文件组建
 * [功能详细描述]
 * @author Amberlo
 * @version [CubeAndroid , 2013-7-12] 
 */
public class UpLoadTask extends HttpRequestAsynTask{
	public UpLoadTask(Context context) {
		super(context);
		this.setLockScreen(false);
		this.setShowProgressDialog(false);
		this.setNeedProgressDialog(false);
		
	}
	
	@Override
	protected String doInBackground(String... params) {
		String filePath = params[0];
		StringBuilder sb = new StringBuilder();
//		sb.append("UPLOAD:file=").append(new File(filePath))
//				.append(";enctype=").append("multipart/form-data");
		sb.append("UPLOAD:file=").append(new File(filePath));
//		.append(";appKey=").append(Application.class.cast(context.getApplicationContext()).getCubeApplication().getAppKey());;
		return super.doInBackground(URL.UPLOAD_URL,sb.toString(),HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);
	}
	
	
	@Override
	protected void doPostExecute(String result) {
		super.doPostExecute(result);
	}
	
	@Override
	public void doPreExecuteWithoutDialog() {
		super.doPreExecuteWithoutDialog();
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
	}
	
	@Override
	protected void doHttpFail(Exception e) {
		super.doHttpFail(e);
	}

	
	
}

