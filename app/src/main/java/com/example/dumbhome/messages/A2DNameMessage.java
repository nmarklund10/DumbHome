package com.example.dumbhome.messages;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class A2DNameMessage extends Message {

    /*
     * This message is sent to a specific device to assign
     * or reassign a name to it
     *
     */

    public static final short NAME_MSG_TYPE = (short) 0x0002;

    public String name;

    public A2DNameMessage(String aName, String aIpAddress){
        super(NAME_MSG_TYPE, aIpAddress);
        this.name = aName;
        port = 17000;
    }

    @Override
    public byte[] toBytes() {
        byte[] buf = super.toBytes();

        //The following fields only present if a name is present
        if(name != null) {
            //The next 2 bytes is the name length
            short nameLen = (short)name.length();
            buf[6] = (byte)((nameLen >> 8) & 0xFF);
            buf[7] = (byte)(nameLen & 0xFF);

            //The next N bytes are the device name
            for(int i = 0; i < name.length(); i++) {
                buf[8 + i] = (byte)name.charAt(i);
            }
        }

        return buf;
    }

    public static A2DNameMessage fromBytes(byte[] receivedPacket) {
        if (Message.verifyHeader(receivedPacket, NAME_MSG_TYPE)) {
            String name = null;
            if (receivedPacket[6] != (byte)0xFF) {
                short nameLength = bytesToShort(receivedPacket[6], receivedPacket[7]);
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 0; i < nameLength; i++) {
                    nameBuilder.append((char)(receivedPacket[8 + i]));
                }
                name = nameBuilder.toString();
            }
            return new A2DNameMessage(name, null);
        }
        return null;
    }
}
