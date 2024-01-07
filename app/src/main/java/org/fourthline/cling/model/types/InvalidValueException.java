package org.fourthline.cling.model.types;


public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String str) {
        super(str);
    }

    public InvalidValueException(String str, Throwable th) {
        super(str, th);
    }
}
