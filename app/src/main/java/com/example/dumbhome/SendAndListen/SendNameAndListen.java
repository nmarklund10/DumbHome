package com.example.dumbhome.SendAndListen;

import android.widget.TextView;

import com.example.dumbhome.Device;
import com.example.dumbhome.DeviceListManager;
import com.example.dumbhome.EditDeviceDialog;
import dh_java.A2DNameMessage;
import dh_java.D2AStatusMessage;

import java.net.DatagramPacket;

import static dh_java.A2DNameMessage.NAME_MSG_TYPE;

public class SendNameAndListen extends SendAndListen {

    private final EditDeviceDialog editDeviceDialog;
    private final Device device;

    public SendNameAndListen(String name, EditDeviceDialog editDeviceDialog) {
        super(new A2DNameMessage(name, editDeviceDialog.getDevice().getIpAddress()));
        this.currentActivity = editDeviceDialog.getCurrentActivity();
        this.device = editDeviceDialog.getDevice();
        this.editDeviceDialog = editDeviceDialog;
    }

    @Override
    public boolean handleResponseMessage(DatagramPacket receivedPacket) {
        D2AStatusMessage response = DeviceListManager.MessageUtils.parseStatusPacket(receivedPacket, NAME_MSG_TYPE);
        this.currentActivity.runOnUiThread(() -> {
            if (response != null) {
                TextView deviceNameView = this.editDeviceDialog.getDeviceNameView();
                String newDeviceName = ((A2DNameMessage)this.message).name;
                this.device.setDisplayName(newDeviceName);
                deviceNameView.setText(newDeviceName);
            }
            else {
                this.displayError("Name message not accepted!");
            }
            this.editDeviceDialog.showLoading(false);
            this.editDeviceDialog.dismiss();
        });
        return false;
    }

    @Override
    protected void handleTimeout() {
        this.displayError("Device did not respond!");
        this.editDeviceDialog.showLoading(false);
    }

    @Override
    public void handleBindException() {
        this.editDeviceDialog.showLoading(false);
    }
}
