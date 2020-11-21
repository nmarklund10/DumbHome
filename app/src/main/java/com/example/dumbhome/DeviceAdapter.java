package com.example.dumbhome;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {

    // Private Members
    private final ArrayList<Device> deviceList;
    private final Context context;

    private void switchClickListener(Device device, SwitchMaterial deviceSwitch) {
        // TODO send Toggle
        device.togglePowerState();
        deviceSwitch.setChecked(device.getPowerState());
    }


    private void textViewListener(Device device, TextView deviceNameView) {
        EditDeviceDialog dialog = new EditDeviceDialog(context, device, deviceNameView);
        dialog.show();
    }

    // Public Functions
    public DeviceAdapter(ArrayList<Device> listItems, Context context) {
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
