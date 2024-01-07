package com.example.chromecastone.CastServer;

import java.io.File;
import java.util.Map;


public interface WebServerPlugin {
    boolean canServeUri(String str, File file);

    void initialize(Map<String, String> map);

    NanoHTTPD.Response serveFile(String str, Map<String, String> map, NanoHTTPD.IHTTPSession iHTTPSession, File file, String str2);
}
