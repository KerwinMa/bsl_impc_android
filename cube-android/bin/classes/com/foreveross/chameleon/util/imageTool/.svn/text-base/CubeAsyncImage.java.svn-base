package com.foreveross.chameleon.util.imageTool;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.foreveross.chameleon.util.imageTool.SyncImageItem.SyncImageItemState;

/**

 */
public class CubeAsyncImage {
	private HashMap<String, SoftReference<SyncImageItem>> map;
	private ExecutorService executorService;
	private Activity activity;
	private ImageUtil imageUtil;
	public static final String TAG = CubeAsyncImage.class.getSimpleName();

	private int sampleSize = 3;

	/** 图片宽度 */
	private int bitmapW = ImageUtil.WRAP_CONTENT;

	/** 图片高度 */
	private int bitmapH = ImageUtil.WRAP_CONTENT;

	/** 图片圆角 */
	private float roundPx = 0;

	private String sdcard = "/sdcard";

	/** SD卡缓存路径 */
	private String imgBufferParent = "/botuba/buffer/";

	/** 图片后缀 */
	private String imgSuffix = ".tuba";

	/** 加载视频截图 */
	public static final int LOAD_FORM_VIDEO_FILEPATH = 1;

	/** 加载网络图片 */
	public static final int LOAD_FORM_NETWORK_URL = 2;

	/** 加载SD卡图片 */
	public static final int LOAD_FORM_SDCARD_FILEPATH = 3;

	/** 加载类型 */
	private int loadType = LOAD_FORM_NETWORK_URL;

	/** 是否创建缓存图片 */
	private boolean createImgBuffer = true;

	/** 是否加载SD卡缓存图片 */
	private boolean loadImgBuffer = true;

	public static String getHost(String url) {
		String u = url;
		String host = null;
		int idx = -1;
		idx = url.indexOf("://");
		if (-1 != idx) {
			url = url.substring(idx + 3);
		}
		idx = url.indexOf("/");
		if (-1 == idx)
			host = url;
		else {
			host = url.substring(0, idx);
			idx = host.indexOf(":");
			if (-1 != idx) {
				host = host.substring(0, idx);
			}
		}
		return host;
	}

	public static int getPort(String url, int defaultPort) {
		String u = url;
		int port = defaultPort;
		String strPort = null;
		int idx = -1;
		idx = url.indexOf("://");
		if (-1 != idx) {
			url = url.substring(idx + 3);
		}
		idx = url.indexOf("/");
		if (-1 == idx)
			strPort = url;
		else {
			strPort = url.substring(0, idx);
			idx = strPort.indexOf(":");
			if (-1 != idx) {
				strPort = strPort.substring(idx + 1);
				try {
					port = Integer.parseInt(strPort);
				} catch (NumberFormatException nfe) {
					port = defaultPort;
				}
				// port=Integer.getInteger(strPort, defaultPort);
			}
		}
		return port;
	}

	/**
	 * 图片宽度
	 * 
	 * @param bitmapWidth
	 */
	public void setBitmapWidth(int bitmapWidth) {
		this.bitmapW = bitmapWidth;
	}

	/**
	 * 图片高度
	 * 
	 * @param bitmapHeight
	 */
	public void setBitmapHeight(int bitmapHeight) {
		this.bitmapH = bitmapHeight;
	}

	/**
	 * 图片圆角
	 * 
	 * @param roundPx
	 */
	public void setRoundPx(float roundPx) {
		this.roundPx = roundPx;
	}

	public void setConsultW(int consultW) {
		imageUtil.setConsultW(consultW);
	}

	public void setConsultH(int consultH) {
		imageUtil.setConsultH(consultH);
	}

	/**
	 * 设置加载类型
	 * 
	 * @param loadType
	 */
	public void setLoadType(int loadType) {
		this.loadType = loadType;
	}

	/**
	 * SD卡缓存路径
	 * 
	 * @param imgBufferParent
	 */
	public void setImgBufferParent(String imgBufferParent) {
		this.imgBufferParent = imgBufferParent;
	}

	/** 是否创建缓存图片 */
	public void setCreateImgBuffer(boolean createImgBuffer) {
		this.createImgBuffer = createImgBuffer;
	}

	/** 是否加载SD卡缓存图片 */
	public void setLoadImgBuffer(boolean loadImgBuffer) {
		this.loadImgBuffer = loadImgBuffer;
	}

