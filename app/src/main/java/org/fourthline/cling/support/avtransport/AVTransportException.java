package org.fourthline.cling.support.avtransport;

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.types.ErrorCode;


public class AVTransportException extends ActionException {
    public AVTransportException(int i, String str) {
        super(i, str);
    }

    public AVTransportException(int i, String str, Throwable th) {
        super(i, str, th);
    }

    public AVTransportException(ErrorCode errorCode, String str) {
        super(errorCode, str);
    }

    public AVTransportException(ErrorCode errorCode) {
        super(errorCode);
    }

//    public AVTransportException(AVTransportErrorCode aVTransportErrorCode, String str) {
//        super(r0, aVTransportErrorCode.getDescription() + ". " + str + CastServerService.ROOT_DIR);
//        int code = aVTransportErrorCode.getCode();
//    }

    public AVTransportException(AVTransportErrorCode aVTransportErrorCode) {
        super(aVTransportErrorCode.getCode(), aVTransportErrorCode.getDescription());
    }
}
