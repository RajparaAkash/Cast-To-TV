package com.example.chromecastone.Dlna.model.upnp;

import android.content.ServiceConnection;

import java.util.Collection;


public interface IServiceListener {
    void addListener(IRegistryListener iRegistryListener);

    void clearListener();

    Collection<IUpnpDevice> getDeviceList();

    Collection<IUpnpDevice> getFilteredDeviceList(ICallableFilter iCallableFilter);

    ServiceConnection getServiceConnexion();

    void refresh();

    void removeListener(IRegistryListener iRegistryListener);
}
