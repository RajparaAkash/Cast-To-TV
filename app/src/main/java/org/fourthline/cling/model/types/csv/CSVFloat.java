package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;


public class CSVFloat extends CSV<Float> {
    public CSVFloat() {
    }

    public CSVFloat(String str) throws InvalidValueException {
        super(str);
    }
}
