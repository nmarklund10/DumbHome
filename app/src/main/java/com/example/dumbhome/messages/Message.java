package com.example.dumbhome.messages;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public abstract class Message {

    public static final int DATA_SIZE = 512;
    public static final byte[] MAGIC_NUMBER = {0x23, 0x61, (byte) 0xA2, (byte) 0xE9};

    public byte[] magic;
    public short msgType;
    public int port;
    public String ipAddress;

    public Message(short aMsgType, String aIpAddress) {
        magic = MAGIC_NUMBER;
        msgType = aMsgType;
        ipAddress = aIpAddress;
    }

    public byte[] toBytes() {
        byte[] buf = new byte[DATA_SIZE];

        //Fill with 0xFF
        for (int i = 0; i < DATA_SIZE; i++) buf[i] = (byte) 0xFF;

        //Copy in the magic number
        System.arraycopy(magic, 0, buf, 0, 4);

        //Copy in the message type
        buf[4] = (byte) ((msgType >> 8) & 0xFF);
        buf[5] = (byte) (msgType & 0xFF);

        return buf;
    }

    public DatagramPacket getPacket() throws UnknownHostException {
        return new DatagramPacket(
                this.toBytes(),
                DATA_SIZE,
                InetAddress.getByName(ipAddress),
                port
        );
    }

    protected static short bytesToShort(byte high, byte low) {
        return (short)(((high << 8)) | (low & 0xFF));
    }

    protected static boolean verifyHeader(byte[] receivedPacket, short messageType) {
        // Get first four bytes and ensure magic number matches
        byte[] receivedMagicNumber = Arrays.copyOfRange(receivedPacket, 0, 4);
        if (Arrays.equals(receivedMagicNumber, MAGIC_NUMBER)) {
            short msgType = Message.bytesToShort(receivedPacket[4], receivedPacket[5]);
            return msgType == messageType;
        }
        return false;
    }
}
