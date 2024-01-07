package org.fourthline.cling.transport;


public class RouterException extends Exception {
    public RouterException() {
    }

    public RouterException(String str) {
        super(str);
    }

    public RouterException(String str, Throwable th) {
        super(str, th);
    }

    public RouterException(Throwable th) {
        super(th);
    }
}
