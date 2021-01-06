package com.example.dumbhome;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.dumbhome.SendAndListen.SendDiscoverAndListen;
import com.example.dumbhome.SendAndListen.SendNameAndListen;
import com.example.dumbhome.SendAndListen.SendToggleAndListen;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import static com.example.dumbhome.messages.A2DNameMessage.NAME_MSG_TYPE;
import static com.example.dumbhome.messages.A2DToggleMessage.TOGGLE_MSG_TYPE;

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
    private boolean listening;

    public DeviceListManager() {
        deviceList = new ArrayList<>();
        listening = false;
    }

    public synchronized boolean isListening() {
        return listening;
    }

    public synchronized void setListening(boolean aIsListening) {
        listening = aIsListening;
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
