package fi.iki.elonen;

import android.support.v4.media.session.PlaybackStateCompat;

import com.example.chromecastone.CastServer.CastServerService;
import com.example.chromecastone.Dlna.model.upnp.MediaCompleteListener;
import com.google.android.gms.cast.HlsSegmentFormat;

import org.fourthline.cling.model.message.header.ContentRangeHeader;
import org.fourthline.cling.model.types.BytesRange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class SimpleWebServer extends NanoHTTPD implements MediaCompleteListener {
    private static final String LICENCE = "Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n\nRedistribution and use in source and binary forms, with or without\nmodification, are permitted provided that the following conditions\nare met:\n\nRedistributions of source code must retain the above copyright notice,\nthis list of conditions and the following disclaimer. Redistributions in\nbinary form must reproduce the above copyright notice, this list of\nconditions and the following disclaimer in the documentation and/or other\nmaterials provided with the distribution. The name of the author may not\nbe used to endorse or promote products derived from this software without\nspecific prior written permission. \n \nTHIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\nIMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\nOF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\nIN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\nINCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\nNOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\nDATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\nTHEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\nOF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
    private static final Map<String, String> MIME_TYPES = new HashMap<String, String>() {
        {
            put("css", "text/css");
            put("htm", "text/html");
            put("html", "text/html");
            put("xml", "text/xml");
            put("java", "text/x-java-source, text/java");
            put("txt", "text/plain");
            put("asc", "text/plain");
            put("gif", "image/gif");
            put("jpg", "image/jpeg");
            put("jpeg", "image/jpeg");
            put("png", "image/png");
            put(HlsSegmentFormat.MP3, "audio/mpeg");
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
    MediaCompleteListener mediaCompleteListener;
    private final boolean quiet;
    private final File rootDir;

    public SimpleWebServer(String str, int i, File file, boolean z) {
        super(str, i);
        this.rootDir = file;
        this.quiet = z;
        setOnMediaCompleteListener(this);
    }

    File getRootDir() {
        return this.rootDir;
    }

    protected String encodeUri(String str) {
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

    protected Response serveFile(String str, Map<String, String> map, File file) {
        Response response = !file.isDirectory() ? new Response(Response.Status.INTERNAL_ERROR, "text/plain", "INTERNAL ERRROR: serveFile(): given homeDir is not a directory.") : null;
        if (response == null) {
            str = str.trim().replace(File.separatorChar, '/');
            if (str.indexOf(63) >= 0) {
                str = str.substring(0, str.indexOf(63));
            }
            if (str.startsWith("src/main") || str.endsWith("src/main") || str.contains("../")) {
                response = new Response(Response.Status.FORBIDDEN, "text/plain", "FORBIDDEN: Won't serve ../ for security reasons.");
            }
        }
        File file2 = new File(file, str);
        if (response == null && !file2.exists()) {
            response = new Response(Response.Status.NOT_FOUND, "text/plain", "Error 404, file not found.");
        }
        if (response == null && file2.isDirectory()) {
            if (!str.endsWith("/")) {
                str = str + "/";
                response = new Response(Response.Status.REDIRECT, "text/html", "<html><body>Redirected: <a href=\"" + str + "\">" + str + "</a></body></html>");
                response.addHeader("Location", str);
            }
            if (response == null) {
                if (new File(file2, "index.html").exists()) {
                    file2 = new File(file, str + "/index.html");
                } else if (new File(file2, "index.htm").exists()) {
                    file2 = new File(file, str + "/index.htm");
                } else if (file2.canRead()) {
                    response = new Response(listDirectory(str, file2));
                } else {
                    response = new Response(Response.Status.FORBIDDEN, "text/plain", "FORBIDDEN: No directory listing.");
                }
            }
        }
        if (response == null) {
            try {
                int lastIndexOf = file2.getCanonicalPath().lastIndexOf(46);
                String str2 = lastIndexOf >= 0 ? MIME_TYPES.get(file2.getCanonicalPath().substring(lastIndexOf + 1).toLowerCase()) : null;
                if (str2 == null) {
                    str2 = "application/octet-stream";
                }
                if (response == null) {
                    response = serveFile(file2, str2, map);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new Response(Response.Status.FORBIDDEN, "text/plain", "FORBIDDEN: Reading file failed.");
            }
        }
        return response;
    }

    public Response serveFile(File file, String str, Map<String, String> map) throws IOException {
        String str2 = null;
        Response response = null;
        String hexString = null;
        long j = 0;
        String str3 = null;
        final long j2 = 0;
        long j1;
        long j3 = 0;
        long length = 0;
        try {
            hexString = Integer.toHexString((file.getAbsolutePath() + file.lastModified() + "" + file.length()).hashCode());
            j = -1;
            str3 = map.get("range");
            j1 = 0;
            if (str3 == null || !str3.startsWith(BytesRange.PREFIX)) {
                j3 = 0;
            } else {
                String substring = str3.substring(6);
                int indexOf = substring.indexOf(45);
                if (indexOf > 0) {
                    try {
                        j3 = Long.parseLong(substring.substring(0, indexOf));
                    } catch (NumberFormatException e) {
                        e = e;
                        j3 = 0;
                    }
                    try {
                        j = Long.parseLong(substring.substring(indexOf + 1));
                    } catch (NumberFormatException e2) {
                        e2.printStackTrace();
                        str3 = substring;
                        length = file.length();
                        if (str3 != null) {
                        }
                        str2 = "text/plain";
                        if (!hexString.equals(map.get("if-none-match"))) {
                        }
                        response.addHeader("Accept-Ranges", "bytes");
                        return response;
                    }
                } else {
                    j3 = 0;
                }
                str3 = substring;
            }
            length = file.length();
            try {
            } catch (Exception e3) {
                e3.printStackTrace();
                response = new Response(Response.Status.FORBIDDEN, str2, "FORBIDDEN: Reading file failed.");
                response.addHeader("Accept-Ranges", "bytes");
                return response;
            }
        } catch (Exception e4) {
            str2 = "text/plain";
        }
        if (str3 != null || j3 < 0) {
            str2 = "text/plain";
            if (!hexString.equals(map.get("if-none-match"))) {
                response = new Response(Response.Status.NOT_MODIFIED, str, "");
            } else {
                Response response2 = new Response(Response.Status.OK, str, new FileInputStream(file));
                response2.addHeader("Content-Length", "" + length);
                response2.addHeader("ETag", hexString);
                response = response2;
            }
        } else if (j3 >= length) {
            response = new Response(Response.Status.RANGE_NOT_SATISFIABLE, "text/plain", "");
            response.addHeader("Content-Range", "bytes 0-0/" + length);
            response.addHeader("ETag", hexString);
            response.addHeader("Accept-Ranges", "bytes");
            return response;
        } else {
            if (j < 0) {
                j = length - 1;
            }
            long j4 = (j - j3) + 1;
            if (j4 < 0) {
                str2 = "text/plain";
            } else {
                str2 = "text/plain";
                j1 = j4;
            }
            FileInputStream fileInputStream = new FileInputStream(file) {
                @Override
                public int available() throws IOException {
                    return (int) j2;
                }
            };
            fileInputStream.skip(j3);
            response = new Response(Response.Status.PARTIAL_CONTENT, str, fileInputStream);
            response.addHeader("Content-Length", "" + j2);
            response.addHeader("Content-Range", ContentRangeHeader.PREFIX + j3 + "-" + j + "/" + length);
            response.addHeader("ETag", hexString);
        }
        response.addHeader("Accept-Ranges", "bytes");
        return response;
    }

    protected String listDirectory(String str, File file) {
        String str2 = null;
        String str3;
        String substring;
        int lastIndexOf;
        String str4 = "Directory " + str;
        String str5 = "<html><head><title>" + str4 + "</title><style><!--\nspan.dirname { font-weight: bold; }\nspan.filesize { font-size: 75%; }\n// -->\n</style></head><body><h1>" + str4 + "</h1>";
        String substring2 = (str.length() <= 1 || (lastIndexOf = (substring = str.substring(0, str.length() - 1)).lastIndexOf(47)) < 0 || lastIndexOf >= substring.length()) ? null : str.substring(0, lastIndexOf + 1);
        List asList = Arrays.asList(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file2, String str6) {
                return new File(file2, str6).isFile();
            }
        }));
        Collections.sort(asList);
        List asList2 = Arrays.asList(file.list(new FilenameFilter() {
            @Override
            public boolean accept(File file2, String str6) {
                return new File(file2, str6).isDirectory();
            }
        }));
        Collections.sort(asList2);
        if (substring2 != null || asList2.size() + asList.size() > 0) {
            String str6 = str5 + "<ul>";
            if (substring2 != null || asList2.size() > 0) {
                String str7 = str6 + "<section class=\"directories\">";
                if (substring2 != null) {
                    str7 = str7 + "<li><a rel=\"directory\" href=\"" + substring2 + "\"><span class=\"dirname\">..</span></a></b></li>";
                }
                for (int i = 0; i < asList2.size(); i++) {
                    str7 = str7 + "<li><a rel=\"directory\" href=\"" + encodeUri(str + str2) + "\"><span class=\"dirname\">" + (((String) asList2.get(i)) + "/") + "</span></a></b></li>";
                }
                str6 = str7 + "</section>";
            }
            if (asList.size() > 0) {
                String str8 = str6 + "<section class=\"files\">";
                for (int i2 = 0; i2 < asList.size(); i2++) {
                    String str9 = (String) asList.get(i2);
                    long length = new File(file, str9).length();
                    String str10 = (str8 + "<li><a href=\"" + encodeUri(str + str9) + "\"><span class=\"filename\">" + str9 + "</span></a>") + "&nbsp;<span class=\"filesize\">(";
                    if (length < 1024) {
                        str3 = str10 + length + " bytes";
                    } else if (length < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
                        str3 = str10 + (length / 1024) + CastServerService.ROOT_DIR + (((length % 1024) / 10) % 100) + " KB";
                    } else {
                        str3 = str10 + (length / PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) + CastServerService.ROOT_DIR + (((length % PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) / 10) % 100) + " MB";
                    }
                    str8 = str3 + ")</span></li>";
                }
                str6 = str8 + "</section>";
            }
            str5 = str6 + "</ul>";
        }
        return str5 + "</body></html>";
    }

    @Override
    public Response serve(String str, Method method, Map<String, String> map, Map<String, String> map2, Map<String, String> map3) {
        if (!this.quiet) {
            PrintStream printStream = System.out;
            printStream.println(method + " '" + str + "' ");
            for (String str2 : map.keySet()) {
                PrintStream printStream2 = System.out;
                printStream2.println("  HDR: '" + str2 + "' = '" + map.get(str2) + "'");
            }
            for (String str3 : map2.keySet()) {
                PrintStream printStream3 = System.out;
                printStream3.println("  PRM: '" + str3 + "' = '" + map2.get(str3) + "'");
            }
            for (String str4 : map3.keySet()) {
                PrintStream printStream4 = System.out;
                printStream4.println("  UPLOADED: '" + str4 + "' = '" + map3.get(str4) + "'");
            }
        }
        return serveFile(str, map, getRootDir());
    }

    public static void main(String[] strArr) {
        File absoluteFile = new File(CastServerService.ROOT_DIR).getAbsoluteFile();
        int i = 0;
        String str = CastServerService.IP_ADDRESS;
        boolean z = false;
        int i2 = CastServerService.SERVER_PORT;
        while (true) {
            if (i >= strArr.length) {
                break;
            }
            if (strArr[i].equalsIgnoreCase("-h") || strArr[i].equalsIgnoreCase("--host")) {
                str = strArr[i + 1];
            } else if (strArr[i].equalsIgnoreCase("-p") || strArr[i].equalsIgnoreCase("--port")) {
                i2 = Integer.parseInt(strArr[i + 1]);
            } else if (strArr[i].equalsIgnoreCase("-q") || strArr[i].equalsIgnoreCase("--quiet")) {
                z = true;
            } else if (strArr[i].equalsIgnoreCase("-d") || strArr[i].equalsIgnoreCase("--dir")) {
                absoluteFile = new File(strArr[i + 1]).getAbsoluteFile();
            } else if (strArr[i].equalsIgnoreCase("--licence")) {
                System.out.println("Copyright (c) 2012-2013 by Paul S. Hawke, 2001,2005-2013 by Jarno Elonen, 2010 by Konstantinos Togias\n\nRedistribution and use in source and binary forms, with or without\nmodification, are permitted provided that the following conditions\nare met:\n\nRedistributions of source code must retain the above copyright notice,\nthis list of conditions and the following disclaimer. Redistributions in\nbinary form must reproduce the above copyright notice, this list of\nconditions and the following disclaimer in the documentation and/or other\nmaterials provided with the distribution. The name of the author may not\nbe used to endorse or promote products derived from this software without\nspecific prior written permission. \n \nTHIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\nIMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\nOF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\nIN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\nINCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\nNOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\nDATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\nTHEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\nOF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n");
                break;
            }
            i++;
        }
        ServerRunner.executeInstance(new SimpleWebServer(str, i2, absoluteFile, z));
    }

    public void setMediaCompleteListener(MediaCompleteListener mediaCompleteListener) {
        this.mediaCompleteListener = mediaCompleteListener;
    }

    public void onMediaComplete() {
        MediaCompleteListener mediaCompleteListener = this.mediaCompleteListener;
        if (mediaCompleteListener != null) {
            mediaCompleteListener.onMediaComplete();
        }
    }
}
