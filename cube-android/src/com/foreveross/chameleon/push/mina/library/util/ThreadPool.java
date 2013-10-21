/**
 * 
 */
package com.foreveross.chameleon.push.mina.library.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [mina_android_lib, 2013-7-12]
 */
public class ThreadPool {

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param args
	 *            2013-7-12 上午9:15:16
	 */
	private static ExecutorService pool = Executors.newCachedThreadPool();

	public static void run(Runnable runnable){
		pool.execute(runnable);
	};
}
