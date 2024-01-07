package org.fourthline.cling.model.action;

import org.fourthline.cling.model.meta.LocalService;


public interface ActionExecutor {
    void execute(ActionInvocation<LocalService> actionInvocation);
}
