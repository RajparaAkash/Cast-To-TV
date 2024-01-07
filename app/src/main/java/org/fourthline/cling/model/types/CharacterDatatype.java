package org.fourthline.cling.model.types;


public class CharacterDatatype extends AbstractDatatype<Character> {
    @Override
    public boolean isHandlingJavaType(Class cls) {
        return cls == Character.TYPE || Character.class.isAssignableFrom(cls);
    }

    @Override
    public Character valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        return Character.valueOf(str.charAt(0));
    }
}
