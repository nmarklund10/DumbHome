package com.example.dumbhome;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dumbhome.messages.A2DNameMessage;
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
    private EditDeviceDialog editDeviceDialog;
    private final Activity currentActivity;
    private DatagramSocket serverSocket;
    private Device device;

    public SendAndListen(Message aMessage, Activity aActivity) {
        this.message = aMessage;
        this.currentActivity = aActivity;
    }

    public SendAndListen(Message aMessage, Activity aActivity, int deviceIndex, View pView) {
        this.message = aMessage;
        this.currentActivity = aActivity;
        this.view = pView;
        this.device = DeviceListManager.getInstance().getDeviceList().get(deviceIndex);
    }

    public SendAndListen(Message aMessage, EditDeviceDialog editDeviceDialog) {
        this.message = aMessage;
        this.currentActivity = editDeviceDialog.getCurrentActivity();
        this.device = editDeviceDialog.getDevice();
        this.editDeviceDialog = editDeviceDialog;
    }

    private void handleNameResponse(D2AStatusMessage response) {
        currentActivity.runOnUiThread(() -> {
            if (response != null) {
                TextView deviceNameView = editDeviceDialog.getDeviceNameView();
                String newDeviceName = ((A2DNameMessage)message).name;
                device.setDisplayName(newDeviceName);
                deviceNameView.setText(newDeviceName);
            }
            else {
                Toast.makeText(
                        currentActivity.getApplicationContext(),
                        "Name message not accepted!", Toast.LENGTH_LONG
                ).show();
            }
            editDeviceDialog.showLoading(false);
            editDeviceDialog.dismiss();
        });
    }

    private void handleToggleRespone(D2AStatusMessage response) {
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
            deviceSwitch.setEnabled(true);
        });
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
                handleNameResponse(response);
            }
            else if (message.msgType == TOGGLE_MSG_TYPE) {
                handleToggleRespone(response);
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
                if (message.msgType == TOGGLE_MSG_TYPE) {
                    view.setEnabled(true);
                }
                else {
                    editDeviceDialog.showLoading(false);
                }
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
                else {
                    editDeviceDialog.showLoading(false);
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
