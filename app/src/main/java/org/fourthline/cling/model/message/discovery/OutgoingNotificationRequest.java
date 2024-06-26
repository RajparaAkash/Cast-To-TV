package org.fourthline.cling.model.message.discovery;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.Location;
import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.message.OutgoingDatagramMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.model.message.header.HostHeader;
import org.fourthline.cling.model.message.header.LocationHeader;
import org.fourthline.cling.model.message.header.MaxAgeHeader;
import org.fourthline.cling.model.message.header.NTSHeader;
import org.fourthline.cling.model.message.header.ServerHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.types.NotificationSubtype;


public abstract class OutgoingNotificationRequest extends OutgoingDatagramMessage<UpnpRequest> {
    private NotificationSubtype type;

    
    public OutgoingNotificationRequest(Location location, LocalDevice localDevice, NotificationSubtype notificationSubtype) {
        super(new UpnpRequest(UpnpRequest.Method.NOTIFY), ModelUtil.getInetAddressByName(Constants.IPV4_UPNP_MULTICAST_GROUP), Constants.UPNP_MULTICAST_PORT);
        this.type = notificationSubtype;
        getHeaders().add(UpnpHeader.Type.MAX_AGE, new MaxAgeHeader(localDevice.getIdentity().getMaxAgeSeconds()));
        getHeaders().add(UpnpHeader.Type.LOCATION, new LocationHeader(location.getURL()));
        getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        getHeaders().add(UpnpHeader.Type.HOST, new HostHeader());
        getHeaders().add(UpnpHeader.Type.NTS, new NTSHeader(notificationSubtype));
    }

    public NotificationSubtype getType() {
        return this.type;
    }
}
