package com.foreveross.chameleon.phone.muc;

import org.jivesoftware.smackx.muc.DefaultParticipantStatusListener;

import android.content.Context;
import android.util.Log;

/**
 * <BR>
 * [功能详细描述]聊天室成员的监听器 
 * @author Amberlo
 * @version [CubeAndroid, 2013-8-17] 
 */
public class MucParticipantStatusListener extends DefaultParticipantStatusListener{
	String TAG = "ParticipantStatusListener";
	
	private Context context;
	private String roomJid;
	public MucParticipantStatusListener(Context context,String roomJid) {
		this.roomJid=roomJid;
		this.context =context;
	}
	
	@Override  
    public void adminGranted(String arg0) {  
        Log.i(TAG, "授予管理员权限" + arg0);  
    }  

    @Override  
    public void adminRevoked(String arg0) {  
        Log.i(TAG, "移除管理员权限" + arg0);  
    }  

    @Override  
    public void banned(String arg0, String arg1, String arg2) {  
        Log.i(TAG, "禁止加入群组（拉黑，不知道怎么理解，呵呵）" + arg0);  
    }  

    @Override  
    public void joined(String arg0) {  
        Log.i(TAG, "执行了joined方法:" + arg0 + "加入了群组");  
        // 更新成员  
//        getAllMember();  
//        android.os.Message msg = new android.os.Message();  
//        msg.what = MEMBER;  
//        handler.sendMessage(msg);
        
    }  

    @Override  
    public void kicked(String arg0, String arg1, String arg2) {  
        Log.i(TAG, "踢人" + arg0 + "被踢出群组");  
    }  

    @Override  
    public void left(String lefter) {  
    	Log.i(TAG, "执行了left方法:" + lefter + "离开的群组");  
//        String lefter = arg0.substring(arg0.indexOf("/") + 1);  
//        // 更新成员  
//        getAllMember();  
//        android.os.Message msg = new android.os.Message();  
//        msg.what = MEMBER;  
//        handler.sendMessage(msg);  
    }  

    @Override  
    public void membershipGranted(String arg0)
    {  
        Log.i(TAG, "授予成员权限" + arg0);  
    }  

    @Override  
    public void membershipRevoked(String arg0) {  
        Log.i(TAG, "成员权限被移除" + arg0);  
    }  

    @Override  
    public void moderatorGranted(String arg0) {  
        Log.i(TAG, "授予主持人权限" + arg0);  
    }  

    @Override  
    public void moderatorRevoked(String arg0) {  
        Log.i(TAG, "移除主持人权限" + arg0);  
    }  

    @Override  
    public void nicknameChanged(String arg0, String arg1) {  
        Log.i(TAG, "昵称改变了" + arg0);  
    }  

    @Override  
    public void ownershipGranted(String arg0) {  
        Log.i(TAG, "授予所有者权限" + arg0);  
    }  

    @Override  
    public void ownershipRevoked(String arg0) {  
        Log.i(TAG, "移除所有者权限" + arg0);  
    }  

    @Override  
    public void voiceGranted(String arg0) {  
        Log.i(TAG, "给" + arg0+"授权发言");  
    }  

    @Override  
    public void voiceRevoked(String arg0) {  
        Log.i(TAG, "禁止" + arg0+"发言");  
    }  

}
