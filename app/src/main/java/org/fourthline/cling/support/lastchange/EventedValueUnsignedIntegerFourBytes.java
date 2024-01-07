package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;

import java.util.Map;


public class EventedValueUnsignedIntegerFourBytes extends EventedValue<UnsignedIntegerFourBytes> {
    public EventedValueUnsignedIntegerFourBytes(UnsignedIntegerFourBytes unsignedIntegerFourBytes) {
        super(unsignedIntegerFourBytes);
    }

    public EventedValueUnsignedIntegerFourBytes(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.UI4.getDatatype();
    }
}
