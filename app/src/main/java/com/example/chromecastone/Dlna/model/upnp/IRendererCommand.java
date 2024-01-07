package com.example.chromecastone.Dlna.model.upnp;

import com.example.chromecastone.Model.MediaFileModel;


public interface IRendererCommand {
    void commandPause();

    void commandPlay();

    void commandSeek(String str);

    void commandStop();

    void commandToggle();

    void launchItem(MediaFileModel mediaFileModel);

    void pause();

    void resume();

    void setMute(boolean z);

    void setPlaybackSpeed(String str);

    void setVolume(int i);

    void toggleMute();

    void updateFull();

    void updatePosition();

    void updateStatus();

    void updateVolume();
}
