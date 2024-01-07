package org.fourthline.cling.model.message.header;

import java.util.Locale;


public class RootDeviceHeader extends UpnpHeader<String> {
    public RootDeviceHeader() {
        setValue("upnp:rootdevice");
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.toLowerCase(Locale.ROOT).equals(getValue())) {
            return;
        }
        throw new InvalidHeaderException("Invalid root device NT header value: " + str);
    }

    @Override
    public String getString() {
        return getValue();
    }
}
