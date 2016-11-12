package com.qweri.phonenumbermanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zqwei on 11/12/16.
 */

public class SharedPreferenceUtils {


    public static final String SP_ALIVE_NOTIFICATION_SWITCH = "is_show_notification";

    public static void setShowAliveNotification(Context context, boolean result) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(SP_ALIVE_NOTIFICATION_SWITCH, result).commit();
    }
    public static boolean isShowAliveNotification(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(SP_ALIVE_NOTIFICATION_SWITCH, true);
    }

}
