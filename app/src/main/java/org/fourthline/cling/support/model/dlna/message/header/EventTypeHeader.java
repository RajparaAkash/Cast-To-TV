package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;

import java.util.regex.Pattern;


public class EventTypeHeader extends DLNAHeader<String> {
    static final Pattern pattern = Pattern.compile("^[0-9]{4}$", 2);

    public EventTypeHeader() {
        setValue("0000");
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (pattern.matcher(str).matches()) {
            setValue(str);
            return;
        }
        throw new InvalidHeaderException("Invalid EventType header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
