package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UDN;


public class UDNHeader extends UpnpHeader<UDN> {
    public UDNHeader() {
    }

    public UDNHeader(UDN udn) {
        setValue(udn);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (!str.startsWith("uuid:")) {
            throw new InvalidHeaderException("Invalid UDA header value, must start with 'uuid:': " + str);
        } else if (str.contains("::urn")) {
            throw new InvalidHeaderException("Invalid UDA header value, must not contain '::urn': " + str);
        } else {
            setValue(new UDN(str.substring(5)));
        }
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
