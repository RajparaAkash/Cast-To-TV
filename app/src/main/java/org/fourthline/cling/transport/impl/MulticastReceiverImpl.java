package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.DatagramProcessor;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.MulticastReceiver;
import org.fourthline.cling.transport.spi.NetworkAddressFactory;

import java.net.DatagramPacket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.logging.Logger;


public class MulticastReceiverImpl implements MulticastReceiver<MulticastReceiverConfigurationImpl> {
    private static Logger log = Logger.getLogger(MulticastReceiver.class.getName());
    protected final MulticastReceiverConfigurationImpl configuration;
    protected DatagramProcessor datagramProcessor;
    protected InetSocketAddress multicastAddress;
    protected NetworkInterface multicastInterface;
    protected NetworkAddressFactory networkAddressFactory;
    protected Router router;
    protected MulticastSocket socket;

    public MulticastReceiverImpl(MulticastReceiverConfigurationImpl multicastReceiverConfigurationImpl) {
        this.configuration = multicastReceiverConfigurationImpl;
    }

    
    @Override
    public MulticastReceiverConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override
    public synchronized void init(NetworkInterface networkInterface, Router router, NetworkAddressFactory networkAddressFactory, DatagramProcessor datagramProcessor) throws InitializationException {
        this.router = router;
        this.networkAddressFactory = networkAddressFactory;
        this.datagramProcessor = datagramProcessor;
        this.multicastInterface = networkInterface;
        try {
            Logger logger = log;
            logger.info("Creating wildcard socket (for receiving multicast datagrams) on port: " + this.configuration.getPort());
            this.multicastAddress = new InetSocketAddress(this.configuration.getGroup(), this.configuration.getPort());
            MulticastSocket multicastSocket = new MulticastSocket(this.configuration.getPort());
            this.socket = multicastSocket;
            multicastSocket.setReuseAddress(true);
            this.socket.setReceiveBufferSize(32768);
            Logger logger2 = log;
            logger2.info("Joining multicast group: " + this.multicastAddress + " on network interface: " + this.multicastInterface.getDisplayName());
            this.socket.joinGroup(this.multicastAddress, this.multicastInterface);
        } catch (Exception e) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + e);
        }
    }

    @Override
    public synchronized void stop() {
        MulticastSocket multicastSocket = this.socket;
        if (multicastSocket != null && !multicastSocket.isClosed()) {
            try {
                log.fine("Leaving multicast group");
                this.socket.leaveGroup(this.multicastAddress, this.multicastInterface);
            } catch (Exception e) {
                Logger logger = log;
                logger.fine("Could not leave multicast group: " + e);
            }
            this.socket.close();
        }
    }

    @Override
    public void run() {
        Logger logger = log;
        logger.fine("Entering blocking receiving loop, listening for UDP datagrams on: " + this.socket.getLocalAddress());
        while (true) {
            try {
                int maxDatagramBytes = getConfiguration().getMaxDatagramBytes();
                DatagramPacket datagramPacket = new DatagramPacket(new byte[maxDatagramBytes], maxDatagramBytes);
                this.socket.receive(datagramPacket);
                InetAddress localAddress = this.networkAddressFactory.getLocalAddress(this.multicastInterface, this.multicastAddress.getAddress() instanceof Inet6Address, datagramPacket.getAddress());
                Logger logger2 = log;
                logger2.fine("UDP datagram received from: " + datagramPacket.getAddress().getHostAddress() + ":" + datagramPacket.getPort() + " on local interface: " + this.multicastInterface.getDisplayName() + " and address: " + localAddress.getHostAddress());
                this.router.received(this.datagramProcessor.read(localAddress, datagramPacket));
            } catch (SocketException unused) {
                log.fine("Socket closed");
                try {
                    if (this.socket.isClosed()) {
                        return;
                    }
                    log.fine("Closing multicast socket");
                    this.socket.close();
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (UnsupportedDataException e2) {
                Logger logger3 = log;
                logger3.info("Could not read datagram: " + e2.getMessage());
            } catch (Exception e3) {
                throw new RuntimeException(e3);
            }
        }
    }
}
