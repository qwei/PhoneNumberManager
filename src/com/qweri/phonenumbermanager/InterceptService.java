package com.qweri.phonenumbermanager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.internal.telephony.ITelephony;

public class InterceptService extends Service {

	public final static String EXTRA_CLICKED_NOTIFY = "click notification";
	private final static int OP_REGISTER = 100;
	private final static int OP_CANCEL = 200;
	
	public final static int NOTIFICATION_REQUEST_CODE = 300;

	// 占线时转移，这里13800000000是空号，所以会提示所拨的号码为空号
	private final String ENABLE_SERVICE = "tel:**67*13800000000%23";
	// 占线时转移
	private final String DISABLE_SERVICE = "tel:%23%2367%23";

	private IncomingCallReceiver mReceiver;
	private ITelephony iTelephony;
	private AudioManager mAudioManager;
	
	public static List<String> interceptNumbers = new ArrayList<>();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message response) {
			int what = response.what;
			switch (what) {
			case OP_REGISTER: {
				Intent i = new Intent(Intent.ACTION_CALL);
				i.setData(Uri.parse(ENABLE_SERVICE));
				startActivity(i);
				break;
			}
			case OP_CANCEL: {
				Intent i = new Intent(Intent.ACTION_CALL);
				i.setData(Uri.parse(DISABLE_SERVICE));
				startActivity(i);
				break;
			}
			}
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mReceiver = new IncomingCallReceiver();
		IntentFilter filter = new IntentFilter(
				"android.intent.action.PHONE_STATE");
		registerReceiver(mReceiver, filter);// 注册BroadcastReceiver
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
Log.d("ttt", "intercept service..");
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

		return START_STICKY;
	}

	private class IncomingCallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
			// Log.i(TAG, "State: " + state);

			String number = intent
					.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			// Log.d(TAG, "Incomng Number: " + number);

			if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {// 电话正在响铃
				if (BlackListUtils.isNumberInBlackList(context, number)) {// 拦截指定的电话号码
//					interceptNumbers.add(number);
					showNotification(number);
					// 先静音处理
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_SILENT);

					try {
						// 挂断电话
						iTelephony.endCall();
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					// 再恢复正常铃声
					mAudioManager
							.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
			}
		}
	}
	private void showNotification(String number) {
		NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(this);
		Notification nf = nfBuilder.build();
		RemoteViews notifRemoteView = new RemoteViews(this.getPackageName(), R.layout.notification_layout);
		notifRemoteView.setTextViewText(R.id.phone, number);
		Intent intent = new Intent(this,AllContactActivity.class);
		intent.putExtra(EXTRA_CLICKED_NOTIFY, true);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		nf.contentView = notifRemoteView;
		nf.contentIntent = pendingIntent;
		nf.icon = R.drawable.icon;
		nf.when = System.currentTimeMillis();
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(1, nf);
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		Intent service = new Intent(this,InterceptService.class);
		startService(service);
		super.onDestroy();
	}
}
