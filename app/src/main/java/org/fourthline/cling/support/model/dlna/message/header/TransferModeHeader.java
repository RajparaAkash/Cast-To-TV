package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;


public class TransferModeHeader extends DLNAHeader<TransferModeHeader.Type> {


    public enum Type {
        Streaming,
        Interactive,
        Background
    }

    public TransferModeHeader() {
        setValue(Type.Interactive);
    }

    public TransferModeHeader(Type type) {
        setValue(type);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() != 0) {
            try {
                setValue(Type.valueOf(str));
                return;
            } catch (Exception unused) {
            }
        }
        throw new InvalidHeaderException("Invalid TransferMode header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
