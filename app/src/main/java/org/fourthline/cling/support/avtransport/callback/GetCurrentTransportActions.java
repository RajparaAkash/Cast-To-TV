package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.TransportAction;

import java.util.logging.Logger;


public abstract class GetCurrentTransportActions extends ActionCallback {
    private static Logger log = Logger.getLogger(GetCurrentTransportActions.class.getName());

    public abstract void received(ActionInvocation actionInvocation, TransportAction[] transportActionArr);

    public GetCurrentTransportActions(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetCurrentTransportActions(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        super(new ActionInvocation(service.getAction("GetCurrentTransportActions")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
    }

    @Override
    public void success(ActionInvocation actionInvocation) {
        received(actionInvocation, TransportAction.valueOfCommaSeparatedList((String) actionInvocation.getOutput("Actions").getValue()));
    }
}
