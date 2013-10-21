package com.foreveross.chameleon.phone.muc;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.UserModel;

public class MucRoomModel {

	
	private String name;
	private String roomJid;
	private int isRead;
	
	private ConversationMessage lastConversation ;
	
	private List<UserModel> users = new ArrayList<UserModel>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoomJid() {
		return roomJid;
	}

	public void setRoomJid(String roomJid) {
		this.roomJid = roomJid;
	}
	
	public List<UserModel> getUsers() {
		return users;
	}

	public void setUsers(List<UserModel> users) {
		this.users = users;
	}
	
	public void deleteMember(String userJid){
		Iterator<UserModel> i = users.iterator();
		while(i.hasNext()){
			UserModel user = i.next();
			if(user.getJid().equals(userJid)){
				users.remove(user);
			}
		}
	}
	
	public void deleteMember(UserModel user){
		if(users.contains(user)){
			users.remove(user);
		}
	}
	
	public void addMember(UserModel user){
		if(!users.contains(user)){
			users.add(user);
		}
	}

	

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public ConversationMessage getLastConversation() {
		return lastConversation;
	}

	public void setLastConversation(ConversationMessage lastConversation) {
		this.lastConversation = lastConversation;
	}
	
	
	public void cleanIsRead(){
		setIsRead(0);
	}
	
	public void addIsRead(){
		int count = getIsRead();
		setIsRead(count+1);
	}
	
}
