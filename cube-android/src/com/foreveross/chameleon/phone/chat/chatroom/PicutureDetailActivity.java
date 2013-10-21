/**
 * 
 */
package com.foreveross.chameleon.phone.chat.chatroom;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.phone.chat.image.CropImage;
import com.foreveross.chameleon.phone.chat.image.Util;

/**
 * @author zhoujun 图片预览
 */
public class PicutureDetailActivity extends Activity implements
		OnClickListener, OnTouchListener {

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;
	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 4f;// 最大缩放比例

	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	int mode = NONE;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;

	private Button back_btn;

	private Button pic_send;

	private TextView pic_origin;

	private TextView pic_thubnail;

	private ImageView myImage;

	private RelativeLayout title_layout;
	private RelativeLayout image_layout;
	private RelativeLayout bottom_layout;

	private Handler mHandler;

	private String imagePath;

	private String originImagePath;

	private float scale;

	private float newScale;

	private boolean zoomFlag = false;

	private float[] values = new float[9];

	private boolean flag;

	public static final int REQUEST_CODE_CROP_IMAGE = 0xf3;

	private ImageView cropImage;

	private String sendFilePath;

	String path;
	
	private boolean canSend;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_pic_detail);
		canSend = false;
		cropImage = (ImageView) findViewById(R.id.cropImage);
		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(this);
		pic_send = (Button) findViewById(R.id.pic_send);
		pic_send.setOnClickListener(this);
		pic_origin = (TextView) findViewById(R.id.pic_origin);
		pic_origin.setOnClickListener(this);
		pic_thubnail = (TextView) findViewById(R.id.pic_thubnail);
		pic_thubnail.setOnClickListener(this);
		myImage = (ImageView) findViewById(R.id.myImage);
		myImage.setOnTouchListener(this);
		image_layout = (RelativeLayout) findViewById(R.id.image_layout);
		title_layout = (RelativeLayout) findViewById(R.id.title_layout);
		bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
		// 隐藏裁剪功能
		bottom_layout.setVisibility(View.GONE);
		// showCustomDialog(true);
		imagePath = getIntent().getStringExtra("imagePath");
		flag = getIntent().getBooleanExtra("showFlag", true);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (!flag) {
			pic_send.setVisibility(View.GONE);
			bottom_layout.setVisibility(View.GONE);

		}
		if (imagePath != null) {
			Log.e("---------点击打开图片：", imagePath);
			path = Environment.getExternalStorageDirectory()
					+ "/CubeImageCache/sendFiles/";
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			path += "temp_photo_org.jpg";
			File fileTemp = new File(path);
			if (!fileTemp.exists()) {
				try {
					fileTemp.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			new AsyncTask<String, Void, Bitmap>() {

				@Override
				protected Bitmap doInBackground(String... params) {
					Bitmap bitmap = getimage(params[0] , params[1]);
					return bitmap;
				}
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						canSend = true;
						sendFilePath = path;

						minZoom(result);
						myImage.setImageBitmap(result);
						center(result);
						myImage.setImageMatrix(matrix);
						// finish();
					} else {
						myImage.setImageResource(R.drawable.pic_bg_03);
					}
				};
			}.execute(imagePath , path);
		}

		// final Bitmap bitmap = getIntent().getParcelableExtra("image");

		// mHandler = new Handler() {
		//
		// @Override
		// public void handleMessage(Message msg) {
		//
		// super.handleMessage(msg);
		// if (msg.what == 0) {
		// InputStream is = null;
		// //ByteArrayInputStream bis = null;
		// try {
		// Log.e("OutOfMemory_", "msg.what == 0");
		// File file = new File(imagePath);
		// is = new FileInputStream(file);
		//
		// // _MARK_标记这里压缩图片
		// System.gc();
		// byte[] bytes = new byte[is.available()];
		// System.gc();
		// is.read(bytes);
		// System.gc();
		// Log.e("OutOfMemory_opt.inSampleSize", bytes.length
		// + "bytes.length");
		// Bitmap bmp = compFromStream(bytes);
		// System.gc();
		// cancelDialog();
		// myImage.setImageBitmap(bmp);
		// dm = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率
		// minZoom(bmp);
		// // center(bitmap);
		// myImage.setImageMatrix(matrix);
		// //if (file.length() / 1024 > 300) {// 这里写得很恶心，大于300K的图片都压缩了，防止溢出
		//
		// String path = Environment
		// .getExternalStorageDirectory()
		// + "/CubeImageCache/sendFiles/";
		// File dest = new File(path);
		// if (!dest.exists()) {
		// dest.mkdirs();
		// }
		// String[] tmpStr = imagePath.split("/");
		// String fileName = tmpStr[tmpStr.length - 1];
		// File sendFile = new File(path + fileName);
		// if (!sendFile.exists()) {
		// try {
		// sendFile.createNewFile();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// FileOutputStream fos = null;
		// try {
		// fos = new FileOutputStream(sendFile);
		// bmp.compress(CompressFormat.PNG, 100, fos);
		// imagePath = path + fileName;
		//
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } finally {
		// if (fos != null) {
		// try {
		// fos.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		//
		// //}
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// if (null != is)
		// is.close();
		//
		// is = null;
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// }
		// Log.e("OutOfMemory_", "msg.what == 0_0");
		// } else if (msg.what == 1) {
		// Log.e("OutOfMemory_", "msg.what == 1");
		// Bitmap newBitmap = comp(bitmap);
		// // cancelDialog();
		// myImage.setImageBitmap(newBitmap);
		// } else if (msg.what == 2) {
		// Log.e("OutOfMemory_", "msg.what == 2");
		// InputStream is = null;
		// try {
		// File file = new File(originImagePath);
		// is = new FileInputStream(file);
		// //Bitmap bitmap = BitmapFactory.decodeStream(is);
		// Bitmap bitmap = comp(BitmapFactory.decodeStream(is));
		// cancelDialog();
		// myImage.setImageBitmap(bitmap);
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// if (null != is)
		// is.close();
		// is = null;
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// }
		// }
		// }
		//
		// };
		// if (imagePath == null && bitmap == null) {
		// // cancelDialog();
		// Toast.makeText(this, "图片为空，请重新选择", Toast.LENGTH_SHORT).show();
		//
		// } else if (bitmap == null && imagePath != null) {
		// mHandler.sendEmptyMessage(0);
		// } else if (bitmap != null && imagePath == null) {
		// mHandler.sendEmptyMessage(1);
		// }
		// originImagePath = imagePath;

	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100 && options > 0) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		image.recycle();
		Log.e("压缩后", "compressImage压缩大小为：" + baos.toByteArray().length);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Options opt = new BitmapFactory.Options();
		// _MARK_标记这里压缩图片

		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	private Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		image.recycle();
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		int height = getWindowManager().getDefaultDisplay().getHeight();
		int width = getWindowManager().getDefaultDisplay().getWidth();
		height -= (bottom_layout.getLayoutParams().height + title_layout
				.getLayoutParams().height);
		float hh = height;// image_layout.getHeight(); //800f;//这里设置高度为800f
		float ww = width;// image_layout.getWidth();//480f;//这里设置宽度为480f
		System.out.println(hh + "   " + ww);
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private Bitmap compFromStream(byte[] bytes) {
		ByteArrayInputStream isBm = new ByteArrayInputStream(bytes);
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		int height = getWindowManager().getDefaultDisplay().getHeight();
		int width = getWindowManager().getDefaultDisplay().getWidth();
		height -= (bottom_layout.getLayoutParams().height + title_layout
				.getLayoutParams().height);
		float hh = height;// image_layout.getHeight(); //800f;//这里设置高度为800f
		float ww = width;// image_layout.getWidth();//480f;//这里设置宽度为480f
		System.out.println(hh + "   " + ww);
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(bytes);
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩

	}

	/**
	 * 触屏监听
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if (flag) {
			return false;
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		// 副点按下
		case MotionEvent.ACTION_POINTER_DOWN:
			dist = spacing(event);
			// 如果连续两点距离大于10，则判定为多点模式
			if (spacing(event) > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
				}
			}
			break;
		}
		myImage.setImageMatrix(matrix);
		Bitmap bitmap = ((BitmapDrawable) myImage.getDrawable()).getBitmap();
		// CheckView(bitmap);
		return true;
	}

	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	private void CheckView(Bitmap bitmap) {
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {
			if (p[0] < minScaleR) {
				matrix.setScale(minScaleR, minScaleR);
			}
			if (p[0] > MAX_SCALE) {
				matrix.set(savedMatrix);
			}
		}
		// center(bitmap);
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom(Bitmap bitmap) {
		minScaleR = Math.min(
				(float) dm.widthPixels / (float) bitmap.getWidth(),
				(float) dm.heightPixels / (float) bitmap.getHeight());
		if (minScaleR < 1.0) {
			matrix.postScale(minScaleR, minScaleR);
		}
	}

	private void center(Bitmap bitmap) {
		center(true, true, bitmap);
	}

	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical, Bitmap bitmap) {

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = dm.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = myImage.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.back_btn:
			myImage.setImageBitmap(null);
			System.gc();
			finish();
			break;
		case R.id.pic_send:
			// Intent data = new Intent();
			// data.putExtra("path", imagePath);
			// setResult(4, data);
			// 发送中
			if(!canSend){
				Toast.makeText(PicutureDetailActivity.this, "图片加载中", Toast.LENGTH_SHORT).show();
				return;
			}
			String imageFilePath = Environment.getExternalStorageDirectory()
					+ "/CubeImageCache/sendFiles/";
			File dir = new File(imageFilePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			imageFilePath += System.currentTimeMillis();
			File imageFile = new File(imageFilePath);
			if (!imageFile.exists()) {
				try {
					imageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			new AsyncTask<String, Integer, String>() {

				@Override
				protected String doInBackground(String... params) {
					try {
						copyFile(new File(params[0]), new File(params[1]));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
/*					String path = params[0];
					Bitmap bitmap = BitmapFactory.decodeFile(path);
					FileOutputStream fos = null;
					try {
						fos = new FileOutputStream(params[1]);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					int degree = Util.readPictureDegree(params[1]);
					bitmap = Util.rotateImage(bitmap, degree);
					bitmap.compress(CompressFormat.JPEG, 100, fos);
					try {
						fos.close();
						baos.close();
						bitmap.recycle();
						System.gc();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
					return params[1];
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					Intent sendintent = getIntent();
					sendintent.putExtra(CropImage.IMAGE_PATH, result);
					setResult(RESULT_OK, sendintent);
					myImage.setImageBitmap(null);
					cropImage.setImageBitmap(null);
					System.gc();
					finish();
				}
			}.execute(sendFilePath, imageFile.getAbsolutePath());

			break;
		case R.id.pic_origin:
			// showCustomDialog(true);
			// Message msg = Message.obtain();
			// msg.what = 2;
			// mHandler.sendMessage(msg);
			myImage.setVisibility(View.VISIBLE);
			cropImage.setVisibility(View.GONE);
			sendFilePath = imagePath;
			break;
		case R.id.pic_thubnail:
			Intent intent = new Intent(this, CropImage.class);
			intent.putExtra(CropImage.IMAGE_PATH, path);
			intent.putExtra(CropImage.SCALE, true);

			intent.putExtra(CropImage.ASPECT_X, 3);
			intent.putExtra(CropImage.ASPECT_Y, 2);
			Log.e("CORPIMAGE", "启动裁剪");
			startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
			/*
			 * Bitmap bitmap = ((BitmapDrawable) myImage.getDrawable())
			 * .getBitmap(); int height = bitmap.getHeight(); int width =
			 * bitmap.getWidth(); Matrix matrix = savedMatrix; float[] p = new
			 * float[9]; matrix.getValues(p); matrix.postScale(p[0], p[0]);
			 * final Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
			 * height, matrix, true); new AsyncTask<String, Integer, String>() {
			 * 
			 * @Override protected String doInBackground(String... params) {
			 * String path = Environment.getExternalStorageDirectory() +
			 * "/CubeImageCache/sendFiles/"; File dest = new File(path); if
			 * (!dest.exists()) { dest.mkdirs(); } String[] tmpStr =
			 * imagePath.split("/"); String fileName = tmpStr[tmpStr.length -
			 * 1]; File sendFile = new File(path + fileName); if
			 * (!sendFile.exists()) { try { sendFile.createNewFile(); } catch
			 * (IOException e) { e.printStackTrace(); } } FileOutputStream fos =
			 * null; try { fos = new FileOutputStream(sendFile);
			 * newBitmap.compress(CompressFormat.PNG, 100, fos); imagePath =
			 * path + fileName;
			 * 
			 * } catch (FileNotFoundException e) { e.printStackTrace(); }
			 * finally { if (fos != null) { try { fos.close(); } catch
			 * (IOException e) { e.printStackTrace(); } } } return imagePath; }
			 * 
			 * @Override protected void onPostExecute(String result) {
			 * super.onPostExecute(result);
			 * Toast.makeText(PicutureDetailActivity.this, "压缩成功",
			 * Toast.LENGTH_SHORT).show();
			 * 
			 * }
			 * 
			 * }.execute();
			 */
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != android.app.Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case REQUEST_CODE_CROP_IMAGE:
			String path = data.getExtras().getString(CropImage.IMAGE_PATH);
			// Bundle extras = data.getExtras();
			// String path = extras.getString(CropImage.IMAGE_PATH);
			Log.e("REQUEST_CODE_CROP_IMAGE",
					"data.getStringExtra(CropImage.IMAGE_PATH)=" + path);
			if (path == null) {
				return;
			}
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			// mImageView.setImageBitmap(bitmap);
			myImage.setVisibility(View.GONE);
			cropImage.setVisibility(View.VISIBLE);
			cropImage.setImageBitmap(bitmap);
			bitmap.recycle();
			sendFilePath = path;
		}
	}

	private Bitmap getimage(String srcPath , String  path) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为

		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;
		float hh = screenHeigh;// 这里设置高度为800f
		float ww = screenWidth;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage1(bitmap , path);// 压缩好比例大小后再进行质量压缩
	}

	private Bitmap compressImage1(Bitmap image , String path) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int degree = Util.readPictureDegree(imagePath);
		bitmap = Util.rotateImage(bitmap, degree);
		/*
		 * if (baos.toByteArray().length / 1024 > 1024) {//
		 * 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
		 * baos.reset();// 重置baos即清空baos
		 * bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);//
		 * 这里压缩50%，把压缩后的数据存放到baos中 } else {
		 * bitmap.compress(CompressFormat.JPEG, 100, fos); }
		 */
		// 压缩 50%
		bitmap.compress(CompressFormat.JPEG, 50, fos);
		return bitmap;
	}
	
    // 复制文件
    public void copyFile(File sourceFile, File targetFile) throws IOException {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }


}
