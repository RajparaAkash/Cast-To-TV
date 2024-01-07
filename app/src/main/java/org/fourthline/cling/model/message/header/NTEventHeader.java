package org.fourthline.cling.model.message.header;

import java.util.Locale;


public class NTEventHeader extends UpnpHeader<String> {
    public NTEventHeader() {
        setValue("upnp:event");
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.toLowerCase(Locale.ROOT).equals(getValue())) {
            return;
        }
        throw new InvalidHeaderException("Invalid event NT header value: " + str);
    }

    @Override
    public String getString() {
        return getValue();
    }
}
