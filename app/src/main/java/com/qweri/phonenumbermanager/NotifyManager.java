package com.qweri.phonenumbermanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.qweri.phonenumbermanager.utils.SharedPreferenceUtils;

import static com.qweri.phonenumbermanager.InterceptService.EXTRA_CLICKED_NOTIFY;
import static com.qweri.phonenumbermanager.InterceptService.NOTIFICATION_REQUEST_CODE;

/**
 * Created by zqwei on 11/12/16.
 */

public class NotifyManager {

    public static final int NOTIFICATION_ID_ALIVE = 2;
    public static final int NOTIFICATION_ID_ALART = 1;


    public static void showKeepAliveNotification(Context context) {
        if(!SharedPreferenceUtils.isShowAliveNotification(context)) {
            return;
        }
        NotificationCompat.Builder nfBuilder = new NotificationCompat.Builder(context);
        Notification nf = nfBuilder.build();
        RemoteViews notifRemoteView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        notifRemoteView.setTextViewText(R.id.notif, context.getString(R.string.alive_notification_title));
        notifRemoteView.setTextViewText(R.id.phone, context.getString(R.string.alive_notification_desc));

        Intent intent = new Intent(context,MainActivity.class);
        intent.putExtra(EXTRA_CLICKED_NOTIFY, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        nf.contentView = notifRemoteView;
        nf.contentIntent = pendingIntent;
        nf.flags = Notification.FLAG_ONGOING_EVENT;
        nf.icon = R.drawable.icon;
        nf.tickerText = context.getString(R.string.alive_notification_title);
//        nf.priority = Notification.PRIORITY_HIGH;


        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID_ALIVE, nf);
    }

    public static void cancelNotification(Context context, int id) {
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }
}
