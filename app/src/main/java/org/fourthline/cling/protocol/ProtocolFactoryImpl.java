package org.fourthline.cling.protocol;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.Namespace;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.model.types.NamedServiceType;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.protocol.async.ReceivingNotification;
import org.fourthline.cling.protocol.async.ReceivingSearch;
import org.fourthline.cling.protocol.async.ReceivingSearchResponse;
import org.fourthline.cling.protocol.async.SendingNotificationAlive;
import org.fourthline.cling.protocol.async.SendingNotificationByebye;
import org.fourthline.cling.protocol.async.SendingSearch;
import org.fourthline.cling.protocol.sync.ReceivingAction;
import org.fourthline.cling.protocol.sync.ReceivingEvent;
import org.fourthline.cling.protocol.sync.ReceivingRetrieval;
import org.fourthline.cling.protocol.sync.ReceivingSubscribe;
import org.fourthline.cling.protocol.sync.ReceivingUnsubscribe;
import org.fourthline.cling.protocol.sync.SendingAction;
import org.fourthline.cling.protocol.sync.SendingEvent;
import org.fourthline.cling.protocol.sync.SendingRenewal;
import org.fourthline.cling.protocol.sync.SendingSubscribe;
import org.fourthline.cling.protocol.sync.SendingUnsubscribe;
import org.fourthline.cling.transport.RouterException;

import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped

public class ProtocolFactoryImpl implements ProtocolFactory {
    private static final Logger log = Logger.getLogger(ProtocolFactory.class.getName());
    protected final UpnpService upnpService;

    protected ProtocolFactoryImpl() {
        this.upnpService = null;
    }

    @Inject
    public ProtocolFactoryImpl(UpnpService upnpService) {
        Logger logger = log;
        logger.fine("Creating ProtocolFactory: " + getClass().getName());
        this.upnpService = upnpService;
    }

