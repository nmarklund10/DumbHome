package com.example.dumbhome.messages;

public class A2DDiscoverMessage extends Message {

    /*
     * When the app wants to initially discover devices or
     * refresh the device list, send this message out
     *
     */

    public static final short DISCOVER_MSG_TYPE = (short) 0x0001;

    public A2DDiscoverMessage(){
        super(DISCOVER_MSG_TYPE, "255.255.255.255");
        port = 17000;
    }

    public static A2DDiscoverMessage fromBytes(byte[] receivedPacket) {
        if (Message.verifyHeader(receivedPacket, DISCOVER_MSG_TYPE)) {
            return new A2DDiscoverMessage();
        }
        return null;
    }
}
