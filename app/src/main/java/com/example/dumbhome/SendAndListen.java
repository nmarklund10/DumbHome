package com.example.dumbhome;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dumbhome.messages.D2AStatusMessage;
import com.example.dumbhome.messages.Message;
import com.example.dumbhome.messages.MessageUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static com.example.dumbhome.messages.A2DDiscoverMessage.DISCOVER_MSG_TYPE;
import static com.example.dumbhome.messages.A2DNameMessage.NAME_MSG_TYPE;
import static com.example.dumbhome.messages.A2DToggleMessage.TOGGLE_MSG_TYPE;

public class SendAndListen implements Runnable {

    private final Message message;
    private final int listeningPort = 17001;
    private final int RECV_SIZE = 4096;
    private final int TIMEOUT = 2000;
    private byte[] receiverBuffer = new byte[RECV_SIZE];
    private View view;
    Activity currentActivity;
    DatagramSocket serverSocket;
    Device device;

    public SendAndListen(Message aMessage, Activity aActivity) {
        this.message = aMessage;
        this.currentActivity = aActivity;
    }

    public SendAndListen(Message aMessage, Activity aActivity, int deviceIndex, View aView) {
        this.message = aMessage;
        this.currentActivity = aActivity;
        this.view = aView;
        this.device = DeviceListManager.getInstance().getDeviceList().get(deviceIndex);
    }

    private boolean listenForResponse() throws IOException {
        DatagramPacket receivedPacket =
                new DatagramPacket(receiverBuffer, receiverBuffer.length);
        serverSocket.setSoTimeout(TIMEOUT);
        serverSocket.receive(receivedPacket);
        if (message.msgType == DISCOVER_MSG_TYPE) {
            MessageUtils.handleIdentityMessage(receivedPacket);
        }
        else if (message.msgType == NAME_MSG_TYPE || message.msgType == TOGGLE_MSG_TYPE) {
            D2AStatusMessage response = MessageUtils.handleStatusMessage(receivedPacket, message.msgType);
            serverSocket.close();
            if (message.msgType == NAME_MSG_TYPE) {
                // TODO: update name on list
            }
            else if (message.msgType == TOGGLE_MSG_TYPE) {
                currentActivity.runOnUiThread(() -> {
                    if (response != null) {
                        device.setPowerState(response.relayOn);
                    }
                    else {
                        Toast.makeText(
                                currentActivity.getApplicationContext(),
                                "Toggle message not accepted!", Toast.LENGTH_LONG
                        ).show();
                    }
                    SwitchMaterial deviceSwitch = (SwitchMaterial) this.view;
                    deviceSwitch.setChecked(device.getPowerState());
                    view.setEnabled(true);
                });
            }
            return false;
        }
        else {
            throw new IllegalArgumentException("Unknown message type!");
        }
        return true;
    }

    private void handleTimeout() {
        serverSocket.close();
        if (message.msgType == DISCOVER_MSG_TYPE) {
            currentActivity.runOnUiThread(() -> {
                Intent intent = new Intent(currentActivity, MainActivity.class);
                currentActivity.startActivity(intent);
                currentActivity.finish();
            });
        }
        else {
            currentActivity.runOnUiThread(() -> {
                Toast.makeText(
                        currentActivity.getApplicationContext(),
                        "Device did not respond!", Toast.LENGTH_LONG
                ).show();
                view.setEnabled(true);
            });
        }
    }

    @Override
    public void run() {
        try {
            DeviceListManager.getInstance().setListening(true);
            serverSocket = new DatagramSocket(listeningPort);
            // Send Message
            serverSocket.send(message.getPacket());
            while (DeviceListManager.getInstance().isListening()) {
                try {
                    DeviceListManager.getInstance().setListening(listenForResponse());
                }
                catch (SocketTimeoutException e) {
                    handleTimeout();
                    DeviceListManager.getInstance().setListening(false);
                }
            }
        }
        catch (BindException e) {
            currentActivity.runOnUiThread(() -> {
                Toast.makeText(
                        currentActivity.getApplicationContext(),
                        "Request in progress, try again later!", Toast.LENGTH_LONG
                ).show();
                if (view != null) {
                    view.setEnabled(true);
                }
            });
        }
        catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
