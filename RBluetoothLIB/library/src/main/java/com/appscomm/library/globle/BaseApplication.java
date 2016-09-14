package com.appscomm.library.globle;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * @创建者	 Administrator
 * @创时间 	 2015-8-14 下午2:19:53
 * @描述	     全局盒子,里面放置一些全局的变量或者方法,Application其实是一个单例
 *
 * @版本       $Rev: 6 $
 * @更新者     $Author: admin $
 * @更新时间    $Date: 2015-08-14 14:38:24 +0800 (星期五, 14 八月 2015) $
 * @更新描述    TODO
 */
public class BaseApplication extends Application {

	private static Context	mContext;

	public static Context getContext() {
		return mContext;
	}

	@Override
	public void onCreate() {// 程序入口方法
		super.onCreate();
		// 创建一些常见的变量
		// 1.上下文
		mContext = this;
	}

}
