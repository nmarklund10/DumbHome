package com.example.dumbhome;

import android.util.Log;

import com.example.dumbhome.messages.A2DDiscoverMessage;
import com.example.dumbhome.messages.D2AIdentityMessage;
import com.example.dumbhome.messages.D2AStatusMessage;
import com.example.dumbhome.messages.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private final int TIMEOUT = 2500;
    private byte[] receiverBuffer = new byte[RECV_SIZE];

    public SendAndListen(Message aMessage) {
        this.message = aMessage;
    }

    private void handlePacket(DatagramPacket receivedPacket) {
        if (message.msgType == DISCOVER_MSG_TYPE) {
            // Should get D2AIdentityMessage back
            D2AIdentityMessage response = D2AIdentityMessage.fromBytes(receivedPacket.getData());
            Log.d("PROCESS", response.name);
        }
        else if (message.msgType == NAME_MSG_TYPE || message.msgType == TOGGLE_MSG_TYPE) {
            // Should get D2AStatusMessage back
            D2AStatusMessage response = D2AStatusMessage.fromBytes(receivedPacket.getData());
            if (response.lastMsgType != message.msgType) {
                response = null;
            }
            Log.d("PROCESS", String.valueOf(response.relayOn));
        }
    }

    @Override
    public void run() {
        boolean isRunning = true;
        try {
            DatagramSocket serverSocket = new DatagramSocket(listeningPort);
            InetAddress serverAddress = InetAddress.getByName(message.ipAddress);
            // Send Message
            serverSocket.send(message.getPacket());
            // Listen for Response
            while (isRunning) {
                try {
                    DatagramPacket receivedPacket =
                            new DatagramPacket(receiverBuffer, receiverBuffer.length);
                    Log.d("DEBUG", "about to wait to receive");
                    serverSocket.setSoTimeout(TIMEOUT);
                    serverSocket.receive(receivedPacket);
                    Log.d("DEBUG", "received packet");
                    handlePacket(receivedPacket);
                    Log.d("DEBUG", "processed packet");
                } catch (SocketTimeoutException e) {
                    Log.e("Timeout Exception","UDP Connection:",e);
                    isRunning = false;
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e("UDP IO Exception", "error: ", e);
                    isRunning = false;
                    serverSocket.close();
                }
            }
        } catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
