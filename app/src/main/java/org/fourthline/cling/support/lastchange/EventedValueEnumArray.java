package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;

import java.util.Map;


public abstract class EventedValueEnumArray<E extends Enum> extends EventedValue<E[]> {
    protected abstract E[] enumValueOf(String[] strArr);

    @Override
    protected Datatype getDatatype() {
        return null;
    }

    public EventedValueEnumArray(E[] eArr) {
        super(eArr);
    }

    public EventedValueEnumArray(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    
    @Override
    public E[] valueOf(String str) throws InvalidValueException {
        return enumValueOf(ModelUtil.fromCommaSeparatedList(str));
    }

    @Override
    public String toString() {
        return ModelUtil.toCommaSeparatedList(getValue());
    }
}
