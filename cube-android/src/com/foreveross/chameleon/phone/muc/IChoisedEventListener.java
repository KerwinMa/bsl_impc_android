package com.foreveross.chameleon.phone.muc;

import com.foreveross.chameleon.store.model.UserModel;

public interface IChoisedEventListener {

	public void onAddChoisedEvent(UserModel model , boolean addmore);
	
	public void onRemoveChoisedEvent(UserModel model);
}
