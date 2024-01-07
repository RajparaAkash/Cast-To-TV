package fi.iki.elonen;

import com.example.chromecastone.Dlna.model.upnp.MediaCompleteListener;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;


public abstract class NanoHTTPD {
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    public static final String MIME_HTML = "text/html";
    public static final String MIME_PLAINTEXT = "text/plain";
    private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
    private AsyncRunner asyncRunner;
    private final String hostname;
    MediaCompleteListener mediaCompleteListener;
    private final int myPort;
    private ServerSocket myServerSocket;
    private Thread myThread;
    private TempFileManagerFactory tempFileManagerFactory;


    public interface AsyncRunner {
        void exec(Runnable runnable);
    }


    public interface TempFile {
        void delete() throws Exception;

        String getName();

        OutputStream open() throws Exception;
    }


    public interface TempFileManager {
        void clear();

        TempFile createTempFile() throws Exception;
    }


    public interface TempFileManagerFactory {
        TempFileManager create();
    }

    public abstract Response serve(String str, Method method, Map<String, String> map, Map<String, String> map2, Map<String, String> map3);

    public NanoHTTPD(int i) {
        this(null, i);
    }

    public NanoHTTPD(String str, int i) {
        this.hostname = str;
        this.myPort = i;
        setTempFileManagerFactory(new DefaultTempFileManagerFactory());
        setAsyncRunner(new DefaultAsyncRunner());
    }

