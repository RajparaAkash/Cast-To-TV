package org.fourthline.cling.support.model;

import java.util.logging.Logger;


public enum Protocol {
    ALL("*"),
    HTTP_GET("http-get"),
    RTSP_RTP_UDP("rtsp-rtp-udp"),
    INTERNAL("internal"),
    IEC61883("iec61883"),
    XBMC_GET("xbmc-get"),
    OTHER("other");
    
    private static final Logger LOG = Logger.getLogger(Protocol.class.getName());
    private String protocolString;

    Protocol(String str) {
        this.protocolString = str;
    }

    @Override
    public String toString() {
        return this.protocolString;
    }

    public static Protocol value(String str) {
        Protocol[] values;
        for (Protocol protocol : values()) {
            if (protocol.toString().equals(str)) {
                return protocol;
            }
        }
        LOG.info("Unsupported OTHER protocol string: " + str);
        return OTHER;
    }
}
