package com.cinemaled;

import android.app.Application;
import android.content.Context;

import com.cinemaled.utils.PreferencesUtil;


public class SoftApplication extends Application {

    public static SoftApplication instance;

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = getApplicationContext();
        PreferencesUtil.getInstance().init(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
