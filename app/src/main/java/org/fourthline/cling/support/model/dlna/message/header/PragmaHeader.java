package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.PragmaType;

import java.util.ArrayList;
import java.util.List;


public class PragmaHeader extends DLNAHeader<List<PragmaType>> {
    public PragmaHeader() {
        setValue(new ArrayList());
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() != 0) {
            if (str.endsWith(";")) {
                str = str.substring(0, str.length() - 1);
            }
            String[] split = str.split("\\s*;\\s*");
            ArrayList arrayList = new ArrayList();
            for (String str2 : split) {
                arrayList.add(PragmaType.valueOf(str2));
            }
            return;
        }
        throw new InvalidHeaderException("Invalid Pragma header value: " + str);
    }

    @Override
    public String getString() {
        String str = "";
        for (PragmaType pragmaType : getValue()) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(str.length() == 0 ? "" : ",");
            sb.append(pragmaType.getString());
            str = sb.toString();
        }
        return str;
    }
}
