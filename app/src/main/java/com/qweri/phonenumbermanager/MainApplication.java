package com.qweri.phonenumbermanager;

import android.app.Application;
import android.util.Log;


import org.saturn.daemon.KeepAliveHelper;

public class MainApplication extends Application {


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
//        TCAgent.LOG_ON = Constants.DEBUG;
//        TCAgent.init(this, Constants.APP_ID, Constants.CHANNEL);
//        TCAgent.setReportUncaughtExceptions(true);
        KeepAliveHelper.getInstance().start(this, InterceptService.class.getName(), 20000);
    }
}
