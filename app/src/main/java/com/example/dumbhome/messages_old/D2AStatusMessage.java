package com.example.dumbhome.messages;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class D2AStatusMessage extends Message {

    /*
     * This message is sent in response to a Name or a Toggle message
     * contains info about whether the last message was processed successfully
     * and the current state of the relay
     *
     */

    public static final short STATUS_MSG_TYPE = (short) 0x1002;

    public short lastMsgType;
    public boolean lastMsgSuccess;
    public boolean relayOn;

    public D2AStatusMessage(short aLastMsgType, boolean aLastMsgSuccess,
                            boolean aRelayOn, String aIpAddress) {
        super(STATUS_MSG_TYPE, aIpAddress);
        lastMsgType = aLastMsgType;
        lastMsgSuccess = aLastMsgSuccess;
        relayOn = aRelayOn;
        ipAddress = aIpAddress;
        port = 17001;
    }

    @Override
    public byte[] toBytes() {
        byte[] buf = super.toBytes();

        //The next 2 bytes is the message type of the last received message
        buf[6] = (byte)((lastMsgType >> 8) & 0xFF);
        buf[7] = (byte)(lastMsgType & 0xFF);

        //The next byte is 1 if the last message was processed successfully, otherwise 0
        buf[8] = lastMsgSuccess ? (byte)0x1 : (byte)0x0;

        //The next byte is 1 if the relay is ON, 0 if the relay is OFF
        buf[9] = relayOn ? (byte)0x1 : (byte)0x0;

        return buf;
    }

    public static D2AStatusMessage fromBytes(byte[] receivedPacket) {
        if (Message.verifyHeader(receivedPacket, STATUS_MSG_TYPE)) {
            short lastMsgType = Message.bytesToShort(receivedPacket[6], receivedPacket[7]);
            boolean lastMsgSuccess = receivedPacket[8] == (byte) 0x1;
            boolean relayOn = receivedPacket[9] == (byte) 0x1;
            return new D2AStatusMessage(lastMsgType, lastMsgSuccess, relayOn, null);
        }
        return null;
    }
}
