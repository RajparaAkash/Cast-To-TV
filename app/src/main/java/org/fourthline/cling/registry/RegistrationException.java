package org.fourthline.cling.registry;

import org.fourthline.cling.model.ValidationError;

import java.util.List;


public class RegistrationException extends RuntimeException {
    public List<ValidationError> errors;

    public RegistrationException(String str) {
        super(str);
    }

    public RegistrationException(String str, Throwable th) {
        super(str, th);
    }

    public RegistrationException(String str, List<ValidationError> list) {
        super(str);
        this.errors = list;
    }

    public List<ValidationError> getErrors() {
        return this.errors;
    }
}
