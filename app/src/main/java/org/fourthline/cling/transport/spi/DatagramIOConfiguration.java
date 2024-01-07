package org.fourthline.cling.transport.spi;


public interface DatagramIOConfiguration {
    int getMaxDatagramBytes();

    int getTimeToLive();
}
