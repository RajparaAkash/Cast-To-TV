package com.example.chromecastone.Interface;

import com.google.android.gms.cast.framework.media.RemoteMediaClient;


public abstract class RemoteMediaClientListener implements RemoteMediaClient.Listener {
    @Override
    public void onAdBreakStatusUpdated() {
    }

    @Override
    public void onMetadataUpdated() {
    }

    @Override
    public void onPreloadStatusUpdated() {
    }

    @Override
    public void onQueueStatusUpdated() {
    }

    @Override
    public void onSendingRemoteMediaRequest() {
    }
}
