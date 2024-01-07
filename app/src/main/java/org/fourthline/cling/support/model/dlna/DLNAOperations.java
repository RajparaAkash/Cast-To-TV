package org.fourthline.cling.support.model.dlna;


public enum DLNAOperations {
    NONE(0),
    RANGE(1),
    TIMESEEK(16);
    
    private int code;

    DLNAOperations(int i) {
        this.code = i;
    }

    public int getCode() {
        return this.code;
    }

    public static DLNAOperations valueOf(int i) {
        DLNAOperations[] values;
        for (DLNAOperations dLNAOperations : values()) {
            if (dLNAOperations.getCode() == i) {
                return dLNAOperations;
            }
        }
        return null;
    }
}
