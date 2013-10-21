package com.foreveross.chameleon.phone.modules;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

public abstract class DataSourceAdapter extends BaseAdapter {
	public DataSourceAdapter(Context context, List<CubeModule> modules) {
		super();
		this.context = context;
		this.modules = modules;
	}

	protected Context context;
	protected List<CubeModule> modules;

	public List<CubeModule> getModules() {
		return modules;
	}
}
