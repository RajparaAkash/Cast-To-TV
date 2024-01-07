package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.NotificationSubtype;


public class STAllHeader extends UpnpHeader<NotificationSubtype> {
    public STAllHeader() {
        setValue(NotificationSubtype.ALL);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.equals(NotificationSubtype.ALL.getHeaderString())) {
            return;
        }
        throw new InvalidHeaderException("Invalid ST header value (not " + NotificationSubtype.ALL + "): " + str);
    }

    @Override
    public String getString() {
        return getValue().getHeaderString();
    }
}
