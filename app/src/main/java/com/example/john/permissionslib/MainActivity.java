package com.example.john.permissionslib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.john.lib.RunTimePermissions;

@RunTimePermissions
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
