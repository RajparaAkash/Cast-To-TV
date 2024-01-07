package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;


public class CSVCharacter extends CSV<Character> {
    public CSVCharacter() {
    }

    public CSVCharacter(String str) throws InvalidValueException {
        super(str);
    }
}
