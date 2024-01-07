package org.fourthline.cling.binding;


public class LocalServiceBindingException extends RuntimeException {
    public LocalServiceBindingException(String str) {
        super(str);
    }

    public LocalServiceBindingException(String str, Throwable th) {
        super(str, th);
    }
}
