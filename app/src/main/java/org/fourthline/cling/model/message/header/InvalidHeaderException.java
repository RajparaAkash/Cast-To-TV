package org.fourthline.cling.model.message.header;


public class InvalidHeaderException extends RuntimeException {
    public InvalidHeaderException(String str) {
        super(str);
    }
}
