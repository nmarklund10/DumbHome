package com.example.dumbhome;

import android.app.Activity;
import android.util.Log;

import com.example.dumbhome.SendAndListen.SendDiscoverAndListen;
import com.example.dumbhome.SendAndListen.SendNameAndListen;
import com.example.dumbhome.SendAndListen.SendToggleAndListen;
import dh_java.D2AIdentityMessage;
import dh_java.D2AStatusMessage;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static dh_java.A2DNameMessage.NAME_MSG_TYPE;
import static dh_java.A2DToggleMessage.TOGGLE_MSG_TYPE;

public class MessageUtils {
    // User should not make instance of this class
    private MessageUtils() {}

    private static final String phoneIP = "10.31.114.13";
    private static final boolean isTesting = true;

    public static void sendDiscoverMessage(Activity activity) {
        new Thread(new SendDiscoverAndListen(activity)).start();
        if (isTesting) { MessageUtils.sendTestDiscoverResponses(); }
    }

    public static void sendToggleMessage(Activity activity, int deviceIndex, SwitchMaterial deviceSwitch) {
        new Thread(new SendToggleAndListen(activity, deviceIndex, deviceSwitch)).start();
        if (isTesting) { MessageUtils.sendTestToggleResponses(); }
    }

    public static void sendNameMessage(String name, EditDeviceDialog editDeviceDialog) {
        new Thread(new SendNameAndListen(name, editDeviceDialog)).start();
        if (isTesting) { MessageUtils.sendTestNameResponses(); }
    }

    public static D2AStatusMessage parseStatusPacket(DatagramPacket receivedPacket, int msgType) {
        D2AStatusMessage response = D2AStatusMessage.fromBytes(receivedPacket.getData());
        if (response != null && (!response.lastMsgSuccess || response.lastMsgType != msgType)) {
            return null;
        }
        return response;
    }

    public static void sendTestDiscoverResponses() {
        D2AIdentityMessage testMessage1 =
                new D2AIdentityMessage(true, true, "Name 1", phoneIP);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DatagramSocket testClient = new DatagramSocket();
                Log.d("DEBUG", "Client sending packet");
                testClient.send(testMessage1.getPacket());
                Log.d("DEBUG", "Client sent packet");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
        D2AIdentityMessage testMessage2 =
                new D2AIdentityMessage(true, true, "Name 2", phoneIP);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DatagramSocket testClient = new DatagramSocket();
                Log.d("DEBUG", "Client sending packet");
                testClient.send(testMessage2.getPacket());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static void sendTestToggleResponses() {
        D2AStatusMessage testMessage =
            new D2AStatusMessage(TOGGLE_MSG_TYPE, true, false, phoneIP);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DatagramSocket testClient = new DatagramSocket();
                Log.d("DEBUG", "Client sending packet");
                testClient.send(testMessage.getPacket());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static void sendTestNameResponses() {
        D2AStatusMessage testMessage =
            new D2AStatusMessage(NAME_MSG_TYPE, true, true, phoneIP);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DatagramSocket testClient = new DatagramSocket();
                Log.d("DEBUG", "Client sending packet");
                testClient.send(testMessage.getPacket());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}