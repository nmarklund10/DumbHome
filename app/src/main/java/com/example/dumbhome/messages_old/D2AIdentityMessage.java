package com.example.dumbhome.messages;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class D2AIdentityMessage extends Message {

    /*
     * This message shall only be sent in response to a Discovery Message.
     * If the device has a name, it will report its name.
     *
     */

    public static final short IDENTITY_MSG_TYPE = (short) 0x1001;

    public boolean hasName;
    public boolean relayState;
    public String name;

    public D2AIdentityMessage(boolean aHasName, boolean aRelayState,
                              String aName, String aIpAddress) {
        super(IDENTITY_MSG_TYPE, aIpAddress);
        hasName = aHasName;
        relayState = aRelayState;
        name = aName;
        port = 17001;
    }

    @Override
    public byte[] toBytes() {
        byte[] buf = super.toBytes();

        //The next byte is 0 if no name, 1 if name present
        buf[6] = hasName ? (byte) 0x1 : (byte) 0x0;
        buf[7] = relayState ? (byte) 0x1 : (byte) 0x0;

        //The following fields only present if a name is present
        if (name != null) {
            //The next 2 bytes is the name length
            short nameLen = (short) name.length();
            buf[8] = (byte) ((nameLen >> 8) & 0xFF);
            buf[9] = (byte) (nameLen & 0xFF);

            //The next N bytes are the device name
            for (int i = 0; i < name.length(); i++) {
                buf[10 + i] = (byte) name.charAt(i);
            }
        }

        return buf;
    }

    public static D2AIdentityMessage fromBytes(byte[] receivedPacket) {
        if (Message.verifyHeader(receivedPacket, IDENTITY_MSG_TYPE)) {
            boolean hasName = receivedPacket[6] == 0x1;
            boolean relayState = receivedPacket[7] == 0x1;
            String name = null;
            if (hasName) {
                short nameLen = bytesToShort(receivedPacket[8], receivedPacket[9]);
                StringBuilder nameBuilder = new StringBuilder();
                for (int i = 0; i < nameLen; i++) {
                    nameBuilder.append((char)(receivedPacket[10 + i]));
                }
                name = nameBuilder.toString();
            }
            return new D2AIdentityMessage(hasName, relayState, name, null);
        }
        return null;
    }
}
