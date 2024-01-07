package com.example.chromecastone.Dlna.model.upnp;


public class CallableContentDirectoryFilter implements ICallableFilter {
    private IUpnpDevice device;

    @Override
    public void setDevice(IUpnpDevice iUpnpDevice) {
        this.device = iUpnpDevice;
    }

    
    @Override
    public Boolean call() throws Exception {
        return Boolean.valueOf(this.device.asService("ContentDirectory"));
    }
}
