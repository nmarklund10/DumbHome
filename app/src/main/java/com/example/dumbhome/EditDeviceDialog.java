package com.example.dumbhome;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class EditDeviceDialog {

    private final AlertDialog editDeviceDialog;

    private View getEditDeviceDialogView(Context context) {
        ViewGroup viewGroup = ((Activity) context).findViewById(android.R.id.content);
        View dialogView =  LayoutInflater.from(context).inflate(
                R.layout.edit_device_dialog, viewGroup, false
        );
        return dialogView;
    }

    private void setEditDeviceInfo(View dialogView, Device device) {
        TextView heading = dialogView.findViewById(R.id.dialog_heading);
        String headingText = String.format(
                Locale.ENGLISH,
                "Device %d\n(%s)",
                device.getPosition() + 1,
                device.getMacAddress()
        );
        heading.setText(headingText);
        EditText editDeviceField = dialogView.findViewById(R.id.edit_device_field);
        editDeviceField.setText(device.getDisplayName());
    }

    private void setEditDeviceListeners(View dialogView, AlertDialog editDeviceDialog,
                                        Device device, TextView deviceNameView) {
        dialogView.findViewById(R.id.dialog_save).setOnClickListener(view -> {
            EditText editDeviceField = dialogView.findViewById(R.id.edit_device_field);
            String newDeviceName = editDeviceField.getText().toString();
            device.setDisplayName(newDeviceName);
            deviceNameView.setText(newDeviceName);
            editDeviceDialog.dismiss();
        });
        dialogView.findViewById(R.id.dialog_cancel).setOnClickListener(view -> {
            editDeviceDialog.dismiss();
        });
    }

    public EditDeviceDialog(Context context, Device device, TextView deviceNameView) {
        View dialogView = getEditDeviceDialogView(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        this.editDeviceDialog = builder.create();
        setEditDeviceInfo(dialogView, device);
        setEditDeviceListeners(dialogView, editDeviceDialog, device, deviceNameView);
    }

    public void show() {
        editDeviceDialog.show();
    }
}
