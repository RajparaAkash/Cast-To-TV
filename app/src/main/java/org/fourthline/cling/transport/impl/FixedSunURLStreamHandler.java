package org.fourthline.cling.transport.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.logging.Logger;

public class FixedSunURLStreamHandler implements URLStreamHandlerFactory {

    final private static Logger log = Logger.getLogger(FixedSunURLStreamHandler.class.getName());

    public URLStreamHandler createURLStreamHandler(String protocol) {
        log.fine("Creating new URLStreamHandler for protocol: " + protocol);
        if ("http".equals(protocol)) {
//            return new sun.net.www.protocol.http.Handler() {
//
//                protected java.net.URLConnection openConnection(URL u) throws IOException {
//                    return openConnection(u, null);
//                }
//
//                protected java.net.URLConnection openConnection(URL u, Proxy p) throws IOException {
////                    return new UpnpURLConnection(u, this);
//                    return null;
//                }
//            };
        } else {
            return null;
        }
        return null;
    }

//    static class UpnpURLConnection extends sun.net.www.protocol.http.HttpURLConnection {
//
//        private static final String[] methods = {
//                "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE",
//                "SUBSCRIBE", "UNSUBSCRIBE", "NOTIFY"
//        };
//
//        protected UpnpURLConnection(URL u, sun.net.www.protocol.http.Handler handler) throws IOException {
//            super(u, handler);
//        }
//
//        public UpnpURLConnection(URL u, String host, int port) throws IOException {
//            super(u, host, port);
//        }
//
//        public synchronized OutputStream getOutputStream() throws IOException {
//            OutputStream os;
//            String savedMethod = method;
//            // see if the method supports output
//            if (method.equals("PUT") || method.equals("POST") || method.equals("NOTIFY")) {
//                // fake the method so the superclass method sets its instance variables
//                method = "PUT";
//            } else {
//                // use any method that doesn't support output, an exception will be
//                // raised by the superclass
//                method = "GET";
//            }
//            os = super.getOutputStream();
//            method = savedMethod;
//            return os;
//        }
//
//        public void setRequestMethod(String method) throws ProtocolException {
//            if (connected) {
//                throw new ProtocolException("Cannot reset method once connected");
//            }
//            for (String m : methods) {
//                if (m.equals(method)) {
//                    this.method = method;
//                    return;
//                }
//            }
//            throw new ProtocolException("Invalid UPnP HTTP method: " + method);
//        }
//    }
}