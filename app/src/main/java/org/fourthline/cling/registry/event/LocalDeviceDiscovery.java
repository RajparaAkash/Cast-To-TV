package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.LocalDevice;


public class LocalDeviceDiscovery extends DeviceDiscovery<LocalDevice> {
    public LocalDeviceDiscovery(LocalDevice localDevice) {
        super(localDevice);
    }
}
