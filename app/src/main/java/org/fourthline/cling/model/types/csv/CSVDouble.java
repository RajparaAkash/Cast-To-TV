package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;


public class CSVDouble extends CSV<Double> {
    public CSVDouble() {
    }

    public CSVDouble(String str) throws InvalidValueException {
        super(str);
    }
}
