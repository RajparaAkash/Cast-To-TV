package org.fourthline.cling.support.model.dlna;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Locale;


public class DLNAFlagsAttribute extends DLNAAttribute<EnumSet<DLNAFlags>> {
    public DLNAFlagsAttribute() {
        setValue(EnumSet.noneOf(DLNAFlags.class));
    }

    public DLNAFlagsAttribute(DLNAFlags... dLNAFlagsArr) {
        if (dLNAFlagsArr == null || dLNAFlagsArr.length <= 0) {
            return;
        }
        DLNAFlags dLNAFlags = dLNAFlagsArr[0];
        if (dLNAFlagsArr.length > 1) {
            System.arraycopy(dLNAFlagsArr, 1, dLNAFlagsArr, 0, dLNAFlagsArr.length - 1);
            setValue(EnumSet.of(dLNAFlags, dLNAFlagsArr));
            return;
        }
        setValue(EnumSet.of(dLNAFlags));
    }

    @Override
    public void setString(String str, String str2) throws InvalidDLNAProtocolAttributeException {
        DLNAFlags[] values;
        EnumSet noneOf = EnumSet.noneOf(DLNAFlags.class);
        try {
            int parseInt = Integer.parseInt(str.substring(0, str.length() - 24), 16);
            for (DLNAFlags dLNAFlags : DLNAFlags.values()) {
                if (dLNAFlags.getCode() == (dLNAFlags.getCode() & parseInt)) {
                    noneOf.add(dLNAFlags);
                }
            }
        } catch (Exception unused) {
        }
        if (noneOf.isEmpty()) {
            throw new InvalidDLNAProtocolAttributeException("Can't parse DLNA flags integer from: " + str);
        }
        setValue(noneOf);
    }

    @Override
    public String getString() {
        Iterator it = getValue().iterator();
        int i = 0;
        while (it.hasNext()) {
            i |= ((DLNAFlags) it.next()).getCode();
        }
        return String.format(Locale.ROOT, "%08x%024x", Integer.valueOf(i), 0);
    }
}
