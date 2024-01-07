package com.example.chromecastone.Dlna.model.didl;

import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;
import com.example.chromecastone.Dlna.view.DeviceDisplay;
import com.example.chromecastone.R;


public class DIDLDevice implements IDIDLObject {
    IUpnpDevice device;

    @Override
    public String getCount() {
        return "";
    }

    @Override
    public String getDataType() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_images;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public String getParentID() {
        return "";
    }

    public DIDLDevice(IUpnpDevice iUpnpDevice) {
        this.device = iUpnpDevice;
    }

    public IUpnpDevice getDevice() {
        return this.device;
    }

    @Override
    public String getTitle() {
        return new DeviceDisplay(this.device).toString();
    }
}
