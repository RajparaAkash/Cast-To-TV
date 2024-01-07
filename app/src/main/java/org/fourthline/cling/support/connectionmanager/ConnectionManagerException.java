package org.fourthline.cling.support.connectionmanager;

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;

public class ConnectionManagerException extends ActionException {

    public ConnectionManagerException(int errorCode, String message) {
        super(errorCode, message);
    }

    public ConnectionManagerException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public ConnectionManagerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ConnectionManagerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConnectionManagerException(ConnectionManagerErrorCode errorCode, String message) {
        super(errorCode.getCode(), errorCode.getDescription() + ". " + message + ".");
    }

    public ConnectionManagerException(ConnectionManagerErrorCode errorCode) {
        super(errorCode.getCode(), errorCode.getDescription());
    }
}