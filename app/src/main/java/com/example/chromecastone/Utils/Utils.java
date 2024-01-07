package com.example.chromecastone.Utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.android.gms.cast.MediaQueueItem;

import java.util.List;
import java.util.Locale;


public class Utils {

    public static String getFilenameFromPath(String str) {
        return str.substring(str.lastIndexOf("/") + 1);
    }

    public static String getParentPath(String str) {
        return str.endsWith(getFilenameFromPath(str)) ? str.substring(0, str.length() - getFilenameFromPath(str).length()) : "";
    }

    public static String getMimeType(Context context, Uri uri) {
        if ("content".equals(uri.getScheme())) {
            return context.getContentResolver().getType(uri);
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()).toLowerCase());
    }

    public static final String makeShortTimeString(Context context, long j) {
        int i = (int) j;
        int i2 = i % 60;
        int i3 = (i / 60) % 60;
        int i4 = i / 3600;
        return i4 > 0 ? String.format("%02d:%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i3), Integer.valueOf(i2)) : String.format("%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i2));
    }

    public static String formatMillis(int i) {
        int i2 = i / 1000;
        int i3 = i2 / 3600;
        int i4 = i2 % 3600;
        int i5 = i4 / 60;
        int i6 = i4 % 60;
        return i3 > 0 ? String.format(Locale.ROOT, "%d:%02d:%02d", Integer.valueOf(i3), Integer.valueOf(i5), Integer.valueOf(i6)) : String.format(Locale.ROOT, "%d:%02d", Integer.valueOf(i5), Integer.valueOf(i6));
    }

    public static MediaQueueItem[] rebuildQueueAndAppend(List<MediaQueueItem> list, MediaQueueItem mediaQueueItem) {
        if (list == null || list.isEmpty()) {
            return new MediaQueueItem[]{mediaQueueItem};
        }
        MediaQueueItem[] mediaQueueItemArr = new MediaQueueItem[list.size() + 1];
        for (int i = 0; i < list.size(); i++) {
            mediaQueueItemArr[i] = rebuildQueueItem(list.get(i));
        }
        mediaQueueItemArr[list.size()] = mediaQueueItem;
        return mediaQueueItemArr;
    }

    public static MediaQueueItem rebuildQueueItem(MediaQueueItem mediaQueueItem) {
        return new MediaQueueItem.Builder(mediaQueueItem).clearItemId().build();
    }

}
