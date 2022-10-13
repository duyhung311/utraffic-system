package com.hcmut.admin.utrafficsystem;

import android.app.Application;

import com.hcmut.admin.utrafficsystem.ui.map.MapActivity;

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