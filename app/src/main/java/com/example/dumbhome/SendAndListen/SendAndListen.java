package com.example.dumbhome.SendAndListen;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.dumbhome.DeviceListManager;
import dh_java.Message;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public abstract class SendAndListen implements Runnable {

    protected final Message message;
    protected final int listeningPort = 17001;
    protected final int RECV_SIZE = 4096;
    protected final int TIMEOUT = 2000;
    protected byte[] receiverBuffer = new byte[this.RECV_SIZE];
    protected Activity currentActivity;
    protected DatagramSocket serverSocket;

    public SendAndListen(Message message) {
        this.message = message;
    }

    protected abstract boolean handleResponseMessage(DatagramPacket receivedPacket);
    protected abstract void handleTimeout();
    protected abstract void handleBindException();

    protected void displayError(String errorMessage) {
        Toast.makeText(
            this.currentActivity.getApplicationContext(),
            errorMessage, Toast.LENGTH_LONG
        ).show();
    }

    private boolean listenForResponse() throws IOException {
        DatagramPacket receivedPacket = new DatagramPacket(this.receiverBuffer, this.receiverBuffer.length);
        this.serverSocket.setSoTimeout(this.TIMEOUT);
        this.serverSocket.receive(receivedPacket);
        return handleResponseMessage(receivedPacket);
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new DatagramSocket(this.listeningPort);
            DeviceListManager.getInstance().setListening(true);
            // Send Message
            this.serverSocket.send(this.message.getPacket());
            // Listen for response
            while (DeviceListManager.getInstance().isListening()) {
                try {
                    DeviceListManager.getInstance().setListening(listenForResponse());
                } catch (SocketTimeoutException e) {
                    DeviceListManager.getInstance().setListening(false);
                    this.currentActivity.runOnUiThread(this::handleTimeout);
                }
            }
            // Stop listening
            this.serverSocket.close();
            DeviceListManager.getInstance().setListening(false);
        } catch (BindException e) {
            this.currentActivity.runOnUiThread(() ->  {
                displayError("Request in progress, try again later!");
                handleBindException();
            });
        } catch (SocketException | UnknownHostException e) {
            Log.e("Socket Open:", "Error:", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
