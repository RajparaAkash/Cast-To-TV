package com.example.chromecastone.Dlna.model.upnp;


public interface IRendererState {

    
    public enum State {
        PLAY,
        PAUSE,
        STOP
    }

    String getArtist();

    String getDuration();

    long getDurationSeconds();

    int getElapsedPercent();

    String getPlaybackSpeed();

    String getPosition();

    String getRemainingDuration();

    State getState();

    String getTitle();

    int getVolume();

    boolean isMute();

    void setMute(boolean z);

    void setPlaybackSpeed(String str);

    void setState(State state);

    void setVolume(int i);
}
