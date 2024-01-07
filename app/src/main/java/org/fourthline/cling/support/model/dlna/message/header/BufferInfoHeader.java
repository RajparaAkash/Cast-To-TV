package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.types.BufferInfoType;


public class BufferInfoHeader extends DLNAHeader<BufferInfoType> {
    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() != 0) {
            try {
                setValue(BufferInfoType.valueOf(str));
                return;
            } catch (Exception unused) {
            }
        }
        throw new InvalidHeaderException("Invalid BufferInfo header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().getString();
    }
}
