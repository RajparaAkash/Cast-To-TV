package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;


public class CSVUnsignedIntegerFourBytes extends CSV<UnsignedIntegerFourBytes> {
    public CSVUnsignedIntegerFourBytes() {
    }

    public CSVUnsignedIntegerFourBytes(String str) throws InvalidValueException {
        super(str);
    }
}
