package org.fourthline.cling.model.resource;

import org.fourthline.cling.model.meta.LocalDevice;

import java.net.URI;


public class DeviceDescriptorResource extends Resource<LocalDevice> {
    public DeviceDescriptorResource(URI uri, LocalDevice localDevice) {
        super(uri, localDevice);
    }
}
