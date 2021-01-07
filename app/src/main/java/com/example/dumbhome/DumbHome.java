package com.example.dumbhome;

import android.app.Application;

public class DumbHome extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceListManager.initialize(getApplicationContext());
    }
}

