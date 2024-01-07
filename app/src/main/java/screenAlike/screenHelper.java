package screenAlike;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.preference.PreferenceManager;

import com.example.chromecastone.R;


public class screenHelper {
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 1;
    private static screenHelper sServiceInstance;
    private String DEFAULT_SERVER_PORT = "8080";
    public Activity activity;
    private final Context mContext;
    private boolean mIsStreaming;
    private MediaProjectionManager mMediaProjectionManager;
    private volatile int mSeverPort;
    private final SharedPreferences mSharedPreferences;

    public screenHelper(Context context) {
        this.mContext = context;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.mSharedPreferences = defaultSharedPreferences;
        this.mSeverPort = Integer.parseInt(defaultSharedPreferences.getString(context.getString(R.string.pref_key_server_port), this.DEFAULT_SERVER_PORT));
    }
}
