package com.example.chromecastone.Dlna.model;

import android.util.Log;
import android.util.Xml;

import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


public class TrackMetadata {
    protected static final String TAG = "TrackMetadata";
    public String artURI;
    public String artist;
    public String genre;
    public String id;
    public String itemClass;
    public String res;
    public String title;

    public String toString() {
        return "TrackMetadata [id=" + this.id + ", title=" + this.title + ", artist=" + this.artist + ", genre=" + this.genre + ", artURI=" + this.artURI + "res=" + this.res + ", itemClass=" + this.itemClass + "]";
    }

    public TrackMetadata(String str) {
        parseTrackMetadata(str);
    }

    public TrackMetadata() {
    }

    public TrackMetadata(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this.id = str;
        this.title = str2;
        this.artist = str3;
        this.genre = str4;
        this.artURI = str5;
        this.res = str6;
        this.itemClass = str7;
    }

    private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
        return SAXParserFactory.newInstance().newSAXParser().getXMLReader();
    }

    public void parseTrackMetadata(String str) {
        if (str == null) {
            return;
        }
        Log.d(TAG, "XML : " + str);
        try {
            XMLReader initializeReader = initializeReader();
            initializeReader.setContentHandler(new UpnpItemHandler());
            initializeReader.parse(new InputSource(new StringReader(str)));
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Error while parsing metadata !");
            Log.w(TAG, "XML : " + str);
        }
    }

    public String getXML() {
        XmlSerializer newSerializer = Xml.newSerializer();
        StringWriter stringWriter = new StringWriter();
        try {
            newSerializer.setOutput(stringWriter);
            newSerializer.startDocument(null, null);
            newSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            newSerializer.startTag(null, "DIDL-Lite");
            newSerializer.attribute(null, "xmlns", DIDLContent.NAMESPACE_URI);
            newSerializer.attribute(null, "xmlns:dc", DIDLObject.Property.DC.NAMESPACE.URI);
            newSerializer.attribute(null, "xmlns:upnp", DIDLObject.Property.UPNP.NAMESPACE.URI);
            newSerializer.attribute(null, "xmlns:dlna", DIDLObject.Property.DLNA.NAMESPACE.URI);
            newSerializer.startTag(null, "item");
            newSerializer.attribute(null, "id", "" + this.id);
            newSerializer.attribute(null, "parentID", "");
            newSerializer.attribute(null, "restricted", "1");
            if (this.title != null) {
                newSerializer.startTag(null, "dc:title");
                newSerializer.text(this.title);
                newSerializer.endTag(null, "dc:title");
            }
            if (this.artist != null) {
                newSerializer.startTag(null, "dc:creator");
                newSerializer.text(this.artist);
                newSerializer.endTag(null, "dc:creator");
            }
            if (this.genre != null) {
                newSerializer.startTag(null, "upnp:genre");
                newSerializer.text(this.genre);
                newSerializer.endTag(null, "upnp:genre");
            }
            if (this.artURI != null) {
                newSerializer.startTag(null, "upnp:albumArtURI");
                newSerializer.attribute(null, "dlna:profileID", "JPEG_TN");
                newSerializer.text(this.artURI);
                newSerializer.endTag(null, "upnp:albumArtURI");
            }
            if (this.res != null) {
                newSerializer.startTag(null, "res");
                newSerializer.text(this.res);
                newSerializer.endTag(null, "res");
            }
            if (this.itemClass != null) {
                newSerializer.startTag(null, "upnp:class");
                newSerializer.text(this.itemClass);
                newSerializer.endTag(null, "upnp:class");
            }
            newSerializer.endTag(null, "item");
            newSerializer.endTag(null, "DIDL-Lite");
            newSerializer.endDocument();
            newSerializer.flush();
        } catch (Exception e) {
            Log.e(TAG, "error occurred while creating xml file : " + e.toString());
            e.printStackTrace();
        }
        String stringWriter2 = stringWriter.toString();
        Log.d(TAG, "TrackMetadata : " + stringWriter2);
        return stringWriter2;
    }


    public class UpnpItemHandler extends DefaultHandler {
        private final StringBuffer buffer = new StringBuffer();

        public UpnpItemHandler() {
        }

        @Override
        public void startElement(String str, String str2, String str3, Attributes attributes) throws SAXException {
            this.buffer.setLength(0);
            if (str2.equals("item")) {
                TrackMetadata.this.id = attributes.getValue("id");
            }
        }

        @Override
        public void endElement(String str, String str2, String str3) throws SAXException {
            if (str2.equals("title")) {
                TrackMetadata.this.title = this.buffer.toString();
            } else if (str2.equals("creator")) {
                TrackMetadata.this.artist = this.buffer.toString();
            } else if (str2.equals("genre")) {
                TrackMetadata.this.genre = this.buffer.toString();
            } else if (str2.equals("albumArtURI")) {
                TrackMetadata.this.artURI = this.buffer.toString();
            } else if (str2.equals("class")) {
                TrackMetadata.this.itemClass = this.buffer.toString();
            } else if (str2.equals("res")) {
                TrackMetadata.this.res = this.buffer.toString();
            }
        }

        @Override
        public void characters(char[] cArr, int i, int i2) {
            this.buffer.append(cArr, i, i2);
        }
    }
}
