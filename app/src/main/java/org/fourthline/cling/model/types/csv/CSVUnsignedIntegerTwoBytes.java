package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;


public class CSVUnsignedIntegerTwoBytes extends CSV<UnsignedIntegerTwoBytes> {
    public CSVUnsignedIntegerTwoBytes() {
    }

    public CSVUnsignedIntegerTwoBytes(String str) throws InvalidValueException {
        super(str);
    }
}
