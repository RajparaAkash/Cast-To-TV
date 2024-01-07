package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

import java.util.Map;


public class EventedValueUnsignedIntegerTwoBytes extends EventedValue<UnsignedIntegerTwoBytes> {
    public EventedValueUnsignedIntegerTwoBytes(UnsignedIntegerTwoBytes unsignedIntegerTwoBytes) {
        super(unsignedIntegerTwoBytes);
    }

    public EventedValueUnsignedIntegerTwoBytes(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI2.getDatatype();
    }
}
