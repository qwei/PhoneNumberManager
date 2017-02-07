package com.qweri.phonenumbermanager;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

public class InComingCallReceiver extends BroadcastReceiver {

	private ITelephony iTelephony;
	private AudioManager mAudioManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		// 利用反射获取隐藏的endcall方法
		TelephonyManager telephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Method getITelephonyMethod = TelephonyManager.class
					.getDeclaredMethod("getITelephony", (Class[]) null);
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyMgr,
					(Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		// Log.i(TAG, "State: " + state);

		String number = intent
				.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		// Log.d(TAG, "Incomng Number: " + number);

		if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {// 电话正在响铃
			if (number.equals(BlackListUtils.getBlackListNumbers(context))) {// 拦截指定的电话号码
				// 先静音处理
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				// Log.d(TAG, "Turn ringtone silent");

				try {
					// 挂断电话
					iTelephony.endCall();
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				// 再恢复正常铃声
				mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			}
		}
	}

}
