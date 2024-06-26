package org.fourthline.cling.protocol.sync;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.gena.IncomingUnsubscribeRequestMessage;
import org.fourthline.cling.model.resource.ServiceEventSubscriptionResource;
import org.fourthline.cling.protocol.ReceivingSync;
import org.fourthline.cling.transport.RouterException;

import java.util.logging.Logger;


public class ReceivingUnsubscribe extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingUnsubscribe.class.getName());

    public ReceivingUnsubscribe(UpnpService upnpService, StreamRequestMessage streamRequestMessage) {
        super(upnpService, streamRequestMessage);
    }

    @Override
    protected StreamResponseMessage executeSync() throws RouterException {
        ServiceEventSubscriptionResource serviceEventSubscriptionResource = (ServiceEventSubscriptionResource) getUpnpService().getRegistry().getResource(ServiceEventSubscriptionResource.class, ((StreamRequestMessage) getInputMessage()).getUri());
        if (serviceEventSubscriptionResource == null) {
            Logger logger = log;
            logger.fine("No local resource found: " + getInputMessage());
            return null;
        }
        Logger logger2 = log;
        logger2.fine("Found local event subscription matching relative request URI: " + ((StreamRequestMessage) getInputMessage()).getUri());
        IncomingUnsubscribeRequestMessage incomingUnsubscribeRequestMessage = new IncomingUnsubscribeRequestMessage((StreamRequestMessage) getInputMessage(), serviceEventSubscriptionResource.getModel());
        if (incomingUnsubscribeRequestMessage.getSubscriptionId() != null && (incomingUnsubscribeRequestMessage.hasNotificationHeader() || incomingUnsubscribeRequestMessage.hasCallbackHeader())) {
            logger2.fine("Subscription ID and NT or Callback in unsubcribe request: " + getInputMessage());
            return new StreamResponseMessage(UpnpResponse.Status.BAD_REQUEST);
        }
        LocalGENASubscription localSubscription = getUpnpService().getRegistry().getLocalSubscription(incomingUnsubscribeRequestMessage.getSubscriptionId());
        if (localSubscription == null) {
            logger2.fine("Invalid subscription ID for unsubscribe request: " + getInputMessage());
            return new StreamResponseMessage(UpnpResponse.Status.PRECONDITION_FAILED);
        }
        logger2.fine("Unregistering subscription: " + localSubscription);
        if (getUpnpService().getRegistry().removeLocalSubscription(localSubscription)) {
            localSubscription.end(null);
        } else {
            logger2.fine("Subscription was already removed from registry");
        }
        return new StreamResponseMessage(UpnpResponse.Status.OK);
    }
}
