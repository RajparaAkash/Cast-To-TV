package org.fourthline.cling.registry.event;

import org.fourthline.cling.model.meta.Device;


public class DeviceDiscovery<D extends Device> {
    protected D device;

    public DeviceDiscovery(D d) {
        this.device = d;
    }

    public D getDevice() {
        return this.device;
    }
}
