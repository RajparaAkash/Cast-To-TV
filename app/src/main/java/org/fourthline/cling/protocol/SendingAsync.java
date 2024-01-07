package org.fourthline.cling.protocol;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.transport.RouterException;
import org.seamless.util.Exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class SendingAsync implements Runnable {
    private static final Logger log = Logger.getLogger(UpnpService.class.getName());
    private final UpnpService upnpService;

    protected abstract void execute() throws RouterException;

    
    public SendingAsync(UpnpService upnpService) {
        this.upnpService = upnpService;
    }

    public UpnpService getUpnpService() {
        return this.upnpService;
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (Exception e) {
            Throwable unwrap = Exceptions.unwrap(e);
            if (unwrap instanceof InterruptedException) {
                Logger logger = log;
                Level level = Level.INFO;
                logger.log(level, "Interrupted protocol '" + getClass().getSimpleName() + "': " + e, unwrap);
                return;
            }
            throw new RuntimeException("Fatal error while executing protocol '" + getClass().getSimpleName() + "': " + e, e);
        }
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
