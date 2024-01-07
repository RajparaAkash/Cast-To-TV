package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

import java.util.Date;


public class CSVDate extends CSV<Date> {
    public CSVDate() {
    }

    public CSVDate(String str) throws InvalidValueException {
        super(str);
    }
}
