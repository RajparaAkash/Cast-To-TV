package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;


public class AvailableRangeHeader extends DLNAHeader<NormalPlayTimeRange> {
    @Override
    public void setString(String str) throws InvalidHeaderException {
        if (str.length() != 0) {
            try {
                setValue(NormalPlayTimeRange.valueOf(str, true));
                return;
            } catch (Exception unused) {
            }
        }
        throw new InvalidHeaderException("Invalid AvailableRange header value: " + str);
    }

    @Override
    public String getString() {
        return getValue().toString();
    }
}
