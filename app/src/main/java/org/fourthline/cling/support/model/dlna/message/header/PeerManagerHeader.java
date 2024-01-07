package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.ServiceReference;
import org.fourthline.cling.model.message.header.InvalidHeaderException;


public class PeerManagerHeader extends DLNAHeader<ServiceReference> {
    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() != 0) {
            try {
                ServiceReference serviceReference = new ServiceReference(str);
                if (serviceReference.getUdn() != null && serviceReference.getServiceId() != null) {
                    setValue(serviceReference);
                    return;
                }
            } catch (Exception unused) {
            }
        }
        throw new InvalidHeaderException("Invalid PeerManager header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
