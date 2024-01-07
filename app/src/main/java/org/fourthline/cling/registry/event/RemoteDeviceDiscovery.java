package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.RemoteDevice;


public class RemoteDeviceDiscovery extends DeviceDiscovery<RemoteDevice> {
    public RemoteDeviceDiscovery(RemoteDevice remoteDevice) {
        super(remoteDevice);
    }
}
