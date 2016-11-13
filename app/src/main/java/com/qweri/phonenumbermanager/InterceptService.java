package com.qweri.phonenumbermanager;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.internal.telephony.ITelephony;

public class InterceptService extends Service {

	public final static String EXTRA_CLICKED_NOTIFY = "click notification";
	
	public final static int NOTIFICATION_REQUEST_CODE = 300;
	public final static int ALART_MANAGER_REQUEST_CODE = 301;
	public final static String UNKNOW_NUMBER = "未知号码";


	private IncomingCallReceiver mReceiver;
	private ITelephony iTelephony;
	private AudioManager mAudioManager;
	

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mReceiver = new IncomingCallReceiver();
		IntentFilter filter = new IntentFilter(
				"android.intent.action.PHONE_STATE");
		registerReceiver(mReceiver, filter);// 注册BroadcastReceiver
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 利用反射获取隐藏的endcall方法
		TelephonyManager telephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Method getITelephonyMethod = TelephonyManager.class
					.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyMgr,
					(Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {// 4.3+系统会把这个空通知栏显示出来，所以只在非4.3+系统使用
            try {
                Notification notification = new Notification();

                notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
                if (Build.VERSION.SDK_INT > 15) {
                    notification.priority = Notification.PRIORITY_MAX;
                    
                }
                startForeground(10811, notification);
            } catch (Exception ex) {
            }
        }
		Log.d("xxxxx", "intercept service.........");
		return START_STICKY;
	}

//	private void startAlarmService() {
//		Intent service = new Intent(this, InterceptService.class);
//		PendingIntent pendingIntent = PendingIntent.getService(this, ALART_MANAGER_REQUEST_CODE, service, 0);
//		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
//				1000 * 60 * 10, pendingIntent);
//	}

	private class IncomingCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			 Log.i("xxxxxx", "State: " + state);

			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			 Log.d("xxxxxx", "Incomng Number: " + number);

			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {// 电话正在响铃
				mAudioManager
				.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				if (isInterceptNumber(number)) {// 拦截指定的电话号码
					showNotification(number);
					// 先静音处理
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_SILENT);

					try {
						// 挂断电话
						if (iTelephony != null) {
							iTelephony.endCall();
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} 
				// 再恢复正常铃声
				mAudioManager
						.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
		}
	}
	
	private String numberName = null;
	
	private boolean isInterceptNumber(String number) {
		if(BlackListUtils.isInterceptAllNumber(this)) {
			numberName = isNumberInContactList(number);
			return true;
		}
		if(BlackListUtils.isInterceptUnknow(this) && (UNKNOW_NUMBER.equals(isNumberInContactList(number)))) {
			numberName = UNKNOW_NUMBER;
			return true;
		}
		if(BlackListUtils.isNumberInBlackList(this, number)) {
			numberName = isNumberInContactList(number);
			return true;
		}
		String name = isInAutoBlockList(number); 
		if(name != null) {
			numberName = name;
			return true;
		}
		numberName = "";
		return false;
	}
	
	private String isInAutoBlockList(String number) {
		if(BlackListUtils.isAutoBlockOpen(this)) {
			try {
				SQLiteDatabase db = AssetsDatabaseManager.initManager(this).getDatabase();
				Cursor cursor = db.rawQuery("select * from location_cn where prenumber=" + number, null);
				if(cursor != null && cursor.moveToNext()) {
					return cursor.getString(cursor.getColumnIndex("location"));
				}
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public String isNumberInContactList(String number) {
		try {
			Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
			ContentResolver resolver = this.getContentResolver();
			String name = null;
			Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				name = cursor.getString(0);
			}
			if(cursor != null) {
				cursor.close();
			}

			if(name != null) {
				return name;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        return UNKNOW_NUMBER;
	}

	private void showNotification(String number) {
		NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(this);
		Notification nf = nfBuilder.build();
		RemoteViews notifRemoteView = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
		notifRemoteView.setTextViewText(R.id.phone, number + "(" + numberName + ")");
		Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra(EXTRA_CLICKED_NOTIFY, true);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		nf.contentView = notifRemoteView;
		nf.contentIntent = pendingIntent;
		nf.icon = R.drawable.icon;
		nf.when = System.currentTimeMillis();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NotifyManager.NOTIFICATION_ID_ALART, nf);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		Intent service = new Intent(this,CoreService.class);
		startService(service);
		super.onDestroy();
	}
}