    public void setOnMediaCompleteListener(MediaCompleteListener mediaCompleteListener) {
        this.mediaCompleteListener = mediaCompleteListener;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        this.myServerSocket = serverSocket;
        if (!serverSocket.isBound()) {
            this.myServerSocket.bind(this.hostname != null ? new InetSocketAddress(this.hostname, this.myPort) : new InetSocketAddress(this.myPort));
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public final void run() {
                NanoHTTPD.this.m296lambda$start$0$fiikielonenNanoHTTPD();
            }
        });
        this.myThread = thread;
        thread.setDaemon(true);
        this.myThread.setName("NanoHttpd Main Listener");
        this.myThread.start();
    }

    
    public void m296lambda$start$0$fiikielonenNanoHTTPD() {
        do {
            try {
                final Socket accept = this.myServerSocket.accept();
                final InputStream inputStream = accept.getInputStream();
                if (inputStream == null) {
                    safeClose(accept);
                } else {
                    this.asyncRunner.exec(new Runnable() { 
                        @Override
                        public void run() {
                            OutputStream outputStream = null;
                            try {
                                try {
                                    outputStream = accept.getOutputStream();
                                    HTTPSession hTTPSession = new HTTPSession(NanoHTTPD.this.tempFileManagerFactory.create(), inputStream, outputStream);
                                    while (!accept.isClosed()) {
                                        hTTPSession.execute();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (Throwable th) {
                                if (NanoHTTPD.this.mediaCompleteListener != null) {
                                    NanoHTTPD.this.mediaCompleteListener.onMediaComplete();
                                }
                                NanoHTTPD.safeClose(outputStream);
                                NanoHTTPD.safeClose(inputStream);
                                NanoHTTPD.safeClose(accept);
                                throw th;
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!this.myServerSocket.isClosed());
        try {
            if (this.myServerSocket.isClosed()) {
                start();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void stop() {
        try {
            safeClose(this.myServerSocket);
            Thread thread = this.myThread;
            if (thread != null) {
                thread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Response serve(HTTPSession hTTPSession) {
        HashMap hashMap = new HashMap();
        try {
            hTTPSession.parseBody(hashMap);
            return serve(hTTPSession.getUri(), hTTPSession.getMethod(), hTTPSession.getHeaders(), hTTPSession.getParms(), hashMap);
        } catch (ResponseException e) {
            return new Response(e.getStatus(), "text/plain", e.getMessage());
        } catch (IOException e2) {
            Response.Status status = Response.Status.INTERNAL_ERROR;
            return new Response(status, "text/plain", "SERVER INTERNAL ERROR: IOException: " + e2.getMessage());
        }
    }

    protected String decodePercent(String str) {
        try {
            return URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected Map<String, List<String>> decodeParameters(Map<String, String> map) {
        return decodeParameters(map.get(QUERY_STRING_PARAMETER));
    }

    protected Map<String, List<String>> decodeParameters(String str) {
        HashMap hashMap = new HashMap();
        if (str != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str, "&");
            while (stringTokenizer.hasMoreTokens()) {
                String nextToken = stringTokenizer.nextToken();
                int indexOf = nextToken.indexOf(61);
                String trim = (indexOf >= 0 ? decodePercent(nextToken.substring(0, indexOf)) : decodePercent(nextToken)).trim();
                if (!hashMap.containsKey(trim)) {
                    hashMap.put(trim, new ArrayList());
                }
                String decodePercent = indexOf >= 0 ? decodePercent(nextToken.substring(indexOf + 1)) : null;
                if (decodePercent != null) {
                    ((List) hashMap.get(trim)).add(decodePercent);
                }
            }
        }
        return hashMap;
    }


    public enum Method {
        GET,
        PUT,
        POST,
        DELETE,
        HEAD;

        static Method lookup(String str) {
            Method[] values;
            for (Method method : values()) {
                if (method.toString().equalsIgnoreCase(str)) {
                    return method;
                }
            }
            return null;
        }
    }

    public void setAsyncRunner(AsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }


    public static class DefaultAsyncRunner implements AsyncRunner {
        private long requestCount;

        @Override
        public void exec(Runnable runnable) {
            this.requestCount++;
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("NanoHttpd Request Processor (#" + this.requestCount + ")");
            thread.start();
        }
    }

    public void setTempFileManagerFactory(TempFileManagerFactory tempFileManagerFactory) {
        this.tempFileManagerFactory = tempFileManagerFactory;
    }


    private class DefaultTempFileManagerFactory implements TempFileManagerFactory {
        private DefaultTempFileManagerFactory() {
        }

        @Override
        public TempFileManager create() {
            return new DefaultTempFileManager();
        }
    }


    public static class DefaultTempFileManager implements TempFileManager {
        private final String tmpdir = System.getProperty("java.io.tmpdir");
        private final List<TempFile> tempFiles = new ArrayList();

        @Override
        public TempFile createTempFile() throws Exception {
            DefaultTempFile defaultTempFile = new DefaultTempFile(this.tmpdir);
            this.tempFiles.add(defaultTempFile);
            return defaultTempFile;
        }

        @Override
        public void clear() {
            for (TempFile tempFile : this.tempFiles) {
                try {
                    tempFile.delete();
                } catch (Exception unused) {
                }
            }
            this.tempFiles.clear();
        }
    }


    public static class DefaultTempFile implements TempFile {
        private File file;
        private OutputStream fstream;

        public DefaultTempFile(String str) throws IOException {
            this.file = File.createTempFile("NanoHTTPD-", "", new File(str));
            this.fstream = new FileOutputStream(this.file);
        }

        @Override
        public OutputStream open() throws Exception {
            return this.fstream;
        }

        @Override
        public void delete() throws Exception {
            NanoHTTPD.safeClose(this.fstream);
            this.file.delete();
        }

        @Override
        public String getName() {
            return this.file.getAbsolutePath();
        }
    }


    public static class Response {
        private InputStream data;
        private Map<String, String> header;
        private String mimeType;
        private Method requestMethod;
        private Status status;

        public Response(String str) {
            this(Status.OK, "text/html", str);
        }

        public Response(Status status, String str, InputStream inputStream) {
            this.header = new HashMap();
            this.status = status;
            this.mimeType = str;
            this.data = inputStream;
        }

        public Response(Status status, String str, String str2) {
            ByteArrayInputStream byteArrayInputStream;
            this.header = new HashMap();
            this.status = status;
            this.mimeType = str;
            if (str2 != null) {
                try {
                    byteArrayInputStream = new ByteArrayInputStream(str2.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                byteArrayInputStream = null;
            }
            this.data = byteArrayInputStream;
        }

        public void addHeader(String str, String str2) {
            this.header.put(str, str2);
        }

        
        public void send(OutputStream outputStream) {
            String str = this.mimeType;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                if (this.status == null) {
                    throw new Error("sendResponse(): Status can't be null.");
                }
                PrintWriter printWriter = new PrintWriter(outputStream);
                printWriter.print("HTTP/1.1 " + this.status.getDescription() + " \r\n");
                if (str != null) {
                    printWriter.print("Content-Type: " + str + "\r\n");
                }
                Map<String, String> map = this.header;
                if (map == null || map.get("Date") == null) {
                    printWriter.print("Date: " + simpleDateFormat.format(new Date()) + "\r\n");
                }
                Map<String, String> map2 = this.header;
                if (map2 != null) {
                    for (String str2 : map2.keySet()) {
                        printWriter.print(str2 + ": " + this.header.get(str2) + "\r\n");
                    }
                }
                InputStream inputStream = this.data;
                int available = inputStream != null ? inputStream.available() : -1;
                if (available > 0) {
                    printWriter.print("Connection: keep-alive\r\n");
                    printWriter.print("Content-Length: " + available + "\r\n");
                }
                printWriter.print("\r\n");
                printWriter.flush();
                if (this.requestMethod != Method.HEAD && this.data != null) {
                    byte[] bArr = new byte[16384];
                    while (available > 0) {
                        int read = this.data.read(bArr, 0, available > 16384 ? 16384 : available);
                        if (read <= 0) {
                            break;
                        }
                        outputStream.write(bArr, 0, read);
                        available -= read;
                    }
                }
                outputStream.flush();
                NanoHTTPD.safeClose(this.data);
            } catch (IOException unused) {
            }
        }

        public Status getStatus() {
            return this.status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public void setMimeType(String str) {
            this.mimeType = str;
        }

        public InputStream getData() {
            return this.data;
        }

        public void setData(InputStream inputStream) {
            this.data = inputStream;
        }

        public Method getRequestMethod() {
            return this.requestMethod;
        }

        public void setRequestMethod(Method method) {
            this.requestMethod = method;
        }


        public enum Status {
            OK(200, "OK"),
            CREATED(201, "Created"),
            ACCEPTED(202, "Accepted"),
            NO_CONTENT(204, "No Content"),
            PARTIAL_CONTENT(206, "Partial Content"),
            REDIRECT(301, "Moved Permanently"),
            NOT_MODIFIED(304, "Not Modified"),
            BAD_REQUEST(400, "Bad Request"),
            UNAUTHORIZED(401, "Unauthorized"),
            FORBIDDEN(403, "Forbidden"),
            NOT_FOUND(404, "Not Found"),
            RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
            INTERNAL_ERROR(500, "Internal Server Error");
            
            private final String description;
            private final int requestStatus;

            Status(int i, String str) {
                this.requestStatus = i;
                this.description = str;
            }

            public int getRequestStatus() {
                return this.requestStatus;
            }

            public String getDescription() {
                return "" + this.requestStatus + " " + this.description;
            }
        }
    }

    

    public class HTTPSession {
        public static final int BUFSIZE = 8192;
        private Map<String, String> headers;
        private InputStream inputStream;
        private Method method;
        private final OutputStream outputStream;
        private Map<String, String> parms;
        private int rlen;
        private int splitbyte;
        private final TempFileManager tempFileManager;
        private String uri;

        public HTTPSession(TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
            this.tempFileManager = tempFileManager;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public void execute() throws IOException {
            byte[] bArr = new byte[0];
            int read = 0;
            try {
                try {
                    try {
                        bArr = new byte[8192];
                        this.splitbyte = 0;
                        this.rlen = 0;
                        read = this.inputStream.read(bArr, 0, 8192);
                    } catch (IOException e2) {
                        Response.Status status = Response.Status.INTERNAL_ERROR;
                        new Response(status, "text/plain", "SERVER INTERNAL ERROR: IOException: " + e2.getMessage()).send(this.outputStream);
                        NanoHTTPD.safeClose(this.outputStream);
                    }
                    if (read == -1) {
                        throw new SocketException();
                    }
                    while (read > 0) {
                        int i = this.rlen + read;
                        this.rlen = i;
                        int findHeaderEnd = findHeaderEnd(bArr, i);
                        this.splitbyte = findHeaderEnd;
                        if (findHeaderEnd > 0) {
                            break;
                        }
                        InputStream inputStream = this.inputStream;
                        int i2 = this.rlen;
                        read = inputStream.read(bArr, i2, 8192 - i2);
                    }
                    if (this.splitbyte < this.rlen) {
                        int i3 = this.splitbyte;
                        this.inputStream = new SequenceInputStream(new ByteArrayInputStream(bArr, i3, this.rlen - i3), this.inputStream);
                    }
                    this.parms = new HashMap();
                    this.headers = new HashMap();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArr, 0, this.rlen)));
                    HashMap hashMap = new HashMap();
                    decodeHeader(bufferedReader, hashMap, this.parms, this.headers);
                    Method lookup = Method.lookup((String) hashMap.get("method"));
                    this.method = lookup;
                    if (lookup == null) {
                        throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error.");
                    }
                    this.uri = (String) hashMap.get("uri");
                    Response serve = NanoHTTPD.this.serve(this);
                    if (serve == null) {
                        throw new ResponseException(Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                    }
                    serve.setRequestMethod(this.method);
                    serve.send(this.outputStream);
                } catch (SocketException e3) {
                    throw e3;
                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            } finally {
                this.tempFileManager.clear();
            }
        }

        public void parseBody(Map<String, String> map) throws IOException, ResponseException {
            BufferedReader bufferedReader;
            RandomAccessFile tmpBucket = null;
            int i = 0;
            int i2 = 0;
            long j;
            MappedByteBuffer map2 = null;
            String str;
            RandomAccessFile randomAccessFile = null;
            StringTokenizer stringTokenizer = null;
            try {
                tmpBucket = getTmpBucket();
                try {
                    if (this.headers.containsKey("content-length")) {
                        j = Integer.parseInt(this.headers.get("content-length"));
                    } else {
                        j = this.splitbyte < this.rlen ? i2 - i : 0L;
                    }
                    byte[] bArr = new byte[512];
                    while (this.rlen >= 0 && j > 0) {
                        int read = this.inputStream.read(bArr, 0, 512);
                        this.rlen = read;
                        j -= read;
                        if (read > 0) {
                            tmpBucket.write(bArr, 0, read);
                        }
                    }
                    map2 = tmpBucket.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, tmpBucket.length());
                    tmpBucket.seek(0L);
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(tmpBucket.getFD())));
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = null;
                }
            } catch (Throwable th2) {
                bufferedReader = null;
            }
            try {
                if (Method.POST.equals(this.method)) {
                    String str2 = this.headers.get("content-type");
                    String str3 = "";
                    if (str2 != null) {
                        stringTokenizer = new StringTokenizer(str2, ",; ");
                        if (stringTokenizer.hasMoreTokens()) {
                            str = stringTokenizer.nextToken();
                            if (!"multipart/form-data".equalsIgnoreCase(str)) {
                                if (!stringTokenizer.hasMoreTokens()) {
                                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                                }
                                String substring = str2.substring(str2.indexOf("boundary=") + 9, str2.length());
                                decodeMultipartData((substring.startsWith("\"") && substring.startsWith("\"")) ? substring.substring(1, substring.length() - 1) : substring, map2, bufferedReader, this.parms, map);
                            } else {
                                char[] cArr = new char[512];
                                for (int read2 = bufferedReader.read(cArr); read2 >= 0 && !str3.endsWith("\r\n"); read2 = bufferedReader.read(cArr)) {
                                    str3 = str3 + String.valueOf(cArr, 0, read2);
                                }
                                decodeParms(str3.trim(), this.parms);
                            }
                        }
                    }
                    str = "";
                    if (!"multipart/form-data".equalsIgnoreCase(str)) {
                    }
                } else if (Method.PUT.equals(this.method)) {
                    map.put("content", saveTmpFile(map2, 0, map2.limit()));
                }
                NanoHTTPD.safeClose(tmpBucket);
                NanoHTTPD.safeClose(bufferedReader);
            } catch (Throwable th3) {
                randomAccessFile = tmpBucket;
                NanoHTTPD.safeClose(randomAccessFile);
                NanoHTTPD.safeClose(bufferedReader);
                throw th3;
            }
        }

        private void decodeHeader(BufferedReader bufferedReader, Map<String, String> map, Map<String, String> map2, Map<String, String> map3) throws ResponseException {
            String decodePercent;
            try {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    return;
                }
                StringTokenizer stringTokenizer = new StringTokenizer(readLine);
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                }
                map.put("method", stringTokenizer.nextToken());
                if (!stringTokenizer.hasMoreTokens()) {
                    throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                }
                String nextToken = stringTokenizer.nextToken();
                int indexOf = nextToken.indexOf(63);
                if (indexOf >= 0) {
                    decodeParms(nextToken.substring(indexOf + 1), map2);
                    decodePercent = NanoHTTPD.this.decodePercent(nextToken.substring(0, indexOf));
                } else {
                    decodePercent = NanoHTTPD.this.decodePercent(nextToken);
                }
                if (stringTokenizer.hasMoreTokens()) {
                    String readLine2 = bufferedReader.readLine();
                    while (readLine2 != null && readLine2.trim().length() > 0) {
                        int indexOf2 = readLine2.indexOf(58);
                        if (indexOf2 >= 0) {
                            map3.put(readLine2.substring(0, indexOf2).trim().toLowerCase(), readLine2.substring(indexOf2 + 1).trim());
                        }
                        readLine2 = bufferedReader.readLine();
                    }
                }
                map.put("uri", decodePercent);
            } catch (IOException e) {
                Response.Status status = Response.Status.INTERNAL_ERROR;
                throw new ResponseException(status, "SERVER INTERNAL ERROR: IOException: " + e.getMessage(), e);
            }
        }

        private void decodeMultipartData(String str, ByteBuffer byteBuffer, BufferedReader bufferedReader, Map<String, String> map, Map<String, String> map2) throws ResponseException {
            String readLine;
            int stripMultipartHeaders = 0;
            Map<String, String> map3;
            int indexOf = 0;
            try {
                int[] boundaryPositions = getBoundaryPositions(byteBuffer, str.getBytes());
                int i = 1;
                for (String readLine2 = bufferedReader.readLine(); readLine2 != null; readLine2 = readLine) {
                    if (!readLine2.contains(str)) {
                        throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html");
                    }
                    i++;
                    HashMap hashMap = new HashMap();
                    readLine = bufferedReader.readLine();
                    while (readLine != null && readLine.trim().length() > 0) {
                        int indexOf2 = readLine.indexOf(58);
                        if (indexOf2 != -1) {
                            hashMap.put(readLine.substring(0, indexOf2).trim().toLowerCase(), readLine.substring(indexOf2 + 1).trim());
                        }
                        readLine = bufferedReader.readLine();
                    }
                    if (readLine != null) {
                        String str2 = (String) hashMap.get("content-disposition");
                        if (str2 == null) {
                            throw new ResponseException(Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html");
                        }
                        StringTokenizer stringTokenizer = new StringTokenizer(str2, "; ");
                        HashMap hashMap2 = new HashMap();
                        while (stringTokenizer.hasMoreTokens()) {
                            String nextToken = stringTokenizer.nextToken();
                            int indexOf3 = nextToken.indexOf(61);
                            if (indexOf3 != -1) {
                                hashMap2.put(nextToken.substring(0, indexOf3).trim().toLowerCase(), nextToken.substring(indexOf3 + 1).trim());
                            }
                        }
                        String str3 = (String) hashMap2.get("name");
                        String substring = str3.substring(1, str3.length() - 1);
                        String str4 = "";
                        if (hashMap.get("content-type") == null) {
                            while (readLine != null && !readLine.contains(str)) {
                                readLine = bufferedReader.readLine();
                                if (readLine != null) {
                                    str4 = readLine.indexOf(str) == -1 ? str4 + readLine : str4 + readLine.substring(0, indexOf - 2);
                                }
                            }
                            map3 = map;
                        } else if (i > boundaryPositions.length) {
                            throw new ResponseException(Response.Status.INTERNAL_ERROR, "Error processing request");
                        } else {
                            map2.put(substring, saveTmpFile(byteBuffer, stripMultipartHeaders(byteBuffer, boundaryPositions[i - 2]), (boundaryPositions[i - 1] - stripMultipartHeaders) - 4));
                            String str5 = (String) hashMap2.get("filename");
                            str4 = str5.substring(1, str5.length() - 1);
                            do {
                                readLine = bufferedReader.readLine();
                                if (readLine == null) {
                                    break;
                                }
                            } while (!readLine.contains(str));
                            map3 = map;
                        }
                        map3.put(substring, str4);
                    }
                }
            } catch (IOException e) {
                throw new ResponseException(Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + e.getMessage(), e);
            }
        }

        private int findHeaderEnd(byte[] bArr, int i) {
            int i2 = 0;
            while (true) {
                int i3 = i2 + 3;
                if (i3 >= i) {
                    return 0;
                }
                if (bArr[i2] == 13 && bArr[i2 + 1] == 10 && bArr[i2 + 2] == 13 && bArr[i3] == 10) {
                    return i2 + 4;
                }
                i2++;
            }
        }

        private int[] getBoundaryPositions(ByteBuffer byteBuffer, byte[] bArr) {
            ArrayList arrayList = new ArrayList();
            int i = 0;
            int i2 = 0;
            int i3 = -1;
            while (i < byteBuffer.limit()) {
                if (byteBuffer.get(i) == bArr[i2]) {
                    if (i2 == 0) {
                        i3 = i;
                    }
                    i2++;
                    if (i2 == bArr.length) {
                        arrayList.add(Integer.valueOf(i3));
                    } else {
                        i++;
                    }
                } else {
                    i -= i2;
                }
                i2 = 0;
                i3 = -1;
                i++;
            }
            int size = arrayList.size();
            int[] iArr = new int[size];
            for (int i4 = 0; i4 < size; i4++) {
                iArr[i4] = ((Integer) arrayList.get(i4)).intValue();
            }
            return iArr;
        }

        private String saveTmpFile(ByteBuffer byteBuffer, int i, int i2) {
            if (i2 > 0) {
                FileOutputStream fileOutputStream = null;
                try {
                    try {
                        TempFile createTempFile = this.tempFileManager.createTempFile();
                        ByteBuffer duplicate = byteBuffer.duplicate();
                        FileOutputStream fileOutputStream2 = new FileOutputStream(createTempFile.getName());
                        try {
                            FileChannel channel = fileOutputStream2.getChannel();
                            duplicate.position(i).limit(i + i2);
                            channel.write(duplicate.slice());
                            String name = createTempFile.getName();
                            NanoHTTPD.safeClose(fileOutputStream2);
                            return name;
                        } catch (Exception e) {
                            e = e;
                            fileOutputStream = fileOutputStream2;
                            PrintStream printStream = System.err;
                            printStream.println("Error: " + e.getMessage());
                            NanoHTTPD.safeClose(fileOutputStream);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            fileOutputStream = fileOutputStream2;
                            NanoHTTPD.safeClose(fileOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                    }
                } catch (Exception e2) {
                    e2.getMessage();
                }
            }
            return "";
        }

        private RandomAccessFile getTmpBucket() {
            try {
                return new RandomAccessFile(this.tempFileManager.createTempFile().getName(), "rw");
            } catch (Exception e) {
                PrintStream printStream = System.err;
                printStream.println("Error: " + e.getMessage());
                return null;
            }
        }

        private int stripMultipartHeaders(ByteBuffer byteBuffer, int i) {
            while (i < byteBuffer.limit()) {
                if (byteBuffer.get(i) == 13) {
                    i++;
                    if (byteBuffer.get(i) == 10) {
                        i++;
                        if (byteBuffer.get(i) == 13) {
                            i++;
                            if (byteBuffer.get(i) == 10) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                i++;
            }
            return i + 1;
        }

        private void decodeParms(String str, Map<String, String> map) {
            if (str == null) {
                map.put(NanoHTTPD.QUERY_STRING_PARAMETER, "");
                return;
            }
            map.put(NanoHTTPD.QUERY_STRING_PARAMETER, str);
            StringTokenizer stringTokenizer = new StringTokenizer(str, "&");
            while (stringTokenizer.hasMoreTokens()) {
                String nextToken = stringTokenizer.nextToken();
                int indexOf = nextToken.indexOf(61);
                if (indexOf >= 0) {
                    map.put(NanoHTTPD.this.decodePercent(nextToken.substring(0, indexOf)).trim(), NanoHTTPD.this.decodePercent(nextToken.substring(indexOf + 1)));
                } else {
                    map.put(NanoHTTPD.this.decodePercent(nextToken).trim(), "");
                }
            }
        }

        public final Map<String, String> getParms() {
            return this.parms;
        }

        public final Map<String, String> getHeaders() {
            return this.headers;
        }

        public final String getUri() {
            return this.uri;
        }

        public final Method getMethod() {
            return this.method;
        }

        public final InputStream getInputStream() {
            return this.inputStream;
        }
    }

    

    public static final class ResponseException extends Exception {
        private final Response.Status status;

        public ResponseException(Response.Status status, String str) {
            super(str);
            this.status = status;
        }

        public ResponseException(Response.Status status, String str, Exception exc) {
            super(str, exc);
            this.status = status;
        }

        public Response.Status getStatus() {
            return this.status;
        }
    }

    private static final void safeClose(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException unused) {
            }
        }
    }

    
    public static final void safeClose(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException unused) {
            }
        }
    }

    
    public static final void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    public final int getListeningPort() {
        ServerSocket serverSocket = this.myServerSocket;
        if (serverSocket == null) {
            return -1;
        }
        return serverSocket.getLocalPort();
    }

    public final boolean wasStarted() {
        return (this.myServerSocket == null || this.myThread == null) ? false : true;
    }

    public final boolean isAlive() {
        return wasStarted() && !this.myServerSocket.isClosed() && this.myThread.isAlive();
    }
}