	/**
	 * 设置图片后缀
	 * 
	 * @param imgSuffix
	 */
	public void setImgSuffix(String imgSuffix) {
		this.imgSuffix = imgSuffix;
	}

	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}

	public CubeAsyncImage(Activity activity) {
		this(activity, LOAD_FORM_NETWORK_URL);
	}

	public CubeAsyncImage(Activity activity, int bitmapW, int bitmapH,
			float roundPx) {
		this(activity, LOAD_FORM_NETWORK_URL, bitmapW, bitmapH, roundPx);
	}

	public CubeAsyncImage(Activity activity, int loadType) {
		this(activity, loadType, 5);
	}

	public CubeAsyncImage(Activity activity, int loadType, int bitmapW,
			int bitmapH, float roundPx) {
		this(activity, loadType, 5, bitmapW, bitmapH, roundPx);
	}

	public CubeAsyncImage(Activity activity, int loadType, int threads) {
		this(activity, loadType, threads, ImageUtil.WRAP_CONTENT,
				ImageUtil.WRAP_CONTENT, 0);
	}

	public CubeAsyncImage(Activity activity, int loadType, int threads,
			int bitmapW, int bitmapH, float roundPx) {
		this.activity = activity;
		this.loadType = loadType;
		this.bitmapW = bitmapW;
		this.bitmapH = bitmapH;
		this.roundPx = roundPx;
		imageUtil = new ImageUtil();
		// map = new HashMap<String, SoftReference<Bitmap>>();
		map = new HashMap<String, SoftReference<SyncImageItem>>();
		sdcard = Environment.getExternalStorageDirectory().toString();
		executorService = Executors.newFixedThreadPool(threads);
	}

	private final Handler handler = new Handler();

	private void setResult(ImageCallback imageCallback, Bitmap result,
			String requestUrl) {
		if (null == imageCallback)
			return;
		imageCallback.imageLoaded(result, requestUrl);
	}

	/**
	 * @param url
	 *            图片地址
	 * @param imageCallback
	 *            Bitmap返回接口
	 * @return
	 */
	public Bitmap loadImage(final String url, final ImageCallback imageCallback) {
		if (url == null)
			return null;
		if (loadType == LOAD_FORM_NETWORK_URL)
			sampleSize = ((url.indexOf("iphoneandroid") != -1) || url
					.indexOf("iphone") != -1) ? sampleSize : 1;
		Bitmap bitmap = null;
		if (map.containsKey(url)) {
			SoftReference<SyncImageItem> softReference = map.get(url);
			SyncImageItem item = softReference.get();
			if (null != item) {
				bitmap = item.getBitmap();
				if (bitmap != null) {
					setResult(imageCallback, bitmap, url);
					return bitmap;
				} else if (SyncImageItem.SyncImageItemState.LOADING == item
						.getState()
						|| SyncImageItem.SyncImageItemState.FAILED == item
								.getState()) {
					return null;
				}
			}
		}
		// 加载SD卡缓存图片
		if (loadImgBuffer && (bitmap = getSdCardBufferImage(url)) != null) {
			SyncImageItem item = new SyncImageItem(bitmap);
			item.setState(SyncImageItemState.SUCCESS);
			map.put(url, new SoftReference<SyncImageItem>(item));
			setResult(imageCallback, bitmap, url);
			return bitmap;
		}
		// 缓存中没有图像，则从网络上取出数据，并将取出的数据缓存到内存中
		executorService.submit(new Runnable() {
			public void run() {
				SyncImageItem item = new SyncImageItem();
				item.setState(SyncImageItemState.LOADING);
				map.put(url, new SoftReference<SyncImageItem>(item));
				final Bitmap bm = (loadType == LOAD_FORM_NETWORK_URL) ? loadImageFromNetworkUrl(url)
						: (loadType == LOAD_FORM_VIDEO_FILEPATH) ? loadImageVideoFile(url)
								: (loadType == LOAD_FORM_SDCARD_FILEPATH) ? getSdCardImage(url)
										: null;
				if (createImgBuffer && bm != null) {
					// 创建本地缓存图片
					doBufferImage(url, bm);
				}
				map.remove(url);
				if (bm != null) {
					item = new SyncImageItem(bm);
					item.setState(SyncImageItemState.SUCCESS);
					map.put(url, new SoftReference<SyncImageItem>(item));
				} else {
					item = new SyncImageItem();
					item.setState(SyncImageItemState.FAILED);
					map.put(url, new SoftReference<SyncImageItem>(item));
				}
				handler.post(new Runnable() {
					public void run() {
						setResult(imageCallback, bm, url);
					}
				});
			}
		});
		return null;
	}

	/**
	 * 根据视频所在SD卡路径获取图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap loadImageVideoFile(String url) {
		return null;
	}

	/**
	 * 加载远程图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap loadImageFromNetworkUrl(String url) {
		return LoadImageFromInternet(url, null, 80, false);
	}

	private String url;

	private Bitmap LoadImageFromInternet(String mmurl, String apnHost,
			int port, boolean isProxy) {
		this.url = mmurl;
		Bitmap bm = null;
		HttpGet request = null;
		for (int idx = 0; idx < 3; ++idx) {
			try {
				// wap
				// 截取 http://klmu.v228.10000net.cn/publicbicycle 为
				// klmu.v228.10000net.cn
				String serHost = getHost(url);
				int serPort = getPort(url, 80);
				if (null == serHost)
					return null;
				HttpHost target = new HttpHost(serHost, serPort);
				request = new HttpGet(url);
				// 新建HttpClient对象
				DefaultHttpClient httpClient = new DefaultHttpClient();
				if (isProxy && null != apnHost) {
					HttpHost proxy = new HttpHost(apnHost, port);
					httpClient.getParams().setParameter(
							ConnRoutePNames.DEFAULT_PROXY, proxy);
				}
				HttpResponse httpResponse = httpClient.execute(target, request);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity = httpResponse.getEntity();
					InputStream is = entity.getContent();
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = sampleSize;
					bm = BitmapFactory.decodeStream(is, null, opts);
					if (bm != null
							&& !((bitmapW == ImageUtil.WRAP_CONTENT || bitmapW == ImageUtil.ORIGINAL_CONTENT)
									&& (bitmapH == ImageUtil.WRAP_CONTENT || bitmapH == ImageUtil.ORIGINAL_CONTENT) && roundPx == 0))
						bm = imageUtil.zoomBitmap(activity, bm, bitmapW,
								bitmapH, roundPx);
					is.close();
					// strReust =
					// EntityUtils.toString(httpResponse.getEntity());
				}
				httpClient.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
			if (null != bm)
				break;
		}
		return bm;
	}

	/** 加载SD卡图片 */
	private Bitmap getSdCardImage(String path) {
		if (!isSdCardExist(activity))
			return null;
		Bitmap bitmap = null;
		File f = new File(path);
		if (f != null && f.exists()) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inSampleSize = sampleSize;
			bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
		}
		if (bitmap != null
				&& !((bitmapW == ImageUtil.WRAP_CONTENT || bitmapW == ImageUtil.ORIGINAL_CONTENT)
						&& (bitmapH == ImageUtil.WRAP_CONTENT || bitmapH == ImageUtil.ORIGINAL_CONTENT) && roundPx == 0))
			bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW, bitmapH,
					roundPx);
		return bitmap;
	}

	/**
	 * 加载SD卡缓存图片
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getSdCardBufferImage(String url) {
		if (!isSdCardExist(activity))
			return null;
		Bitmap bitmap = null;
		try {
			String fileName = getImageFileName(url);
			File f = new File(sdcard + imgBufferParent + fileName);
			if (f != null && f.exists()) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = sampleSize;
				bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), opts);
			}
			if (bitmap != null
					&& !((bitmapW == ImageUtil.WRAP_CONTENT || bitmapW == ImageUtil.ORIGINAL_CONTENT)
							&& (bitmapH == ImageUtil.WRAP_CONTENT || bitmapH == ImageUtil.ORIGINAL_CONTENT) && roundPx == 0))
			{
				if(null!=activity){
					bitmap = imageUtil.zoomBitmap(activity, bitmap, bitmapW,
							bitmapH, roundPx);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 保存缓存图片
	 * 
	 * @param url
	 * @param bitmap
	 */
	private void doBufferImage(String url, Bitmap bitmap) {
		try {
			String fileName;
			if (!isSdCardExist(activity)
					|| (fileName = getImageFileName(url)) == null)
			{
				Log.e(TAG, "缓存sd卡失败");
				return;
			}
			File f = new File(new File(sdcard, imgBufferParent), fileName);
			if (!f.getParentFile().exists())
				f.getParentFile().mkdirs();
			if (f.exists())
				f.delete();
			// return;
			f.createNewFile();
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(f));
			if (url.indexOf(".png") > 0)
				bitmap.compress(Bitmap.CompressFormat.PNG, 40, bos);
			else
				bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos);
			bos.flush();
			bos.close();
			Log.e(TAG, "缓存sd卡成功");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取缓存图片文件名
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	private String getImageFileName(String url) throws IOException,
			NoSuchAlgorithmException {
		String parent = md5(url.getBytes());
		return parent != null ? parent + imgSuffix : null;
	}

	/**
	 * MD5加密图片路径
	 * 
	 * @param source
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String md5(byte[] source) throws NoSuchAlgorithmException {
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		java.security.MessageDigest md = java.security.MessageDigest
				.getInstance("MD5");
		md.update(source);
		byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
		// 用字节表示就是 16 个字节
		char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
		// 所以表示成 16 进制需要 32 个字符
		int k = 0; // 表示转换结果中对应的字符位置
		for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
			// 转换成 16 进制字符的转换
			byte byte0 = tmp[i]; // 取第 i 个字节
			str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
			// >>> 为逻辑右移，将符号位一起右移
			str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
		}
		String s = new String(str);
		return s != null ? s : null; // 换后的结果转换为字符串
	}

	public interface ImageCallback {
		public void imageLoaded(Bitmap bitmap, String imageUrl);
	}

	/**
	 * 判断SD卡是否存在
	 * 
	 * @param context
	 * @return
	 */
	private boolean isSdCardExist(Context context) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		return sdCardExist;
	}
}
