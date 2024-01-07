package org.fourthline.cling.model.profile;

import org.fourthline.cling.model.meta.DeviceDetails;


public interface DeviceDetailsProvider {
    DeviceDetails provide(RemoteClientInfo remoteClientInfo);
}
