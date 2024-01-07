package com.example.chromecastone.CastServer;

import android.support.v4.media.session.PlaybackStateCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class SimpleWebServer extends NanoHTTPD {
    private static final String LICENCE = "Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n\nRedistribution and use in source and binary forms, with or without\nmodification, are permitted provided that the following conditions\nare met:\n\nRedistributions of source code must retain the above copyright notice,\nthis list of conditions and the following disclaimer. Redistributions in\nbinary form must reproduce the above copyright notice, this list of\nconditions and the following disclaimer in the documentation and/or other\nmaterials provided with the distribution. The name of the author may not\nbe used to endorse or promote products derived from this software without\nspecific prior written permission. \n \nTHIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\nIMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\nOF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\nIN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\nINCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\nNOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\nDATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\nTHEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\nOF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    private final boolean quiet;
    private final List<File> rootDirs;
    public static final List<String> INDEX_FILE_NAMES = new ArrayList<String>() {
        {
            add("index.html");
            add("index.htm");
        }
    };
    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
        {
            put("css", "text/css");
            put("htm", "text/html");
            put("html", "text/html");
            put("xml", "text/xml");
            put("java", "text/x-java-source, text/java");
            put("md", "text/plain");
            put("txt", "text/plain");
            put("asc", "text/plain");
            put("gif", "image/gif");
            put("jpg", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("png", "image/png");
            put("mp3", "audio/mpeg");
            put("m3u", "audio/mpeg-url");
            put("mp4", "video/mp4");
            put("ogv", "video/ogg");
            put("flv", "video/x-flv");
            put("mov", "video/quicktime");
            put("swf", "application/x-shockwave-flash");
            put("js", "application/javascript");
            put("pdf", "application/pdf");
            put("doc", "application/msword");
            put("ogg", "application/x-ogg");
            put("zip", "application/octet-stream");
            put("exe", "application/octet-stream");
            put("class", "application/octet-stream");
        }
    };
    private static Map<String, WebServerPlugin> mimeTypeHandlers = new HashMap();

    public void init() {
    }

    public SimpleWebServer(String str, int i, File file, boolean z) {
        super(str, i);
        this.quiet = z;
        ArrayList arrayList = new ArrayList();
        this.rootDirs = arrayList;
        arrayList.add(file);
        init();
    }

    public SimpleWebServer(String str, int i, List<File> list, boolean z) {
        super(str, i);
        this.quiet = z;
        this.rootDirs = new ArrayList(list);
        init();
    }

    protected static void registerPluginForMimeType(String[] strArr, String str, WebServerPlugin webServerPlugin, Map<String, String> map) {
        if (str == null || webServerPlugin == null) {
            return;
        }
        if (strArr != null) {
            for (String str2 : strArr) {
                int lastIndexOf = str2.lastIndexOf(46);
                if (lastIndexOf >= 0) {
                    MIME_TYPES.put(str2.substring(lastIndexOf + 1).toLowerCase(), str);
                }
            }
            INDEX_FILE_NAMES.addAll(Arrays.asList(strArr));
        }
        mimeTypeHandlers.put(str, webServerPlugin);
        webServerPlugin.initialize(map);
    }

    private File getRootDir() {
        return this.rootDirs.get(0);
    }

    private List<File> getRootDirs() {
        return this.rootDirs;
    }

    private void addWwwRootDir(File file) {
        this.rootDirs.add(file);
    }

    private String encodeUri(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, "/ ", true);
        String str2 = "";
        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            if (nextToken.equals("/")) {
                str2 = str2 + "/";
            } else if (nextToken.equals(" ")) {
                str2 = str2 + "%20";
            } else {
                try {
                    str2 = str2 + URLEncoder.encode(nextToken, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return str2;
    }

    @Override
    public Response serve(IHTTPSession iHTTPSession) {
        Map<String, String> headers = iHTTPSession.getHeaders();
        Map<String, String> parms = iHTTPSession.getParms();
        String uri = iHTTPSession.getUri();
        if (!this.quiet) {
            PrintStream printStream = System.out;
            printStream.println(iHTTPSession.getMethod() + " '" + uri + "' ");
            for (String str : headers.keySet()) {
                PrintStream printStream2 = System.out;
                printStream2.println("  HDR: '" + str + "' = '" + headers.get(str) + "'");
            }
            for (String str2 : parms.keySet()) {
                PrintStream printStream3 = System.out;
                printStream3.println("  PRM: '" + str2 + "' = '" + parms.get(str2) + "'");
            }
        }
        for (File file : getRootDirs()) {
            if (!file.isDirectory()) {
                return getInternalErrorResponse("given path is not a directory (" + file + ").");
            }
        }
        return respond(Collections.unmodifiableMap(headers), iHTTPSession, uri);
    }

    private Response respond(Map<String, String> map, IHTTPSession iHTTPSession, String str) {
        Response serveFile;
        String replace = str.trim().replace(File.separatorChar, '/');
        boolean z = false;
        if (replace.indexOf(63) >= 0) {
            replace = replace.substring(0, replace.indexOf(63));
        }
        String str2 = replace;
        if (str2.startsWith("src/main") || str2.endsWith("src/main") || str2.contains("../")) {
            return getForbiddenResponse("Won't serve ../ for security reasons.");
        }
        File file = null;
        List<File> rootDirs = getRootDirs();
        for (int i = 0; !z && i < rootDirs.size(); i++) {
            file = rootDirs.get(i);
            z = canServeUri(str2, file);
        }
        if (!z) {
            return getNotFoundResponse();
        }
        File file2 = new File(file, str2);
        if (file2.isDirectory() && !str2.endsWith("/")) {
            String str3 = str2 + "/";
            Response createResponse = createResponse(Response.Status.REDIRECT, "text/html", "<html><body>Redirected: <a href=\"" + str3 + "\">" + str3 + "</a></body></html>");
            createResponse.addHeader("Location", str3);
            return createResponse;
        } else if (file2.isDirectory()) {
            String findIndexFileInDirectory = findIndexFileInDirectory(file2);
            if (findIndexFileInDirectory == null) {
                if (file2.canRead()) {
                    return createResponse(Response.Status.OK, "text/html", listDirectory(str2, file2));
                }
                return getForbiddenResponse("No directory listing.");
            }
            return respond(map, iHTTPSession, str2 + findIndexFileInDirectory);
        } else {
            String mimeTypeForFile = getMimeTypeForFile(str2);
            WebServerPlugin webServerPlugin = mimeTypeHandlers.get(mimeTypeForFile);
            if (webServerPlugin != null) {
                serveFile = webServerPlugin.serveFile(str2, map, iHTTPSession, file2, mimeTypeForFile);
                if (serveFile != null && (serveFile instanceof InternalRewrite)) {
                    InternalRewrite internalRewrite = (InternalRewrite) serveFile;
                    return respond(internalRewrite.getHeaders(), iHTTPSession, internalRewrite.getUri());
                }
            } else {
                serveFile = serveFile(str2, map, file2, mimeTypeForFile);
            }
            return serveFile != null ? serveFile : getNotFoundResponse();
        }
    }

    protected Response getNotFoundResponse() {
        return createResponse(Response.Status.NOT_FOUND, "text/plain", "Error 404, file not found.");
    }

    protected Response getForbiddenResponse(String str) {
        Response.Status status = Response.Status.FORBIDDEN;
        return createResponse(status, "text/plain", "FORBIDDEN: " + str);
    }

    protected Response getInternalErrorResponse(String str) {
        Response.Status status = Response.Status.INTERNAL_ERROR;
        return createResponse(status, "text/plain", "INTERNAL ERRROR: " + str);
    }

    private boolean canServeUri(String str, File file) {
        boolean exists = new File(file, str).exists();
        if (exists) {
            return exists;
        }
        WebServerPlugin webServerPlugin = mimeTypeHandlers.get(getMimeTypeForFile(str));
        return webServerPlugin != null ? webServerPlugin.canServeUri(str, file) : exists;
    }

    Response serveFile(String str, Map<String, String> map, File file, String str2) {
        long j;
        try {
            String hexString = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());
            long j2 = -1;
            String str3 = map.get("range");
            long j3 = 0;
            if (str3 == null || !str3.startsWith("bytes=")) {
                j = 0;
            } else {
                String substring = str3.substring(6);
                int indexOf = substring.indexOf(45);
                if (indexOf > 0) {
                    try {
                        j = Long.parseLong(substring.substring(0, indexOf));
                    } catch (NumberFormatException e) {
                        e = e;
                        j = 0;
                    }
                    try {
                        j2 = Long.parseLong(substring.substring(indexOf + 1));
                    } catch (NumberFormatException e2) {
                        e2.printStackTrace();
                        str3 = substring;
                        long length = file.length();
                        if (str3 != null) {
                        }
                        if (!hexString.equals(map.get("if-none-match"))) {
                        }
                    }
                } else {
                    j = 0;
                }
                str3 = substring;
            }
            long length2 = file.length();
            if (str3 != null || j < 0) {
                if (!hexString.equals(map.get("if-none-match"))) {
                    return createResponse(Response.Status.NOT_MODIFIED, str2, "");
                }
                Response createResponse = createResponse(Response.Status.OK, str2, new FileInputStream(file));
                createResponse.addHeader("Content-Length", "" + length2);
                createResponse.addHeader("ETag", hexString);
                return createResponse;
            } else if (j >= length2) {
                Response createResponse2 = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, "text/plain", "");
                createResponse2.addHeader("Content-Range", "bytes 0-0/" + length2);
                createResponse2.addHeader("ETag", hexString);
                return createResponse2;
            } else {
                if (j2 < 0) {
                    j2 = length2 - 1;
                }
                long j4 = (j2 - j) + 1;
                if (j4 >= 0) {
                    j3 = j4;
                }
                long finalJ = j3;
                FileInputStream fileInputStream = new FileInputStream(file) {
                    @Override
                    public int available() throws IOException {
                        return (int) finalJ;
                    }
                };
                fileInputStream.skip(j);
                Response createResponse3 = createResponse(Response.Status.PARTIAL_CONTENT, str2, fileInputStream);
                createResponse3.addHeader("Content-Length", "" + j3);
                createResponse3.addHeader("Content-Range", "bytes " + j + "-" + j2 + "/" + length2);
                createResponse3.addHeader("ETag", hexString);
                return createResponse3;
            }
        } catch (IOException e3) {
            e3.printStackTrace();
            return getForbiddenResponse("Reading file failed.");
        }
    }

    private String getMimeTypeForFile(String str) {
        int lastIndexOf = str.lastIndexOf(46);
        String str2 = lastIndexOf >= 0 ? MIME_TYPES.get(str.substring(lastIndexOf + 1).toLowerCase()) : null;
        return str2 == null ? "application/octet-stream" : str2;
    }

    private Response createResponse(Response.Status status, String str, InputStream inputStream) {
        Response response = new Response(status, str, inputStream);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private Response createResponse(Response.Status status, String str, String str2) {
        Response response = new Response(status, str, str2);
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    private String findIndexFileInDirectory(File file) {
        for (String str : INDEX_FILE_NAMES) {
            if (new File(file, str).exists()) {
                return str;
            }
        }
        return null;
    }

    protected String listDirectory(String str, File file) {
        String substring;
        int lastIndexOf;
        String str2 = "Directory " + str;
        StringBuilder sb = new StringBuilder("<html><head><title>" + str2 + "</title><style><!--\nspan.dirname { font-weight: bold; }\nspan.filesize { font-size: 75%; }\n// -->\n</style></head><body><h1>" + str2 + "</h1>");
        String substring2 = (str.length() <= 1 || (lastIndexOf = (substring = str.substring(0, str.length() - 1)).lastIndexOf(47)) < 0 || lastIndexOf >= substring.length()) ? null : str.substring(0, lastIndexOf + 1);
        List<String> asList = Arrays.asList(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file2, String str3) {
                return new File(file2, str3).isFile();
            }
        }));
        Collections.sort(asList);
        List asList2 = Arrays.asList(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file2, String str3) {
                return new File(file2, str3).isDirectory();
            }
        }));
        Collections.sort(asList2);
        if (substring2 != null || asList2.size() + asList.size() > 0) {
            sb.append("<ul>");
            if (substring2 != null || asList2.size() > 0) {
                sb.append("<section class=\"directories\">");
                if (substring2 != null) {
                    sb.append("<li><a rel=\"directory\" href=\"");
                    sb.append(substring2);
                    sb.append("\"><span class=\"dirname\">..</span></a></b></li>");
                }
                Iterator it = asList2.iterator();
                while (it.hasNext()) {
                    String str3 = ((String) it.next()) + "/";
                    sb.append("<li><a rel=\"directory\" href=\"");
                    sb.append(encodeUri(str + str3));
                    sb.append("\"><span class=\"dirname\">");
                    sb.append(str3);
                    sb.append("</span></a></b></li>");
                }
                sb.append("</section>");
            }
            if (asList.size() > 0) {
                sb.append("<section class=\"files\">");
                for (String str4 : asList) {
                    sb.append("<li><a href=\"");
                    sb.append(encodeUri(str + str4));
                    sb.append("\"><span class=\"filename\">");
                    sb.append(str4);
                    sb.append("</span></a>");
                    long length = new File(file, str4).length();
                    sb.append("&nbsp;<span class=\"filesize\">(");
                    if (length < 1024) {
                        sb.append(length);
                        sb.append(" bytes");
                    } else if (length < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
                        sb.append(length / 1024);
                        sb.append(CastServerService.ROOT_DIR);
                        sb.append(((length % 1024) / 10) % 100);
                        sb.append(" KB");
                    } else {
                        sb.append(length / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED);
                        sb.append(CastServerService.ROOT_DIR);
                        sb.append(((length % PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) / 10) % 100);
                        sb.append(" MB");
                    }
                    sb.append(")</span></li>");
                }
                sb.append("</section>");
            }
            sb.append("</ul>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }
}
