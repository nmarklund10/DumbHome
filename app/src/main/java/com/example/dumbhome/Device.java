package com.example.dumbhome;

import java.util.Locale;

public class Device {
    private String displayName;
    private final String ipAddress;
    private final String macAddress;
    private boolean powerState;
    private final int position;

    // Constructor
    public Device(String aDisplayName, String aIpAddress, String aMacAddress, int aPosition) {
        this.displayName = aDisplayName;
        this.ipAddress = aIpAddress;
        this.macAddress = aMacAddress;
        this.powerState = false;
        this.position = aPosition;
    }

    // If Device does not have name assigned
    public Device(String aIpAddress, String aMacAddress, int aPosition) {
        this.displayName = "Device " + (aPosition + 1);
        this.ipAddress = aIpAddress;
        this.macAddress = aMacAddress;
        this.powerState = false;
        this.position = aPosition;
    }

    // Getters
    public String getDisplayName() {
        return displayName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public boolean getPowerState() {
        return powerState;
    }
    public int getPosition() {
        return position;
    }

    // Setters
    public void togglePowerState() {
        powerState = !powerState;
    }
    public void setDisplayName(String newDisplayName) {
        displayName = newDisplayName;
    }

    public String toString() {
        String powerText = powerState ? "ON" : "OFF";
        return String.format(
                Locale.ENGLISH,
                "%d: %s - %s - \"%s\" - %s",
                position + 1,
                macAddress,
                ipAddress,
                displayName,
                powerText
        );
    }
}
