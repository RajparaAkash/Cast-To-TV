package org.fourthline.cling.model.types;

import java.util.Locale;


public class BooleanDatatype extends AbstractDatatype<Boolean> {
    @Override
    public boolean isHandlingJavaType(Class cls) {
        return cls == Boolean.TYPE || Boolean.class.isAssignableFrom(cls);
    }

    @Override
    public Boolean valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        if (str.equals("1") || str.toUpperCase(Locale.ROOT).equals("YES") || str.toUpperCase(Locale.ROOT).equals("TRUE")) {
            return true;
        }
        if (str.equals("0") || str.toUpperCase(Locale.ROOT).equals("NO") || str.toUpperCase(Locale.ROOT).equals("FALSE")) {
            return false;
        }
        throw new InvalidValueException("Invalid boolean value string: " + str);
    }

    @Override
    public String getString(Boolean bool) throws InvalidValueException {
        return bool == null ? "" : bool.booleanValue() ? "1" : "0";
    }
}
