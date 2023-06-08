package com.hcmut.admin.utraffictest;

import android.app.Application;

import com.hcmut.admin.utraffictest.ui.map.MapActivity;

public class MyApplication extends Application {

    private MapActivity mCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public MapActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(MapActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

}