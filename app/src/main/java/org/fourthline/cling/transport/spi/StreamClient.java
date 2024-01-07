package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;


public interface StreamClient<C extends StreamClientConfiguration> {
    C getConfiguration();

    StreamResponseMessage sendRequest(StreamRequestMessage streamRequestMessage) throws InterruptedException;

    void stop();
}
