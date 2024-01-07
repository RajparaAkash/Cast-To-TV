package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.ServiceType;

import java.net.URI;


public class ServiceTypeHeader extends UpnpHeader<ServiceType> {
    public ServiceTypeHeader() {
    }

    public ServiceTypeHeader(URI uri) {
        setString(uri.toString());
    }

    public ServiceTypeHeader(ServiceType serviceType) {
        setValue(serviceType);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(ServiceType.valueOf(str));
        } catch (RuntimeException e) {
            throw new InvalidHeaderException("Invalid service type header value, " + e.getMessage());
        }
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
