package org.fourthline.cling.transport.impl.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.fourthline.cling.transport.spi.ServletContainerAdapter;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;


public class JettyServletContainer implements ServletContainerAdapter {
    protected Server server;
    private static final Logger log = Logger.getLogger(JettyServletContainer.class.getName());
    public static final JettyServletContainer INSTANCE = new JettyServletContainer();

    private JettyServletContainer() {
        resetServer();
    }

    @Override
    public synchronized void setExecutorService(ExecutorService executorService) {
        JettyServletContainer jettyServletContainer = INSTANCE;
        if (jettyServletContainer.server.getThreadPool() == null) {
            jettyServletContainer.server.setThreadPool(new ExecutorThreadPool(executorService) {

                @Override
                public void doStop() throws Exception {
                }
            });
        }
    }

    @Override
    public synchronized int addConnector(String str, int i) throws IOException {
        SocketConnector socketConnector;
        socketConnector = new SocketConnector();
        socketConnector.setHost(str);
        socketConnector.setPort(i);
        socketConnector.open();
        this.server.addConnector(socketConnector);
        if (this.server.isStarted()) {
            try {
                socketConnector.start();
            } catch (Exception e) {
                Logger logger = log;
                logger.severe("Couldn't start connector: " + socketConnector + " " + e);
                throw new RuntimeException(e);
            }
        }
        return socketConnector.getLocalPort();
    }

    public synchronized void removeConnector(String str, int i) {
        Connector[] connectors = this.server.getConnectors();
        int length = connectors.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            }
            Connector connector = connectors[i2];
            if (connector.getHost().equals(str) && connector.getLocalPort() == i) {
                break;
            }
            i2++;
        }
    }

    @Override
    public synchronized void registerServlet(String str, Servlet servlet) {
        if (this.server.getHandler() != null) {
            return;
        }
        Logger logger = log;
        logger.info("Registering UPnP servlet under context path: " + str);
//        ServletContextHandler servletContextHandler = new ServletContextHandler(0);
//        if (str != null && str.length() > 0) {
//            servletContextHandler.setContextPath(str);
//        }
//        servletContextHandler.addServlet(new ServletHolder(servlet), "/*");
//        this.server.setHandler(servletContextHandler);
    }

    @Override
    public synchronized void startIfNotRunning() {
        if (!this.server.isStarted() && !this.server.isStarting()) {
            log.info("Starting Jetty server... ");
            try {
                this.server.start();
            } catch (Exception e) {
                Logger logger = log;
                logger.severe("Couldn't start Jetty server: " + e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void stopIfRunning() {
        if (!this.server.isStopped() && !this.server.isStopping()) {
            log.info("Stopping Jetty server...");
            try {
                this.server.stop();
                resetServer();
            } catch (Exception e) {
                Logger logger = log;
                logger.severe("Couldn't stop Jetty server: " + e);
                throw new RuntimeException(e);
            }
        }
    }

    protected void resetServer() {
        Server server = new Server();
        this.server = server;
        server.setGracefulShutdown(1000);
    }

    public static boolean isConnectionOpen(HttpServletRequest httpServletRequest) {
        return isConnectionOpen(httpServletRequest, " ".getBytes());
    }

    public static boolean isConnectionOpen(HttpServletRequest httpServletRequest, byte[] bArr) {
        Socket socket = (Socket) ((Request) httpServletRequest).getConnection().getEndPoint().getTransport();
        Logger logger = log;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Checking if client connection is still open: " + socket.getRemoteSocketAddress());
        }
        try {
            socket.getOutputStream().write(bArr);
            socket.getOutputStream().flush();
            return true;
        } catch (IOException unused) {
            Logger logger2 = log;
            if (logger2.isLoggable(Level.FINE)) {
                logger2.fine("Client connection has been closed: " + socket.getRemoteSocketAddress());
                return false;
            }
            return false;
        }
    }
}
