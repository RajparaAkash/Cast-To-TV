package org.fourthline.cling.model.types;


public class CustomDatatype extends AbstractDatatype<String> {
    private String name;

    public CustomDatatype(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        return str;
    }

    @Override
    public String toString() {
        return "(" + getClass().getSimpleName() + ") '" + getName() + "'";
    }
}
