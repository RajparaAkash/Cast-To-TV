package org.fourthline.cling.model.message.header;


public class EXTHeader extends UpnpHeader<String> {
    public static final String DEFAULT_VALUE = "";

    public EXTHeader() {
        setValue("");
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str == null || str.length() <= 0) {
            return;
        }
        throw new InvalidHeaderException("Invalid EXT header, it has no value: " + str);
    }

    @Override
    public String getString() {
        return getValue();
    }
}
