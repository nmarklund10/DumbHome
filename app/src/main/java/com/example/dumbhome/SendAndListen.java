package com.example.dumbhome;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.example.dumbhome.messages.Message;
import com.example.dumbhome.messages.MessageUtils;

import java.io.IOException;
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
    Activity currentActivity;
    DatagramSocket serverSocket;

    public SendAndListen(Message aMessage, Activity aActivity) {
        this.message = aMessage;
        this.currentActivity = aActivity;
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
            MessageUtils.handleStatusMessage(receivedPacket, message.msgType);
            serverSocket.close();
            if (message.msgType == NAME_MSG_TYPE) {
                // TODO: update name on list
            }
            else if (message.msgType == TOGGLE_MSG_TYPE) {
                // TODO: update switch
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
                // TODO: go to Main Activity
                Intent intent = new Intent(currentActivity, MainActivity.class);
                currentActivity.startActivity(intent);
                currentActivity.finish();
            });
        }
        else {
            currentActivity.runOnUiThread(() -> {
                // TODO: Display Error Message
            });
        }
    }

    @Override
    public void run() {
        boolean isRunning = true;
        try {
            serverSocket = new DatagramSocket(listeningPort);
            // Send Message
            serverSocket.send(message.getPacket());
            while (isRunning) {
                try {
                    isRunning = listenForResponse();
                } catch (SocketTimeoutException e) {
                    handleTimeout();
                    isRunning = false;
                }
            }
        } catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
