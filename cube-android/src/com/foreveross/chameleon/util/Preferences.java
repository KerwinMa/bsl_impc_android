package com.foreveross.chameleon.util;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @Title: Preferences.java
 * @Description: 偏好控制类
 * @author XiaoMa
 */
public class Preferences
{
	
	public static final String APPMAINVIEW ="appmainview";
	
	/** 第一次安装运行应用标识*/
    public static final String FIRST_TIME ="fristTime";
    /** 存储于SP文件中的用户名USERNAME值 */
    public static final String USERNAME = "username";
    /** 存储当前登录的用户名 */
    public static final String CURRENT_USERNAME = "currentUserName";
    
    /** 存储于SP文件中的用户名PASSWORD值 */
    public static final String PASSWORD = "password";
    
    public static final String SESSIONID ="sessionId";
    
    public static final String PASSWORD_BAK = "passwordbak";
    
    public static final String ISREMEMBER = "isremember";
//    /** 存储于SP文件中的密码TOKEN值 */
//    public static final String TOKEN = "token";
    /** 存储于SP文件中的密码SESSION值 */
    public static final String SESSION = "session";
    
    /** 存储于SP文件中的聊天的JID值 */
    public static final String CHATJID = "chatjid";
    
    /** 存储当前用户Jid*/
    public static final String JID= "jid";
    /** 中文名称 */
    public static final String ZHNAME = "zhName";
    /** 性别 */
    public static final String SEX = "sex";
    /** 电话 */
    public static final String PHONE = "phone";
    /** 用户标签 */
    public static final String PRIVILEGES = "privileges";
    
    /** 系统ID */
    public static final String SYSTEMID = "systemid";
    /** 离线登录 */
    public static final String OUTLINE = "outline";
    
    /** 用户昵称 */
    public static final String EXPERT_DATE = "expertDate";
    public static void saveFirsttime(Boolean fristTime,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putBoolean(FIRST_TIME, fristTime);
        editor.commit();
    }
    public static void saveSessionID(Long sessionid,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putLong(SESSIONID, sessionid);
        editor.commit();
    }
    public static Long getSessionID(SharedPreferences preference) {
    	return preference.getLong(SESSIONID, 0);
    }
    
    
    public static void saveAppMainView(Boolean isView,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putBoolean(APPMAINVIEW, isView);
        editor.commit();
    }
    public static boolean getAppMainView(SharedPreferences preference) {
    	
   	 return preference.getBoolean(APPMAINVIEW, false);
   }
    
    public static boolean getFirsttime(SharedPreferences preference) {
    	
    	 return preference.getBoolean(FIRST_TIME, true);
    }
    public static void saveUserInfo(String username, String session, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(USERNAME, username);
        editor.putString(SESSION, session);
//        editor.putString(EXPERT_DATE, expertDate);
        editor.commit();
    }
    
    /* 记住账户实现* */
    public static void saveUser(String password, String username, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(PASSWORD, password);
        editor.putString(USERNAME, username);
        editor.commit();
    }
    
    /* 记住账户实现* */
    public static void saveUserName(String username, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(USERNAME, username);
        editor.commit();
    }
    
    /* 记住账户实现* */
    public static void saveUserJid(String userJid, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(JID, userJid);
        editor.commit();
    }
    
    
    
    /* 记住账户实现* */
    public static void saveUser(String password, String username,boolean isRemember, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(PASSWORD, password);
        editor.putString(USERNAME, username);
        editor.putBoolean(ISREMEMBER,isRemember);
        editor.commit();
    }
    
    public static void savePWD(String password,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(PASSWORD_BAK, password);
         editor.commit();
    }
    
    
    public static void saveAutoDownload(String UserName,boolean auto,SharedPreferences preference) {
    	 Editor editor = preference.edit();
         editor.putBoolean(UserName, auto);
         editor.commit();
    }
    
    public static boolean getAutoDownload(String UserName ,SharedPreferences preference)
    {
        return preference.getBoolean(UserName, true);
    }
    
    
    /**
     * 从SP文件中获取用户名
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户名
     */
    public static String getUserName(SharedPreferences preference)
    {
        return preference.getString(USERNAME, "");
    }
    
