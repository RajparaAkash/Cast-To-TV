package org.fourthline.cling.controlpoint.event;

import org.fourthline.cling.controlpoint.ActionCallback;


public class ExecuteAction {
    protected ActionCallback callback;

    public ExecuteAction(ActionCallback actionCallback) {
        this.callback = actionCallback;
    }

    public ActionCallback getCallback() {
        return this.callback;
    }
}
