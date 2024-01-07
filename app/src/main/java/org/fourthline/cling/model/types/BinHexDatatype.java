package org.fourthline.cling.model.types;


import org.seamless.util.io.HexBin;

public class BinHexDatatype extends AbstractDatatype<byte[]> {
    @Override
    public Class<byte[]> getValueType() {
        return byte[].class;
    }

    @Override
    public byte[] valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        try {
            return HexBin.stringToBytes(str);
        } catch (Exception e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }

    @Override
    public String getString(byte[] bArr) throws InvalidValueException {
        if (bArr == null) {
            return "";
        }
        try {
            return HexBin.bytesToString(bArr);
        } catch (Exception e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }
}
