package org.fourthline.cling.transport.impl;

import org.eclipse.jetty.server.AsyncContext;
import org.fourthline.cling.model.message.Connection;
import org.fourthline.cling.transport.Router;
import org.fourthline.cling.transport.spi.InitializationException;
import org.fourthline.cling.transport.spi.StreamServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.servlet.AsyncContext;
//import javax.servlet.AsyncEvent;
//import javax.servlet.AsyncListener;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AsyncServletStreamServerImpl implements StreamServer<AsyncServletStreamServerConfigurationImpl> {
    private static final Logger log = Logger.getLogger(StreamServer.class.getName());
    protected final AsyncServletStreamServerConfigurationImpl configuration;
    protected String hostAddress;
    protected int localPort;
    private int mCounter = 0;

    protected boolean isConnectionOpen(HttpServletRequest httpServletRequest) {
        return true;
    }

    static int access$008(AsyncServletStreamServerImpl asyncServletStreamServerImpl) {
        int i = asyncServletStreamServerImpl.mCounter;
        asyncServletStreamServerImpl.mCounter = i + 1;
        return i;
    }

    public AsyncServletStreamServerImpl(AsyncServletStreamServerConfigurationImpl asyncServletStreamServerConfigurationImpl) {
        this.configuration = asyncServletStreamServerConfigurationImpl;
    }

    
    @Override
    public AsyncServletStreamServerConfigurationImpl getConfiguration() {
        return this.configuration;
    }

    @Override
    public synchronized void init(InetAddress inetAddress, Router router) throws InitializationException {
        try {
            Logger logger = log;
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Setting executor service on servlet container adapter");
            }
            getConfiguration().getServletContainerAdapter().setExecutorService(router.getConfiguration().getStreamServerExecutorService());
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Adding connector: " + inetAddress + ":" + getConfiguration().getListenPort());
            }
            this.hostAddress = inetAddress.getHostAddress();
            this.localPort = getConfiguration().getServletContainerAdapter().addConnector(this.hostAddress, getConfiguration().getListenPort());
            getConfiguration().getServletContainerAdapter().registerServlet(router.getConfiguration().getNamespace().getBasePath().getPath(), createServlet(router));
        } catch (Exception e) {
            throw new InitializationException("Could not initialize " + getClass().getSimpleName() + ": " + e.toString(), e);
        }
    }

    @Override
    public synchronized int getPort() {
        return this.localPort;
    }

    @Override
    public synchronized void stop() {
        getConfiguration().getServletContainerAdapter().removeConnector(this.hostAddress, this.localPort);
    }

    @Override
    public void run() {
        getConfiguration().getServletContainerAdapter().startIfNotRunning();
    }

    protected Servlet createServlet(final Router router) {
        return new HttpServlet() {
            @Override
            protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
                final long currentTimeMillis = System.currentTimeMillis();
                final int access$008 = AsyncServletStreamServerImpl.access$008(AsyncServletStreamServerImpl.this);
                if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
                    AsyncServletStreamServerImpl.log.fine(String.format("HttpServlet.service(): id: %3d, request URI: %s", Integer.valueOf(access$008), httpServletRequest.getRequestURI()));
                }
//                AsyncContext startAsync = httpServletRequest.startAsync();
//                startAsync.setTimeout(AsyncServletStreamServerImpl.this.getConfiguration().getAsyncTimeoutSeconds() * 1000);
//                startAsync.addListener(new AsyncListener() {
//                    @Override
//                    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
//                        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
//                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
//                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onTimeout(): id: %3d, duration: %,4d, request: %s", Integer.valueOf(access$008), Long.valueOf(currentTimeMillis2), asyncEvent.getSuppliedRequest()));
//                        }
//                    }
//
//                    @Override
//                    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
//                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
//                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onStartAsync(): id: %3d, request: %s", Integer.valueOf(access$008), asyncEvent.getSuppliedRequest()));
//                        }
//                    }
//
//                    @Override
//                    public void onError(AsyncEvent asyncEvent) throws IOException {
//                        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
//                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
//                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onError(): id: %3d, duration: %,4d, response: %s", Integer.valueOf(access$008), Long.valueOf(currentTimeMillis2), asyncEvent.getSuppliedResponse()));
//                        }
//                    }
//
//                    @Override
//                    public void onComplete(AsyncEvent asyncEvent) throws IOException {
//                        long currentTimeMillis2 = System.currentTimeMillis() - currentTimeMillis;
//                        if (AsyncServletStreamServerImpl.log.isLoggable(Level.FINE)) {
//                            AsyncServletStreamServerImpl.log.fine(String.format("AsyncListener.onComplete(): id: %3d, duration: %,4d, response: %s", Integer.valueOf(access$008), Long.valueOf(currentTimeMillis2), asyncEvent.getSuppliedResponse()));
//                        }
//                    }
//                });
//                router.received(new AsyncServletUpnpStream(router.getProtocolFactory(), startAsync, httpServletRequest) {
//                    @Override
//                    protected Connection createConnection() {
//                        return new AsyncServletConnection(getRequest());
//                    }
//                });
            }
        };
    }


    protected class AsyncServletConnection implements Connection {
        protected HttpServletRequest request;

        public AsyncServletConnection(HttpServletRequest httpServletRequest) {
            this.request = httpServletRequest;
        }

        public HttpServletRequest getRequest() {
            return this.request;
        }

        @Override
        public boolean isOpen() {
            return AsyncServletStreamServerImpl.this.isConnectionOpen(getRequest());
        }

        @Override
        public InetAddress getRemoteAddress() {
            try {
                return InetAddress.getByName(getRequest().getRemoteAddr());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InetAddress getLocalAddress() {
            try {
                return InetAddress.getByName(getRequest().getLocalAddr());
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
