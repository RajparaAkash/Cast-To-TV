package com.example.chromecastone.Utils;

import android.app.Activity;
import android.app.Application;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;

import screenAlike.AppData;
import screenAlike.HttpServer;
import screenAlike.ImageGenerator;


public class MyApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, LifecycleObserver {

    private static HttpServer mHttpServer = null;
    private static MyApplication sAppInstance = null;
    private AppData mAppData;
    private ImageGenerator mImageGenerator;
    private MediaProjection mMediaProjection;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        sAppInstance = this;
        this.mAppData = new AppData(this);
        getAppData().initIndexHtmlPage(this);
        mHttpServer = new HttpServer();
        this.mImageGenerator = new ImageGenerator();
        this.mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
    }

    public static void startServer() {
        mHttpServer.start();
    }

    public static AppData getAppData() {
        return sAppInstance.mAppData;
    }

    public static void setMediaProjection(MediaProjection mediaProjection) {
        sAppInstance.mMediaProjection = mediaProjection;
    }

    public static MediaProjection getMediaProjection() {
        MyApplication myApplication = sAppInstance;
        if (myApplication == null) {
            return null;
        }
        return myApplication.mMediaProjection;
    }

    public static ImageGenerator getImageGenerator() {
        MyApplication myApplication = sAppInstance;
        if (myApplication == null) {
            return null;
        }
        return myApplication.mImageGenerator;
    }

    public static MediaProjectionManager getProjectionManager() {
        MyApplication myApplication = sAppInstance;
        if (myApplication == null) {
            return null;
        }
        return myApplication.mMediaProjectionManager;
    }
}
