package com.foreveross.chameleon.phone.chat.camera;

import java.util.List;

import com.csair.impc.R;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


@TargetApi(14)
public class Preview extends SurfaceView implements SurfaceHolder.Callback{
	public Preview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Log.e("初始化", "Preview(Context context, AttributeSet attrs, int defStyle) ");
	}
	Context mycontext;
	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		time = System.currentTimeMillis();
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Log.e("初始化", "Preview(Context context, AttributeSet attrs) ");
		mycontext = context;
	}

	SurfaceHolder mHolder;
	Camera mCamera;
	Bitmap CameraBitmap;
	@SuppressWarnings("deprecation")
	public Preview(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Log.e("初始化", " Preview(Context context)");	
	}

	@Override
	public void surfaceChanged(SurfaceHolder sholder, int format, int swidth,
			int sheight) {
		Log.e("surfaceChanged", ""+(System.currentTimeMillis()-time));
		final SurfaceHolder holder = sholder;
		final int width = swidth;
		final int height = sheight;
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				openCamera(CameraType.BACK);
				try{
					
					mCamera.setPreviewDisplay(holder);
				
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setPictureFormat(PixelFormat.JPEG);
				List<Camera.Size> sizes = parameters.getSupportedPreviewSizes(); 
				Size optsize = getOptimalPreviewSize(sizes, width, height); 
				// mFrameWidth = optsize.width; 
				// mFrameHeight = optsize.height; 
				parameters.setPreviewSize(optsize.width, optsize.height); 
				parameters.setRotation(90);
				//Log.e("相机大小", width+"X"+height);
				mCamera.setParameters(parameters);
				mCamera.startPreview();
				Log.e("mCamera.startPreview()",""+(System.currentTimeMillis()-time));
				}catch(Exception e){
					mCamera.release();
					mCamera = null;	
					cancelDialog();
				}
				cancelDialog();
			}}).start();
		
	}
	public void StartPreview(){
		mCamera.stopPreview();
		mCamera.startPreview();
	}
	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) { 
		final double ASPECT_TOLERANCE = 0.05; 
		double targetRatio = (double) w / h; 
		if (sizes == null) 
		return null; 
		Size optimalSize = null; 
		double minDiff = Double.MAX_VALUE; 
		int targetHeight = h; 

		for (Size size : sizes) { 
		double ratio = (double) size.width / size.height; 
		if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) 
		continue; 
		if (Math.abs(size.height - targetHeight) < minDiff) { 
		optimalSize = size; 
		minDiff = Math.abs(size.height - targetHeight); 
		} 
		}
		// Cannot find the one match the aspect ratio, ignore the requirement 
		if (optimalSize == null) { 
		minDiff = Double.MAX_VALUE; 
		for (Size size : sizes) { 
		if (Math.abs(size.height - targetHeight) < minDiff) { 
		optimalSize = size; 
		minDiff = Math.abs(size.height - targetHeight); 
		} 
		} 
		} 
		return optimalSize; 
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		showCustomDialog(false);
		
	}
	long time;
	public Dialog progressDialog;
	public void showCustomDialog(boolean cancelable){
		Log.e("加载对话框", ""+(System.currentTimeMillis()-time));
		if(progressDialog==null){
			progressDialog = new Dialog(mycontext,R.style.dialog);
			progressDialog.setContentView(R.layout.dialog_layout);
		}
		
		if(progressDialog.isShowing()){
			return;
		}
//		View v = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
//		progressDialog.setContentView(v);
//		TextView text = (TextView) v.findViewById(R.id.dialog_text);
//		text.setText(message);
		progressDialog.setCancelable(cancelable);
		progressDialog.show();
	}
	
	public void cancelDialog(){
		Log.e("取消对话框", ""+(System.currentTimeMillis()-time));
		if(progressDialog==null){
			return;
		}
		if(progressDialog.isShowing()){
			progressDialog.cancel();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}
	public void openCamera(CameraType type){
		if (SdkUtil.is2x3()) {
			// 使用2.3方法进行连接
			switch (type) {
			case FRONT:// 连接前置相机
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
				//realType = CameraType.FRONT;
				break;
			case BACK:// 连接后置相机
			default:
				mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
				//realType = CameraType.BACK;
				break;
			}
		} else {
			// 使用2.2方法进行连接
			mCamera = Camera.open();
			//realType = CameraType.BACK;
		}
		mCamera.setDisplayOrientation(90);
	}
	public void takePicture(Camera.ShutterCallback paramShutterCallback, Camera.PictureCallback paramPictureCallback1, Camera.PictureCallback paramPictureCallback2)
	  {
	    this.mCamera.takePicture(paramShutterCallback, paramPictureCallback1, paramPictureCallback2);
	  }
	
private CameraType mType;
public enum CameraType {
	FRONT, BACK, NONE;
	
	public static CameraType parseStr(String str) {
		
			try {
				return CameraType.valueOf(str);
			} catch (Exception e) {
				// TODO 记录异常到日志
				e.printStackTrace();
				return CameraType.BACK;
			}
	}
}

}