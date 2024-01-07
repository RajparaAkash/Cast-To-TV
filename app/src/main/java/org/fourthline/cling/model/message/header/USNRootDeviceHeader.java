package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UDN;


public class USNRootDeviceHeader extends UpnpHeader<UDN> {
    public static final String ROOT_DEVICE_SUFFIX = "::upnp:rootdevice";

    public USNRootDeviceHeader() {
    }

    public USNRootDeviceHeader(UDN udn) {
        setValue(udn);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (!str.startsWith("uuid:") || !str.endsWith(ROOT_DEVICE_SUFFIX)) {
            throw new InvalidHeaderException("Invalid root device USN header value, must start with 'uuid:' and end with '::upnp:rootdevice' but is '" + str + "'");
        }
        setValue(new UDN(str.substring(5, str.length() - 17)));
    }

    @Override
    public String getString() {
        return getValue().toString() + ROOT_DEVICE_SUFFIX;
    }
}
