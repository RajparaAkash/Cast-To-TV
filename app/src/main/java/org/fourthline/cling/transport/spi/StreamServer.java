package org.fourthline.cling.transport.spi;

import org.fourthline.cling.transport.Router;

import java.net.InetAddress;


public interface StreamServer<C extends StreamServerConfiguration> extends Runnable {
    C getConfiguration();

    int getPort();

    void init(InetAddress inetAddress, Router router) throws InitializationException;

    void stop();
}
