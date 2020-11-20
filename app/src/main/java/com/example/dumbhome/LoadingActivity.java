package com.example.dumbhome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        Toolbar dumbHomeToolbar = findViewById(R.id.dumb_home_toolbar);
        setSupportActionBar(dumbHomeToolbar);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        Log.d("DEBUG", intent.getStringExtra("EXTRA_SESSION_ID"));

    }
}
