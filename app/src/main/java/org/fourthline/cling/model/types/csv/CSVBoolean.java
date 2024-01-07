package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;


public class CSVBoolean extends CSV<Boolean> {
    public CSVBoolean() {
    }

    public CSVBoolean(String str) throws InvalidValueException {
        super(str);
    }
}
