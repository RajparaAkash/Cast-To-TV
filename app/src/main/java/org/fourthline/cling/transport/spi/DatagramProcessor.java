package org.fourthline.cling.transport.spi;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;

import java.net.DatagramPacket;
import java.net.InetAddress;


public interface DatagramProcessor {
    IncomingDatagramMessage read(InetAddress inetAddress, DatagramPacket datagramPacket) throws UnsupportedDataException;

    DatagramPacket write(OutgoingDatagramMessage outgoingDatagramMessage) throws UnsupportedDataException;
}
