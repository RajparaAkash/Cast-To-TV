package org.fourthline.cling.transport.impl.jetty;

import org.fourthline.cling.transport.spi.AbstractStreamClientConfiguration;

import java.util.concurrent.ExecutorService;


public class StreamClientConfigurationImpl extends AbstractStreamClientConfiguration {
    public int getRequestRetryCount() {
        return 0;
    }

    public StreamClientConfigurationImpl(ExecutorService executorService) {
        super(executorService);
    }

    public StreamClientConfigurationImpl(ExecutorService executorService, int i) {
        super(executorService, i);
    }
}
