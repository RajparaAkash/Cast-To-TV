package org.fourthline.cling.android;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.registry.Registry;


public interface AndroidUpnpService {
    UpnpService get();

    UpnpServiceConfiguration getConfiguration();

    ControlPoint getControlPoint();

    Registry getRegistry();
}
