package org.fourthline.cling.support.messagebox.parser;

import org.seamless.xml.DOM;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;


public class MessageDOM extends DOM {
    public static final String NAMESPACE_URI = "urn:samsung-com:messagebox-1-0";

    @Override
    public String getRootElementNamespace() {
        return NAMESPACE_URI;
    }

    public MessageDOM(Document document) {
        super(document);
    }

    @Override
    public MessageElement getRoot(XPath xPath) {
        return new MessageElement(xPath, getW3CDocument().getDocumentElement());
    }

    @Override
    public MessageDOM copy() {
        return new MessageDOM((Document) getW3CDocument().cloneNode(true));
    }

    public MessageElement createRoot(XPath xPath, String str) {
        super.createRoot(str);
        return getRoot(xPath);
    }
}
