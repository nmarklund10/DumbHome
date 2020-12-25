package com.example.dumbhome;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dumbhome.SendAndListen.SendAndListen;
import com.example.dumbhome.SendAndListen.SendDiscoverAndListen;
import com.example.dumbhome.messages.MessageUtils;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        Toolbar dumbHomeToolbar = findViewById(R.id.dumb_home_toolbar);
        setSupportActionBar(dumbHomeToolbar);

        DeviceListManager.getInstance().clearDeviceList();
    }
}
