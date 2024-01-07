package org.fourthline.cling.protocol.async;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.NotificationSubtype;
import org.fourthline.cling.transport.RouterException;

import java.util.logging.Logger;


public class SendingNotificationAlive extends SendingNotification {
    private static final Logger log = Logger.getLogger(SendingNotification.class.getName());

    public SendingNotificationAlive(UpnpService upnpService, LocalDevice localDevice) {
        super(upnpService, localDevice);
    }

    
    @Override
    public void execute() throws RouterException {
        Logger logger = log;
        logger.fine("Sending alive messages (" + getBulkRepeat() + " times) for: " + getDevice());
        super.execute();
    }

    @Override
    protected NotificationSubtype getNotificationSubtype() {
        return NotificationSubtype.ALIVE;
    }
}
