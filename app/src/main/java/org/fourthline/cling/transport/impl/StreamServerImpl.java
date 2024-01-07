package org.fourthline.cling.transport.impl;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.logging.Logger;


public class StreamServerImpl implements StreamServer<StreamServerConfigurationImpl> {
    private static Logger log = Logger.getLogger(StreamServer.class.getName());
    protected final StreamServerConfigurationImpl configuration;
    protected HttpServer server;

    public StreamServerImpl(StreamServerConfigurationImpl streamServerConfigurationImpl) {
        this.configuration = streamServerConfigurationImpl;
    }

    @Override
    public synchronized void init(InetAddress inetAddress, Router router) throws InitializationException {
        try {
            HttpServer create = HttpServer.create(new InetSocketAddress(inetAddress, this.configuration.getListenPort()), this.configuration.getTcpConnectionBacklog());
            this.server = create;
            create.createContext("/", new RequestHttpHandler(router));
            Logger logger = log;
            logger.info("Created server (for receiving TCP streams) on: " + this.server.getAddress());
        } catch (Exception e) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + e.toString(), e);
        }
    }

    @Override
    public synchronized int getPort() {
        return this.server.getAddress().getPort();
    }

    
    @Override
    public StreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override
    public synchronized void run() {
        log.fine("Starting StreamServer...");
        this.server.start();
    }

    @Override
    public synchronized void stop() {
        log.fine("Stopping StreamServer...");
        HttpServer httpServer = this.server;
        if (httpServer != null) {
            httpServer.stop(1);
        }
    }


    protected class RequestHttpHandler implements HttpHandler {
        private final Router router;

        public RequestHttpHandler(Router router) {
            this.router = router;
        }

        public void handle(final HttpExchange httpExchange) throws IOException {
            Logger logger = StreamServerImpl.log;
            logger.fine("Received HTTP exchange: " + httpExchange.getRequestMethod() + " " + httpExchange.getRequestURI());
            this.router.received(new HttpExchangeUpnpStream(this.router.getProtocolFactory(), httpExchange) {
                @Override
                protected Connection createConnection() {
                    return new HttpServerConnection(httpExchange);
                }
            });
        }
    }

    protected boolean isConnectionOpen(HttpExchange httpExchange) {
        log.warning("Can't check client connection, socket access impossible on JDK webserver!");
        return true;
    }


    protected class HttpServerConnection implements Connection {
        protected HttpExchange exchange;

        public HttpServerConnection(HttpExchange httpExchange) {
            this.exchange = httpExchange;
        }

        @Override
        public boolean isOpen() {
            return StreamServerImpl.this.isConnectionOpen(this.exchange);
        }

        @Override
        public InetAddress getRemoteAddress() {
            if (this.exchange.getRemoteAddress() != null) {
                return this.exchange.getRemoteAddress().getAddress();
            }
            return null;
        }

        @Override
        public InetAddress getLocalAddress() {
            if (this.exchange.getLocalAddress() != null) {
                return this.exchange.getLocalAddress().getAddress();
            }
            return null;
        }
    }
}
