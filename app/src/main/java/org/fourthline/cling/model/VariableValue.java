package org.fourthline.cling.model;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.logging.Logger;


public class VariableValue {
    private static final Logger log = Logger.getLogger(VariableValue.class.getName());
    private final Datatype datatype;
    private final Object value;

    public VariableValue(Datatype datatype, Object obj) throws InvalidValueException {
        this.datatype = datatype;
        this.value = obj instanceof String ? datatype.valueOf((String) obj) : obj;
        if (ModelUtil.ANDROID_RUNTIME) {
            return;
        }
        if (!getDatatype().isValid(getValue())) {
            throw new InvalidValueException("Invalid value for " + getDatatype() + ": " + getValue());
        }
        logInvalidXML(toString());
    }

    public Datatype getDatatype() {
        return this.datatype;
    }

    public Object getValue() {
        return this.value;
    }

    protected void logInvalidXML(String str) {
        int i = 0;
        while (i < str.length()) {
            int codePointAt = str.codePointAt(i);
            if (codePointAt != 9 && codePointAt != 10 && codePointAt != 13 && ((codePointAt < 32 || codePointAt > 55295) && ((codePointAt < 57344 || codePointAt > 65533) && (codePointAt < 65536 || codePointAt > 1114111)))) {
                Logger logger = log;
                logger.warning("Found invalid XML char code: " + codePointAt);
            }
            i += Character.charCount(codePointAt);
        }
    }

    public String toString() {
        return getDatatype().getString(getValue());
    }
}
