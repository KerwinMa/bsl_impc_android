package com.foreveross.chameleon.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * 签名/验签工具类
 * 
 * @author chencao 2012-11-8 created
 * 
 */
public class RSAUtil {

	private static final String ALGORITHM = "RSA";
	// public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	public static final String SIGN_ALGORITHMS = "MD5WithRSA";

	/**
	 * 签名
	 * 
	 * @param content
	 * @param privateKey
	 * @param encoding
	 * @return
	 */
	public static String sign(String content, String privateKey, String encoding) {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(encoding));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 验签
	 * 
	 * @param content
	 * @param sign
	 * @param publicKey
	 * @param encoding
	 * @return
	 */
	public static boolean doCheck(String content, String sign,
			String publicKey, String encoding) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
			byte[] encodedKey = Base64.decode(publicKey);
			PublicKey pubKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(encoding));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean doCheckByCertFile(String content, String sign,
			InputStream certFileInputStream, String encoding) {
		try {
			CertificateFactory certificatefactory = CertificateFactory
					.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) certificatefactory
					.generateCertificate(certFileInputStream);
			PublicKey pubKey = certificate.getPublicKey();

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(content.getBytes(encoding));

			boolean bverify = signature.verify(Base64.decode(sign));
			return bverify;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static void main(String[] args) throws Exception {
		String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOKfbHS+q59D22ZHw8VL/ewC0OVsyJRSUEnErPEACK7qW6cg3eeP/Q5Qjj+HQdchfzx1wQbsgGlCHu+tprqzzkcbFTnfa9nD8Gewu2VT9TObekpEwCwQMEEcQYgUAxD78loBKUgINFFrFaSn8I3Vad5ymEcVlo7ETkKu1/lS+g6LAgMBAAECgYB1Myee1MDjE+/SXIjlbyB5vxcTn4e4FT3KeLlLxc230CHoM/ou+GtRzN1UA3pMbNllhix2jTb3uKdRIshYRAcIC9mygR9grBRyzE8uqqe+vOjaGgrbaEVS/74M+WjmwNraOmh1VD06ghGkg12xf2iTLHrXSrIUtLPXEuD8SaBY0QJBAPIKUJ1FquGJxKpw+uF3CYB4+HitTpA1ax1IIpwXobdZhgQMn//A2ie9ecxBrUNSYb/WPy7zrZATLlPUR5eYZokCQQDvsXikTbuv9bmn3lZm/mgwDSmu2co9A+2L2xR8v0E9nu4wUTxuwyGVx1iKKui4Q9XB5uZQv4LCu4fWl3LHdEdzAkA32xGHedBZhAWSn8gFyAa1UzVkA/qhZPJ3K3JxOzLisRIwVQmHZ+XwTdWRwYZOhvBv6O1j1HA1U3fZeJ+c6FqhAkBCYZ4NstF169Gc4gB/yZlFJYATwpE10K6q+uNzoOwKisdgbj8UVcopVun4aeXFklPSvYWvezpVf+Yg0hShlFxtAkASy5aYxNF+TG1rc4sfZxZnjHmzevMqVeugcaXiMwa1ShApirpf0zPVHiJbC51ihQF94eFRZIS/Dce6a5qifncY";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDin2x0vqufQ9tmR8PFS/3sAtDlbMiUUlBJxKzxAAiu6lunIN3nj/0OUI4/h0HXIX88dcEG7IBpQh7vraa6s85HGxU532vZw/BnsLtlU/Uzm3pKRMAsEDBBHEGIFAMQ+/JaASlICDRRaxWkp/CN1WnecphHFZaOxE5Crtf5UvoOiwIDAQAB";
		// String publicKey = Read_Bin();
		String sign = sign("hello", privateKey, "UTF-8");
		System.out.println("sign=" + sign);
		// boolean ok = doCheck("hello", sign, publicKey, "UTF-8");
		boolean ok = doCheckByCertFile("hello", sign, new FileInputStream(
				"rsa_cert.pem"), "UTF-8");
		System.out.println("ok=" + ok);
	}
}
