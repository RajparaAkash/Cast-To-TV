package org.fourthline.cling.model.message.header;


public class AVClientInfoHeader extends UpnpHeader<String> {
    public AVClientInfoHeader() {
    }

    public AVClientInfoHeader(String str) {
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
