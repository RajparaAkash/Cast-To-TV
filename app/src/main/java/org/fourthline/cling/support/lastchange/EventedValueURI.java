package org.fourthline.cling.support.lastchange;

import org.fourthline.cling.model.types.Datatype;
import org.fourthline.cling.model.types.InvalidValueException;
import org.seamless.util.Exceptions;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;


public class EventedValueURI extends EventedValue<URI> {
    private static final Logger log = Logger.getLogger(EventedValueURI.class.getName());

    public EventedValueURI(URI uri) {
        super(uri);
    }

    public EventedValueURI(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    
    @Override
    public URI valueOf(String str) throws InvalidValueException {
        try {
            return (URI) super.valueOf(str);
        } catch (InvalidValueException e) {
            Logger logger = log;
            logger.info("Ignoring invalid URI in evented value '" + str + "': " + Exceptions.unwrap(e));
            return null;
        }
    }

    @Override
    protected Datatype getDatatype() {
        return Datatype.Builtin.URI.getDatatype();
    }
}
