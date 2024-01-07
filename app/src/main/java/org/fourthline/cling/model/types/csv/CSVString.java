package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;


public class CSVString extends CSV<String> {
    public CSVString() {
    }

    public CSVString(String str) throws InvalidValueException {
        super(str);
    }
}
