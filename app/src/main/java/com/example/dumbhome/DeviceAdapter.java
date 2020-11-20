package com.example.dumbhome;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dumbhome.messages.A2DDiscoverMessage;
import com.example.dumbhome.messages.A2DNameMessage;
import com.example.dumbhome.messages.A2DToggleMessage;
import com.example.dumbhome.messages.D2AIdentityMessage;
import com.example.dumbhome.messages.D2AStatusMessage;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import static com.example.dumbhome.messages.A2DNameMessage.NAME_MSG_TYPE;
import static com.example.dumbhome.messages.A2DToggleMessage.TOGGLE_MSG_TYPE;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {

    // Private Members
    private final List<Device> deviceList;
    private final Context context;

    private void sendDiscoverMessage() {
        A2DDiscoverMessage message = new A2DDiscoverMessage();
        D2AIdentityMessage testMessage =
                new D2AIdentityMessage(true, false, "Hello", "127.0.0.1");
        new Thread(new SendAndListen(message)).start();
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DatagramSocket testClient = new DatagramSocket();
                Log.d("DEBUG", "Client sending packet");
                testClient.send(testMessage.getPacket());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void sendNameMessage(String name, String ipAddress) {
        A2DNameMessage message = new A2DNameMessage(name, ipAddress);
        D2AStatusMessage testMessage =
                new D2AStatusMessage(NAME_MSG_TYPE, true, true, "127.0.0.1");
        new Thread(new SendAndListen(message)).start();
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                DatagramSocket testClient = new DatagramSocket();
                Log.d("DEBUG", "Client sending packet");
                testClient.send(testMessage.getPacket());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    // Private Functions
    private void sendToggleMessage(String ipAddress) {
        A2DToggleMessage message = new A2DToggleMessage(ipAddress);
        new Thread(new SendAndListen(message)).start();
//        D2AStatusMessage testMessage =
//                new D2AStatusMessage(TOGGLE_MSG_TYPE, true, true, "127.0.0.1");
//        new Thread(() -> {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            try {
//                DatagramSocket testClient = new DatagramSocket();
//                Log.d("DEBUG", "Client sending packet");
//                testClient.send(testMessage.getPacket());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }).start();
    }

    private void switchClickListener(Device device, SwitchMaterial deviceSwitch) {
        device.togglePowerState();
        deviceSwitch.setChecked(device.getPowerState());
        if (device.getPowerState()) {
//            sendToggleMessage(device.getIpAddress());
//            sendDiscoverMessage();
//            sendNameMessage("Hello", "10.31.114.43");
        }
    }


    private void textViewListener(Device device, TextView deviceNameView) {
        EditDeviceDialog dialog = new EditDeviceDialog(context, device, deviceNameView);
        dialog.show();
    }

    // Public Functions
    public DeviceAdapter(List<Device> listItems, Context context) {
        this.deviceList = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(
                parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new DeviceHolder(v);
    }

    @Override
    public void onBindViewHolder(DeviceHolder holder, int position) {
        Device device = deviceList.get(position);

        holder.deviceNameView.setText(device.getDisplayName());
        holder.deviceNameView.setOnClickListener(view -> {
            textViewListener(device, holder.deviceNameView);
        });

        holder.deviceSwitch.setChecked(device.getPowerState());
        holder.deviceSwitch.setOnClickListener(view -> {
            switchClickListener(device, holder.deviceSwitch);
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceHolder extends RecyclerView.ViewHolder{

        public TextView deviceNameView;
        public SwitchMaterial deviceSwitch;

        public DeviceHolder(View deviceView) {
            super(deviceView);
            deviceNameView = deviceView.findViewById(R.id.device_name);
            deviceSwitch = deviceView.findViewById(R.id.device_switch);
        }
    }

}
