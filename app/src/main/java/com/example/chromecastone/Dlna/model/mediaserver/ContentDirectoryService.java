package com.example.chromecastone.Dlna.model.mediaserver;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import org.fourthline.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.fourthline.cling.support.contentdirectory.ContentDirectoryException;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.BrowseResult;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;

import java.util.ArrayList;


public class ContentDirectoryService extends AbstractContentDirectoryService {
    public static final int ALBUM_ID = 3;
    public static final int ALL_ID = 0;
    public static final int ARTIST_ID = 2;
    public static final int AUDIO_ID = 2;
    public static final String AUDIO_PREFIX = "a-";
    public static final String AUDIO_TXT = "Music";
    public static final String DIRECTORY_PREFIX = "d-";
    public static final int FOLDER_ID = 1;
    public static final int IMAGE_ID = 3;
    public static final String IMAGE_PREFIX = "i-";
    public static final String IMAGE_TXT = "Images";
    public static final int ROOT_ID = 0;
    public static final char SEPARATOR = '$';
    private static final String TAG = "ContentDirectoryService";
    public static final int VIDEO_ID = 1;
    public static final String VIDEO_PREFIX = "v-";
    public static final String VIDEO_TXT = "Videos";
    private static String baseURL;
    private static Context ctx;

    public ContentDirectoryService() {
        Log.v(TAG, "Call default constructor...");
    }

    public ContentDirectoryService(Context context, String str) {
        ctx = context;
        baseURL = str;
    }

    public void setContext(Context context) {
        ctx = context;
    }

    public void setBaseURL(String str) {
        baseURL = str;
    }

    @Override
    public BrowseResult browse(String str, BrowseFlag browseFlag, String str2, long j, long j2, SortCriterion[] sortCriterionArr) throws ContentDirectoryException {
        Log.d(TAG, "Will browse " + str);
        try {
            new DIDLContent();
            TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter('$');
            simpleStringSplitter.setString(str);
            ArrayList arrayList = new ArrayList();
            int i = -1;
            for (String str3 : simpleStringSplitter) {
                int parseInt = Integer.parseInt(str3);
                if (i == -1) {
                    if (parseInt != 0 && parseInt != 1 && parseInt != 2 && parseInt != 3) {
                        throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT, "Invalid type!");
                    }
                    i = parseInt;
                } else {
                    arrayList.add(Integer.valueOf(parseInt));
                }
            }
            Log.d(TAG, "Browsing type " + i);
            PreferenceManager.getDefaultSharedPreferences(ctx);
            Log.e(TAG, "No container for this ID !!!");
            throw new ContentDirectoryException(ContentDirectoryErrorCode.NO_SUCH_OBJECT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ContentDirectoryException(ContentDirectoryErrorCode.CANNOT_PROCESS, e.toString());
        }
    }
}
