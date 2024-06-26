package org.fourthline.cling.support.messagebox;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.messagebox.model.Message;
import org.seamless.util.MimeType;


public abstract class AddMessage extends ActionCallback {
    protected final MimeType mimeType;

    public AddMessage(Service service, Message message) {
        super(new ActionInvocation(service.getAction("AddMessage")));
        MimeType valueOf = MimeType.valueOf("text/xml;charset=\"utf-8\"");
        this.mimeType = valueOf;
        getActionInvocation().setInput("MessageID", Integer.toString(message.getId()));
        getActionInvocation().setInput("MessageType", valueOf.toString());
        getActionInvocation().setInput("Message", message.toString());
    }
}
