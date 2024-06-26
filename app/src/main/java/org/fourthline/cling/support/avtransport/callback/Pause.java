package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.logging.Logger;


public abstract class Pause extends ActionCallback {
    private static Logger log = Logger.getLogger(Pause.class.getName());

    protected Pause(ActionInvocation actionInvocation, ControlPoint controlPoint) {
        super(actionInvocation, controlPoint);
    }

    protected Pause(ActionInvocation actionInvocation) {
        super(actionInvocation);
    }

    public Pause(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public Pause(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        super(new ActionInvocation(service.getAction("Pause")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
    }

    @Override
    public void success(ActionInvocation actionInvocation) {
        log.fine("Execution successful");
    }
}
