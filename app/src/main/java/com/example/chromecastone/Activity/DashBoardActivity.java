package com.example.chromecastone.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;

public class DashBoardActivity extends AppCompatActivity implements DeviceConnectListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.db_cast_to_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCast();
            }
        });
        findViewById(R.id.db_screen_cast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, ScreenCastActivity.class));
            }
        });
        findViewById(R.id.db_how_to_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, HowToConnectActivity.class));
            }
        });
    }

    public void goToCast() {
        try {
            startActivity(new Intent(Constant.CAST_SETTING));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            try {
                try {
                    try {
                        startActivity(new Intent(Constant.LAUNCH_WFD_PICKER_DLG));
                    } catch (Exception unused) {
                        startActivity(new Intent(Constant.CAST_HTC));
                    }
                } catch (Exception unused2) {
                    startActivity(new Intent(Constant.WIFI_DISPLAY_SETTING));
                }
            } catch (Exception unused3) {
                Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
    }
}