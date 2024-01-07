package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.NotificationSubtype;


public class NTSHeader extends UpnpHeader<NotificationSubtype> {
    public NTSHeader() {
    }

    public NTSHeader(NotificationSubtype notificationSubtype) {
        setValue(notificationSubtype);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        NotificationSubtype[] values = NotificationSubtype.values();
        int length = values.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            NotificationSubtype notificationSubtype = values[i];
            if (str.equals(notificationSubtype.getHeaderString())) {
                setValue(notificationSubtype);
                break;
            }
            i++;
        }
        if (getValue() != null) {
            return;
        }
        throw new InvalidHeaderException("Invalid NTS header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().getHeaderString();
    }
}