    /**
     * 从SP文件中获取用户密码
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户名
     */
    public static String getPassword(SharedPreferences preference)
    {
        return preference.getString(PASSWORD, "");
    }
    /**
     * 从SP文件中获取用户密码
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户名
     */
    public static String getPasswordbak(SharedPreferences preference)
    {
        return preference.getString(PASSWORD_BAK, "");
    }
    /**
     * 从SP文件中获取SESSION
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户密码
     */
    public static String getSESSION(SharedPreferences preference)
    {
        return preference.getString(SESSION, "");
    }
    
    /**
     * 从SP文件中获取SESSION
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户密码
     */
    public static String getUserJID(SharedPreferences preference)
    {
        return preference.getString(JID, "");
    }
    
    /**
     * 从SP文件中获取用户密码
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户密码
     */
    public static String getExpertDate(SharedPreferences preference)
    {
        return preference.getString(EXPERT_DATE, "");
    }
    
    
    public static void putIsDeleteFloder(SharedPreferences preference,boolean isDelete){
    	Editor editor = preference.edit();
        editor.putBoolean("IsDeleteFloder",isDelete );
        editor.commit();
    }
    
    public static boolean getIsDeleteFloder(SharedPreferences preference){
    	return preference.getBoolean("IsDeleteFloder", false);
    }
    
    public static boolean getIsRemember(SharedPreferences preference){
    	return preference.getBoolean(ISREMEMBER, false);
    	
    }
    public static void saveCurrentUserName(String username, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(CURRENT_USERNAME, username);
        editor.commit();
    }
    public static String getCurrentUserName (SharedPreferences preference) {
    	
    	 return preference.getString(CURRENT_USERNAME, "");
    }
    
    /* 记住当前聊天对像* */
    public static void saveChatJid(String chatjid, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(CHATJID, chatjid);
        editor.commit();
    }
    
    public static String getChatJid (SharedPreferences preference) {
   	 return preference.getString(CHATJID, "");
   }
    
    /**
     * 保存中文名
     * @param zhName
     * @param preference
     */
    public static void saveZhName(String zhName,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(ZHNAME, zhName);
         editor.commit();
    }
    /**
     * 保存性别
     * @param sex
     * @param preference
     */
    public static void saveSex(String sex,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(SEX, sex);
         editor.commit();
    }
    /**
     * 保存电话号码
     * @param phone
     * @param preference
     */
    public static void savePhone(String phone,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(PHONE, phone);
         editor.commit();
    }
    /**
     * 保存用户标签
     * @param privileges
     * @param preference
     */
    public static void savePrivileges(String privileges,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(PRIVILEGES, privileges);
         editor.commit();
    }
    /**
     * 获取中文名
     * @param preference
     * @return
     */
    public static String getZhName (SharedPreferences preference) {
    	
   	 return preference.getString(ZHNAME, "");
   }
    /**
     * 获取性别
     * @param preference
     * @return
     */
    public static String getSex (SharedPreferences preference) {
    	
   	 return preference.getString(SEX, "");
   }
    /**
     * 获取电话号码
     * @param preference
     * @return
     */
    public static String getPhone (SharedPreferences preference) {
    	
   	 return preference.getString(PHONE, "");
   }
    /**
     * 获取用户标签
     * @param preference
     * @return
     */
    public static String getPrivileges (SharedPreferences preference) {
    	
   	 return preference.getString(PRIVILEGES, "");
   }
    
    
    /* 记住当前系统ID* */
    public static void saveSytemId(String systemId, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(SYSTEMID, systemId);
        editor.commit();
    }
    
    public static String getSystemId(SharedPreferences preference) {
   	 return preference.getString(SYSTEMID, "");
   }
    
    /* 记住当前系统是否为离线登录* */
    public static void saveOutLine(Boolean outline, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putBoolean(OUTLINE, outline);
        editor.commit();
    }
    
    public static Boolean getOutLine(SharedPreferences preference) {
   	 return preference.getBoolean(OUTLINE, false);
   }
    
    public static void saveZhongName(String userName ,String zhongName, SharedPreferences preference){
    	Editor editor = preference.edit();
    	editor.putString(userName + "zhong", zhongName);
    	editor.commit();
    }
    
    public static String getZhongName(String userName , SharedPreferences preference){
    	 return preference.getString(userName + "zhong", "");
    }
}
