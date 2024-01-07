package com.example.chromecastone.Dlna.model.upnp;


public interface IDeviceDiscoveryObserver {
    void addedDevice(IUpnpDevice iUpnpDevice);

    void removedDevice(IUpnpDevice iUpnpDevice);
}
