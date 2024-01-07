package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.RemoteDevice;


public class FailedRemoteDeviceDiscovery extends DeviceDiscovery<RemoteDevice> {
    protected Exception exception;

    public FailedRemoteDeviceDiscovery(RemoteDevice remoteDevice, Exception exc) {
        super(remoteDevice);
        this.exception = exc;
    }

    public Exception getException() {
        return this.exception;
    }
}
