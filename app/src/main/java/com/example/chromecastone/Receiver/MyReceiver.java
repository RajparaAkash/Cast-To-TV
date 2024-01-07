package com.example.chromecastone.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.chromecastone.Service.BackgroundService;
import com.example.chromecastone.Utils.Constant;


public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean booleanExtra = intent.getBooleanExtra("action", false);
        Intent intent2 = new Intent(context, BackgroundService.class);
        intent2.putExtra(Constant.IS_STOP_STREAMING, booleanExtra);
        context.startService(intent2);
    }
}
