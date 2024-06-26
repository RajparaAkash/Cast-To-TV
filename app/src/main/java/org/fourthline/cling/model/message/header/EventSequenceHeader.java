package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;


public class EventSequenceHeader extends UpnpHeader<UnsignedIntegerFourBytes> {
    public EventSequenceHeader() {
    }

    public EventSequenceHeader(long j) {
        setValue(new UnsignedIntegerFourBytes(j));
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (!"0".equals(str)) {
            while (str.startsWith("0")) {
                str = str.substring(1);
            }
        }
        try {
            setValue(new UnsignedIntegerFourBytes(str));
        } catch (NumberFormatException e) {
            throw new InvalidHeaderException("Invalid event sequence, " + e.getMessage());
        }
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
