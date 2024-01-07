package com.example.chromecastone.Dlna.model;

import android.app.Activity;
import android.util.Log;

import com.example.chromecastone.Dlna.controller.IUpnpServiceController;
import com.example.chromecastone.Dlna.model.upnp.ContentDirectoryDiscovery;
import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;
import com.example.chromecastone.Dlna.model.upnp.RendererDiscovery;

import java.util.Observer;


public abstract class UpnpServiceController implements IUpnpServiceController {
    private static final String TAG = "UpnpServiceController";
    protected IUpnpDevice contentDirectory;
    protected IUpnpDevice renderer;
    protected CObservable rendererObservable = new CObservable();
    protected CObservable contentDirectoryObservable = new CObservable();
    private final ContentDirectoryDiscovery contentDirectoryDiscovery = new ContentDirectoryDiscovery(getServiceListener());
    private final RendererDiscovery rendererDiscovery = new RendererDiscovery(getServiceListener());

    @Override
    public ContentDirectoryDiscovery getContentDirectoryDiscovery() {
        return this.contentDirectoryDiscovery;
    }

    @Override
    public RendererDiscovery getRendererDiscovery() {
        return this.rendererDiscovery;
    }

    @Override
    public void setSelectedRenderer(IUpnpDevice iUpnpDevice) {
        setSelectedRenderer(iUpnpDevice, false);
    }

    @Override
    public void setSelectedRenderer(IUpnpDevice iUpnpDevice, boolean z) {
        IUpnpDevice iUpnpDevice2;
        if (z || iUpnpDevice == null || (iUpnpDevice2 = this.renderer) == null || !iUpnpDevice2.equals(iUpnpDevice)) {
            this.renderer = iUpnpDevice;
            this.rendererObservable.notifyAllObservers();
        }
    }

    @Override
    public void setSelectedContentDirectory(IUpnpDevice iUpnpDevice) {
        setSelectedContentDirectory(iUpnpDevice, false);
    }

    @Override
    public void setSelectedContentDirectory(IUpnpDevice iUpnpDevice, boolean z) {
        IUpnpDevice iUpnpDevice2;
        if (z || iUpnpDevice == null || (iUpnpDevice2 = this.contentDirectory) == null || !iUpnpDevice2.equals(iUpnpDevice)) {
            this.contentDirectory = iUpnpDevice;
            this.contentDirectoryObservable.notifyAllObservers();
        }
    }

    @Override
    public IUpnpDevice getSelectedRenderer() {
        return this.renderer;
    }

    @Override
    public IUpnpDevice getSelectedContentDirectory() {
        return this.contentDirectory;
    }

    @Override
    public void addSelectedRendererObserver(Observer observer) {
        Log.i(TAG, "New SelectedRendererObserver");
        this.rendererObservable.addObserver(observer);
    }

    @Override
    public void delSelectedRendererObserver(Observer observer) {
        this.rendererObservable.deleteObserver(observer);
    }

    @Override
    public void addSelectedContentDirectoryObserver(Observer observer) {
        this.contentDirectoryObservable.addObserver(observer);
    }

    @Override
    public void delSelectedContentDirectoryObserver(Observer observer) {
        this.contentDirectoryObservable.deleteObserver(observer);
    }

    @Override
    public void pause() {
        this.rendererDiscovery.pause(getServiceListener());
        this.contentDirectoryDiscovery.pause(getServiceListener());
    }

    @Override
    public void resume(Activity activity) {
        this.rendererDiscovery.resume(getServiceListener());
        this.contentDirectoryDiscovery.resume(getServiceListener());
    }
}
