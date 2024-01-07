package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;

import java.util.Map;


public class EventedValueShort extends EventedValue<Short> {
    public EventedValueShort(Short sh) {
        super(sh);
    }

    public EventedValueShort(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.I2_SHORT.getDatatype();
    }
}
