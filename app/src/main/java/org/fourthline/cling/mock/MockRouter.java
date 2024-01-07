package org.fourthline.cling.mock;

import com.example.chromecastone.CastServer.CastServerService;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.NetworkAddress;
import org.fourthline.cling.model.message.IncomingDatagramMessage;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.RouterException;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.UpnpStream;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Alternative;

@Alternative

public class MockRouter implements Router {
    protected UpnpServiceConfiguration configuration;
    protected ProtocolFactory protocolFactory;
    public int counter = -1;
    public List<IncomingDatagramMessage> incomingDatagramMessages = new ArrayList();
    public List<OutgoingDatagramMessage> outgoingDatagramMessages = new ArrayList();
    public List<UpnpStream> receivedUpnpStreams = new ArrayList();
    public List<StreamRequestMessage> sentStreamRequestMessages = new ArrayList();
    public List<byte[]> broadcastedBytes = new ArrayList();

    @Override
    public boolean disable() throws RouterException {
        return false;
    }

    @Override
    public boolean enable() throws RouterException {
        return false;
    }

    public StreamResponseMessage getStreamResponseMessage(StreamRequestMessage streamRequestMessage) {
        return null;
    }

    public StreamResponseMessage[] getStreamResponseMessages() {
        return null;
    }

    @Override
    public void handleStartFailure(InitializationException initializationException) throws InitializationException {
    }

    @Override
    public boolean isEnabled() throws RouterException {
        return false;
    }

    @Override
    public void shutdown() throws RouterException {
    }

    public MockRouter(UpnpServiceConfiguration upnpServiceConfiguration, ProtocolFactory protocolFactory) {
        this.configuration = upnpServiceConfiguration;
        this.protocolFactory = protocolFactory;
    }

    @Override
    public UpnpServiceConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public ProtocolFactory getProtocolFactory() {
        return this.protocolFactory;
    }

    @Override
    public List<NetworkAddress> getActiveStreamServers(InetAddress inetAddress) throws RouterException {
        try {
            return Arrays.asList(new NetworkAddress(InetAddress.getByName(CastServerService.IP_ADDRESS), 0));
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void received(IncomingDatagramMessage incomingDatagramMessage) {
        this.incomingDatagramMessages.add(incomingDatagramMessage);
    }

    @Override
    public void received(UpnpStream upnpStream) {
        this.receivedUpnpStreams.add(upnpStream);
    }

    @Override
    public void send(OutgoingDatagramMessage outgoingDatagramMessage) throws RouterException {
        this.outgoingDatagramMessages.add(outgoingDatagramMessage);
    }

    @Override
    public StreamResponseMessage send(StreamRequestMessage streamRequestMessage) throws RouterException {
        this.sentStreamRequestMessages.add(streamRequestMessage);
        this.counter++;
        if (getStreamResponseMessages() != null) {
            return getStreamResponseMessages()[this.counter];
        }
        return getStreamResponseMessage(streamRequestMessage);
    }

    @Override
    public void broadcast(byte[] bArr) {
        this.broadcastedBytes.add(bArr);
    }

    public void resetStreamRequestMessageCounter() {
        this.counter = -1;
    }

    public List<IncomingDatagramMessage> getIncomingDatagramMessages() {
        return this.incomingDatagramMessages;
    }

    public List<OutgoingDatagramMessage> getOutgoingDatagramMessages() {
        return this.outgoingDatagramMessages;
    }

    public List<UpnpStream> getReceivedUpnpStreams() {
        return this.receivedUpnpStreams;
    }

    public List<StreamRequestMessage> getSentStreamRequestMessages() {
        return this.sentStreamRequestMessages;
    }

    public List<byte[]> getBroadcastedBytes() {
        return this.broadcastedBytes;
    }
}
