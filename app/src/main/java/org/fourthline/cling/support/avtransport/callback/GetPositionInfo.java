package org.fourthline.cling.support.avtransport.callback;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.PositionInfo;

import java.util.logging.Logger;


public abstract class GetPositionInfo extends ActionCallback {
    private static Logger log = Logger.getLogger(GetPositionInfo.class.getName());

    public abstract void received(ActionInvocation actionInvocation, PositionInfo positionInfo);

    public GetPositionInfo(Service service) {
        this(new UnsignedIntegerFourBytes(0L), service);
    }

    public GetPositionInfo(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service) {
        super(new ActionInvocation(service.getAction("GetPositionInfo")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
    }

    @Override
    public void success(ActionInvocation actionInvocation) {
        received(actionInvocation, new PositionInfo(actionInvocation.getOutputMap()));
    }
}
