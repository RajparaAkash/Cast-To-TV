package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.Map;


public abstract class EventedValueEnum<E extends Enum> extends EventedValue<E> {
    protected abstract E enumValueOf(String str);

    @Override
    protected Datatype getDatatype() {
        return null;
    }

    public EventedValueEnum(E e) {
        super(e);
    }

    public EventedValueEnum(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    
    @Override
    public E valueOf(String str) throws InvalidValueException {
        return enumValueOf(str);
    }

    @Override
    public String toString() {
        return ((Enum) getValue()).name();
    }
}
