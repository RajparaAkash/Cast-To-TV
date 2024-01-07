package org.fourthline.cling.transport.impl;

import org.fourthline.cling.model.UnsupportedDataException;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionException;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.ActionRequestMessage;
import org.fourthline.cling.model.message.control.ActionResponseMessage;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.transport.spi.SOAPActionProcessor;
import org.seamless.xml.XmlPullParserUtils;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.inject.Alternative;

@Alternative

public class PullSOAPActionProcessorImpl extends SOAPActionProcessorImpl {
    protected static Logger log = Logger.getLogger(SOAPActionProcessor.class.getName());

    @Override
    public void readBody(ActionRequestMessage actionRequestMessage, ActionInvocation actionInvocation) throws UnsupportedDataException {
        String messageBody = getMessageBody(actionRequestMessage);
        try {
            readBodyRequest(XmlPullParserUtils.createParser(messageBody), actionRequestMessage, actionInvocation);
        } catch (Exception e) {
            throw new UnsupportedDataException("Can't transform message payload: " + e, e, messageBody);
        }
    }

    @Override
    public void readBody(ActionResponseMessage actionResponseMessage, ActionInvocation actionInvocation) throws UnsupportedDataException {
        String messageBody = getMessageBody(actionResponseMessage);
        try {
            XmlPullParser createParser = XmlPullParserUtils.createParser(messageBody);
            readBodyElement(createParser);
            readBodyResponse(createParser, actionInvocation);
        } catch (Exception e) {
            throw new UnsupportedDataException("Can't transform message payload: " + e, e, messageBody);
        }
    }

    protected void readBodyElement(XmlPullParser xmlPullParser) throws Exception {
        XmlPullParserUtils.searchTag(xmlPullParser, "Body");
    }

    protected void readBodyRequest(XmlPullParser xmlPullParser, ActionRequestMessage actionRequestMessage, ActionInvocation actionInvocation) throws Exception {
        XmlPullParserUtils.searchTag(xmlPullParser, actionInvocation.getAction().getName());
        readActionInputArguments(xmlPullParser, actionInvocation);
    }

    protected void readBodyResponse(XmlPullParser xmlPullParser, ActionInvocation actionInvocation) throws Exception {
        while (true) {
            int next = xmlPullParser.next();
            if (next == 2) {
                if (xmlPullParser.getName().equals("Fault")) {
                    actionInvocation.setFailure(readFaultElement(xmlPullParser));
                    return;
                }
                String name = xmlPullParser.getName();
                if (name.equals(actionInvocation.getAction().getName() + "Response")) {
                    readActionOutputArguments(xmlPullParser, actionInvocation);
                    return;
                }
            }
            if (next == 1 || (next == 3 && xmlPullParser.getName().equals("Body"))) {
                break;
            }
        }
    }

    protected void readActionInputArguments(XmlPullParser xmlPullParser, ActionInvocation actionInvocation) throws Exception {
        actionInvocation.setInput(readArgumentValues(xmlPullParser, actionInvocation.getAction().getInputArguments()));
    }

    protected void readActionOutputArguments(XmlPullParser xmlPullParser, ActionInvocation actionInvocation) throws Exception {
        actionInvocation.setOutput(readArgumentValues(xmlPullParser, actionInvocation.getAction().getOutputArguments()));
    }

    protected Map<String, String> getMatchingNodes(XmlPullParser xmlPullParser, ActionArgument[] actionArgumentArr) throws Exception {
        ArrayList arrayList = new ArrayList();
        for (ActionArgument actionArgument : actionArgumentArr) {
            arrayList.add(actionArgument.getName().toUpperCase(Locale.ROOT));
            for (String str : Arrays.asList(actionArgument.getAliases())) {
                arrayList.add(str.toUpperCase(Locale.ROOT));
            }
        }
        HashMap hashMap = new HashMap();
        String name = xmlPullParser.getName();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 2 && arrayList.contains(xmlPullParser.getName().toUpperCase(Locale.ROOT))) {
                hashMap.put(xmlPullParser.getName(), xmlPullParser.nextText());
            }
            if (next == 1 || (next == 3 && xmlPullParser.getName().equals(name))) {
                break;
            }
        }
        if (hashMap.size() >= actionArgumentArr.length) {
            return hashMap;
        }
        throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Invalid number of input or output arguments in XML message, expected " + actionArgumentArr.length + " but found " + hashMap.size());
    }

    protected ActionArgumentValue[] readArgumentValues(XmlPullParser xmlPullParser, ActionArgument[] actionArgumentArr) throws Exception {
        Map<String, String> matchingNodes = getMatchingNodes(xmlPullParser, actionArgumentArr);
        ActionArgumentValue[] actionArgumentValueArr = new ActionArgumentValue[actionArgumentArr.length];
        for (int i = 0; i < actionArgumentArr.length; i++) {
            ActionArgument actionArgument = actionArgumentArr[i];
            String findActionArgumentValue = findActionArgumentValue(matchingNodes, actionArgument);
            if (findActionArgumentValue == null) {
                ErrorCode errorCode = ErrorCode.ARGUMENT_VALUE_INVALID;
                throw new ActionException(errorCode, "Could not find argument '" + actionArgument.getName() + "' node");
            }
            Logger logger = log;
            logger.fine("Reading action argument: " + actionArgument.getName());
            actionArgumentValueArr[i] = createValue(actionArgument, findActionArgumentValue);
        }
        return actionArgumentValueArr;
    }

    protected String findActionArgumentValue(Map<String, String> map, ActionArgument actionArgument) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (actionArgument.isNameOrAlias(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    protected ActionException readFaultElement(XmlPullParser xmlPullParser) throws Exception {
        XmlPullParserUtils.searchTag(xmlPullParser, "UPnPError");
        String str = null;
        String str2 = null;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("errorCode")) {
                    str = xmlPullParser.nextText();
                } else if (name.equals("errorDescription")) {
                    str2 = xmlPullParser.nextText();
                }
            }
            if (next == 1 || (next == 3 && xmlPullParser.getName().equals("UPnPError"))) {
                break;
            }
        }
        if (str != null) {
            try {
                int intValue = Integer.valueOf(str).intValue();
                ErrorCode byCode = ErrorCode.getByCode(intValue);
                if (byCode != null) {
                    log.fine("Reading fault element: " + byCode.getCode() + " - " + str2);
                    return new ActionException(byCode, str2, false);
                }
                log.fine("Reading fault element: " + intValue + " - " + str2);
                return new ActionException(intValue, str2);
            } catch (NumberFormatException unused) {
                throw new RuntimeException("Error code was not a number");
            }
        }
        throw new RuntimeException("Received fault element but no error code");
    }
}
