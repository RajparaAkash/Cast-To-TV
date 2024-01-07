package com.example.chromecastone.Dlna.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.chromecastone.Dlna.model.UpnpServiceController;

import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.model.meta.LocalDevice;


public class ServiceController extends UpnpServiceController {
    private static final String TAG = "Cling.ServiceController";
    private Activity activity = null;
    private final ServiceListener upnpServiceListener;

    public ServiceController(Context context) {
        this.upnpServiceListener = new ServiceListener(context);
    }

    protected void finalize() {
        pause();
    }

    @Override
    public ServiceListener getServiceListener() {
        return this.upnpServiceListener;
    }

    @Override
    public void pause() {
        super.pause();
        this.activity.unbindService(this.upnpServiceListener.getServiceConnexion());
        this.activity = null;
    }

    @Override
    public void resume(Activity activity) {
        super.resume(activity);
        this.activity = activity;
        Log.d(TAG, "Start upnp service");
        Intent intent = new Intent(activity, AndroidUpnpServiceImpl.class);
        activity.startService(intent);
        activity.bindService(intent, this.upnpServiceListener.getServiceConnexion(), 1);
    }

    @Override
    public void addDevice(LocalDevice localDevice) {
        this.upnpServiceListener.getUpnpService().getRegistry().addDevice(localDevice);
    }

    @Override
    public void removeDevice(LocalDevice localDevice) {
        this.upnpServiceListener.getUpnpService().getRegistry().removeDevice(localDevice);
    }
}
