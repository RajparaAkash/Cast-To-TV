package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerOneByte;


public class CSVUnsignedIntegerOneByte extends CSV<UnsignedIntegerOneByte> {
    public CSVUnsignedIntegerOneByte() {
    }

    public CSVUnsignedIntegerOneByte(String str) throws InvalidValueException {
        super(str);
    }
}
