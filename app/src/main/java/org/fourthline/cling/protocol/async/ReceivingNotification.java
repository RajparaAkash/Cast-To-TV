package org.fourthline.cling.protocol.async;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.discovery.IncomingNotificationRequest;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteDeviceIdentity;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.protocol.ReceivingAsync;
import org.fourthline.cling.protocol.RetrieveRemoteDescriptors;
import org.fourthline.cling.transport.RouterException;

import java.util.logging.Logger;


public class ReceivingNotification extends ReceivingAsync<IncomingNotificationRequest> {
    private static final Logger log = Logger.getLogger(ReceivingNotification.class.getName());

    public ReceivingNotification(UpnpService upnpService, IncomingDatagramMessage<UpnpRequest> incomingDatagramMessage) {
        super(upnpService, new IncomingNotificationRequest(incomingDatagramMessage));
    }

    @Override
    protected void execute() throws RouterException {
        UDN udn = getInputMessage().getUDN();
        if (udn == null) {
            Logger logger = log;
            logger.fine("Ignoring notification message without UDN: " + getInputMessage());
            return;
        }
        RemoteDeviceIdentity remoteDeviceIdentity = new RemoteDeviceIdentity(getInputMessage());
        Logger logger2 = log;
        logger2.fine("Received device notification: " + remoteDeviceIdentity);
        try {
            RemoteDevice remoteDevice = new RemoteDevice(remoteDeviceIdentity);
            if (getInputMessage().isAliveMessage()) {
                logger2.fine("Received device ALIVE advertisement, descriptor location is: " + remoteDeviceIdentity.getDescriptorURL());
                if (remoteDeviceIdentity.getDescriptorURL() == null) {
                    logger2.finer("Ignoring message without location URL header: " + getInputMessage());
                } else if (remoteDeviceIdentity.getMaxAgeSeconds() == null) {
                    logger2.finer("Ignoring message without max-age header: " + getInputMessage());
                } else if (getUpnpService().getRegistry().update(remoteDeviceIdentity)) {
                    logger2.finer("Remote device was already known: " + udn);
                } else {
                    getUpnpService().getConfiguration().getAsyncProtocolExecutor().execute(new RetrieveRemoteDescriptors(getUpnpService(), remoteDevice));
                }
            } else if (getInputMessage().isByeByeMessage()) {
                logger2.fine("Received device BYEBYE advertisement");
                if (getUpnpService().getRegistry().removeDevice(remoteDevice)) {
                    logger2.fine("Removed remote device from registry: " + remoteDevice);
                }
            } else {
                logger2.finer("Ignoring unknown notification message: " + getInputMessage());
            }
        } catch (ValidationException e) {
            Logger logger3 = log;
            logger3.warning("Validation errors of device during discovery: " + remoteDeviceIdentity);
            for (ValidationError validationError : e.getErrors()) {
                log.warning(validationError.toString());
            }
        }
    }
}
