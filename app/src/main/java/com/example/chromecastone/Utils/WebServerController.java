package com.example.chromecastone.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.example.chromecastone.CastServer.CastServerService;
import com.example.chromecastone.Model.WebVideo;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;


public class WebServerController {
    private final Context context;

    public WebServerController(Context context) {
        this.context = context.getApplicationContext();
    }

    public void stopCastServer() {
        this.context.stopService(new Intent(this.context, CastServerService.class));
    }

    private void startCastServer(String str, String str2) {
        Intent intent = new Intent(this.context, CastServerService.class);
        intent.putExtra(CastServerService.IP_ADDRESS, str);
        intent.putExtra(CastServerService.ROOT_DIR, str2);
        this.context.startService(intent);
    }

    public MediaInfo getMediaInfo(String str, boolean z) {
        String str2;
        String str3;
        MediaMetadata mediaMetadata;
        stopCastServer();
        String formatIpAddress = Formatter.formatIpAddress(((WifiManager) this.context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
        int lastIndexOf = str.lastIndexOf(47);
        if (lastIndexOf > 1) {
            str2 = str.substring(lastIndexOf + 1);
            str3 = str.substring(0, lastIndexOf);
        } else {
            str2 = "";
            str3 = CastServerService.ROOT_DIR;
        }
        startCastServer(formatIpAddress, str3);
        String str4 = "http://" + formatIpAddress + ":" + CastServerService.SERVER_PORT + "/" + str2;
        if (z) {
            mediaMetadata = new MediaMetadata(4);
        } else {
            mediaMetadata = new MediaMetadata(1);
        }
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, str2);
        mediaMetadata.addImage(new WebImage(Uri.parse(str4)));
        mediaMetadata.addImage(new WebImage(Uri.parse(str4)));
        if (z) {
            return new MediaInfo.Builder(str4).setContentType(Utils.getMimeType(this.context, Uri.parse(str2))).setStreamType(0).setMetadata(mediaMetadata).build();
        }
        return new MediaInfo.Builder(str4).setContentType(Utils.getMimeType(this.context, Uri.parse(str2))).setStreamType(1).setMetadata(mediaMetadata).build();
    }

    public MediaInfo getMediaQueueInfo(String str, boolean z) {
        String str2;
        String str3;
        MediaMetadata mediaMetadata;
        stopCastServer();
        String formatIpAddress = Formatter.formatIpAddress(((WifiManager) this.context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
        int lastIndexOf = str.lastIndexOf(47);
        if (lastIndexOf > 1) {
            str2 = str.substring(lastIndexOf + 1);
            str3 = str.substring(0, lastIndexOf);
        } else {
            str2 = "";
            str3 = CastServerService.ROOT_DIR;
        }
        startCastServer(formatIpAddress, str3);
        String str4 = "http://" + formatIpAddress + ":" + CastServerService.SERVER_PORT + "/" + str2;
        if (z) {
            mediaMetadata = new MediaMetadata(1);
        } else {
            mediaMetadata = new MediaMetadata(1);
        }
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, str2);
        mediaMetadata.addImage(new WebImage(Uri.parse(str4)));
        mediaMetadata.addImage(new WebImage(Uri.parse(str4)));
        if (z) {
            return new MediaInfo.Builder(str4).setContentType(Utils.getMimeType(this.context, Uri.parse(str2))).setStreamType(1).setMetadata(mediaMetadata).setStreamDuration(2000L).build();
        }
        return new MediaInfo.Builder(str4).setContentType(Utils.getMimeType(this.context, Uri.parse(str2))).setStreamType(1).setMetadata(mediaMetadata).build();
    }

    public MediaInfo getAudioMediaInfo(String str) {
        String str2;
        String str3;
        stopCastServer();
        String formatIpAddress = Formatter.formatIpAddress(((WifiManager) this.context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
        int lastIndexOf = str.lastIndexOf(47);
        if (lastIndexOf > 1) {
            str2 = str.substring(lastIndexOf + 1);
            str3 = str.substring(0, lastIndexOf);
        } else {
            str2 = "";
            str3 = CastServerService.ROOT_DIR;
        }
        startCastServer(formatIpAddress, str3);
        String str4 = "http://" + formatIpAddress + ":" + CastServerService.SERVER_PORT + "/" + str2;
        MediaMetadata mediaMetadata = new MediaMetadata(3);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, str2);
        mediaMetadata.addImage(new WebImage(Uri.parse(str4)));
        mediaMetadata.addImage(new WebImage(Uri.parse(str4)));
        return new MediaInfo.Builder(str4).setContentType(Utils.getMimeType(this.context, Uri.parse(str2))).setStreamType(1).setMetadata(mediaMetadata).build();
    }

    public MediaInfo getUrlMediaInfo(WebVideo webVideo) {
        MediaMetadata mediaMetadata = new MediaMetadata(1);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, webVideo.getName());
        mediaMetadata.addImage(new WebImage(Uri.parse(webVideo.getLink())));
        mediaMetadata.addImage(new WebImage(Uri.parse(webVideo.getLink())));
        return new MediaInfo.Builder(webVideo.getLink()).setContentType(webVideo.getType()).setStreamType(1).setMetadata(mediaMetadata).build();
    }

    public MediaInfo getUrlMediaInfo1(WebVideo webVideo) {
        MediaMetadata mediaMetadata = new MediaMetadata(1);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, webVideo.getName());
        mediaMetadata.addImage(new WebImage(Uri.parse(webVideo.getLink())));
        mediaMetadata.addImage(new WebImage(Uri.parse(webVideo.getLink())));
        return new MediaInfo.Builder(webVideo.getLink()).setContentType(webVideo.getType()).setStreamType(2).setMetadata(mediaMetadata).build();
    }
}
