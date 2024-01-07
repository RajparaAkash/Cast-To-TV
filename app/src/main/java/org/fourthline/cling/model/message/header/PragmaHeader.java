package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.PragmaType;


public class PragmaHeader extends UpnpHeader<PragmaType> {
    public PragmaHeader() {
    }

    public PragmaHeader(PragmaType pragmaType) {
        setValue(pragmaType);
    }

    public PragmaHeader(String str) {
        setString(str);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        try {
            setValue(PragmaType.valueOf(str));
        } catch (InvalidValueException e) {
            throw new InvalidHeaderException("Invalid Range Header: " + e.getMessage());
        }
    }

    @Override
    public String getString() {
        return getValue().getString();
    }
}
