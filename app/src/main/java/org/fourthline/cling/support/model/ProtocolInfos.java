package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.ArrayList;


public class ProtocolInfos extends ArrayList<ProtocolInfo> {
    public ProtocolInfos(ProtocolInfo... protocolInfoArr) {
        for (ProtocolInfo protocolInfo : protocolInfoArr) {
            add(protocolInfo);
        }
    }

    public ProtocolInfos(String str) throws InvalidValueException {
        String[] fromCommaSeparatedList = ModelUtil.fromCommaSeparatedList(str);
        if (fromCommaSeparatedList != null) {
            for (String str2 : fromCommaSeparatedList) {
                add(new ProtocolInfo(str2));
            }
        }
    }

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(toArray(new ProtocolInfo[size()]));
    }
}
