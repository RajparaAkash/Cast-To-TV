package org.seamless.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;


public abstract class DOMElement<CHILD extends DOMElement, PARENT extends DOMElement> {
    private Element element;
    private final XPath xpath;
    public final Builder<PARENT> PARENT_BUILDER = createParentBuilder(this);
    public final ArrayBuilder<CHILD> CHILD_BUILDER = createChildBuilder(this);

    protected abstract ArrayBuilder<CHILD> createChildBuilder(DOMElement dOMElement);

    protected abstract Builder<PARENT> createParentBuilder(DOMElement dOMElement);

    protected String prefix(String str) {
        return str;
    }

    public DOMElement(XPath xPath, Element element) {
        this.xpath = xPath;
        this.element = element;
    }

    public Element getW3CElement() {
        return this.element;
    }

    public String getElementName() {
        return getW3CElement().getNodeName();
    }

    public String getContent() {
        return getW3CElement().getTextContent();
    }

    public DOMElement<CHILD, PARENT> setContent(String str) {
        getW3CElement().setTextContent(str);
        return this;
    }

    public String getAttribute(String str) {
        String attribute = getW3CElement().getAttribute(str);
        if (attribute.length() > 0) {
            return attribute;
        }
        return null;
    }

    public DOMElement setAttribute(String str, String str2) {
        getW3CElement().setAttribute(str, str2);
        return this;
    }

    public PARENT getParent() {
        return (PARENT) this.PARENT_BUILDER.build((Element) getW3CElement().getParentNode());
    }

