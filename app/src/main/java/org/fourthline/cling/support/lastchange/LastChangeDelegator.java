package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;


public interface LastChangeDelegator {
    void appendCurrentState(LastChange lastChange, UnsignedIntegerFourBytes unsignedIntegerFourBytes) throws Exception;

    UnsignedIntegerFourBytes[] getCurrentInstanceIds();

    LastChange getLastChange();
}
