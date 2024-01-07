package com.example.chromecastone.Dlna.model.mediaserver;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.example.chromecastone.Dlna.model.upnp.MediaCompleteListener;
import com.example.chromecastone.R;

import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationError;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.SimpleWebServer;


public class MediaServer extends SimpleWebServer implements MediaCompleteListener {
    private static final String TAG = "MediaServer";
    private static InetAddress localAddress = null;
    public static final int port = 8192;
    private Context ctx;
    private LocalDevice localDevice;
    private LocalService localService;
    private UDN udn;

    public MediaServer(InetAddress inetAddress, Context context) throws ValidationException {
        super(null, 8192, (File) null, true);
        this.udn = null;
        this.localDevice = null;
        this.localService = null;
        this.ctx = null;
        Log.i(TAG, "Creating media server !");
        LocalService read = new AnnotationLocalServiceBinder().read(ContentDirectoryService.class);
        this.localService = read;
        read.setManager(new DefaultServiceManager(this.localService, ContentDirectoryService.class));
        this.udn = UDN.valueOf(new UUID(0L, 10L).toString());
        localAddress = inetAddress;
        this.ctx = context;
        createLocalDevice();
        ContentDirectoryService contentDirectoryService = (ContentDirectoryService) this.localService.getManager().getImplementation();
        contentDirectoryService.setContext(context);
        contentDirectoryService.setBaseURL(getAddress());
        setMediaCompleteListener(this);
    }

    public void restart() {
        Log.d(TAG, "Restart mediaServer");
        try {
            stop();
            createLocalDevice();
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLocalDevice() throws ValidationException {
        String str;
        try {
            str = this.ctx.getPackageManager().getPackageInfo(this.ctx.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Application version name not found");
            str = "";
        }
        DeviceDetails deviceDetails = new DeviceDetails("", new ManufacturerDetails(this.ctx.getString(R.string.app_name), "http://jksol.com/"), new ModelDetails(this.ctx.getString(R.string.app_name), "http://jksol.com/"), this.ctx.getString(R.string.app_name), str);
        for (ValidationError validationError : deviceDetails.validate()) {
            Log.e(TAG, "Validation pb for property " + validationError.getPropertyName());
            Log.e(TAG, "Error is " + validationError.getMessage());
        }
        this.localDevice = new LocalDevice(new DeviceIdentity(this.udn), new UDADeviceType(TAG, 1), deviceDetails, this.localService);
    }

    public LocalDevice getDevice() {
        return this.localDevice;
    }

    public String getAddress() {
        return localAddress.getHostAddress() + ":8192";
    }

    @Override
    public void onMediaComplete() {
        this.ctx.sendBroadcast(new Intent("playNextVideo"));
    }


    public class InvalidIdentificatorException extends Exception {
        public InvalidIdentificatorException() {
        }

        public InvalidIdentificatorException(String str) {
            super(str);
        }
    }



    public class ServerObject {
        public String mime;
        public String path;

        ServerObject(String str, String str2) {
            this.path = str;
            this.mime = str2;
        }
    }

    private ServerObject getFileServerObject(String str) throws InvalidIdentificatorException {
        int parseInt = 0;
        String str2 = null;
        Object obj;
        Uri uri;
        Uri uri2;
        Cursor query;
        String str3;
        try {
            int lastIndexOf = str.lastIndexOf(46);
            if (lastIndexOf >= 0) {
                str = str.substring(0, lastIndexOf);
            }
            parseInt = Integer.parseInt(str.substring(3));
            Log.v(TAG, "media of id is " + parseInt);
            str2 = null;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error while parsing " + str);
            Log.e(TAG, "exception", e);
        }
        if (str.startsWith("/a-")) {
            Log.v(TAG, "Ask for audio");
            uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            obj = new MediaStore.Audio.Media();
        } else if (str.startsWith("/v-")) {
            Log.v(TAG, "Ask for video");
            uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            obj = new MediaStore.Video.Media();
        } else if (str.startsWith("/i-")) {
            Log.v(TAG, "Ask for image");
            uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            obj = new MediaStore.Images.Media();
        } else {
            obj = null;
            uri = null;
            if (uri != null && obj != null) {
                query = this.ctx.getContentResolver().query(uri, new String[]{"_data", "mime_type"}, "_id=?", new String[]{"" + parseInt}, null);
                if (query.moveToFirst()) {
                    str3 = null;
                } else {
                    str2 = query.getString(query.getColumnIndexOrThrow("_data"));
                    str3 = query.getString(query.getColumnIndexOrThrow("mime_type"));
                }
                query.close();
                if (str2 != null) {
                    return new ServerObject(str2, str3);
                }
            }
            throw new InvalidIdentificatorException(str + " was not found in media database");
        }
        uri = uri2;
        if (uri != null) {
            query = this.ctx.getContentResolver().query(uri, new String[]{"_data", "mime_type"}, "_id=?", new String[]{"" + parseInt}, null);
            if (query.moveToFirst()) {
            }
            query.close();
        }
        throw new InvalidIdentificatorException(str + " was not found in media database");
    }

    public NanoHTTPD.Response serve(String str, NanoHTTPD.Method method, Map<String, String> map, Map<String, String> map2, Map<String, String> map3) {
        Log.i(TAG, "Serve uri : " + str);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Log.d(TAG, "Header : key=" + entry.getKey() + " value=" + entry.getValue());
        }
        for (Map.Entry<String, String> entry2 : map2.entrySet()) {
            Log.d(TAG, "Params : key=" + entry2.getKey() + " value=" + entry2.getValue());
        }
        for (Map.Entry<String, String> entry3 : map3.entrySet()) {
            try {
                Log.d(TAG, "Files : key=" + entry3.getKey() + " value=" + entry3.getValue());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Unexpected error while serving file");
                Log.e(TAG, "exception", e);
                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "INTERNAL ERROR: unexpected error.");
            }
        }
        try {
            ServerObject fileServerObject = getFileServerObject(str);
            Log.i(TAG, "Will serve " + fileServerObject.path);
            Response serveFile = null;
            try {
                serveFile = serveFile(new File(fileServerObject.path), fileServerObject.mime, map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serveFile != null) {
                try {
                    String str2 = this.ctx.getPackageManager().getPackageInfo(this.ctx.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e2) {
                    e2.printStackTrace();
                    Log.e(TAG, "Application version name not found");
                }
                serveFile.addHeader("realTimeInfo.dlna.org", "DLNA.ORG_TLAG=*");
                serveFile.addHeader("contentFeatures.dlna.org", "");
                serveFile.addHeader("transferMode.dlna.org", "Streaming");
                serveFile.addHeader("Server", "DLNADOC/1.50 UPnP/1.0 Cling/2.0 ScreenMirroring/0.0 Android/" + Build.VERSION.RELEASE);
            }
            return serveFile;
        } catch (InvalidIdentificatorException e3) {
            e3.printStackTrace();
            return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "Error 404, file not found.");
        }
    }
}
