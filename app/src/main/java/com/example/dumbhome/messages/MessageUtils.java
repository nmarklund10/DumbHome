package com.example.dumbhome.messages;

import android.app.Activity;
import android.util.Log;

import com.example.dumbhome.Device;
import com.example.dumbhome.DeviceListManager;
import com.example.dumbhome.SendAndListen;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.example.dumbhome.messages.A2DToggleMessage.TOGGLE_MSG_TYPE;

public class MessageUtils {
    // User should not make instance of this class
    private MessageUtils() {}

    public static void sendDiscoverMessage(Activity activity) {
        A2DDiscoverMessage message = new A2DDiscoverMessage();
        new Thread(new SendAndListen(message, activity)).start();
        D2AIdentityMessage testMessage =
                new D2AIdentityMessage(true, true, "Name", "10.31.114.42");
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

    public static void sendToggleMessage(Activity activity, int deviceIndex, SwitchMaterial deviceSwitch) {
        String ipAddress = DeviceListManager.getInstance().getDeviceList().get(deviceIndex).getIpAddress();
        A2DToggleMessage message = new A2DToggleMessage(ipAddress);
        new Thread(new SendAndListen(message, activity, deviceIndex, deviceSwitch)).start();
//        D2AStatusMessage testMessage =
//                new D2AStatusMessage(TOGGLE_MSG_TYPE, true, false, "10.31.114.42");
//        new Thread(() -> {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            try {
//                DatagramSocket testClient = new DatagramSocket();
//                Log.d("DEBUG", "Client sending packet");
//                testClient.send(testMessage.getPacket());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
    }

    public void sendNameMessage(String name, String ipAddress, Activity activity) {
        A2DNameMessage message = new A2DNameMessage(name, ipAddress);
        new Thread(new SendAndListen(message, activity)).start();
    }

    public static void handleIdentityMessage(DatagramPacket receivedPacket) {
        D2AIdentityMessage response = D2AIdentityMessage.fromBytes(receivedPacket.getData());
        if (response != null) {
            InetAddress ipAddress = receivedPacket.getAddress();
            Device newDevice = new Device(
                response.name, ipAddress.toString().substring(1), "00:00:00:00:00:00",
                response.relayState, DeviceListManager.getInstance().getDeviceList().size() + 1
            );
            DeviceListManager.getInstance().addDevice(newDevice);
        }
    }

    public static D2AStatusMessage handleStatusMessage(DatagramPacket receivedPacket, int msgType) {
        D2AStatusMessage response = D2AStatusMessage.fromBytes(receivedPacket.getData());
        if (!response.lastMsgSuccess || response.lastMsgType != msgType) {
            return null;
        }
        return response;
    }

}