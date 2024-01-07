package org.fourthline.cling.transport.spi;

import java.net.InetAddress;


public interface MulticastReceiverConfiguration {
    InetAddress getGroup();

    int getMaxDatagramBytes();

    int getPort();
}
