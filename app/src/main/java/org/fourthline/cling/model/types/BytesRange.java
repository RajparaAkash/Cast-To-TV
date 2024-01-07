package org.fourthline.cling.model.types;


public class BytesRange {
    public static final String PREFIX = "bytes=";
    private Long byteLength;
    private Long firstByte;
    private Long lastByte;

    public BytesRange(Long l, Long l2) {
        this.firstByte = l;
        this.lastByte = l2;
        this.byteLength = null;
    }

    public BytesRange(Long l, Long l2, Long l3) {
        this.firstByte = l;
        this.lastByte = l2;
        this.byteLength = l3;
    }

    public Long getFirstByte() {
        return this.firstByte;
    }

    public Long getLastByte() {
        return this.lastByte;
    }

    public Long getByteLength() {
        return this.byteLength;
    }

    public String getString() {
        return getString(false, null);
    }

    public String getString(boolean z) {
        return getString(z, null);
    }

    public String getString(boolean z, String str) {
        if (str == null) {
            str = PREFIX;
        }
        if (this.firstByte != null) {
            str = str + this.firstByte.toString();
        }
        String str2 = str + "-";
        if (this.lastByte != null) {
            str2 = str2 + this.lastByte.toString();
        }
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append("/");
            Long l = this.byteLength;
            sb.append(l != null ? l.toString() : "*");
            return sb.toString();
        }
        return str2;
    }

    public static BytesRange valueOf(String str) throws InvalidValueException {
        return valueOf(str, null);
    }

    public static BytesRange valueOf(String str, String str2) throws InvalidValueException {
        Long l;
        Long l2 = null;
        if (str.startsWith(str2 != null ? str2 : PREFIX)) {
            if (str2 == null) {
                str2 = PREFIX;
            }
            String[] split = str.substring(str2.length()).split("[-/]");
            int length = split.length;
            if (length != 1) {
                if (length != 2) {
                    if (length == 3) {
                        if (split[2].length() != 0 && !split[2].equals("*")) {
                            l = Long.valueOf(Long.parseLong(split[2]));
                            l2 = split[1].length() == 0 ? Long.valueOf(Long.parseLong(split[1])) : null;
                        }
                    }
                }
                l = null;
                if (split[1].length() == 0) {
                }
            } else {
                l = null;
                l2 = null;
            }
            Long valueOf = split[0].length() != 0 ? Long.valueOf(Long.parseLong(split[0])) : null;
            if (valueOf != null || l2 != null) {
                return new BytesRange(valueOf, l2, l);
            }
        }
        throw new InvalidValueException("Can't parse Bytes Range: " + str);
    }
}
