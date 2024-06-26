package org.fourthline.cling.controlpoint;

import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.registry.Registry;

import java.util.concurrent.Future;


public interface ControlPoint {
    Future execute(ActionCallback actionCallback);

    void execute(SubscriptionCallback subscriptionCallback);

    UpnpServiceConfiguration getConfiguration();

    ProtocolFactory getProtocolFactory();

    Registry getRegistry();

    void search();

    void search(int i);

    void search(UpnpHeader upnpHeader);

    void search(UpnpHeader upnpHeader, int i);
}
