package com.example.dumbhome.SendAndListen;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.dumbhome.Device;
import com.example.dumbhome.DeviceListManager;
import com.example.dumbhome.LoadingActivity;
import com.example.dumbhome.MainActivity;
import dh_java.A2DDiscoverMessage;
import dh_java.A2DNameMessage;
import dh_java.A2DToggleMessage;
import dh_java.D2AIdentityMessage;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class SendDiscoverAndListen extends SendAndListen {

    public SendDiscoverAndListen(Activity activity) {
        super(new A2DDiscoverMessage());
        this.currentActivity = activity;
        if (!DeviceListManager.getInstance().isListening()) {
            startLoadingActivity();
        }
    }

    private void startLoadingActivity() {
        this.currentActivity.runOnUiThread(() -> {
            Intent intent = new Intent(this.currentActivity, LoadingActivity.class);
            this.currentActivity.startActivity(intent);
            this.currentActivity.finish();
        });
    }

    @Override
    protected boolean handleResponseMessage(DatagramPacket receivedPacket) {
        Log.d("DEBUG", "Received Identity message");
        D2AIdentityMessage response = D2AIdentityMessage.fromBytes(receivedPacket.getData());
        if (response != null) {
            InetAddress ipAddress = receivedPacket.getAddress();
            Device newDevice = new Device(
                    response.name, ipAddress.toString().substring(1), "00:00:00:00:00:00",
                    response.relayState, DeviceListManager.getInstance().getDeviceList().size() + 1
            );
            DeviceListManager.getInstance().addDevice(newDevice);
            return true;
        }
        return false;
    }

    @Override
    protected void handleTimeout() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        this.currentActivity.startActivity(intent);
        this.currentActivity.finish();
    }

    @Override
    protected void handleBindException() {

    }
}
