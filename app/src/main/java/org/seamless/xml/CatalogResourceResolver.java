package org.seamless.xml;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;


public class CatalogResourceResolver implements LSResourceResolver {
    private static Logger log = Logger.getLogger(CatalogResourceResolver.class.getName());
    private final Map<URI, URL> catalog;

    public CatalogResourceResolver(Map<URI, URL> map) {
        this.catalog = map;
    }

    @Override
    public LSInput resolveResource(String str, String str2, String str3, String str4, String str5) {
        Logger logger = log;
        logger.finest("Trying to resolve system identifier URI in catalog: " + str4);
        URL url = this.catalog.get(URI.create(str4));
        if (url != null) {
            Logger logger2 = log;
            logger2.finest("Loading catalog resource: " + url);
            try {
                Input input = new Input(url.openStream());
                input.setBaseURI(str5);
                input.setSystemId(str4);
                input.setPublicId(str3);
                return input;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Logger logger3 = log;
        logger3.info("System identifier not found in catalog, continuing with default resolution (this most likely means remote HTTP request!): " + str4);
        return null;
    }

    
    private static final class Input implements LSInput {
        InputStream in;

        @Override
        public String getBaseURI() {
            return null;
        }

        @Override
        public boolean getCertifiedText() {
            return false;
        }

        @Override
        public Reader getCharacterStream() {
            return null;
        }

        @Override
        public String getEncoding() {
            return null;
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public String getStringData() {
            return null;
        }

        @Override
        public String getSystemId() {
            return null;
        }

        @Override
        public void setBaseURI(String str) {
        }

        @Override
        public void setByteStream(InputStream inputStream) {
        }

        @Override
        public void setCertifiedText(boolean z) {
        }

        @Override
        public void setCharacterStream(Reader reader) {
        }

        @Override
        public void setEncoding(String str) {
        }

        @Override
        public void setPublicId(String str) {
        }

        @Override
        public void setStringData(String str) {
        }

        @Override
        public void setSystemId(String str) {
        }

        public Input(InputStream inputStream) {
            this.in = inputStream;
        }

        @Override
        public InputStream getByteStream() {
            return this.in;
        }
    }
}
