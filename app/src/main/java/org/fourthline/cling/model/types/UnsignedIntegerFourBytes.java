package org.fourthline.cling.model.types;


public final class UnsignedIntegerFourBytes extends UnsignedVariableInteger {
    public UnsignedIntegerFourBytes(long j) throws NumberFormatException {
        super(j);
    }

    public UnsignedIntegerFourBytes(String str) throws NumberFormatException {
        super(str);
    }

    @Override
    public Bits getBits() {
        return Bits.THIRTYTWO;
    }
}
