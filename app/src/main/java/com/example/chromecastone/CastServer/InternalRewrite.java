package com.example.chromecastone.CastServer;

import java.util.Map;


public class InternalRewrite extends NanoHTTPD.Response {
    private final Map<String, String> headers;
    private final String uri;

    public InternalRewrite(Map<String, String> map, String str) {
        super(null);
        this.headers = map;
        this.uri = str;
    }

    public String getUri() {
        return this.uri;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
