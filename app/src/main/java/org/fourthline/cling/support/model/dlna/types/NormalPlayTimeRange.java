package org.fourthline.cling.support.model.dlna.types;

import org.fourthline.cling.model.types.InvalidValueException;


public class NormalPlayTimeRange {
    public static final String PREFIX = "npt=";
    private NormalPlayTime timeDuration;
    private NormalPlayTime timeEnd;
    private NormalPlayTime timeStart;

    public NormalPlayTimeRange(long j, long j2) {
        this.timeStart = new NormalPlayTime(j);
        this.timeEnd = new NormalPlayTime(j2);
    }

    public NormalPlayTimeRange(NormalPlayTime normalPlayTime, NormalPlayTime normalPlayTime2) {
        this.timeStart = normalPlayTime;
        this.timeEnd = normalPlayTime2;
    }

    public NormalPlayTimeRange(NormalPlayTime normalPlayTime, NormalPlayTime normalPlayTime2, NormalPlayTime normalPlayTime3) {
        this.timeStart = normalPlayTime;
        this.timeEnd = normalPlayTime2;
        this.timeDuration = normalPlayTime3;
    }

    public NormalPlayTime getTimeStart() {
        return this.timeStart;
    }

    public NormalPlayTime getTimeEnd() {
        return this.timeEnd;
    }

    public NormalPlayTime getTimeDuration() {
        return this.timeDuration;
    }

    public String getString() {
        return getString(true);
    }

    public String getString(boolean z) {
        String str = PREFIX + this.timeStart.getString() + "-";
        if (this.timeEnd != null) {
            str = str + this.timeEnd.getString();
        }
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("/");
            NormalPlayTime normalPlayTime = this.timeDuration;
            sb.append(normalPlayTime != null ? normalPlayTime.getString() : "*");
            return sb.toString();
        }
        return str;
    }

    public static NormalPlayTimeRange valueOf(String str) throws InvalidValueException {
        return valueOf(str, false);
    }


    public static NormalPlayTimeRange valueOf(String str, boolean z) throws InvalidValueException {
        NormalPlayTime normalPlayTime;
        if (str.startsWith(PREFIX)) {
            String[] split = str.substring(4).split("[-/]");
            int length = split.length;
            NormalPlayTime normalPlayTime2 = null;
            if (length != 1) {
                if (length != 2) {
                    if (length == 3) {
                        if (split[2].length() != 0 && !split[2].equals("*")) {
                            normalPlayTime = NormalPlayTime.valueOf(split[2]);
                            if (split[1].length() != 0) {
                                normalPlayTime2 = NormalPlayTime.valueOf(split[1]);
                            }
                        }
                    }
                }
                normalPlayTime = null;
                if (split[1].length() != 0) {
                }
            } else {
                normalPlayTime = null;
            }
            if (split[0].length() != 0 && (!z || (z && split.length > 1))) {
                return new NormalPlayTimeRange(NormalPlayTime.valueOf(split[0]), normalPlayTime2, normalPlayTime);
            }
        }
        throw new InvalidValueException("Can't parse NormalPlayTimeRange: " + str);
    }
}
