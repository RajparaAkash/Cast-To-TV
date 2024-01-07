package org.fourthline.cling.transport.impl.jetty;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.transport.spi.AbstractStreamClient;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamClient;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


public class StreamClientImpl extends AbstractStreamClient {
    private static final Logger log = Logger.getLogger(StreamClient.class.getName());
    protected final StreamClientConfigurationImpl configuration;

    @Override
    protected boolean logExecutionException(Throwable th) {
        return false;
    }

    public StreamClientImpl(StreamClientConfigurationImpl streamClientConfigurationImpl) throws InitializationException {
        this.configuration = streamClientConfigurationImpl;
        log.info("Starting Jetty HttpClient...");
    }

    @Override
    public StreamClientConfigurationImpl getConfiguration() {
        return this.configuration;
    }


    @Override
    protected void abort(Object o) {

    }

    @Override
    protected Callable<StreamResponseMessage> createCallable(StreamRequestMessage streamRequestMessage, Object o) {
        return null;
    }

    @Override
    protected Object createRequest(StreamRequestMessage streamRequestMessage) {
        return null;
    }

    @Override
    public void stop() {
        try {
//            this.client.stop();
        } catch (Exception e) {
            Logger logger = log;
            logger.info("Error stopping HTTP client: " + e);
        }
    }
}
