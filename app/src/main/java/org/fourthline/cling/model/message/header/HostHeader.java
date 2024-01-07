package org.fourthline.cling.model.message.header;

import org.fourthline.cling.model.Constants;
import org.fourthline.cling.model.types.HostPort;


public class HostHeader extends UpnpHeader<HostPort> {
    int port = Constants.UPNP_MULTICAST_PORT;
    String group = Constants.IPV4_UPNP_MULTICAST_GROUP;

    public HostHeader() {
        setValue(new HostPort(Constants.IPV4_UPNP_MULTICAST_GROUP, Constants.UPNP_MULTICAST_PORT));
    }

    public HostHeader(int i) {
        setValue(new HostPort(Constants.IPV4_UPNP_MULTICAST_GROUP, i));
    }

    public HostHeader(String str, int i) {
        setValue(new HostPort(str, i));
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.contains(":")) {
            try {
                this.port = Integer.valueOf(str.substring(str.indexOf(":") + 1)).intValue();
                String substring = str.substring(0, str.indexOf(":"));
                this.group = substring;
                setValue(new HostPort(substring, this.port));
                return;
            } catch (NumberFormatException e) {
                throw new InvalidHeaderException("Invalid HOST header value, can't parse port: " + str + " - " + e.getMessage());
            }
        }
        this.group = str;
        setValue(new HostPort(str, this.port));
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
