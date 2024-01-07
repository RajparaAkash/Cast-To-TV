package com.example.chromecastone.CastServer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;


public class CastServerService extends Service {
    public static final String IP_ADDRESS = "127.0.0.1";
    public static final boolean QUIET = false;
    public static final String ROOT_DIR = ".";
    public static final int SERVER_PORT = 8080;
    NanoHTTPD server;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i("HTTPSERVICE", "Creating and starting httpService");
        super.onCreate();
        SimpleWebServer simpleWebServer = new SimpleWebServer(intent.getStringExtra(IP_ADDRESS), (int) SERVER_PORT, new File(intent.getStringExtra(ROOT_DIR)), false);
        this.server = simpleWebServer;
        try {
            simpleWebServer.start();
            return START_NOT_STICKY;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("HTTPSERVICE", "IOException: " + e.getMessage());
            return START_NOT_STICKY;
        }
    }

    @Override
    public void onDestroy() {
        Log.i("HTTPSERVICE", "Destroying httpService");
        this.server.stop();
        super.onDestroy();
    }
}
