package com.qweri.phonenumbermanager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service{
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(CoreService.this, InterceptService.class);
			startService(intent);

		};
	};
	@Override
	public void onCreate() {
		super.onCreate();
		handler.sendEmptyMessageDelayed(1, 2000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
