package org.seamless.xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;


public class XmlPullParserUtils {
    private static final Logger log = Logger.getLogger(XmlPullParserUtils.class.getName());
    static XmlPullParserFactory xmlPullParserFactory;

    static {
        try {
            XmlPullParserFactory newInstance = XmlPullParserFactory.newInstance();
            xmlPullParserFactory = newInstance;
            newInstance.setNamespaceAware(true);
        } catch (XmlPullParserException e) {
            log.severe("cannot create XmlPullParserFactory instance: " + e);
        }
    }

    public static XmlPullParser createParser(String str) throws XmlPullParserException {
        XmlPullParser createParser = createParser();
        try {
            createParser.setInput(new ByteArrayInputStream(str.getBytes("UTF-8")), "UTF-8");
            return createParser;
        } catch (UnsupportedEncodingException unused) {
            throw new XmlPullParserException("UTF-8: unsupported encoding");
        }
    }

    public static XmlPullParser createParser() throws XmlPullParserException {
        XmlPullParserFactory xmlPullParserFactory2 = xmlPullParserFactory;
        if (xmlPullParserFactory2 == null) {
            throw new XmlPullParserException("no XML Pull parser factory");
        }
        return xmlPullParserFactory2.newPullParser();
    }

    public static XmlSerializer createSerializer() throws XmlPullParserException {
        XmlPullParserFactory xmlPullParserFactory2 = xmlPullParserFactory;
        if (xmlPullParserFactory2 == null) {
            throw new XmlPullParserException("no XML Pull parser factory");
        }
        return xmlPullParserFactory2.newSerializer();
    }

    public static void setSerializerIndentation(XmlSerializer xmlSerializer, int i) {
        if (i > 0) {
            try {
                xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            } catch (Exception e) {
                Logger logger = log;
                logger.warning("error setting feature of XmlSerializer: " + e);
            }
        }
    }

    public static void skipTag(XmlPullParser xmlPullParser, String str) throws IOException, XmlPullParserException {
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && xmlPullParser.getName().equals(str)) {
                return;
            }
        }
    }

    public static void searchTag(XmlPullParser xmlPullParser, String str) throws IOException, XmlPullParserException {
        while (true) {
            int next = xmlPullParser.next();
            if (next != 1) {
                if (next == 2 && xmlPullParser.getName().equals(str)) {
                    return;
                }
            } else {
                throw new IOException(String.format("tag '%s' not found", str));
            }
        }
    }

    public static void serializeIfNotNullOrEmpty(XmlSerializer xmlSerializer, String str, String str2, String str3) throws Exception {
        if (isNullOrEmpty(str3)) {
            return;
        }
        xmlSerializer.startTag(str, str2);
        xmlSerializer.text(str3);
        xmlSerializer.endTag(str, str2);
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static void serializeIfNotEqual(XmlSerializer xmlSerializer, String str, String str2, Object obj, Object obj2) throws Exception {
        if (obj == null || obj.equals(obj2)) {
            return;
        }
        xmlSerializer.startTag(str, str2);
        xmlSerializer.text(obj.toString());
        xmlSerializer.endTag(str, str2);
    }

    public static String fixXMLEntities(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        boolean z = false;
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '&') {
                String substring = str.substring(i, Math.min(i + 10, str.length()));
                if (!substring.startsWith("&#") && !substring.startsWith("&lt;") && !substring.startsWith("&gt;") && !substring.startsWith("&amp;") && !substring.startsWith("&apos;") && !substring.startsWith("&quot;")) {
                    sb.append("&amp;");
                    z = true;
                } else {
                    sb.append(charAt);
                }
            } else {
                sb.append(charAt);
            }
        }
        if (z) {
            log.warning("fixed badly encoded entities in XML");
        }
        return sb.toString();
    }
}
