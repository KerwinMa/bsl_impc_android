package com.foreveross.chameleon.phone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewStub;

import com.csair.impc.R;
import com.foreveross.chameleon.util.PadUtils;

/**
 * @TODO [加载页面]
 * @author XiaoMa
 * @version [版本号, 2012-11-15]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 * 
 *        <modified by fengweili> [增加注解] [缩小变量作用域]
 * 
 */
public class LoadingActivity extends BaseActivity {

	/**
	 * 重载方法
	 * 
	 * @param savedInstanceState
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

		if(PadUtils.isPad(this)){
			((ViewStub)findViewById(R.id.pad_splash_screen_stub)).inflate();
		}else{
		 ((ViewStub)findViewById(R.id.phone_splash_screen_stub)).inflate();

		}
		init();
	}

	/*
	 * 初始化方法实现
	 */
	private void init() {
		// 启动加载页面方式：一
		final long currentStart = System.currentTimeMillis();
		new Thread(new Runnable() {

			public void run() {
				// 休眠标识
				boolean flag = false;
				while (!flag) {
					// 如果休眠时间小于两秒，继续休眠
					if (System.currentTimeMillis() - currentStart > 1000) {
						flag = true;
						startActivity(new Intent(LoadingActivity.this,
								AdminActivity.class));
						finish();
						application.getActivityManager().popActivity(LoadingActivity.this);
					}
				}
			}
		}).start();
	}

}
