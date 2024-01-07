package com.example.chromecastone.Dlna.controller;

import android.app.Activity;

import com.example.chromecastone.Dlna.model.upnp.ContentDirectoryDiscovery;
import com.example.chromecastone.Dlna.model.upnp.IServiceListener;
import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;
import com.example.chromecastone.Dlna.model.upnp.RendererDiscovery;

import org.fourthline.cling.model.meta.LocalDevice;

import java.util.Observer;


public interface IUpnpServiceController {
    void addDevice(LocalDevice localDevice);

    void addSelectedContentDirectoryObserver(Observer observer);

    void addSelectedRendererObserver(Observer observer);

    void delSelectedContentDirectoryObserver(Observer observer);

    void delSelectedRendererObserver(Observer observer);

    ContentDirectoryDiscovery getContentDirectoryDiscovery();

    RendererDiscovery getRendererDiscovery();

    IUpnpDevice getSelectedContentDirectory();

    IUpnpDevice getSelectedRenderer();

    IServiceListener getServiceListener();

    void pause();

    void removeDevice(LocalDevice localDevice);

    void resume(Activity activity);

    void setSelectedContentDirectory(IUpnpDevice iUpnpDevice);

    void setSelectedContentDirectory(IUpnpDevice iUpnpDevice, boolean z);

    void setSelectedRenderer(IUpnpDevice iUpnpDevice);

    void setSelectedRenderer(IUpnpDevice iUpnpDevice, boolean z);
}
