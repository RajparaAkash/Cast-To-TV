package org.fourthline.cling.support.model.dlna.message.header;

import org.fourthline.cling.model.message.header.InvalidHeaderException;
import org.fourthline.cling.model.types.BytesRange;
import org.fourthline.cling.model.types.InvalidValueException;
import org.fourthline.cling.support.model.dlna.types.AvailableSeekRangeType;
import org.fourthline.cling.support.model.dlna.types.NormalPlayTimeRange;


public class AvailableSeekRangeHeader extends DLNAHeader<AvailableSeekRangeType> {
    public AvailableSeekRangeHeader() {
    }

    public AvailableSeekRangeHeader(AvailableSeekRangeType availableSeekRangeType) {
        setValue(availableSeekRangeType);
    }

    @Override
    public void setString(String str) throws InvalidHeaderException {
        NormalPlayTimeRange normalPlayTimeRange;
        if (str.length() != 0) {
            String[] split = str.split(" ");
            boolean z = true;
            try {
                if (split.length > 1) {
                    try {
                        AvailableSeekRangeType.Mode valueOf = AvailableSeekRangeType.Mode.valueOf("MODE_" + split[0]);
                        BytesRange bytesRange = null;
                        try {
                            try {
                                normalPlayTimeRange = NormalPlayTimeRange.valueOf(split[1], true);
                            } catch (InvalidValueException unused) {
                                normalPlayTimeRange = null;
                                bytesRange = BytesRange.valueOf(split[1]);
                                z = false;
                            }
                            if (z) {
                                if (split.length > 2) {
                                    setValue(new AvailableSeekRangeType(valueOf, normalPlayTimeRange, BytesRange.valueOf(split[2])));
                                    return;
                                } else {
                                    setValue(new AvailableSeekRangeType(valueOf, normalPlayTimeRange));
                                    return;
                                }
                            }
                            setValue(new AvailableSeekRangeType(valueOf, bytesRange));
                            return;
                        } catch (InvalidValueException unused2) {
                            throw new InvalidValueException("Invalid AvailableSeekRange Range");
                        }
                    } catch (IllegalArgumentException unused3) {
                        throw new InvalidValueException("Invalid AvailableSeekRange Mode");
                    }
                }
            } catch (InvalidValueException e) {
                throw new InvalidHeaderException("Invalid AvailableSeekRange header value: " + str + "; " + e.getMessage());
            }
        }
        throw new InvalidHeaderException("Invalid AvailableSeekRange header value: " + str);
    }

    @Override
    public String getString() {
        AvailableSeekRangeType value = getValue();
        String num = Integer.toString(value.getModeFlag().ordinal());
        if (value.getNormalPlayTimeRange() != null) {
            num = num + " " + value.getNormalPlayTimeRange().getString(false);
        }
        if (value.getBytesRange() != null) {
            return num + " " + value.getBytesRange().getString(false);
        }
        return num;
    }
}
