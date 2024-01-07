package com.example.chromecastone.Dlna.model;

import android.util.Log;

import com.example.chromecastone.Dlna.model.upnp.ARendererState;

import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;


public class RendererState extends ARendererState {
    protected static final String TAG = "RendererState";
    private boolean isSuccess;
    private MediaInfo mediaInfo;
    private boolean mute;
    private PositionInfo positionInfo;
    private int randomMode;
    private int repeatMode;
    private TransportInfo transportInfo;
    private String currentSpeed = "";
    private State state = State.STOP;
    private int volume = -1;

    public RendererState() {
        resetTrackInfo();
        notifyAllObservers();
    }

    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void setState(State state) {
        if (this.state == state) {
            return;
        }
        if (state == State.STOP && (this.state == State.PLAY || this.state == State.PAUSE)) {
            resetTrackInfo();
        }
        this.state = state;
        notifyAllObservers();
    }

    @Override
    public int getVolume() {
        return this.volume;
    }

    @Override
    public void setVolume(int i) {
        if (this.volume == i) {
            return;
        }
        this.volume = i;
        notifyAllObservers();
    }

    @Override
    public boolean isMute() {
        return this.mute;
    }

    @Override
    public void setMute(boolean z) {
        if (this.mute == z) {
            return;
        }
        this.mute = z;
        notifyAllObservers();
    }

    public void setPositionInfo(PositionInfo positionInfo) {
        try {
            if (this.positionInfo.getRelTime().compareTo(positionInfo.getRelTime()) == 0 && this.positionInfo.getAbsTime().compareTo(positionInfo.getAbsTime()) == 0) {
                return;
            }
            this.positionInfo = positionInfo;
            notifyAllObservers();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage() == null ? "\u00cbxception !" : e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                Log.e(TAG, stackTraceElement.toString());
            }
        }
    }

    public MediaInfo getMediaInfo() {
        return this.mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        if (this.mediaInfo.hashCode() == mediaInfo.hashCode()) {
            return;
        }
        this.mediaInfo = mediaInfo;
    }

    public TransportInfo getTransportInfo() {
        return this.transportInfo;
    }

    public void setTransportInfo(TransportInfo transportInfo) {
        this.transportInfo = transportInfo;
        if (transportInfo.getCurrentTransportState() == TransportState.PAUSED_PLAYBACK || transportInfo.getCurrentTransportState() == TransportState.PAUSED_RECORDING) {
            setState(State.PAUSE);
        } else if (transportInfo.getCurrentTransportState() == TransportState.PLAYING) {
            setState(State.PLAY);
        } else {
            setState(State.STOP);
        }
    }

    private TrackMetadata getTrackMetadata() {
        return new TrackMetadata(this.positionInfo.getTrackMetaData());
    }

    private String formatTime(long j, long j2, long j3) {
        StringBuilder sb;
        StringBuilder sb2;
        StringBuilder sb3;
        StringBuilder sb4 = new StringBuilder();
        if (j >= 10) {
            sb = new StringBuilder();
            sb.append("");
        } else {
            sb = new StringBuilder();
            sb.append("0");
        }
        sb.append(j);
        sb4.append(sb.toString());
        sb4.append(":");
        if (j2 >= 10) {
            sb2 = new StringBuilder();
            sb2.append("");
        } else {
            sb2 = new StringBuilder();
            sb2.append("0");
        }
        sb2.append(j2);
        sb4.append(sb2.toString());
        sb4.append(":");
        if (j3 >= 10) {
            sb3 = new StringBuilder();
            sb3.append("");
        } else {
            sb3 = new StringBuilder();
            sb3.append("0");
        }
        sb3.append(j3);
        sb4.append(sb3.toString());
        return sb4.toString();
    }

    @Override
    public String getRemainingDuration() {
        long trackRemainingSeconds = this.positionInfo.getTrackRemainingSeconds();
        long j = trackRemainingSeconds / 3600;
        long j2 = trackRemainingSeconds - (3600 * j);
        long j3 = j2 / 60;
        long j4 = j2 - (60 * j3);
        return "-" + formatTime(j, j3, j4);
    }

    @Override
    public String getDuration() {
        long trackDurationSeconds = this.positionInfo.getTrackDurationSeconds();
        long j = trackDurationSeconds / 3600;
        long j2 = trackDurationSeconds - (3600 * j);
        long j3 = j2 / 60;
        return formatTime(j, j3, j2 - (60 * j3));
    }

    @Override
    public String getPosition() {
        long trackElapsedSeconds = this.positionInfo.getTrackElapsedSeconds();
        long j = trackElapsedSeconds / 3600;
        long j2 = trackElapsedSeconds - (3600 * j);
        long j3 = j2 / 60;
        return formatTime(j, j3, j2 - (60 * j3));
    }

    @Override
    public long getDurationSeconds() {
        return this.positionInfo.getTrackDurationSeconds();
    }

    public void resetTrackInfo() {
        this.positionInfo = new PositionInfo();
        this.mediaInfo = new MediaInfo();
        notifyAllObservers();
    }

    public String toString() {
        return "RendererState [state=" + this.state + ", volume=" + this.volume + ", repeatMode=" + this.repeatMode + ", randomMode=" + this.randomMode + ", positionInfo=" + this.positionInfo + ", mediaInfo=" + this.mediaInfo + ", trackMetadata=" + new TrackMetadata(this.positionInfo.getTrackMetaData()) + "]";
    }

    @Override
    public int getElapsedPercent() {
        return this.positionInfo.getElapsedPercent();
    }

    @Override
    public String getTitle() {
        return getTrackMetadata().title;
    }

    @Override
    public String getArtist() {
        return getTrackMetadata().artist;
    }

    @Override
    public void setPlaybackSpeed(String str) {
        String str2 = this.currentSpeed;
        if (str2 == null || !str2.equals(str)) {
            this.currentSpeed = str;
            notifyAllObservers();
        }
    }

    @Override
    public String getPlaybackSpeed() {
        return this.currentSpeed;
    }
}