    public CHILD[] getChildren() {
        NodeList childNodes = getW3CElement().getChildNodes();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                arrayList.add(this.CHILD_BUILDER.build((Element) item));
            }
        }
        return (CHILD[]) ((DOMElement[]) arrayList.toArray(this.CHILD_BUILDER.newChildrenArray(arrayList.size())));
    }

    public CHILD[] getChildren(String str) {
        Collection<CHILD> xPathChildElements = getXPathChildElements(this.CHILD_BUILDER, prefix(str));
        return (CHILD[]) ((DOMElement[]) xPathChildElements.toArray(this.CHILD_BUILDER.newChildrenArray(xPathChildElements.size())));
    }

    public CHILD getRequiredChild(String str) throws ParserException {
        CHILD[] children = getChildren(str);
        if (children.length != 1) {
            throw new ParserException("Required single child element of '" + getElementName() + "' not found: " + str);
        }
        return children[0];
    }

    public CHILD[] findChildren(String str) {
        ArrayBuilder<CHILD> arrayBuilder = this.CHILD_BUILDER;
        Collection<CHILD> xPathChildElements = getXPathChildElements(arrayBuilder, "descendant::" + prefix(str));
        return (CHILD[]) ((DOMElement[]) xPathChildElements.toArray(this.CHILD_BUILDER.newChildrenArray(xPathChildElements.size())));
    }

    public CHILD findChildWithIdentifier(String str) {
        ArrayBuilder<CHILD> arrayBuilder = this.CHILD_BUILDER;
        Collection<CHILD> xPathChildElements = getXPathChildElements(arrayBuilder, "descendant::" + prefix("*") + "[@id=\"" + str + "\"]");
        if (xPathChildElements.size() == 1) {
            return xPathChildElements.iterator().next();
        }
        return null;
    }

    public CHILD getFirstChild(String str) {
        ArrayBuilder<CHILD> arrayBuilder = this.CHILD_BUILDER;
        return getXPathChildElement(arrayBuilder, prefix(str) + "[1]");
    }

    public CHILD createChild(String str) {
        return createChild(str, null);
    }

    public CHILD createChild(String str, String str2) {
        CHILD child = (CHILD) this.CHILD_BUILDER.build(str2 == null ? getW3CElement().getOwnerDocument().createElement(str) : getW3CElement().getOwnerDocument().createElementNS(str2, str));
        getW3CElement().appendChild(child.getW3CElement());
        return child;
    }

    public CHILD appendChild(CHILD child, boolean z) {
        CHILD adoptOrImport = adoptOrImport(getW3CElement().getOwnerDocument(), child, z);
        getW3CElement().appendChild(adoptOrImport.getW3CElement());
        return adoptOrImport;
    }

    public CHILD replaceChild(CHILD child, CHILD child2, boolean z) {
        CHILD adoptOrImport = adoptOrImport(getW3CElement().getOwnerDocument(), child2, z);
        getW3CElement().replaceChild(adoptOrImport.getW3CElement(), child.getW3CElement());
        return adoptOrImport;
    }
    
    public void replaceEqualChild(DOMElement dOMElement, String str) {
        CHILD findChildWithIdentifier = findChildWithIdentifier(str);
        findChildWithIdentifier.getParent().replaceChild(findChildWithIdentifier, dOMElement.findChildWithIdentifier(str), true);
    }

    public void removeChild(CHILD child) {
        getW3CElement().removeChild(child.getW3CElement());
    }

    public void removeChildren() {
        NodeList childNodes = getW3CElement().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            getW3CElement().removeChild(childNodes.item(i));
        }
    }

    protected CHILD adoptOrImport(Document document, CHILD child, boolean z) {
        if (document != null) {
            if (z) {
                return (CHILD) this.CHILD_BUILDER.build((Element) document.importNode(child.getW3CElement(), true));
            }
            return (CHILD) this.CHILD_BUILDER.build((Element) document.adoptNode(child.getW3CElement()));
        }
        return child;
    }

    public String toSimpleXMLString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(getElementName());
        NamedNodeMap attributes = getW3CElement().getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            sb.append(" ");
            sb.append(item.getNodeName());
            sb.append("=\"");
            sb.append(item.getTextContent());
            sb.append("\"");
        }
        if (getContent().length() > 0) {
            sb.append(">");
            sb.append(getContent());
            sb.append("</");
            sb.append(getElementName());
            sb.append(">");
        } else {
            sb.append("/>");
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(getClass().getSimpleName());
        sb.append(") ");
        sb.append(getW3CElement() == null ? "UNBOUND" : getElementName());
        return sb.toString();
    }

    public XPath getXpath() {
        return this.xpath;
    }

    public Collection<PARENT> getXPathParentElements(Builder<CHILD> builder, String str) {
        return getXPathElements(builder, str);
    }

    public Collection<CHILD> getXPathChildElements(Builder<CHILD> builder, String str) {
        return getXPathElements(builder, str);
    }

    public PARENT getXPathParentElement(Builder<PARENT> builder, String str) {
        Node node = (Node) getXPathResult(getW3CElement(), str, XPathConstants.NODE);
        if (node == null || node.getNodeType() != 1) {
            return null;
        }
        return (PARENT) builder.build((Element) node);
    }

    public CHILD getXPathChildElement(Builder<CHILD> builder, String str) {
        Node node = (Node) getXPathResult(getW3CElement(), str, XPathConstants.NODE);
        if (node == null || node.getNodeType() != 1) {
            return null;
        }
        return (CHILD) builder.build((Element) node);
    }

    public Collection getXPathElements(Builder builder, String str) {
        ArrayList arrayList = new ArrayList();
        NodeList nodeList = (NodeList) getXPathResult(getW3CElement(), str, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            arrayList.add(builder.build((Element) nodeList.item(i)));
        }
        return arrayList;
    }

    public String getXPathString(XPath xPath, String str) {
        return getXPathResult(getW3CElement(), str, null).toString();
    }

    public Object getXPathResult(String str, QName qName) {
        return getXPathResult(getW3CElement(), str, qName);
    }

    public Object getXPathResult(Node node, String str, QName qName) {
        try {
            if (qName == null) {
                return this.xpath.evaluate(str, node);
            }
            return this.xpath.evaluate(str, node, qName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    
    public abstract class Builder<T extends DOMElement> {
        public DOMElement element;

        public abstract T build(Element element);

        
        public Builder(DOMElement dOMElement) {
            this.element = dOMElement;
        }

        public T firstChildOrNull(String str) {
            DOMElement firstChild = this.element.getFirstChild(str);
            if (firstChild != null) {
                return build(firstChild.getW3CElement());
            }
            return null;
        }
    }

    
    public abstract class ArrayBuilder<T extends DOMElement> extends Builder<T> {
        public abstract T[] newChildrenArray(int i);

        
        public ArrayBuilder(DOMElement dOMElement) {
            super(dOMElement);
        }

        public T[] getChildElements() {
            return buildArray(this.element.getChildren());
        }

        public T[] getChildElements(String str) {
            return buildArray(this.element.getChildren(str));
        }

        protected T[] buildArray(DOMElement[] dOMElementArr) {
            T[] newChildrenArray = newChildrenArray(dOMElementArr.length);
            for (int i = 0; i < newChildrenArray.length; i++) {
                newChildrenArray[i] = build(dOMElementArr[i].getW3CElement());
            }
            return newChildrenArray;
        }
    }
}
