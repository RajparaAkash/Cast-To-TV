package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;

import java.util.Map;


public class EventedValueString extends EventedValue<String> {
    public EventedValueString(String str) {
        super(str);
    }

    public EventedValueString(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.STRING.getDatatype();
    }
}
