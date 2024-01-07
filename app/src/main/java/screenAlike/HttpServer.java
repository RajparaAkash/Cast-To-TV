package screenAlike;

import com.example.chromecastone.Utils.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public final class HttpServer {
    private static final String DEFAULT_ADDRESS = "/";
    private static final String DEFAULT_ICO_ADDRESS = "/favicon.ico";
    private static final String DEFAULT_PIN_ADDRESS = "/?pin=";
    private static final String DEFAULT_STREAM_ADDRESS = "/screen_stream.mjpeg";
    private static final int SEVER_SOCKET_TIMEOUT = 50;
    private ImageDispatcher mImageDispatcher;
    private ServerSocket mServerSocket;
    private final Object mLock = new Object();
    private String mCurrentStreamAddress = DEFAULT_STREAM_ADDRESS;
    private HttpServerThread mHttpServerThread = new HttpServerThread();

    

    public class HttpServerThread extends Thread {
        HttpServerThread() {
            super(HttpServerThread.class.getSimpleName());
        }

        @Override
        public void run() {
            Socket accept = null;
            String readLine = null;
            while (!isInterrupted()) {
                synchronized (HttpServer.this.mLock) {
                    try {
                        try {
                            accept = HttpServer.this.mServerSocket.accept();
                            readLine = new BufferedReader(new InputStreamReader(accept.getInputStream(), "UTF8")).readLine();
                        } catch (IOException unused) {
                        }
                        if (readLine != null && readLine.startsWith("GET")) {
                            String[] split = readLine.split(" ");
                            if (split.length >= 2) {
                                String str = split[1];
                                if ("/".equals(str)) {
                                    sendMainPage(accept, HttpServer.this.mCurrentStreamAddress);
                                } else if (HttpServer.this.mCurrentStreamAddress.equals(str)) {
                                    HttpServer.this.mImageDispatcher.addClient(accept);
                                } else if (HttpServer.DEFAULT_ICO_ADDRESS.equals(str)) {
                                    sendFavicon(accept);
                                } else {
                                    sendNotFound(accept);
                                }
                            } else {
                                sendNotFound(accept);
                            }
                        }
                        sendNotFound(accept);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                    }
                }
            }
        }

        private void sendMainPage(Socket socket, String str) throws IOException {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
            try {
                outputStreamWriter.write("HTTP/1.1 200 OK\r\n");
                outputStreamWriter.write("Content-Type: text/html\r\n");
                outputStreamWriter.write("Connection: close\r\n");
                outputStreamWriter.write("\r\n");
                outputStreamWriter.write(MyApplication.getAppData().getIndexHtml(str));
                outputStreamWriter.write("\r\n");
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (Throwable th) {
                try {
                    outputStreamWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }

        private void sendFavicon(Socket socket) throws IOException {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
            try {
                outputStreamWriter.write("HTTP/1.1 200 OK\r\n");
                outputStreamWriter.write("Content-Type: image/png\r\n");
                outputStreamWriter.write("Connection: close\r\n");
                outputStreamWriter.write("\r\n");
                outputStreamWriter.flush();
                socket.getOutputStream().write(MyApplication.getAppData().getIcon());
                socket.getOutputStream().flush();
                outputStreamWriter.close();
            } catch (Throwable th) {
                try {
                    outputStreamWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }

        private void sendNotFound(Socket socket) throws IOException {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
            try {
                outputStreamWriter.write("HTTP/1.1 301 Moved Permanently\r\n");
                outputStreamWriter.write("Location: " + MyApplication.getAppData().getServerAddress() + "\r\n");
                outputStreamWriter.write("Connection: close\r\n");
                outputStreamWriter.write("\r\n");
                outputStreamWriter.flush();
                outputStreamWriter.close();
            } catch (Throwable th) {
                try {
                    outputStreamWriter.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }
    }

    public void start() {
        if (this.mHttpServerThread.isAlive()) {
            return;
        }
        this.mCurrentStreamAddress = DEFAULT_STREAM_ADDRESS;
        try {
            ServerSocket serverSocket = new ServerSocket(MyApplication.getAppData().getServerPort(), 4, MyApplication.getAppData().getIpAddress());
            this.mServerSocket = serverSocket;
            serverSocket.setSoTimeout(50);
            ImageDispatcher imageDispatcher = new ImageDispatcher();
            this.mImageDispatcher = imageDispatcher;
            imageDispatcher.start();
            this.mHttpServerThread.start();
        } catch (IOException unused) {
        }
    }

    public void stop(byte[] bArr) {
        if (this.mHttpServerThread.isAlive()) {
            this.mHttpServerThread.interrupt();
            synchronized (this.mLock) {
                this.mImageDispatcher.stop(bArr);
                this.mImageDispatcher = null;
                try {
                    this.mServerSocket.close();
                } catch (IOException unused) {
                }
                this.mServerSocket = null;
                this.mHttpServerThread = new HttpServerThread();
            }
        }
    }

    private String getRandomStreamAddress(String str) {
        Random random = new Random(Long.parseLong(str));
        char[] cArr = new char[10];
        for (int i = 0; i < 10; i++) {
            cArr[i] = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(random.nextInt(62));
        }
        return "/screen_stream_" + String.valueOf(cArr) + ".mjpeg";
    }
}
