package org.fourthline.cling.transport.spi;

import org.fourthline.cling.transport.Router;

import java.net.NetworkInterface;


public interface MulticastReceiver<C extends MulticastReceiverConfiguration> extends Runnable {
    C getConfiguration();

    void init(NetworkInterface networkInterface, Router router, NetworkAddressFactory networkAddressFactory, DatagramProcessor datagramProcessor) throws InitializationException;

    void stop();
}
