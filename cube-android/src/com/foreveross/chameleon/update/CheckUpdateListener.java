package com.foreveross.chameleon.update;

import com.foreveross.chameleon.phone.modules.CubeApplication;

public interface CheckUpdateListener {
	void onCheckStart();
	void onUpdateAvaliable(final CubeApplication curApp, final CubeApplication newApp);
	void onUpdateUnavailable();
	void onCheckError(final Throwable error);
	void onCancelled();
}
