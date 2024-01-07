package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;


public class MaxPrateHeader extends DLNAHeader<Long> {
    public MaxPrateHeader() {
        setValue(0L);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(Long.valueOf(Long.parseLong(str)));
        } catch (NumberFormatException unused) {
            throw new InvalidHeaderException("Invalid SCID header value: " + str);
        }
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
