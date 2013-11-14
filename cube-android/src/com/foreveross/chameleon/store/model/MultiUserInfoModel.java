package com.foreveross.chameleon.store.model;

import java.security.NoSuchAlgorithmException;

import com.foreveross.chameleon.store.core.BaseModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MultiUserInfoModel")
public class MultiUserInfoModel extends BaseModel<MultiUserInfoModel, String> {

	public MultiUserInfoModel() {

	}

	public MultiUserInfoModel(String userName, String passWord,
			String systemId, String mD5Str) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.systemId = systemId;
		MD5Str = mD5Str;
	}
	/**
	 * [用户名]
	 */
	@DatabaseField
	private String userName = null;
	/**
	 * [密码]
	 */
	@DatabaseField
	private String passWord = null;

	/**
	 * [系统ID]
	 */
	@DatabaseField
	private String systemId = null;
	/**
	 * [MD5Str]
	 */
	@DatabaseField(id = true, generatedId = false)
	private String MD5Str = null;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getMD5Str() {
		return MD5Str;
	}

	public void setMD5Str(String userName , String sysId) {
		String mD5Str = null;
		try {
			mD5Str = md5(userName, sysId);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MD5Str = mD5Str;
	}

	
	/* 
	* MD5加密 
	*/  
	/**
	 * MD5加密图片路径
	 * 
	 * @param source
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private String md5(String userName , String passWord) throws NoSuchAlgorithmException {
		byte[] source = (userName + passWord).getBytes();
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
}
