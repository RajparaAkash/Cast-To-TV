package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

import java.util.regex.Pattern;


public class WCTHeader extends DLNAHeader<Boolean> {
    static final Pattern pattern = Pattern.compile("^[01]{1}$", 2);

    public WCTHeader() {
        setValue(false);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (pattern.matcher(str).matches()) {
            setValue(Boolean.valueOf(str.equals("1")));
            return;
        }
        throw new InvalidHeaderException("Invalid SCID header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().booleanValue() ? "1" : "0";
    }
}