    @Override
    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override
    public ReceivingAsync createReceivingAsync(IncomingDatagramMessage incomingDatagramMessage) throws ProtocolCreationException {
        Logger logger = log;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Creating protocol for incoming asynchronous: " + incomingDatagramMessage);
        }
        if (incomingDatagramMessage.getOperation() instanceof UpnpRequest) {
            int i = AnonymousClass1.$SwitchMap$org$fourthline$cling$model$message$UpnpRequest$Method[((UpnpRequest) incomingDatagramMessage.getOperation()).getMethod().ordinal()];
            if (i == 1) {
                if (isByeBye(incomingDatagramMessage) || isSupportedServiceAdvertisement(incomingDatagramMessage)) {
                    return createReceivingNotification(incomingDatagramMessage);
                }
                return null;
            } else if (i == 2) {
                return createReceivingSearch(incomingDatagramMessage);
            }
        } else if (incomingDatagramMessage.getOperation() instanceof UpnpResponse) {
            if (isSupportedServiceAdvertisement(incomingDatagramMessage)) {
                return createReceivingSearchResponse(incomingDatagramMessage);
            }
            return null;
        }
        throw new ProtocolCreationException("Protocol for incoming datagram message not found: " + incomingDatagramMessage);
    }

    static class AnonymousClass1 {
        static final int[] $SwitchMap$org$fourthline$cling$model$message$UpnpRequest$Method;

        static {
            int[] iArr = new int[UpnpRequest.Method.values().length];
            $SwitchMap$org$fourthline$cling$model$message$UpnpRequest$Method = iArr;
            try {
                iArr[UpnpRequest.Method.NOTIFY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$fourthline$cling$model$message$UpnpRequest$Method[UpnpRequest.Method.MSEARCH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    protected ReceivingAsync createReceivingNotification(IncomingDatagramMessage<UpnpRequest> incomingDatagramMessage) {
        return new ReceivingNotification(getUpnpService(), incomingDatagramMessage);
    }

    protected ReceivingAsync createReceivingSearch(IncomingDatagramMessage<UpnpRequest> incomingDatagramMessage) {
        return new ReceivingSearch(getUpnpService(), incomingDatagramMessage);
    }

    protected ReceivingAsync createReceivingSearchResponse(IncomingDatagramMessage<UpnpResponse> incomingDatagramMessage) {
        return new ReceivingSearchResponse(getUpnpService(), incomingDatagramMessage);
    }

    protected boolean isByeBye(IncomingDatagramMessage incomingDatagramMessage) {
        String firstHeader = incomingDatagramMessage.getHeaders().getFirstHeader(UpnpHeader.Type.NTS.getHttpName());
        return firstHeader != null && firstHeader.equals(NotificationSubtype.BYEBYE.getHeaderString());
    }

    protected boolean isSupportedServiceAdvertisement(IncomingDatagramMessage incomingDatagramMessage) {
        ServiceType[] exclusiveServiceTypes = getUpnpService().getConfiguration().getExclusiveServiceTypes();
        if (exclusiveServiceTypes == null) {
            return false;
        }
        if (exclusiveServiceTypes.length == 0) {
            return true;
        }
        String firstHeader = incomingDatagramMessage.getHeaders().getFirstHeader(UpnpHeader.Type.USN.getHttpName());
        if (firstHeader == null) {
            return false;
        }
        try {
            NamedServiceType valueOf = NamedServiceType.valueOf(firstHeader);
            for (ServiceType serviceType : exclusiveServiceTypes) {
                if (valueOf.getServiceType().implementsVersion(serviceType)) {
                    return true;
                }
            }
        } catch (InvalidValueException unused) {
            log.finest("Not a named service type header value: " + firstHeader);
        }
        log.fine("Service advertisement not supported, dropping it: " + firstHeader);
        return false;
    }

    @Override
    public ReceivingSync createReceivingSync(StreamRequestMessage streamRequestMessage) throws ProtocolCreationException {
        Logger logger = log;
        logger.fine("Creating protocol for incoming synchronous: " + streamRequestMessage);
        if (streamRequestMessage.getOperation().getMethod().equals(UpnpRequest.Method.GET)) {
            return createReceivingRetrieval(streamRequestMessage);
        }
        if (getUpnpService().getConfiguration().getNamespace().isControlPath(streamRequestMessage.getUri())) {
            if (streamRequestMessage.getOperation().getMethod().equals(UpnpRequest.Method.POST)) {
                return createReceivingAction(streamRequestMessage);
            }
        } else if (getUpnpService().getConfiguration().getNamespace().isEventSubscriptionPath(streamRequestMessage.getUri())) {
            if (streamRequestMessage.getOperation().getMethod().equals(UpnpRequest.Method.SUBSCRIBE)) {
                return createReceivingSubscribe(streamRequestMessage);
            }
            if (streamRequestMessage.getOperation().getMethod().equals(UpnpRequest.Method.UNSUBSCRIBE)) {
                return createReceivingUnsubscribe(streamRequestMessage);
            }
        } else if (getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(streamRequestMessage.getUri())) {
            if (streamRequestMessage.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY)) {
                return createReceivingEvent(streamRequestMessage);
            }
        } else if (streamRequestMessage.getUri().getPath().contains("/event/cb")) {
            logger.warning("Fixing trailing garbage in event message path: " + streamRequestMessage.getUri().getPath());
            String uri = streamRequestMessage.getUri().toString();
            streamRequestMessage.setUri(URI.create(uri.substring(0, uri.indexOf(Namespace.CALLBACK_FILE) + 3)));
            if (getUpnpService().getConfiguration().getNamespace().isEventCallbackPath(streamRequestMessage.getUri()) && streamRequestMessage.getOperation().getMethod().equals(UpnpRequest.Method.NOTIFY)) {
                return createReceivingEvent(streamRequestMessage);
            }
        }
        throw new ProtocolCreationException("Protocol for message type not found: " + streamRequestMessage);
    }

    @Override
    public SendingNotificationAlive createSendingNotificationAlive(LocalDevice localDevice) {
        return new SendingNotificationAlive(getUpnpService(), localDevice);
    }

    @Override
    public SendingNotificationByebye createSendingNotificationByebye(LocalDevice localDevice) {
        return new SendingNotificationByebye(getUpnpService(), localDevice);
    }

    @Override
    public SendingSearch createSendingSearch(UpnpHeader upnpHeader, int i) {
        return new SendingSearch(getUpnpService(), upnpHeader, i);
    }

    @Override
    public SendingAction createSendingAction(ActionInvocation actionInvocation, URL url) {
        return new SendingAction(getUpnpService(), actionInvocation, url);
    }

    @Override
    public SendingSubscribe createSendingSubscribe(RemoteGENASubscription remoteGENASubscription) throws ProtocolCreationException {
        try {
            return new SendingSubscribe(getUpnpService(), remoteGENASubscription, getUpnpService().getRouter().getActiveStreamServers(remoteGENASubscription.getService().getDevice().getIdentity().getDiscoveredOnLocalAddress()));
        } catch (RouterException e) {
            throw new ProtocolCreationException("Failed to obtain local stream servers (for event callback URL creation) from router", e);
        }
    }

    @Override
    public SendingRenewal createSendingRenewal(RemoteGENASubscription remoteGENASubscription) {
        return new SendingRenewal(getUpnpService(), remoteGENASubscription);
    }

    @Override
    public SendingUnsubscribe createSendingUnsubscribe(RemoteGENASubscription remoteGENASubscription) {
        return new SendingUnsubscribe(getUpnpService(), remoteGENASubscription);
    }

    @Override
    public SendingEvent createSendingEvent(LocalGENASubscription localGENASubscription) {
        return new SendingEvent(getUpnpService(), localGENASubscription);
    }

    protected ReceivingRetrieval createReceivingRetrieval(StreamRequestMessage streamRequestMessage) {
        return new ReceivingRetrieval(getUpnpService(), streamRequestMessage);
    }

    protected ReceivingAction createReceivingAction(StreamRequestMessage streamRequestMessage) {
        return new ReceivingAction(getUpnpService(), streamRequestMessage);
    }

    protected ReceivingSubscribe createReceivingSubscribe(StreamRequestMessage streamRequestMessage) {
        return new ReceivingSubscribe(getUpnpService(), streamRequestMessage);
    }

    protected ReceivingUnsubscribe createReceivingUnsubscribe(StreamRequestMessage streamRequestMessage) {
        return new ReceivingUnsubscribe(getUpnpService(), streamRequestMessage);
    }

    protected ReceivingEvent createReceivingEvent(StreamRequestMessage streamRequestMessage) {
        return new ReceivingEvent(getUpnpService(), streamRequestMessage);
    }
}
