package com.example.chromecastone.Dlna.model;

import com.example.chromecastone.Dlna.model.upnp.IRegistryListener;

import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;


public class CRegistryListener extends DefaultRegistryListener {
    private final IRegistryListener registryListener;

    public CRegistryListener(IRegistryListener iRegistryListener) {
        this.registryListener = iRegistryListener;
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice remoteDevice) {
        this.registryListener.deviceAdded(new CDevice(remoteDevice));
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice remoteDevice, Exception exc) {
        this.registryListener.deviceRemoved(new CDevice(remoteDevice));
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice remoteDevice) {
        this.registryListener.deviceAdded(new CDevice(remoteDevice));
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice remoteDevice) {
        this.registryListener.deviceRemoved(new CDevice(remoteDevice));
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice localDevice) {
        this.registryListener.deviceAdded(new CDevice(localDevice));
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice localDevice) {
        this.registryListener.deviceRemoved(new CDevice(localDevice));
    }
}
