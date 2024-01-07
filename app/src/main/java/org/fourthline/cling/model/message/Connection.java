package org.fourthline.cling.model.message;

import java.net.InetAddress;


public interface Connection {
    InetAddress getLocalAddress();

    InetAddress getRemoteAddress();

    boolean isOpen();
}
