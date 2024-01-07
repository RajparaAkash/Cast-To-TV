package org.seamless.xml;

import org.xml.sax.SAXParseException;


public class ParserException extends Exception {
    public ParserException() {
    }

    public ParserException(String str) {
        super(str);
    }

    public ParserException(String str, Throwable th) {
        super(str, th);
    }

    public ParserException(Throwable th) {
        super(th);
    }

    public ParserException(SAXParseException sAXParseException) {
        super("(Line/Column: " + sAXParseException.getLineNumber() + ":" + sAXParseException.getColumnNumber() + ") " + sAXParseException.getMessage());
    }
}
