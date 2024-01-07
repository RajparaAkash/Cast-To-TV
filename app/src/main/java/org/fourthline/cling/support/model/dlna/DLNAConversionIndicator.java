package org.fourthline.cling.support.model.dlna;


public enum DLNAConversionIndicator {
    NONE(0),
    TRANSCODED(1);
    
    private int code;

    DLNAConversionIndicator(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }

    public static DLNAConversionIndicator valueOf(int i) {
        DLNAConversionIndicator[] values;
        for (DLNAConversionIndicator dLNAConversionIndicator : values()) {
            if (dLNAConversionIndicator.getCode() == i) {
                return dLNAConversionIndicator;
            }
        }
        return null;
    }
}
