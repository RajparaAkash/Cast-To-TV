package com.example.chromecastone.Dlna.model.upnp;


public interface IRegistryListener {
    void deviceAdded(IUpnpDevice iUpnpDevice);

    void deviceRemoved(IUpnpDevice iUpnpDevice);
}
