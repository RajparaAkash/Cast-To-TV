package com.example.chromecastone.Dlna.model.upnp;

import com.example.chromecastone.Activity.CastDeviceListActivity;


public class ContentDirectoryDiscovery extends DeviceDiscovery {
    protected static final String TAG = "ContentDirectoryDeviceFragment";

    public ContentDirectoryDiscovery(IServiceListener iServiceListener) {
        super(iServiceListener);
    }

    @Override
    protected ICallableFilter getCallableFilter() {
        return new CallableContentDirectoryFilter();
    }

    @Override
    protected boolean isSelected(IUpnpDevice iUpnpDevice) {
        if (CastDeviceListActivity.upnpServiceController == null || CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory() == null) {
            return false;
        }
        return iUpnpDevice.equals(CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory());
    }

    @Override
    protected void select(IUpnpDevice iUpnpDevice) {
        select(iUpnpDevice, false);
    }

    @Override
    protected void select(IUpnpDevice iUpnpDevice, boolean z) {
        CastDeviceListActivity.upnpServiceController.setSelectedContentDirectory(iUpnpDevice, z);
    }

    @Override
    protected void removed(IUpnpDevice iUpnpDevice) {
        if (CastDeviceListActivity.upnpServiceController == null || CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory() == null || !iUpnpDevice.equals(CastDeviceListActivity.upnpServiceController.getSelectedContentDirectory())) {
            return;
        }
        CastDeviceListActivity.upnpServiceController.setSelectedContentDirectory(null);
    }
}
