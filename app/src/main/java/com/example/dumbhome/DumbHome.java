package com.example.dumbhome;

import android.app.Application;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DumbHome extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceListManager.initialize(getApplicationContext());
    }
}

