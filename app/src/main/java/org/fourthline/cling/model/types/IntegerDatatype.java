package org.fourthline.cling.model.types;


public class IntegerDatatype extends AbstractDatatype<Integer> {
    private int byteSize;

    public IntegerDatatype(int i) {
        this.byteSize = i;
    }

    @Override
    public boolean isHandlingJavaType(Class cls) {
        return cls == Integer.TYPE || Integer.class.isAssignableFrom(cls);
    }

    @Override
    public Integer valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        try {
            Integer valueOf = Integer.valueOf(Integer.parseInt(str.trim()));
            if (isValid(valueOf)) {
                return valueOf;
            }
            throw new InvalidValueException("Not a " + getByteSize() + " byte(s) integer: " + str);
        } catch (NumberFormatException e) {
            if (str.equals("NOT_IMPLEMENTED")) {
                return Integer.valueOf(getMaxValue());
            }
            throw new InvalidValueException("Can't convert string to number: " + str, e);
        }
    }

    @Override
    public boolean isValid(Integer num) {
        return num == null || (num.intValue() >= getMinValue() && num.intValue() <= getMaxValue());
    }

    public int getMinValue() {
        int byteSize = getByteSize();
        if (byteSize != 1) {
            if (byteSize != 2) {
                if (byteSize == 4) {
                    return Integer.MIN_VALUE;
                }
                throw new IllegalArgumentException("Invalid integer byte size: " + getByteSize());
            }
            return -32768;
        }
        return -128;
    }

    public int getMaxValue() {
        int byteSize = getByteSize();
        if (byteSize != 1) {
            if (byteSize != 2) {
                if (byteSize == 4) {
                    return Integer.MAX_VALUE;
                }
                throw new IllegalArgumentException("Invalid integer byte size: " + getByteSize());
            }
            return 32767;
        }
        return 127;
    }

    public int getByteSize() {
        return this.byteSize;
    }
}
