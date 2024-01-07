package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;


public class CSVShort extends CSV<Short> {
    public CSVShort() {
    }

    public CSVShort(String str) throws InvalidValueException {
        super(str);
    }
}
