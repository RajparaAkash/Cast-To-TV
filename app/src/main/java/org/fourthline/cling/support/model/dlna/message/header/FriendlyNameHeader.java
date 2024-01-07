package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;


public class FriendlyNameHeader extends DLNAHeader<String> {
    public FriendlyNameHeader() {
        setValue("");
    }

    public FriendlyNameHeader(String str) {
        setValue(str);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() != 0) {
            setValue(str);
            return;
        }
        throw new InvalidHeaderException("Invalid GetAvailableSeekRange header value: " + str);
    }

    @Override
    public String getString() {
        return getValue();
    }
}
