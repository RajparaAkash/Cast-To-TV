package org.fourthline.cling.transport.impl;

import com.sun.net.httpserver.HttpExchange;

import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.model.message.StreamRequestMessage;
import org.fourthline.cling.model.message.StreamResponseMessage;
import org.fourthline.cling.model.message.UpnpHeaders;
import org.fourthline.cling.model.message.UpnpMessage;
import org.fourthline.cling.model.message.UpnpRequest;
import org.fourthline.cling.protocol.ProtocolFactory;
import org.fourthline.cling.transport.spi.UpnpStream;
import org.seamless.util.Exceptions;
import org.seamless.util.io.IO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class HttpExchangeUpnpStream extends UpnpStream {
    private static Logger log = Logger.getLogger(UpnpStream.class.getName());
    private HttpExchange httpExchange;

    protected abstract Connection createConnection();

    public HttpExchangeUpnpStream(ProtocolFactory protocolFactory, HttpExchange httpExchange) {
        super(protocolFactory);
        this.httpExchange = httpExchange;
    }

    public HttpExchange getHttpExchange() {
        return this.httpExchange;
    }

    @Override
    public void run() {
        InputStream requestBody = null;
        try {
            Logger logger = log;
            logger.fine("Processing HTTP request: " + getHttpExchange().getRequestMethod() + " " + getHttpExchange().getRequestURI());
            StreamRequestMessage streamRequestMessage = new StreamRequestMessage(UpnpRequest.Method.getByHttpName(getHttpExchange().getRequestMethod()), getHttpExchange().getRequestURI());
            if (((UpnpRequest) streamRequestMessage.getOperation()).getMethod().equals(UpnpRequest.Method.UNKNOWN)) {
                Logger logger2 = log;
                logger2.fine("Method not supported by UPnP stack: " + getHttpExchange().getRequestMethod());
                throw new RuntimeException("Method not supported: " + getHttpExchange().getRequestMethod());
            }
            ((UpnpRequest) streamRequestMessage.getOperation()).setHttpMinorVersion(getHttpExchange().getProtocol().toUpperCase(Locale.ROOT).equals("HTTP/1.1") ? 1 : 0);
            Logger logger3 = log;
            logger3.fine("Created new request message: " + streamRequestMessage);
            streamRequestMessage.setConnection(createConnection());
            streamRequestMessage.setHeaders(new UpnpHeaders((Map<String, List<String>>) getHttpExchange().getRequestHeaders()));
            InputStream inputStream = null;
            try {
                requestBody = getHttpExchange().getRequestBody();
            } catch (Throwable th) {
                th = th;
            }
            try {
                byte[] readBytes = IO.readBytes(requestBody);
                if (requestBody != null) {
                    requestBody.close();
                }
                Logger logger4 = log;
                logger4.fine("Reading request body bytes: " + readBytes.length);
                if (readBytes.length > 0 && streamRequestMessage.isContentTypeMissingOrText()) {
                    log.fine("Request contains textual entity body, converting then setting string on message");
                    streamRequestMessage.setBodyCharacters(readBytes);
                } else if (readBytes.length > 0) {
                    log.fine("Request contains binary entity body, setting bytes on message");
                    streamRequestMessage.setBody(UpnpMessage.BodyType.BYTES, readBytes);
                } else {
                    log.fine("Request did not contain entity body");
                }
                StreamResponseMessage process = process(streamRequestMessage);
                if (process != null) {
                    Logger logger5 = log;
                    logger5.fine("Preparing HTTP response message: " + process);
                    getHttpExchange().getResponseHeaders().putAll(process.getHeaders());
                    byte[] bodyBytes = process.hasBody() ? process.getBodyBytes() : null;
                    int length = bodyBytes != null ? bodyBytes.length : -1;
                    Logger logger6 = log;
                    logger6.fine("Sending HTTP response message: " + process + " with content length: " + length);
                    getHttpExchange().sendResponseHeaders(process.getOperation().getStatusCode(), (long) length);
                    if (length > 0) {
                        log.fine("Response message has body, writing bytes to stream...");
                        OutputStream responseBody = getHttpExchange().getResponseBody();
                        IO.writeBytes(responseBody, bodyBytes);
                        responseBody.flush();
                        if (responseBody != null) {
                            responseBody.close();
                        }
                    }
                } else {
                    log.fine("Sending HTTP response status: 404");
                    getHttpExchange().sendResponseHeaders(404, -1L);
                }
                responseSent(process);
            } catch (Throwable th2) {
                inputStream = requestBody;
                if (inputStream != null) {
                    inputStream.close();
                }
                throw th2;
            }
        } catch (Throwable th3) {
            Logger logger7 = log;
            logger7.fine("Exception occured during UPnP stream processing: " + th3);
            if (log.isLoggable(Level.FINE)) {
                Logger logger8 = log;
                Level level = Level.FINE;
                logger8.log(level, "Cause: " + Exceptions.unwrap(th3), Exceptions.unwrap(th3));
            }
            try {
                this.httpExchange.sendResponseHeaders(500, -1L);
            } catch (IOException e) {
                Logger logger9 = log;
                logger9.warning("Couldn't send error response: " + e);
            }
            responseException(th3);
        }
    }
}
