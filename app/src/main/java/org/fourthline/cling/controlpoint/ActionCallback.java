package org.fourthline.cling.controlpoint;

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.protocol.sync.SendingAction;


public abstract class ActionCallback implements Runnable {
    protected final ActionInvocation actionInvocation;
    protected ControlPoint controlPoint;

    public abstract void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str);

    public abstract void success(ActionInvocation actionInvocation);

    
    public static final class Default extends ActionCallback {
        @Override
        public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
        }

        @Override
        public void success(ActionInvocation actionInvocation) {
        }

        public Default(ActionInvocation actionInvocation, ControlPoint controlPoint) {
            super(actionInvocation, controlPoint);
        }
    }

    
    public ActionCallback(ActionInvocation actionInvocation, ControlPoint controlPoint) {
        this.actionInvocation = actionInvocation;
        this.controlPoint = controlPoint;
    }

    
    public ActionCallback(ActionInvocation actionInvocation) {
        this.actionInvocation = actionInvocation;
    }

    public ActionInvocation getActionInvocation() {
        return this.actionInvocation;
    }

    public synchronized ControlPoint getControlPoint() {
        return this.controlPoint;
    }

    public synchronized ActionCallback setControlPoint(ControlPoint controlPoint) {
        this.controlPoint = controlPoint;
        return this;
    }

    @Override
    public void run() {
        Service service = this.actionInvocation.getAction().getService();
        if (service instanceof LocalService) {
            ((LocalService) service).getExecutor(this.actionInvocation.getAction()).execute(this.actionInvocation);
            if (this.actionInvocation.getFailure() != null) {
                failure(this.actionInvocation, null);
            } else {
                success(this.actionInvocation);
            }
        } else if (service instanceof RemoteService) {
            if (getControlPoint() == null) {
                throw new IllegalStateException("Callback must be executed through ControlPoint");
            }
            RemoteService remoteService = (RemoteService) service;
            try {
                SendingAction createSendingAction = getControlPoint().getProtocolFactory().createSendingAction(this.actionInvocation, remoteService.getDevice().normalizeURI(remoteService.getControlURI()));
                createSendingAction.run();
                IncomingActionResponseMessage outputMessage = createSendingAction.getOutputMessage();
                if (outputMessage == null) {
                    failure(this.actionInvocation, null);
                } else if (outputMessage.getOperation().isFailed()) {
                    failure(this.actionInvocation, outputMessage.getOperation());
                } else {
                    success(this.actionInvocation);
                }
            } catch (IllegalArgumentException unused) {
                ActionInvocation actionInvocation = this.actionInvocation;
                failure(actionInvocation, null, "bad control URL: " + remoteService.getControlURI());
            }
        }
    }

    protected String createDefaultFailureMessage(ActionInvocation actionInvocation, UpnpResponse upnpResponse) {
        ActionException failure = null;
        String str = "Error: ";
        if (actionInvocation.getFailure() != null) {
            str = "Error: " + failure.getMessage();
        }
        if (upnpResponse != null) {
            return str + " (HTTP response was: " + upnpResponse.getResponseDetails() + ")";
        }
        return str;
    }

    
    public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse) {
        failure(actionInvocation, upnpResponse, createDefaultFailureMessage(actionInvocation, upnpResponse));
    }

    public String toString() {
        return "(ActionCallback) " + this.actionInvocation;
    }
}
