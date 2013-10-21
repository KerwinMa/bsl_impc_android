package com.foreveross.chameleon.util;


import android.util.Log;

/**
 * 加密压缩/解密解压缩工具类
 * 
 * @author chencao 2012-11-8 created
 * 
 */
public class DESEncryptAndGzipUtil {

	public static final String ENCODING = HttpUtil.UTF8_ENCODING;

	/**
	 * 加密并压缩
	 * 
	 * @param source
	 * @return
	 */
	public static byte[] encryptAndGzip(String source) {
		try {
			byte[] encoded = DESEncrypt.getInstance().encryptMode(
					source.getBytes(ENCODING));
			String encodedString = DESEncrypt.getInstance().encode(encoded);

			byte[] zip_data = GZipUtil.gzip(encodedString.getBytes(ENCODING));

			return zip_data;
		} catch (Exception ex) {
			Log.e("cube", "加密并压缩失败：" + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 解压缩并解密
	 * 
	 * @param source
	 * @return
	 */
	public static String unzipAndDecrypt(byte[] source) {
		try {
			byte[] unzip_data = GZipUtil.unzip(source);

			byte[] srcBytes = DESEncrypt.getInstance().decryptMode(
					DESEncrypt.getInstance().decode(
							new String(unzip_data, ENCODING)));
			return new String(srcBytes, ENCODING);
		} catch (Exception ex) {
			Log.e("cube", "解压缩并解密失败：" + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static String encrypt(String source) {
		try {
			byte[] encoded = DESEncrypt.getInstance().encryptMode(
					source.getBytes(ENCODING));
			return DESEncrypt.getInstance().encode(encoded);
		} catch (Exception ex) {
			Log.e("cube", "加密失败：" + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
}
