package org.fourthline.cling.model.message.header;

import java.net.MalformedURLException;
import java.net.URL;


public class LocationHeader extends UpnpHeader<URL> {
    public LocationHeader() {
    }

    public LocationHeader(URL url) {
        setValue(url);
    }

    public LocationHeader(String str) {
        setString(str);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(new URL(str));
        } catch (MalformedURLException e) {
            throw new InvalidHeaderException("Invalid URI: " + e.getMessage());
        }
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
