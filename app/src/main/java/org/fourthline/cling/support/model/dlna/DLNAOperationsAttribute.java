package org.fourthline.cling.support.model.dlna;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;


public class DLNAOperationsAttribute extends DLNAAttribute<EnumSet<DLNAOperations>> {
    public DLNAOperationsAttribute() {
        setValue(EnumSet.of(DLNAOperations.NONE));
    }

    public DLNAOperationsAttribute(DLNAOperations... dLNAOperationsArr) {
        if (dLNAOperationsArr == null || dLNAOperationsArr.length <= 0) {
            return;
        }
        DLNAOperations dLNAOperations = dLNAOperationsArr[0];
        if (dLNAOperationsArr.length > 1) {
            System.arraycopy(dLNAOperationsArr, 1, dLNAOperationsArr, 0, dLNAOperationsArr.length - 1);
            setValue(EnumSet.of(dLNAOperations, dLNAOperationsArr));
            return;
        }
        setValue(EnumSet.of(dLNAOperations));
    }

    @Override
    public void setString(String str, String str2) throws InvalidDLNAProtocolAttributeException {
        DLNAOperations[] values;
        EnumSet noneOf = EnumSet.noneOf(DLNAOperations.class);
        try {
            int parseInt = Integer.parseInt(str, 16);
            for (DLNAOperations dLNAOperations : DLNAOperations.values()) {
                int code = dLNAOperations.getCode() & parseInt;
                if (dLNAOperations != DLNAOperations.NONE && dLNAOperations.getCode() == code) {
                    noneOf.add(dLNAOperations);
                }
            }
        } catch (NumberFormatException unused) {
        }
        if (noneOf.isEmpty()) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA operations integer from: " + str);
        }
        setValue(noneOf);
    }

    @Override
    public String getString() {
        int code = DLNAOperations.NONE.getCode();
        Iterator it = getValue().iterator();
        while (it.hasNext()) {
            code |= ((DLNAOperations) it.next()).getCode();
        }
        return String.format(Locale.ROOT, "%02x", Integer.valueOf(code));
    }
}
