package org.seamless.xml;

import org.seamless.xml.DOM;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;


public abstract class DOMParser<D extends DOM> implements ErrorHandler, EntityResolver {
    protected Schema schema;
    protected Source[] schemaSources;
    private static Logger log = Logger.getLogger(DOMParser.class.getName());
    public static final URL XML_SCHEMA_RESOURCE = Thread.currentThread().getContextClassLoader().getResource("org/seamless/schemas/xml.xsd");

    protected abstract D createDOM(Document document);

    public DOMParser() {
        this(null);
    }

    public DOMParser(Source[] sourceArr) {
        this.schemaSources = sourceArr;
    }

    public Schema getSchema() {
        if (this.schema == null) {
            try {
                SchemaFactory newInstance = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
                newInstance.setResourceResolver(new CatalogResourceResolver(new HashMap<URI, URL>() {
                    {
                        put(DOM.XML_SCHEMA_NAMESPACE, DOMParser.XML_SCHEMA_RESOURCE);
                    }
                }));
                Source[] sourceArr = this.schemaSources;
                if (sourceArr != null) {
                    this.schema = newInstance.newSchema(sourceArr);
                } else {
                    this.schema = newInstance.newSchema();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return this.schema;
    }

    public DocumentBuilderFactory createFactory(boolean z) throws ParserException {
        DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
        try {
            newInstance.setNamespaceAware(true);
            if (z) {
                newInstance.setXIncludeAware(true);
                newInstance.setFeature("http://apache.org/xml/features/xinclude/fixup-base-uris", false);
                newInstance.setFeature("http://apache.org/xml/features/xinclude/fixup-language", false);
                newInstance.setSchema(getSchema());
                newInstance.setFeature("http://apache.org/xml/features/validation/dynamic", true);
            }
            return newInstance;
        } catch (ParserConfigurationException e) {
            throw new ParserException(e);
        }
    }

    public Transformer createTransformer(String str, int i, boolean z) throws ParserException {
        try {
            TransformerFactory newInstance = TransformerFactory.newInstance();
            if (i > 0) {
                try {
                    newInstance.setAttribute("indent-number", Integer.valueOf(i));
                } catch (IllegalArgumentException unused) {
                }
            }
            Transformer newTransformer = newInstance.newTransformer();
            newTransformer.setOutputProperty("omit-xml-declaration", z ? "no" : "yes");
            if (z) {
                try {
                    newTransformer.setOutputProperty("http://www.oracle.com/xml/is-standalone", "yes");
                } catch (IllegalArgumentException unused2) {
                }
            }
            newTransformer.setOutputProperty("indent", i > 0 ? "yes" : "no");
            if (i > 0) {
                newTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(i));
            }
            newTransformer.setOutputProperty("method", str);
            return newTransformer;
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    public D createDocument() {
        try {
            return createDOM(createFactory(false).newDocumentBuilder().newDocument());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public D parse(URL url) throws ParserException {
        return parse(url, true);
    }

    public D parse(String str) throws ParserException {
        return parse(str, true);
    }

    public D parse(File file) throws ParserException {
        return parse(file, true);
    }

    public D parse(InputStream inputStream) throws ParserException {
        return parse(inputStream, true);
    }

    public D parse(URL url, boolean z) throws ParserException {
        if (url == null) {
            throw new IllegalArgumentException("Can't parse null URL");
        }
        try {
            return parse(url.openStream(), z);
        } catch (Exception e) {
            throw new ParserException("Parsing URL failed: " + url, e);
        }
    }

    public D parse(String str, boolean z) throws ParserException {
        if (str == null) {
            throw new IllegalArgumentException("Can't parse null string");
        }
        return parse(new InputSource(new StringReader(str)), z);
    }

    public D parse(File file, boolean z) throws ParserException {
        if (file == null) {
            throw new IllegalArgumentException("Can't parse null file");
        }
        try {
            return parse(file.toURI().toURL(), z);
        } catch (Exception e) {
            throw new ParserException("Parsing file failed: " + file, e);
        }
    }

    public D parse(InputStream inputStream, boolean z) throws ParserException {
        return parse(new InputSource(inputStream), z);
    }

    public D parse(InputSource inputSource, boolean z) throws ParserException {
        try {
            DocumentBuilder newDocumentBuilder = createFactory(z).newDocumentBuilder();
            newDocumentBuilder.setEntityResolver(this);
            newDocumentBuilder.setErrorHandler(this);
            Document parse = newDocumentBuilder.parse(inputSource);
            parse.normalizeDocument();
            return createDOM(parse);
        } catch (Exception e) {
            throw unwrapException(e);
        }
    }

    public void validate(URL url) throws ParserException {
        if (url == null) {
            throw new IllegalArgumentException("Can't validate null URL");
        }
        Logger logger = log;
        logger.fine("Validating XML of URL: " + url);
        validate(new StreamSource(url.toString()));
    }

    public void validate(String str) throws ParserException {
        if (str == null) {
            throw new IllegalArgumentException("Can't validate null string");
        }
        Logger logger = log;
        logger.fine("Validating XML string characters: " + str.length());
        validate(new SAXSource(new InputSource(new StringReader(str))));
    }

    public void validate(Document document) throws ParserException {
        validate(new DOMSource(document));
    }

    public void validate(DOM dom) throws ParserException {
        validate(new DOMSource(dom.getW3CDocument()));
    }

    public void validate(Source source) throws ParserException {
        try {
            Validator newValidator = getSchema().newValidator();
            newValidator.setErrorHandler(this);
            newValidator.validate(source);
        } catch (Exception e) {
            throw unwrapException(e);
        }
    }

    public XPathFactory createXPathFactory() {
        return XPathFactory.newInstance();
    }

    public XPath createXPath(NamespaceContext namespaceContext) {
        XPath newXPath = createXPathFactory().newXPath();
        newXPath.setNamespaceContext(namespaceContext);
        return newXPath;
    }

    public XPath createXPath(XPathFactory xPathFactory, NamespaceContext namespaceContext) {
        XPath newXPath = xPathFactory.newXPath();
        newXPath.setNamespaceContext(namespaceContext);
        return newXPath;
    }

    public Object getXPathResult(DOM dom, XPath xPath, String str, QName qName) {
        return getXPathResult(dom.getW3CDocument(), xPath, str, qName);
    }

    public Object getXPathResult(DOMElement dOMElement, XPath xPath, String str, QName qName) {
        return getXPathResult(dOMElement.getW3CElement(), xPath, str, qName);
    }

    public Object getXPathResult(Node node, XPath xPath, String str, QName qName) {
        try {
            Logger logger = log;
            logger.fine("Evaluating xpath query: " + str);
            return xPath.evaluate(str, node, qName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String print(DOM dom) throws ParserException {
        return print(dom, 4, true);
    }

    public String print(DOM dom, int i) throws ParserException {
        return print(dom, i, true);
    }

    public String print(DOM dom, boolean z) throws ParserException {
        return print(dom, 4, z);
    }

    public String print(DOM dom, int i, boolean z) throws ParserException {
        return print(dom.getW3CDocument(), i, z);
    }

    public String print(Document document, int i, boolean z) throws ParserException {
        removeIgnorableWSNodes(document.getDocumentElement());
        return print(new DOMSource(document.getDocumentElement()), i, z);
    }

    public String print(String str, int i, boolean z) throws ParserException {
        return print(new StreamSource(new StringReader(str)), i, z);
    }

    public String print(Source source, int i, boolean z) throws ParserException {
        try {
            Transformer createTransformer = createTransformer("xml", i, z);
            createTransformer.setOutputProperty("encoding", "utf-8");
            StringWriter stringWriter = new StringWriter();
            createTransformer.transform(source, new StreamResult(stringWriter));
            stringWriter.flush();
            return stringWriter.toString();
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    public String printHTML(Document document) throws ParserException {
        return printHTML(document, 4, true, true);
    }

    public String printHTML(Document document, int i, boolean z, boolean z2) throws ParserException {
        Document document2 = (Document) document.cloneNode(true);
        accept(document2.getDocumentElement(), new NodeVisitor((short) 4) {
            @Override
            public void visit(Node node) {
                CDATASection cDATASection = (CDATASection) node;
                cDATASection.getParentNode().setTextContent(cDATASection.getData());
            }
        });
        removeIgnorableWSNodes(document2.getDocumentElement());
        try {
            Transformer createTransformer = createTransformer("html", i, z);
            if (z2) {
                createTransformer.setOutputProperty("doctype-public", "-//W3C//DTD HTML 4.01 Transitional//EN");
                createTransformer.setOutputProperty("doctype-system", "http://www.w3.org/TR/html4/loose.dtd");
            }
            StringWriter stringWriter = new StringWriter();
            createTransformer.transform(new DOMSource(document2), new StreamResult(stringWriter));
            stringWriter.flush();
            return stringWriter.toString().replaceFirst("\\s*<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">", "").replaceFirst("<html xmlns=\"http://www.w3.org/1999/xhtml\">", "<html>");
        } catch (Exception e) {
            throw new ParserException(e);
        }
    }

    public void removeIgnorableWSNodes(Element element) {
        Node firstChild = element.getFirstChild();
        while (firstChild != null) {
            Node nextSibling = firstChild.getNextSibling();
            if (isIgnorableWSNode(firstChild)) {
                element.removeChild(firstChild);
            } else if (firstChild.getNodeType() == 1) {
                removeIgnorableWSNodes((Element) firstChild);
            }
            firstChild = nextSibling;
        }
    }

    public boolean isIgnorableWSNode(Node node) {
        return node.getNodeType() == 3 && node.getTextContent().matches("[\\t\\n\\x0B\\f\\r\\s]+");
    }

    @Override
    public void warning(SAXParseException sAXParseException) throws SAXException {
        log.warning(sAXParseException.toString());
    }

    @Override
    public void error(SAXParseException sAXParseException) throws SAXException {
        throw new SAXException(new ParserException(sAXParseException));
    }

    @Override
    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        throw new SAXException(new ParserException(sAXParseException));
    }

    protected ParserException unwrapException(Exception exc) {
        if (exc.getCause() != null && (exc.getCause() instanceof ParserException)) {
            return (ParserException) exc.getCause();
        }
        return new ParserException(exc);
    }

    @Override
    public InputSource resolveEntity(String str, String str2) throws SAXException, IOException {
        InputSource inputSource;
        if (str2.startsWith("file://")) {
            inputSource = new InputSource(new FileInputStream(new File(URI.create(str2))));
        } else {
            inputSource = new InputSource(new ByteArrayInputStream(new byte[0]));
        }
        inputSource.setPublicId(str);
        inputSource.setSystemId(str2);
        return inputSource;
    }

    public static String escape(String str) {
        return escape(str, false, false);
    }

    public static String escape(String str, boolean z, boolean z2) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            String str2 = charAt != '\"' ? charAt != '&' ? charAt != '<' ? charAt != '>' ? null : "&#62;" : "&#60;" : "&#38;" : "&#34;";
            if (str2 != null) {
                sb.append(str2);
            } else {
                sb.append(charAt);
            }
        }
        String sb2 = sb.toString();
        if (z2) {
            Matcher matcher = Pattern.compile("(\\n+)(\\s*)(.*)").matcher(sb2);
            StringBuffer stringBuffer = new StringBuffer();
            while (matcher.find()) {
                String group = matcher.group(2);
                StringBuilder sb3 = new StringBuilder();
                for (int i2 = 0; i2 < group.length(); i2++) {
                    sb3.append("&#160;");
                }
                matcher.appendReplacement(stringBuffer, "$1" + sb3.toString() + "$3");
            }
            matcher.appendTail(stringBuffer);
            sb2 = stringBuffer.toString();
        }
        return z ? sb2.replaceAll("\n", "<br/>") : sb2;
    }

    public static String stripElements(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("<([a-zA-Z]|/).*?>", "");
    }

    public static void accept(Node node, NodeVisitor nodeVisitor) {
        if (node == null || nodeVisitor.isHalted()) {
            return;
        }
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == nodeVisitor.nodeType) {
                nodeVisitor.visit(item);
                if (nodeVisitor.isHalted()) {
                    return;
                }
            }
            accept(item, nodeVisitor);
        }
    }

    
    public static abstract class NodeVisitor {
        static final boolean $assertionsDisabled = false;
        private short nodeType;

        public boolean isHalted() {
            return false;
        }

        public abstract void visit(Node node);

        
        public NodeVisitor(short s) {
            this.nodeType = s;
        }
    }

    public static String wrap(String str, String str2) {
        return wrap(str, null, str2);
    }

    public static String wrap(String str, String str2, String str3) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(str);
        if (str2 != null) {
            sb.append(" xmlns=\"");
            sb.append(str2);
            sb.append("\"");
        }
        sb.append(">");
        sb.append(str3);
        sb.append("</");
        sb.append(str);
        sb.append(">");
        return sb.toString();
    }
}
