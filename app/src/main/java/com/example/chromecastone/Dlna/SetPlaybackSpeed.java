package com.example.chromecastone.Dlna;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.model.Channel;

import java.util.logging.Logger;

public abstract class SetPlaybackSpeed extends ActionCallback {
    private static Logger log = Logger.getLogger(SetPlaybackSpeed.class.getName());

    public SetPlaybackSpeed(Service service, String str) {
        this(new UnsignedIntegerFourBytes(0L), service, str);
    }

    public SetPlaybackSpeed(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Service service, String str) {
        super(new ActionInvocation(service.getAction("SetCurrentSpeed")));
        getActionInvocation().setInput("InstanceID", unsignedIntegerFourBytes);
        getActionInvocation().setInput("Channel", Channel.Master.toString());
        getActionInvocation().setInput("DesiredCurrentSpeed", str);
    }

    @Override
    public void success(ActionInvocation actionInvocation) {
        log.fine("Executed successfully");
    }
}
