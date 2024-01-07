package com.example.chromecastone.Dlna.view;

import com.example.chromecastone.Dlna.model.upnp.IUpnpDevice;


public class DeviceDisplay {
    private final IUpnpDevice device;
    private final boolean extendedInformation;

    public DeviceDisplay(IUpnpDevice iUpnpDevice, boolean z) {
        this.device = iUpnpDevice;
        this.extendedInformation = z;
    }

    public DeviceDisplay(IUpnpDevice iUpnpDevice) {
        this(iUpnpDevice, false);
    }

    public IUpnpDevice getDevice() {
        return this.device;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.device.equals(((DeviceDisplay) obj).device);
    }

    public int hashCode() {
        IUpnpDevice iUpnpDevice = this.device;
        if (iUpnpDevice == null) {
            return 0;
        }
        return iUpnpDevice.hashCode();
    }

    public String toString() {
        if (this.device == null) {
            return "";
        }
        String friendlyName = getDevice().getFriendlyName();
        if (this.extendedInformation) {
            return friendlyName + getDevice().getExtendedInformation();
        }
        return friendlyName;
    }
}
