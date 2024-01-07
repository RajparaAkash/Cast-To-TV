package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.seamless.xml.XmlPullParserUtils;

import java.util.logging.Logger;

import javax.enterprise.inject.Alternative;

@Alternative

public class RecoveringSOAPActionProcessorImpl extends PullSOAPActionProcessorImpl {
    private static Logger log = Logger.getLogger(SOAPActionProcessor.class.getName());

    @Override
    public void readBody(ActionRequestMessage actionRequestMessage, ActionInvocation actionInvocation) throws UnsupportedDataException {
        try {
            super.readBody(actionRequestMessage, actionInvocation);
        } catch (UnsupportedDataException e) {
            if (!actionRequestMessage.isBodyNonEmptyString()) {
                throw e;
            }
            Logger logger = log;
            logger.warning("Trying to recover from invalid SOAP XML request: " + e);
            try {
                actionRequestMessage.setBody(XmlPullParserUtils.fixXMLEntities(getMessageBody(actionRequestMessage)));
                super.readBody(actionRequestMessage, actionInvocation);
            } catch (UnsupportedDataException e2) {
                handleInvalidMessage(actionInvocation, e, e2);
            }
        }
    }

    @Override
    public void readBody(ActionResponseMessage actionResponseMessage, ActionInvocation actionInvocation) throws UnsupportedDataException {
        try {
            super.readBody(actionResponseMessage, actionInvocation);
        } catch (UnsupportedDataException e) {
            if (!actionResponseMessage.isBodyNonEmptyString()) {
                throw e;
            }
            log.warning("Trying to recover from invalid SOAP XML response: " + e);
            String fixXMLEntities = XmlPullParserUtils.fixXMLEntities(getMessageBody(actionResponseMessage));
            if (fixXMLEntities.endsWith("</s:Envelop")) {
                fixXMLEntities = fixXMLEntities + "e>";
            }
            try {
                actionResponseMessage.setBody(fixXMLEntities);
                super.readBody(actionResponseMessage, actionInvocation);
            } catch (UnsupportedDataException e2) {
                handleInvalidMessage(actionInvocation, e, e2);
            }
        }
    }

    protected void handleInvalidMessage(ActionInvocation actionInvocation, UnsupportedDataException unsupportedDataException, UnsupportedDataException unsupportedDataException2) throws UnsupportedDataException {
        throw unsupportedDataException;
    }
}
