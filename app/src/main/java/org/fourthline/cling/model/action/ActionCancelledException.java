package org.fourthline.cling.model.action;

import org.fourthline.cling.model.types.ErrorCode;


public class ActionCancelledException extends ActionException {
    public ActionCancelledException(InterruptedException interruptedException) {
        super(ErrorCode.ACTION_FAILED, "Action execution interrupted", interruptedException);
    }
}
