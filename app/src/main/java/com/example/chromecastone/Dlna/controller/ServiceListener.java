package com.example.chromecastone.Dlna.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.chromecastone.Activity.CastDeviceListActivity;
import com.example.chromecastone.Dlna.model.CDevice;
import com.example.chromecastone.Dlna.model.CRegistryListener;
import com.example.chromecastone.Dlna.model.mediaserver.MediaServer;
import com.example.chromecastone.Dlna.model.upnp.ICallableFilter;
import com.example.chromecastone.Dlna.model.upnp.IRegistryListener;
import com.example.chromecastone.Dlna.model.upnp.IServiceListener;
import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.Device;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class ServiceListener implements IServiceListener {

    private static final String TAG = "Cling.ServiceListener";
    private Context ctx;
    protected AndroidUpnpService upnpService;
    private MediaServer mediaServer = null;
    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(ServiceListener.TAG, "Service connexion");
            ServiceListener.this.upnpService = (AndroidUpnpService) iBinder;
            if (ServiceListener.this.mediaServer == null) {
                try {
                    if (ServiceListener.this.mediaServer != null) {
                        ServiceListener.this.mediaServer.restart();
                    } else {
                        try {
                            ServiceListener.this.mediaServer = new MediaServer(CastDeviceListActivity.getLocalIpAddress(ServiceListener.this.ctx), ServiceListener.this.ctx);
                        } catch (ValidationException e) {
                            e.printStackTrace();
                        }
                        ServiceListener.this.mediaServer.start();
                    }
                    if (ServiceListener.this.mediaServer.getDevice().getIdentity() != null) {
                        ServiceListener.this.upnpService.getRegistry().addDevice(ServiceListener.this.mediaServer.getDevice());
                    }
                } catch (NullPointerException e2) {
                    Log.e(ServiceListener.TAG, "Starting http server failed");
                    Log.e(ServiceListener.TAG, "exception", e2);
                    e2.printStackTrace();
                } catch (UnknownHostException e3) {
                    Log.e(ServiceListener.TAG, "Creating demo device failed");
                    Log.e(ServiceListener.TAG, "exception", e3);
                    e3.printStackTrace();
                } catch (IOException e4) {
                    Log.e(ServiceListener.TAG, "Starting http server failed");
                    Log.e(ServiceListener.TAG, "exception", e4);
                    e4.printStackTrace();
                }
            } else if (ServiceListener.this.mediaServer != null) {
                Log.i(ServiceListener.TAG, "MediaServer stop");
                ServiceListener.this.mediaServer.stop();
                ServiceListener.this.mediaServer = null;
            }
            Iterator<IRegistryListener> it = ServiceListener.this.waitingListener.iterator();
            while (it.hasNext()) {
                ServiceListener.this.addListenerSafe(it.next());
            }
            ServiceListener.this.upnpService.getControlPoint().search();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(ServiceListener.TAG, "Service disconnected");
            ServiceListener.this.upnpService = null;
        }
    };
    protected ArrayList<IRegistryListener> waitingListener = new ArrayList<>();

    public ServiceListener(Context context) {
        this.ctx = null;
        this.ctx = context;
    }

    @Override
    public void refresh() {
        this.upnpService.getControlPoint().search();
    }

    @Override
    public Collection<IUpnpDevice> getDeviceList() {
        ArrayList arrayList = new ArrayList();
        AndroidUpnpService androidUpnpService = this.upnpService;
        if (androidUpnpService != null && androidUpnpService.getRegistry() != null) {
            for (Device device : this.upnpService.getRegistry().getDevices()) {
                arrayList.add(new CDevice(device));
            }
        }
        return arrayList;
    }

    @Override
    public Collection<IUpnpDevice> getFilteredDeviceList(ICallableFilter iCallableFilter) {
        ArrayList arrayList = new ArrayList();
        try {
            AndroidUpnpService androidUpnpService = this.upnpService;
            if (androidUpnpService != null && androidUpnpService.getRegistry() != null) {
                for (Device device : this.upnpService.getRegistry().getDevices()) {
                    CDevice cDevice = new CDevice(device);
                    iCallableFilter.setDevice(cDevice);
                    if (iCallableFilter.call().booleanValue()) {
                        arrayList.add(cDevice);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override
    public ServiceConnection getServiceConnexion() {
        return this.serviceConnection;
    }

    public AndroidUpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override
    public void addListener(IRegistryListener iRegistryListener) {
        Log.d(TAG, "Add Listener !");
        if (this.upnpService != null) {
            addListenerSafe(iRegistryListener);
        } else {
            this.waitingListener.add(iRegistryListener);
        }
    }

    
    public void addListenerSafe(IRegistryListener iRegistryListener) {
        Log.d(TAG, "Add Listener Safe !");
        this.upnpService.getRegistry().addListener(new CRegistryListener(iRegistryListener));
        for (Device device : this.upnpService.getRegistry().getDevices()) {
            iRegistryListener.deviceAdded(new CDevice(device));
        }
    }

    @Override
    public void removeListener(IRegistryListener iRegistryListener) {
        Log.d(TAG, "remove listener");
        if (this.upnpService != null) {
            removeListenerSafe(iRegistryListener);
        } else {
            this.waitingListener.remove(iRegistryListener);
        }
    }

    private void removeListenerSafe(IRegistryListener iRegistryListener) {
        Log.d(TAG, "remove listener Safe");
        this.upnpService.getRegistry().removeListener(new CRegistryListener(iRegistryListener));
    }

    @Override
    public void clearListener() {
        this.waitingListener.clear();
    }
}
