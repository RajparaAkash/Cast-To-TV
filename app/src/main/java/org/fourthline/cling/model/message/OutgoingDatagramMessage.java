package org.fourthline.cling.model.message;

import java.net.InetAddress;


public abstract class OutgoingDatagramMessage<O extends UpnpOperation> extends UpnpMessage<O> {
    private InetAddress destinationAddress;
    private int destinationPort;
    private UpnpHeaders headers;

    
    public OutgoingDatagramMessage(O o, InetAddress inetAddress, int i) {
        super(o);
        this.headers = new UpnpHeaders(false);
        this.destinationAddress = inetAddress;
        this.destinationPort = i;
    }

    protected OutgoingDatagramMessage(O o, BodyType bodyType, Object obj, InetAddress inetAddress, int i) {
        super(o, bodyType, obj);
        this.headers = new UpnpHeaders(false);
        this.destinationAddress = inetAddress;
        this.destinationPort = i;
    }

    public InetAddress getDestinationAddress() {
        return this.destinationAddress;
    }

    public int getDestinationPort() {
        return this.destinationPort;
    }

    @Override
    public UpnpHeaders getHeaders() {
        return this.headers;
    }
}
