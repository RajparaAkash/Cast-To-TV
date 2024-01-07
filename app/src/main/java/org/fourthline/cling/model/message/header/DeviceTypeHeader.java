package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.DeviceType;

import java.net.URI;


public class DeviceTypeHeader extends UpnpHeader<DeviceType> {
    public DeviceTypeHeader() {
    }

    public DeviceTypeHeader(URI uri) {
        setString(uri.toString());
    }

    public DeviceTypeHeader(DeviceType deviceType) {
        setValue(deviceType);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(DeviceType.valueOf(str));
        } catch (RuntimeException e) {
            throw new InvalidHeaderException("Invalid device type header value, " + e.getMessage());
        }
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
