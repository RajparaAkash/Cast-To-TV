package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.XMLUtil;
import org.fourthline.cling.model.message.gena.IncomingEventRequestMessage;
import org.fourthline.cling.transport.spi.GENAEventProcessor;
import org.seamless.xml.XmlPullParserUtils;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.inject.Alternative;

@Alternative

public class RecoveringGENAEventProcessorImpl extends PullGENAEventProcessorImpl {
    private static Logger log = Logger.getLogger(GENAEventProcessor.class.getName());

    @Override
    public void readBody(IncomingEventRequestMessage incomingEventRequestMessage) throws UnsupportedDataException {
        try {
            super.readBody(incomingEventRequestMessage);
        } catch (UnsupportedDataException e) {
            if (!incomingEventRequestMessage.isBodyNonEmptyString()) {
                throw e;
            }
            Logger logger = log;
            logger.warning("Trying to recover from invalid GENA XML event: " + e);
            incomingEventRequestMessage.getStateVariableValues().clear();
            try {
                incomingEventRequestMessage.setBody(fixXMLEncodedLastChange(XmlPullParserUtils.fixXMLEntities(getMessageBody(incomingEventRequestMessage))));
                super.readBody(incomingEventRequestMessage);
            } catch (UnsupportedDataException unused) {
                if (incomingEventRequestMessage.getStateVariableValues().isEmpty()) {
                    throw e;
                }
                log.warning("Partial read of GENA event properties (probably due to truncated XML)");
            }
        }
    }

    protected String fixXMLEncodedLastChange(String str) {
        Matcher matcher = Pattern.compile("<LastChange>(.*)</LastChange>", 32).matcher(str);
        if (matcher.find() && matcher.groupCount() == 1) {
            String group = matcher.group(1);
            if (XmlPullParserUtils.isNullOrEmpty(group)) {
                return str;
            }
            String trim = group.trim();
            String encodeText = trim.charAt(0) == '<' ? XMLUtil.encodeText(trim) : trim;
            if (encodeText.equals(trim)) {
                return str;
            }
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?><e:propertyset xmlns:e=\"urn:schemas-upnp-org:event-1-0\"><e:property><LastChange>" + encodeText + "</LastChange></e:property></e:propertyset>";
        }
        return str;
    }
}
