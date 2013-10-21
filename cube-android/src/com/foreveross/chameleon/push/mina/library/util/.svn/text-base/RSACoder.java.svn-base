package com.foreveross.chameleon.push.mina.library.util;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.util.Base64;
import android.util.Log;

/**
 * [RSA加解密]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-20]
 */
public class RSACoder {
	/**
	 * 非对称密钥算法
	 */
	private static final String KEY_ALGORITHM="RSA";

	
	/**
	 * [私钥加密]
	 * @param data待加密数据
	 * @param key 密钥
	 * @return byte[] 加密数据
	 * */
	public static byte[] encryptByPrivateKey(byte[] data,String key) throws Exception{
		return encryptByPrivateKey(data,Base64.decode(key, Base64.DEFAULT));
	}
	
	
	/**
	 * [私钥加密]
	 * @param data待加密数据
	 * @param key 密钥
	 * @return byte[] 加密数据
	 * */
	public static byte[] encryptByPrivateKey(byte[] data,byte[] key) throws Exception{
		
		//取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
		//生成私钥
		PrivateKey privateKey=keyFactory.generatePrivate(pkcs8KeySpec);
		//数据加密
		Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
		Log.i("keygen.getProvider().getName()", cipher.getProvider().getClass().getName()+" "+keyFactory.getProvider().getName());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	/**
	 * [公钥加密]
	 * @param data待加密数据
	 * @param key 密钥
	 * @return byte[] 加密数据
	 * */
	public static byte[] encryptByPublicKey(byte[] data,byte[] key) throws Exception{
		
		//实例化密钥工厂
		KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
		//初始化公钥
		//密钥材料转换
		X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(key);
		//产生公钥
		PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);
		//数据加密
		Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		return cipher.doFinal(data);
	}
	/**
	 * [私钥解密]
	 * @param data 待解密数据
	 * @param key 密钥
	 * @return byte[] 解密数据
	 * */
	public static byte[] decryptByPrivateKey(byte[] data,byte[] key) throws Exception{
		//取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec=new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
		//生成私钥
		PrivateKey privateKey=keyFactory.generatePrivate(pkcs8KeySpec);
		//数据解密
		Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}
	/**
	 * [公钥解密]
	 * @param data 待解密数据
	 * @param key 密钥
	 * @return byte[] 解密数据
	 * */
	public static byte[] decryptByPublicKey(byte[] data,byte[] key) throws Exception{
		
		//实例化密钥工厂
		KeyFactory keyFactory=KeyFactory.getInstance(KEY_ALGORITHM);
		//初始化公钥
		//密钥材料转换
		X509EncodedKeySpec x509KeySpec=new X509EncodedKeySpec(key);
		//产生公钥
		PublicKey pubKey=keyFactory.generatePublic(x509KeySpec);
		//数据解密
		Cipher cipher=Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, pubKey);
		return cipher.doFinal(data);
	}

	
}
