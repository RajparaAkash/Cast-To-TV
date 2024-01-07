package com.example.chromecastone.Dlna.model;

import android.content.Intent;
import android.util.Log;

import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;


public class UpnpService extends AndroidUpnpServiceImpl {

    @Override
    public AndroidUpnpServiceConfiguration createConfiguration() {
        return new AndroidUpnpServiceConfiguration() {
            @Override
            public int getRegistryMaintenanceIntervalMillis() {
                return 1000;
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(getClass().getName(), "Unbind");
        return super.onUnbind(intent);
    }
}
