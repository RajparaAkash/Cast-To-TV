package org.fourthline.cling.support.messagebox.parser;

import org.seamless.xml.DOMElement;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;


public class MessageElement extends DOMElement<MessageElement, MessageElement> {
    public static final String XPATH_PREFIX = "m";

    public MessageElement(XPath xPath, Element element) {
        super(xPath, element);
    }

    @Override
    protected String prefix(String str) {
        return "m:" + str;
    }

    @Override
    protected DOMElement<MessageElement, MessageElement>.Builder<MessageElement> createParentBuilder(DOMElement dOMElement) {
        return new Builder<MessageElement>(dOMElement) {

            @Override
            public MessageElement build(Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }

    @Override
    protected DOMElement<MessageElement, MessageElement>.ArrayBuilder<MessageElement> createChildBuilder(DOMElement dOMElement) {
        return new ArrayBuilder<MessageElement>(dOMElement) {

            @Override
            public MessageElement[] newChildrenArray(int i) {
                return new MessageElement[i];
            }

            @Override
            public MessageElement build(Element element) {
                return new MessageElement(MessageElement.this.getXpath(), element);
            }
        };
    }
}
