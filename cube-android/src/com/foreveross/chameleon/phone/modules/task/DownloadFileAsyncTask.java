package com.foreveross.chameleon.phone.modules.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.foreveross.chameleon.util.FileWriterUtil;

public abstract class DownloadFileAsyncTask extends GeneralAsynTask {

	public static final String SDCARD = "SDCARD";
	public static final String PRIVATE = "PRIVATE";
	public int EXCEPTION_MESSAGE = 0x0a;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0)
				doHttpFail(null);
			else{
				Exception e = (Exception) msg.getData()
						.getSerializable("exception");
				doHttpFail(e);
			}
		};
	};

	public DownloadFileAsyncTask(Context context) {
		super(context);
	}

	public DownloadFileAsyncTask(Context context, String prompt) {
		super(context, prompt);
	}

	public DownloadFileAsyncTask(Context context, ProgressDialog progressDialog) {
		super(context, progressDialog);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// Log.d("cube", "ANDRO_ASYNC=" + progress[0]);
	}

	@Override
	public void doPreExecuteBeforeDialogShow() {
//		progressDialog.setMessage("下载文件");
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog.setCancelable(true);
	}

	@Override
	public void doPreExecuteWithoutDialog() {
		super.doPreExecuteWithoutDialog();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ThreadPlatformUtils.addDownloadTask2List(this);
	}

	@Override
	protected void doPostExecute(String result) {

	}

	// 此方法在UI线程中执行
	// 当后台计算结束时，调用 UI线程。后台计算结果作为一个参数传递到这步
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ThreadPlatformUtils.finishDownloadTask(this);
		if (stopped) {
			return;
		} else {

			doPostExecute(result);
		}
	}

	@Override
	protected String doInBackground(String... params) {

//		Log.d("cube", "下载 url=" + params[0]);

		// 参数的第一个为url，第二个参数为File名称，第三个参数选填为写私有(PRIVATE)还是写sdcard(SDCARD)，第四个参数表示子目录名（模块名）
		Message exceptionMessage = new Message();
		// exceptionMessage.what=EXCEPTION_MESSAGE;
		try {

			if (params.length < 2) {
				throw new IllegalArgumentException("参数个数不正确");
			}
			HttpClient client = new DefaultHttpClient();
			for (String string : params) {
				System.out.println("params="+string);
			}
			HttpGet post = new HttpGet(params[0]);
			HttpResponse response;

			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			
			String contentDisposition = response.getFirstHeader(
					"Content-Disposition").getValue();
			Header lenHeader = response.getFirstHeader("Content-Length");
			String contentLength = "-1";
			if (lenHeader != null) {
				contentLength = lenHeader.getValue();
			}

			long length = entity.getContentLength();
//			Log.d("cube", "entity.getContentLength():" + length);
//			Log.d("cube", "Header Content-Length:" + contentLength);
//			Log.d("cube", "Header Content-Length:" + contentDisposition);

			if (length == -1) {
				length = Long.valueOf(contentLength);
			}

			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			String fileName = "";
			try {
				if (params[1] == null || "".equals(params[1])) {
					if (contentDisposition == null
							|| "".equals(contentDisposition)) {
						Exception e = new Exception();
						Log.e("cube", "无法获取下载文件名");
						exceptionMessage.getData().putSerializable(
								"excepttion", e);
						handler.sendMessage(exceptionMessage);
					} else {
						fileName = contentDisposition;
					}
				} else {
					fileName = params[1];
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionMessage.getData().putSerializable("excepttion", ex);
				handler.sendMessage(exceptionMessage);
				return null;
			}
			String writeType = PRIVATE;
			try {
				writeType = params[2];
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionMessage.getData().putSerializable("excepttion", ex);
				handler.sendMessage(exceptionMessage);
				return null;
			}
			String moduleName = "";
			try {
				moduleName = params[3];
			} catch (Exception ex) {
				exceptionMessage.getData().putSerializable("excepttion", ex);
				handler.sendMessage(exceptionMessage);
				ex.printStackTrace();
				return null;
			}

			Log.d("cube", "url=" + params[0] + " fileName=" + fileName
					+ " writeType=" + writeType + " moduleName=" + moduleName);
			if (is != null) {
				if (PRIVATE.equals(writeType)) {
					if ("".equals(moduleName)) {
						fileOutputStream = context.openFileOutput(fileName,
								Context.MODE_WORLD_READABLE);
					} else {
						File moduleDir = context.getDir(moduleName,
								Context.MODE_PRIVATE);
						fileOutputStream = new FileOutputStream(new File(
								moduleDir, fileName));
					}
				} else {
					if ("".equals(moduleName)) {
						fileOutputStream = new FileOutputStream(new File(
								Environment.getExternalStorageDirectory(),
								fileName));
					} else {
						FileWriterUtil.mkdirInSdcard(moduleName);
						String dirpath = Environment
								.getExternalStorageDirectory()
								+ "/"
								+ moduleName;
						fileOutputStream = new FileOutputStream(new File(
								dirpath, fileName));
					}

				}
				// Log.d("cube", "开始下载 length=" + length);

				byte[] buf = new byte[1024 * 256];
				int ch = -1;
				int count = 0;
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					publishProgress(length == -1 ? -1
							: (int) ((count * 100) / length));
//					System.out.println("read:"+ch);
				}
				publishProgress(100);

			}
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			Log.d("cube", "下载完成");

			if (is != null) {
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("cube", "下载失败");
//			exceptionMessage.getData().putSerializable("excepttion", e);
//			handler.sendMessage(exceptionMessage);
			handler.sendEmptyMessage(0);
		}
		return null;
	}

	protected void doHttpFail(Exception e) {
//		ThreadPlatformUtils.finishDownloadTask(this);
	}

}
