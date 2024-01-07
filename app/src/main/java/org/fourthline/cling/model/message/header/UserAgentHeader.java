package org.fourthline.cling.model.message.header;


public class UserAgentHeader extends UpnpHeader<String> {
    public UserAgentHeader() {
    }

    public UserAgentHeader(String str) {
        setValue(str);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        setValue(str);
    }

    @Override
    public String getString() {
        return getValue();
    }
}
