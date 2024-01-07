package org.fourthline.cling.model.types;


public final class UnsignedIntegerTwoBytes extends UnsignedVariableInteger {
    public UnsignedIntegerTwoBytes(long j) throws NumberFormatException {
        super(j);
    }

    public UnsignedIntegerTwoBytes(String str) throws NumberFormatException {
        super(str);
    }

    @Override
    public Bits getBits() {
        return Bits.SIXTEEN;
    }
}
