package org.fourthline.cling.model.message.control;

import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.header.SoapActionHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.QueryStateVariableAction;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.SoapActionType;


public class IncomingActionRequestMessage extends StreamRequestMessage implements ActionRequestMessage {
    private final Action action;
    private final String actionNamespace;

    public IncomingActionRequestMessage(StreamRequestMessage streamRequestMessage, LocalService localService) throws ActionException {
        super(streamRequestMessage);
        SoapActionHeader soapActionHeader = (SoapActionHeader) getHeaders().getFirstHeader(UpnpHeader.Type.SOAPACTION, SoapActionHeader.class);
        if (soapActionHeader == null) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Missing SOAP action header");
        }
        SoapActionType value = soapActionHeader.getValue();
        Action<LocalService> action = localService.getAction(value.getActionName());
        this.action = action;
        if (action == null) {
            ErrorCode errorCode = ErrorCode.INVALID_ACTION;
            throw new ActionException(errorCode, "Service doesn't implement action: " + value.getActionName());
        } else if (!QueryStateVariableAction.ACTION_NAME.equals(value.getActionName()) && !localService.getServiceType().implementsVersion(value.getServiceType())) {
            throw new ActionException(ErrorCode.INVALID_ACTION, "Service doesn't support the requested service version");
        } else {
            this.actionNamespace = value.getTypeString();
        }
    }

    public Action getAction() {
        return this.action;
    }

    @Override
    public String getActionNamespace() {
        return this.actionNamespace;
    }
}
