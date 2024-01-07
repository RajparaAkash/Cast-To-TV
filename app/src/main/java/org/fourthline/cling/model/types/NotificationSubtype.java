package org.fourthline.cling.model.types;


public enum NotificationSubtype {
    ALIVE("ssdp:alive"),
    UPDATE("ssdp:update"),
    BYEBYE("ssdp:byebye"),
    ALL("ssdp:all"),
    DISCOVER("ssdp:discover"),
    PROPCHANGE("upnp:propchange");
    
    private String headerString;

    NotificationSubtype(String str) {
        this.headerString = str;
    }

    public String getHeaderString() {
        return this.headerString;
    }
}
