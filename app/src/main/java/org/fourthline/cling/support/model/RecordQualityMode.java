package org.fourthline.cling.support.model;

import org.fourthline.cling.model.ModelUtil;

import java.util.ArrayList;


public enum RecordQualityMode {
    EP("0:EP"),
    LP("1:LP"),
    SP("2:SP"),
    BASIC("0:BASIC"),
    MEDIUM("1:MEDIUM"),
    HIGH("2:HIGH"),
    NOT_IMPLEMENTED("NOT_IMPLEMENTED");
    
    private String protocolString;

    RecordQualityMode(String str) {
        this.protocolString = str;
    }

    @Override
    public String toString() {
        return this.protocolString;
    }

    public static RecordQualityMode valueOrExceptionOf(String str) throws IllegalArgumentException {
        RecordQualityMode[] values;
        for (RecordQualityMode recordQualityMode : values()) {
            if (recordQualityMode.protocolString.equals(str)) {
                return recordQualityMode;
            }
        }
        throw new IllegalArgumentException("Invalid record quality mode string: " + str);
    }

    public static RecordQualityMode[] valueOfCommaSeparatedList(String str) {
        RecordQualityMode[] values;
        String[] fromCommaSeparatedList = ModelUtil.fromCommaSeparatedList(str);
        if (fromCommaSeparatedList == null) {
            return new RecordQualityMode[0];
        }
        ArrayList arrayList = new ArrayList();
        for (String str2 : fromCommaSeparatedList) {
            for (RecordQualityMode recordQualityMode : values()) {
                if (recordQualityMode.protocolString.equals(str2)) {
                    arrayList.add(recordQualityMode);
                }
            }
        }
        return (RecordQualityMode[]) arrayList.toArray(new RecordQualityMode[arrayList.size()]);
    }
}
