package com.example.dumbhome.SendAndListen;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import com.example.dumbhome.Device;
import com.example.dumbhome.DeviceListManager;
import com.example.dumbhome.messages.A2DToggleMessage;
import com.example.dumbhome.messages.D2AStatusMessage;
import com.example.dumbhome.messages.Message;
import com.example.dumbhome.messages.MessageUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.net.DatagramPacket;

import static com.example.dumbhome.messages.A2DToggleMessage.TOGGLE_MSG_TYPE;

public class SendToggleAndListen extends SendAndListen{

    private final SwitchMaterial deviceSwitch;
    private final Device device;

    public SendToggleAndListen(Activity activity, int deviceIndex, SwitchMaterial deviceSwitch) {
        super(
            new A2DToggleMessage(DeviceListManager.getInstance().getDeviceList().get(deviceIndex).getIpAddress())
        );
        this.currentActivity = activity;
        this.deviceSwitch = deviceSwitch;
        this.device = DeviceListManager.getInstance().getDeviceList().get(deviceIndex);
    }

    @Override
    public boolean handleResponseMessage(DatagramPacket receivedPacket) {
        D2AStatusMessage response = MessageUtils.parseStatusPacket(receivedPacket, TOGGLE_MSG_TYPE);
        this.currentActivity.runOnUiThread(() -> {
            if (response != null) {
                this.device.setPowerState(response.relayOn);
            }
            else {
                this.displayError("Toggle message not accepted!");
            }
            this.deviceSwitch.setChecked(this.device.getPowerState());
            this.deviceSwitch.setEnabled(true);
        });
        return false;
    }

    @Override
    protected void handleTimeout() {
        this.displayError("Device did not respond!");
        this.deviceSwitch.setEnabled(true);
    }

    @Override
    public void handleBindException() {
        this.deviceSwitch.setEnabled(true);
    }
}
