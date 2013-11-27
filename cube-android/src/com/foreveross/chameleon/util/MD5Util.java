package com.foreveross.chameleon.util;

import java.security.MessageDigest;

public class MD5Util {
	
	public static String toMD5(String content){
		byte[] b = encode(content);
		return toHex(b);
	}
	
	private static byte[] encode(String content) {
        try {
//          MessageDigest不仅仅只为我们提供了"MD5"加密,还提供了"SHA-1"
//          创建的方法只为: MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
//          MD5与SHA-1的区别为:MD5是16位,SHA是20位（这是两种报文摘要的算法）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(content.getBytes());
            return messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	  // 转换为16进制字符
	private static String toHex(byte buffer[]) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }
 
        return sb.toString();
    }
}
