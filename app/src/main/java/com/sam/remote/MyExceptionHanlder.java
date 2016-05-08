package com.sam.remote;

import java.lang.Thread.UncaughtExceptionHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.util.Log;

/**
 * 异常处理
 *
 */
public class MyExceptionHanlder implements UncaughtExceptionHandler {

	private Context mContext = null;

	public MyExceptionHanlder(Context context) {
		this.mContext = context;
		// this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		Intent intent = new Intent(mContext, activitiMain.class);
        mContext.startActivity(intent);
			crash(mContext);


		
	}

	public void uncaughtException(Exception e) {
		uncaughtException(Thread.currentThread(), e);
	}

	private boolean crash(Context context) {
		if (context == null) {
			return false;
		}

		if (context instanceof Activity) {
			((Activity) context).finish();
		}

		Process.killProcess(Process.myPid());
		return true;
	}

}
