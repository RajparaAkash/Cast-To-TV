package org.fourthline.cling.protocol;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.profile.RemoteClientInfo;
import org.fourthline.cling.transport.RouterException;

import java.util.logging.Logger;


public abstract class ReceivingSync<IN extends StreamRequestMessage, OUT extends StreamResponseMessage> extends ReceivingAsync<IN> {
    private static final Logger log = Logger.getLogger(UpnpService.class.getName());
    protected OUT outputMessage;
    protected final RemoteClientInfo remoteClientInfo;

    protected abstract OUT executeSync() throws RouterException;

    public void responseException(Throwable th) {
    }

    public void responseSent(StreamResponseMessage streamResponseMessage) {
    }

    
    public ReceivingSync(UpnpService upnpService, IN in) {
        super(upnpService, in);
        this.remoteClientInfo = new RemoteClientInfo(in);
    }

    public OUT getOutputMessage() {
        return this.outputMessage;
    }

    @Override
    protected final void execute() throws RouterException {
        OUT executeSync = executeSync();
        this.outputMessage = executeSync;
        if (executeSync == null || getRemoteClientInfo().getExtraResponseHeaders().size() <= 0) {
            return;
        }
        Logger logger = log;
        logger.fine("Setting extra headers on response message: " + getRemoteClientInfo().getExtraResponseHeaders().size());
        this.outputMessage.getHeaders().putAll(getRemoteClientInfo().getExtraResponseHeaders());
    }

    public RemoteClientInfo getRemoteClientInfo() {
        return this.remoteClientInfo;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
