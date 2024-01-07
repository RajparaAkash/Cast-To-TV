package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.transport.Router;

import java.net.DatagramPacket;
import java.net.InetAddress;


public interface DatagramIO<C extends DatagramIOConfiguration> extends Runnable {
    C getConfiguration();

    void init(InetAddress inetAddress, Router router, DatagramProcessor datagramProcessor) throws InitializationException;

    void send(DatagramPacket datagramPacket);

    void send(OutgoingDatagramMessage outgoingDatagramMessage);

    void stop();
}
