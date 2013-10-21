package com.foreveross.chameleon.util;

import java.security.Key;

import javax.crypto.Cipher;

public class SymEncrypt {

	public static Key getKey(byte[] arrBTmp, String alg) {
		byte[] arrB = new byte[8];
		int i = 0;
		int j = 0;
		while (i < arrB.length) {
			if (j > arrBTmp.length - 1) {
				j = 0;
			}
			arrB[i] = arrBTmp[j];
			i++;
			j++;
		}
		Key key = new javax.crypto.spec.SecretKeySpec(arrB, alg);
		return key;
	}
	
	public static byte[] encrypt(String s, String strKey) {
		byte[] r = null;
		try {
			Key key = getKey(strKey.getBytes(), "DES");
			Cipher c;
			c = Cipher.getInstance("DES");
			c.init(Cipher.ENCRYPT_MODE, key);
			r = c.doFinal(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	public static String decrypt(byte[] code, String strKey) {
		String r = null;
		try {
			Key key = getKey(strKey.getBytes(), "DES");
			Cipher c;
			c = Cipher.getInstance("DES");
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] clearByte = c.doFinal(code);
			r = new String(clearByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	// public static void main(String args[]){
	// String content = "CHINA CHINA SHSH";
	// byte[] outputBytes=SymEncrypt.encrypt(content,"123456");
	// try {
	// System.out.println(InputStreamUtils.byteTOString(outputBytes));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// String result = SymEncrypt.decrypt(outputBytes, "123456");
	// System.out.println(result);
	// }
}
