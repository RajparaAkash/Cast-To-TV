package org.fourthline.cling.model.types.csv;

import org.fourthline.cling.model.types.InvalidValueException;

import java.net.URI;


public class CSVURI extends CSV<URI> {
    public CSVURI() {
    }

    public CSVURI(String str) throws InvalidValueException {
        super(str);
    }
}
