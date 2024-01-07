package org.fourthline.cling.model.types;

import java.net.URI;
import java.net.URISyntaxException;


public class URIDatatype extends AbstractDatatype<URI> {
    @Override
    public URI valueOf(String str) throws InvalidValueException {
        if (str.equals("")) {
            return null;
        }
        try {
            return new URI(str);
        } catch (URISyntaxException e) {
            throw new InvalidValueException(e.getMessage(), e);
        }
    }
}
