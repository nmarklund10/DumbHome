package com.example.dumbhome.messages;

public class A2DToggleMessage extends Message {

    /*
     * This message is sent to a specific device to flip
     * the state of the relay
     *
     */

    public static final short TOGGLE_MSG_TYPE = (short) 0x0003;

    public A2DToggleMessage(String aIpAddress){
        super(TOGGLE_MSG_TYPE, aIpAddress);
        port = 17000;
    }

    public static A2DToggleMessage fromBytes(byte[] receivedPacket) {
        if (Message.verifyHeader(receivedPacket, TOGGLE_MSG_TYPE)) {
            return new A2DToggleMessage(null);
        }
        return null;
    }
}
