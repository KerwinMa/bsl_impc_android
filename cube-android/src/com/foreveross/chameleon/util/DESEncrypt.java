/**
 * 
 */
package com.foreveross.chameleon.util;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
 * @author Administrator
 * 
 */
public class DESEncrypt {
	private static final String Algorithm = "DESede";
	public static final byte[] keyBytes = "This is a secret keynews".getBytes();

	private static DESEncrypt encrypt;

	public static DESEncrypt getInstance() {
		if (encrypt == null) {
			encrypt = new DESEncrypt();
		}
		return encrypt;
	}

	// 24字节的密钥

	/*
	 * @ use DESede algorithm to enc the src
	 * 
	 * @ keybyte: secretkey 24 byte
	 * 
	 * @ src:the text needs to be encrypt
	 * 
	 * @ return the enc result
	 */
	public byte[] encryptMode(byte[] src) {
		try {
			SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// 解密
	public String decrypt(byte[] src) throws Exception {
		String srcString = new String(src, "UTF-8");
		byte[] srcBytes = decryptMode(decode(srcString));
		return new String(srcBytes, "UTF-8");
	}

	// 加密
	public byte[] encrypt(String src) throws Exception {
		String srcString = encode(encryptMode(src.getBytes("UTF-8")));
		return srcString.getBytes("UTF-8");
	}

	/*
	 * @ use DESede algorithm to dec the src
	 * 
	 * @ keybyte: secretkey 24 byte
	 * 
	 * @ src:the text needs to be dec
	 * 
	 * @ return the dec result
	 */
	public byte[] decryptMode(byte[] src) {
		try {
			SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// private void writeFile(String src) throws Exception {
	// File file = new File("C://encrypt.txt");
	// FileWriter fw = new FileWriter(file);
	// fw.write(src);
	// fw.close();
	// }
	//
	// private String readFile(String path) {
	// File file = new File(path);
	// FileReader fr = null;
	// try {
	// fr = new FileReader(file);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// char[] cbuf = new char[10240];
	// try {
	// fr.read(cbuf);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String res = new String(cbuf);
	// return res;
	// }

	/**
	 * 编码
	 * 
	 * @param bstr
	 * @return String
	 */
	public String encode(byte[] bstr) {
		// return new sun.misc.BASE64Encoder().encode(bstr);
		// return new String(Base64.encodeBase64(bstr));
		return MyBase64.encodeToString(bstr, MyBase64.DEFAULT);
	}

	/**
	 * 解码
	 * 
	 * @param str
	 * @return string
	 */
	public byte[] decode(String str) {
		byte[] bt = null;
		// try {
		bt = MyBase64.decode(str, MyBase64.DEFAULT);
		// bt = Base64.decodeBase64(str.getBytes("UTF-8"));
		// sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		// bt = decoder.decodeBuffer(str);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return bt;
	}

	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		// TripleDESEncrypt encrypt = new TripleDESEncrypt();
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		// String keyString ="This is a secret keynews";
		// byte[] keyBytes = keyString.getBytes();
		// System.out.println("密钥是："+new String(keyBytes));
		// String szSrc = "AFE0AFE6-8DBA-E6CB-3240-103AC6F94766";
		// System.out.println("加密前的字符串:" + szSrc);
		// byte[] encoded =
		// TripleDESEncrypt.getInstance().encryptMode(szSrc.getBytes("UTF-8"));
		// String encodedString = encrypt.encode(encoded);
		// System.out.println("加密后的字符串:" + encodedString);
		//
		//
		// String urlEncodeStr = URLEncoder.encode(encodedString, "UTF-8");
		// System.out.println("UTF-9编码的字符串:" + urlEncodeStr);
		//
		// try {
		// encrypt.writeFile(encodedString);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// byte[] srcBytes =
		// TripleDESEncrypt.getInstance().decryptMode(encrypt.decode(encodedString));
		// System.out.println("解密后的字符串:" + (new String(srcBytes,"UTF-8")));
	}
	
	public static String encryptString(String keySrc , String passWord) {
		byte[] key = keySrc.getBytes(); // 长度最少要8个字符
		String encBase64Content = null;
		try {
			byte[] encContent = encrypt(key, passWord);
			encBase64Content = Base64
					.encodeToString(encContent, Base64.DEFAULT);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encBase64Content;
	}

	public static byte[] encrypt(byte[] rawKeyData, String str)
			throws InvalidKeyException, NoSuchAlgorithmException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchPaddingException, InvalidKeySpecException{
		byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

		// DES算法要求有一个可信任的随机数源
		// SecureRandom sr = new SecureRandom();
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(rawKeyData);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey key = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 用密匙初始化Cipher对象
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
		// 现在，获取数据并加密
		byte data[] = str.getBytes();
		// 正式执行加密操作
		byte[] encryptedData = cipher.doFinal(data);
		return encryptedData;
	}
}
