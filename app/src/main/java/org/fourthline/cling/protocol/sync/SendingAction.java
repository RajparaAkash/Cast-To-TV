package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionCancelledException;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;
import org.fourthline.cling.model.message.control.OutgoingActionRequestMessage;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.protocol.SendingSync;
import org.fourthline.cling.transport.RouterException;
import org.seamless.util.Exceptions;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SendingAction extends SendingSync<OutgoingActionRequestMessage, IncomingActionResponseMessage> {
    private static final Logger log = Logger.getLogger(SendingAction.class.getName());
    protected final ActionInvocation actionInvocation;

    public SendingAction(UpnpService upnpService, ActionInvocation actionInvocation, URL url) {
        super(upnpService, new OutgoingActionRequestMessage(actionInvocation, url));
        this.actionInvocation = actionInvocation;
    }

    
    @Override
    public IncomingActionResponseMessage executeSync() throws RouterException {
        return invokeRemote(getInputMessage());
    }

    protected IncomingActionResponseMessage invokeRemote(OutgoingActionRequestMessage outgoingActionRequestMessage) throws RouterException {
        Device device = this.actionInvocation.getAction().getService().getDevice();
        Logger logger = log;
        logger.fine("Sending outgoing action call '" + this.actionInvocation.getAction().getName() + "' to remote service of: " + device);
        IncomingActionResponseMessage incomingActionResponseMessage = null;
        try {
            StreamResponseMessage sendRemoteRequest = sendRemoteRequest(outgoingActionRequestMessage);
            if (sendRemoteRequest == null) {
                logger.fine("No connection or no no response received, returning null");
                this.actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Connection error or no response received"));
                return null;
            }
            IncomingActionResponseMessage incomingActionResponseMessage2 = new IncomingActionResponseMessage(sendRemoteRequest);
            try {
                if (incomingActionResponseMessage2.isFailedNonRecoverable()) {
                    logger.fine("Response was a non-recoverable failure: " + incomingActionResponseMessage2);
                    ErrorCode errorCode = ErrorCode.ACTION_FAILED;
                    throw new ActionException(errorCode, "Non-recoverable remote execution failure: " + incomingActionResponseMessage2.getOperation().getResponseDetails());
                }
                if (incomingActionResponseMessage2.isFailedRecoverable()) {
                    handleResponseFailure(incomingActionResponseMessage2);
                } else {
                    handleResponse(incomingActionResponseMessage2);
                }
                return incomingActionResponseMessage2;
            } catch (ActionException e) {
                e = e;
                incomingActionResponseMessage = incomingActionResponseMessage2;
                Logger logger2 = log;
                logger2.fine("Remote action invocation failed, returning Internal Server Error message: " + e.getMessage());
                this.actionInvocation.setFailure(e);
                return (incomingActionResponseMessage == null || !incomingActionResponseMessage.getOperation().isFailed()) ? new IncomingActionResponseMessage(new UpnpResponse(UpnpResponse.Status.INTERNAL_SERVER_ERROR)) : incomingActionResponseMessage;
            }
        } catch (ActionException e2) {
//            e = e2;
        }
        return incomingActionResponseMessage;
    }

    protected StreamResponseMessage sendRemoteRequest(OutgoingActionRequestMessage outgoingActionRequestMessage) throws ActionException, RouterException {
        try {
            Logger logger = log;
            logger.fine("Writing SOAP request body of: " + outgoingActionRequestMessage);
            getUpnpService().getConfiguration().getSoapActionProcessor().writeBody(outgoingActionRequestMessage, this.actionInvocation);
            logger.fine("Sending SOAP body of message as stream to remote device");
            return getUpnpService().getRouter().send(outgoingActionRequestMessage);
        } catch (UnsupportedDataException e) {
            Logger logger2 = log;
            if (logger2.isLoggable(Level.FINE)) {
                logger2.fine("Error writing SOAP body: " + e);
                logger2.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
            }
            ErrorCode errorCode = ErrorCode.ACTION_FAILED;
            throw new ActionException(errorCode, "Error writing request message. " + e.getMessage());
        } catch (RouterException e2) {
            Throwable unwrap = Exceptions.unwrap(e2);
            if (unwrap instanceof InterruptedException) {
                Logger logger3 = log;
                if (logger3.isLoggable(Level.FINE)) {
                    logger3.fine("Sending action request message was interrupted: " + unwrap);
                }
                throw new ActionCancelledException((InterruptedException) unwrap);
            }
            throw e2;
        }
    }

    protected void handleResponse(IncomingActionResponseMessage incomingActionResponseMessage) throws ActionException {
        try {
            Logger logger = log;
            logger.fine("Received response for outgoing call, reading SOAP response body: " + incomingActionResponseMessage);
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(incomingActionResponseMessage, this.actionInvocation);
        } catch (UnsupportedDataException e) {
            Logger logger2 = log;
            logger2.fine("Error reading SOAP body: " + e);
            logger2.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
            ErrorCode errorCode = ErrorCode.ACTION_FAILED;
            throw new ActionException(errorCode, "Error reading SOAP response message. " + e.getMessage(), false);
        }
    }

    protected void handleResponseFailure(IncomingActionResponseMessage incomingActionResponseMessage) throws ActionException {
        try {
            log.fine("Received response with Internal Server Error, reading SOAP failure message");
            getUpnpService().getConfiguration().getSoapActionProcessor().readBody(incomingActionResponseMessage, this.actionInvocation);
        } catch (UnsupportedDataException e) {
            Logger logger = log;
            logger.fine("Error reading SOAP body: " + e);
            logger.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(e));
            ErrorCode errorCode = ErrorCode.ACTION_FAILED;
            throw new ActionException(errorCode, "Error reading SOAP response failure message. " + e.getMessage(), false);
        }
    }
}
