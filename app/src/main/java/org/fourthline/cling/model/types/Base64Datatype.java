package org.fourthline.cling.model.types;

public class Base64Datatype extends AbstractDatatype<byte[]> {

    public Base64Datatype() {
    }

    public Class<byte[]> getValueType() {
        return byte[].class;
    }

    public byte[] valueOf(String s) throws InvalidValueException {
        if (s.equals("")) return null;
        try {
//            return Base64Coder.decode(s);
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
        return new byte[0];
    }

    @Override
    public String getString(byte[] value) throws InvalidValueException {
        if (value == null) return "";
        try {
//            return new String(Base64Coder.encode(value), "UTF-8");
        } catch (Exception ex) {
            throw new InvalidValueException(ex.getMessage(), ex);
        }
        return null;
    }

}