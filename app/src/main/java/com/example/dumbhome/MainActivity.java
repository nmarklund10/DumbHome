package com.example.dumbhome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView deviceListView;
    private RecyclerView.Adapter deviceAdapter;
    private TextView spinnerText;
    private ProgressBar spinner;
    private List<Device> devices;

    private void initializeDevices(int numDevices) {
        devices = new ArrayList<>();
        for(int i = 0; i < numDevices; i++){
            Device newDevice = new Device(
                    "127.0.0.1",
                    "00:00:00:00:00",
                    i
            );
            devices.add(newDevice);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.refresh_device_list) {
            // TODO:  send Discovery Message
            deviceListView.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            spinnerText.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar dumbHomeToolbar = findViewById(R.id.dumb_home_toolbar);
        setSupportActionBar(dumbHomeToolbar);

        deviceListView = findViewById(R.id.device_list);
        // Recycler View maintains height when items are added or removed from view
        deviceListView. setHasFixedSize(true);
        // Have devices displayed in a list format
        deviceListView.setLayoutManager(new LinearLayoutManager(this));

        initializeDevices(20);

        deviceAdapter = new DeviceAdapter(devices, this);
        deviceListView.setAdapter(deviceAdapter);

        spinner = findViewById(R.id.progress_bar);
        spinnerText = findViewById(R.id.progress_bar_text);
        spinner.setVisibility(View.GONE);
        spinnerText.setVisibility(View.GONE);
    }
}