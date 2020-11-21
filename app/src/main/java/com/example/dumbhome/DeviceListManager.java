package com.example.dumbhome;

import android.content.Context;

import java.util.ArrayList;

public class DeviceListManager {

    private static DeviceListManager uniqueInstance;

    public synchronized static void initialize(Context applicationContext) {
        if (applicationContext == null)
            throw new NullPointerException("Provided application context is null");
        else if (uniqueInstance == null) {
            uniqueInstance = new DeviceListManager();
        }
    }

    public static DeviceListManager getInstance() {
        if (uniqueInstance == null)
            throw new NullPointerException("Please call initialize() before getting the instance.");
        return uniqueInstance;
    }

    private ArrayList<Device> deviceList;

    public DeviceListManager() {
        deviceList = new ArrayList<>();
    }

    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }

    public void addDevice(Device newDevice) {
        deviceList.add(newDevice);
    }

    public void clearDeviceList() {
        deviceList = new ArrayList<>();
    }

}
