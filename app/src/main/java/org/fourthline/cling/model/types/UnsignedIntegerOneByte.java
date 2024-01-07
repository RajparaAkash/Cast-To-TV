package org.fourthline.cling.model.types;


public final class UnsignedIntegerOneByte extends UnsignedVariableInteger {
    public UnsignedIntegerOneByte(long j) throws NumberFormatException {
        super(j);
    }

    public UnsignedIntegerOneByte(String str) throws NumberFormatException {
        super(str);
    }

    @Override
    public Bits getBits() {
        return Bits.EIGHT;
    }
}
