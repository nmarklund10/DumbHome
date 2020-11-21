package com.example.dumbhome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView deviceListView;
    public DeviceAdapter deviceAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    private void goToLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        if (item.getItemId() == R.id.refresh_device_list) {
            goToLoadingActivity();
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

        ArrayList<Device> devices = DeviceListManager.getInstance().getDeviceList();
        TextView deviceNotFound = findViewById(R.id.device_not_found);
        if (devices.size() != 0) {
            deviceNotFound.setVisibility(View.GONE);
        }
        deviceAdapter = new DeviceAdapter(devices, this);
        deviceListView = findViewById(R.id.device_list);
        deviceListView.setAdapter(deviceAdapter);
        // Recycler View maintains height when items are added or removed from view
        deviceListView.setHasFixedSize(true);
        // Have devices displayed in a list format
        deviceListView.setLayoutManager(new LinearLayoutManager(this));
    }
}