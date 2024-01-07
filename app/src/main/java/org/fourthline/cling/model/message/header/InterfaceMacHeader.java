package org.fourthline.cling.model.message.header;

import org.seamless.util.io.HexBin;


public class InterfaceMacHeader extends UpnpHeader<byte[]> {

    public InterfaceMacHeader() {
    }

    public InterfaceMacHeader(byte[] value) {
        setValue(value);
    }

    public InterfaceMacHeader(String s) {
        setString(s);
    }

    public void setString(String s) throws InvalidHeaderException {
        byte[] bytes = HexBin.stringToBytes(s, ":");
        setValue(bytes);
        if (bytes.length != 6) {
            throw new InvalidHeaderException("Invalid MAC address: " + s);
        }
    }

    public String getString() {
        return HexBin.bytesToString(getValue(), ":");
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getString() + "'";
    }
}