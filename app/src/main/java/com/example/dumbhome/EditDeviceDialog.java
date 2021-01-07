package com.example.dumbhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class EditDeviceDialog {

    private final AlertDialog editDeviceDialog;
    private final TextView deviceNameView;
    private final Device device;
    private final View dialogView;
    private final Activity currentActivity;

    private View getEditDeviceDialogView(Context context) {
        ViewGroup viewGroup = ((Activity)context).findViewById(android.R.id.content);
        return LayoutInflater.from(context).inflate(
                R.layout.edit_device_dialog, viewGroup, false
        );
    }

    private void setEditDeviceInfo(View dialogView) {
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
        // Put cursor at end of text box
        editDeviceField.setSelection(editDeviceField.getText().length());
    }

    private void saveName() {
        EditText editDeviceField = dialogView.findViewById(R.id.edit_device_field);
        String newDeviceName = editDeviceField.getText().toString();
        if (newDeviceName.equals(device.getDisplayName())) {
            editDeviceDialog.dismiss();
        }
        else {
            showLoading(true);
            MessageUtils.sendNameMessage(newDeviceName, this);
        }
    }

    private void setEditDeviceListeners() {
        dialogView.findViewById(R.id.dialog_save).setOnClickListener(view -> saveName());
        dialogView.findViewById(R.id.dialog_cancel).setOnClickListener(view -> editDeviceDialog.dismiss());
    }

    public EditDeviceDialog(Context context, int deviceIndex, TextView deviceNameView) {
        this.currentActivity = (Activity)context;
        this.dialogView = getEditDeviceDialogView(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        this.editDeviceDialog = builder.create();
        this.editDeviceDialog.setCancelable(false);
        this.deviceNameView = deviceNameView;
        this.device = DeviceListManager.getInstance().getDeviceList().get(deviceIndex);
        setEditDeviceInfo(dialogView);
        setEditDeviceListeners();
    }

    public Device getDevice() {
        return device;
    }

    public TextView getDeviceNameView() {
        return deviceNameView;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void showLoading(boolean showLoading) {
        LinearLayout loadingField = dialogView.findViewById(R.id.edit_device_loading);
        LinearLayout buttonField = dialogView.findViewById(R.id.edit_device_buttons);
        int loadingVisibility = showLoading ? View.VISIBLE : View.GONE;
        int buttonVisibility = showLoading ? View.GONE : View.VISIBLE;
        loadingField.setVisibility(loadingVisibility);
        buttonField.setVisibility(buttonVisibility);
    }

    public void show() {
        editDeviceDialog.show();
    }

    public void dismiss() {
        editDeviceDialog.dismiss();
    }
}
