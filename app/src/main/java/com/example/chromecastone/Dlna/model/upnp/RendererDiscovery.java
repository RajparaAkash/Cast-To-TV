package com.example.chromecastone.Dlna.model.upnp;

import com.example.chromecastone.Activity.CastDeviceListActivity;


public class RendererDiscovery extends DeviceDiscovery {
    protected static final String TAG = "RendererDeviceFragment";

    public RendererDiscovery(IServiceListener iServiceListener) {
        super(iServiceListener);
    }

    @Override
    protected ICallableFilter getCallableFilter() {
        return new CallableRendererFilter();
    }

    @Override
    protected boolean isSelected(IUpnpDevice iUpnpDevice) {
        if (CastDeviceListActivity.upnpServiceController == null || CastDeviceListActivity.upnpServiceController.getSelectedRenderer() == null) {
            return false;
        }
        return iUpnpDevice.equals(CastDeviceListActivity.upnpServiceController.getSelectedRenderer());
    }

    @Override
    protected void select(IUpnpDevice iUpnpDevice) {
        select(iUpnpDevice, false);
    }

    @Override
    protected void select(IUpnpDevice iUpnpDevice, boolean z) {
        CastDeviceListActivity.upnpServiceController.setSelectedRenderer(iUpnpDevice, z);
    }

    @Override
    protected void removed(IUpnpDevice iUpnpDevice) {
        if (CastDeviceListActivity.upnpServiceController == null || CastDeviceListActivity.upnpServiceController.getSelectedRenderer() == null || !iUpnpDevice.equals(CastDeviceListActivity.upnpServiceController.getSelectedRenderer())) {
            return;
        }
        CastDeviceListActivity.upnpServiceController.setSelectedRenderer(null);
    }
}
