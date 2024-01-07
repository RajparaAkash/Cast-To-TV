package com.example.chromecastone.Dlna.controller;

import android.util.Log;

import com.example.chromecastone.Activity.CastDeviceListActivity;
import com.example.chromecastone.Dlna.SetPlaybackSpeed;
import com.example.chromecastone.Dlna.model.CDevice;
import com.example.chromecastone.Dlna.model.RendererState;
import com.example.chromecastone.Dlna.model.TrackMetadata;
import com.example.chromecastone.Dlna.model.upnp.IRendererCommand;
import com.example.chromecastone.Dlna.model.upnp.IRendererState;
import com.example.chromecastone.Model.MediaFileModel;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.state.StateVariableValue;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;


public class RendererCommand implements Runnable, IRendererCommand {
    private static final String TAG = "RendererCommand";
    private final ControlPoint controlPoint;
    boolean pause;
    private final RendererState rendererState;
    public Thread thread = new Thread(this);

    @Override
    public void updatePosition() {
    }

    @Override
    public void updateStatus() {
    }

    public RendererCommand(ControlPoint controlPoint, RendererState rendererState) {
        this.pause = false;
        this.rendererState = rendererState;
        this.controlPoint = controlPoint;
        this.pause = true;
    }

    public void finalize() {
        pause();
    }

    @Override
    public void pause() {
        Log.v(TAG, "Interrupt");
        this.pause = true;
        this.thread.interrupt();
    }

    @Override
    public void resume() {
        Log.v(TAG, "Resume");
        this.pause = false;
        if (!this.thread.isAlive()) {
            this.thread.start();
        } else {
            this.thread.interrupt();
        }
    }

    public static Service getRenderingControlService() {
        if (CastDeviceListActivity.upnpServiceController.getSelectedRenderer() == null) {
            return null;
        }
        return ((CDevice) CastDeviceListActivity.upnpServiceController.getSelectedRenderer()).getDevice().findService(new UDAServiceType("RenderingControl"));
    }

    public static Service getAVTransportService() {
        if (CastDeviceListActivity.upnpServiceController.getSelectedRenderer() == null) {
            return null;
        }
        return ((CDevice) CastDeviceListActivity.upnpServiceController.getSelectedRenderer()).getDevice().findService(new UDAServiceType("AVTransport"));
    }

    @Override
    public void commandPlay() {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new Play(getAVTransportService()) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                Log.v(RendererCommand.TAG, "Success playing ! ");
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to play ! " + str);
            }
        });
    }

    @Override
    public void commandStop() {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new Stop(getAVTransportService()) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                Log.v(RendererCommand.TAG, "Success stopping ! ");
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to stop ! " + str);
            }
        });
    }

    @Override
    public void commandPause() {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new Pause(getAVTransportService()) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                Log.v(RendererCommand.TAG, "Success pausing ! ");
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to pause ! " + str);
            }
        });
    }

    @Override
    public void commandToggle() {
        if (this.rendererState.getState() == IRendererState.State.PLAY) {
            commandPause();
        } else {
            commandPlay();
        }
    }

    @Override
    public void commandSeek(String str) {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new Seek(getAVTransportService(), str) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                Log.v(RendererCommand.TAG, "Success seeking !");
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                Log.w(RendererCommand.TAG, "Fail to seek ! " + str2);
            }
        });
    }

    @Override
    public void setVolume(final int i) {
        if (getRenderingControlService() == null) {
            return;
        }
        this.controlPoint.execute(new SetVolume(getRenderingControlService(), i) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                super.success(actionInvocation);
                Log.v(RendererCommand.TAG, "Success to set volume");
                RendererCommand.this.rendererState.setVolume(i);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to set volume ! " + str);
            }
        });
    }

    @Override
    public void setMute(final boolean z) {
        if (getRenderingControlService() == null) {
            return;
        }
        this.controlPoint.execute(new SetMute(getRenderingControlService(), z) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                Log.v(RendererCommand.TAG, "Success setting mute status ! ");
                RendererCommand.this.rendererState.setMute(z);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to set mute status ! " + str);
            }
        });
    }

    @Override
    public void toggleMute() {
        setMute(!this.rendererState.isMute());
    }

    public void setURI(String str, TrackMetadata trackMetadata) {
        Log.i(TAG, "Set uri to " + str);
        this.controlPoint.execute(new SetAVTransportURI(getAVTransportService(), str, trackMetadata.getXML()) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                super.success(actionInvocation);
                Log.i(RendererCommand.TAG, "URI successfully set !");
                RendererCommand.this.commandPlay();
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                Log.w(RendererCommand.TAG, "Fail to set URI ! " + str2);
            }
        });
    }

    @Override
    public void launchItem(final MediaFileModel mediaFileModel) {
        String str;
        if (getAVTransportService() == null) {
            return;
        }
        if (mediaFileModel.getType() == 3) {
            str = "audioItem";
        } else if (mediaFileModel.getType() == 2) {
            str = "videoItem";
        } else {
            str = mediaFileModel.getType() == 1 ? "imageItem" : "";
        }
        String id = mediaFileModel.getId();
        String fileName = mediaFileModel.getFileName();
        String filePath = mediaFileModel.getFilePath();
        String mediaCastUrl = mediaFileModel.getMediaCastUrl();
        final TrackMetadata trackMetadata = new TrackMetadata(id, fileName, filePath, "", "", mediaCastUrl, "object.item." + str);
        Log.i(TAG, "TrackMetadata : " + trackMetadata.toString());
        this.controlPoint.execute(new Stop(getAVTransportService()) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                Log.v(RendererCommand.TAG, "Success stopping ! ");
                callback();
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                Log.w(RendererCommand.TAG, "Fail to stop ! " + str2);
                callback();
            }

            public void callback() {
                RendererCommand.this.setURI(mediaFileModel.getMediaCastUrl(), trackMetadata);
            }
        });
    }

    public void updateMediaInfo() {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new GetMediaInfo(getAVTransportService()) {
            @Override
            public void received(ActionInvocation actionInvocation, MediaInfo mediaInfo) {
                Log.d(RendererCommand.TAG, "Receive media info ! " + mediaInfo);
                RendererCommand.this.rendererState.setMediaInfo(mediaInfo);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to get media info ! " + str);
            }
        });
    }

    public void updatePositionInfo() {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new GetPositionInfo(getAVTransportService()) {
            @Override
            public void received(ActionInvocation actionInvocation, PositionInfo positionInfo) {
                Log.d(RendererCommand.TAG, "Receive position info ! " + positionInfo);
                RendererCommand.this.rendererState.setPositionInfo(positionInfo);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to get position info ! " + str);
            }
        });
    }

    public void updateTransportInfo() {
        if (getAVTransportService() == null) {
            return;
        }
        this.controlPoint.execute(new GetTransportInfo(getAVTransportService()) {
            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to get position info ! " + str);
            }

            @Override
            public void received(ActionInvocation actionInvocation, TransportInfo transportInfo) {
                Log.d(RendererCommand.TAG, "Receive position info ! " + transportInfo);
                RendererCommand.this.rendererState.setTransportInfo(transportInfo);
            }
        });
    }

    @Override
    public void updateVolume() {
        if (getRenderingControlService() == null) {
            return;
        }
        this.controlPoint.execute(new GetVolume(getRenderingControlService()) {
            @Override
            public void received(ActionInvocation actionInvocation, int i) {
                Log.d(RendererCommand.TAG, "Receive volume ! " + i);
                RendererCommand.this.rendererState.setVolume(i);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to get volume ! " + str);
            }
        });
    }

    public void updateMute() {
        if (getRenderingControlService() == null) {
            return;
        }
        this.controlPoint.execute(new GetMute(getRenderingControlService()) {
            @Override
            public void received(ActionInvocation actionInvocation, boolean z) {
                Log.d(RendererCommand.TAG, "Receive mute status ! " + z);
                RendererCommand.this.rendererState.setMute(z);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str) {
                Log.w(RendererCommand.TAG, "Fail to get mute status ! " + str);
            }
        });
    }

    @Override
    public void updateFull() {
        updateMediaInfo();
        updatePositionInfo();
        updateVolume();
        updateMute();
        updateTransportInfo();
    }

    @Override
    public void setPlaybackSpeed(final String str) {
        if (getRenderingControlService() == null) {
            return;
        }
        this.controlPoint.execute(new SetPlaybackSpeed(getRenderingControlService(), str) {
            @Override
            public void success(ActionInvocation actionInvocation) {
                super.success(actionInvocation);
                Log.v(RendererCommand.TAG, "Success to set volume");
                RendererCommand.this.rendererState.setPlaybackSpeed(str);
            }

            @Override
            public void failure(ActionInvocation actionInvocation, UpnpResponse upnpResponse, String str2) {
                Log.w(RendererCommand.TAG, "Fail to set volume ! " + str2);
            }
        });
    }

    @Override
    public void run() {
        try {
            new LastChange(new AVTransportLastChangeParser(), String.valueOf(AVTransportVariable.CurrentTrackMetaData.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.controlPoint.execute(new SubscriptionCallback(getRenderingControlService(), 600) {
            @Override
            public void ended(GENASubscription gENASubscription, CancelReason cancelReason, UpnpResponse upnpResponse) {
            }

            @Override
            public void established(GENASubscription gENASubscription) {
                Log.e(RendererCommand.TAG, "Established: " + gENASubscription.getSubscriptionId());
            }

            @Override
            public void failed(GENASubscription gENASubscription, UpnpResponse upnpResponse, Exception exc, String str) {
                Log.e(RendererCommand.TAG, createDefaultFailureMessage(upnpResponse, exc));
            }

            @Override
            public void eventReceived(GENASubscription gENASubscription) {
                Log.e(RendererCommand.TAG, "Event: " + gENASubscription.getCurrentSequence().getValue());
                StateVariableValue stateVariableValue = (StateVariableValue) gENASubscription.getCurrentValues().get("Status");
                if (stateVariableValue != null) {
                    Log.e(RendererCommand.TAG, "Status is: " + stateVariableValue.toString());
                }
            }

            @Override
            public void eventsMissed(GENASubscription gENASubscription, int i) {
                Log.e(RendererCommand.TAG, "Missed events: " + i);
            }
        });
        while (true) {
            int i = 0;
            while (true) {
                try {
                    if (!this.pause) {
                        Log.d(TAG, "Update state !");
                        i++;
                        updatePositionInfo();
                        if (i % 3 == 0) {
                            updateVolume();
                            updateMute();
                            updateTransportInfo();
                        }
                        if (i % 6 == 0) {
                            updateMediaInfo();
                        }
                    }
                    Thread.sleep(1000L);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    StringBuilder sb = new StringBuilder();
                    sb.append("State updater interrupt, new state ");
                    sb.append(this.pause ? "pause" : "running");
                    Log.i(TAG, sb.toString());
                }
            }
        }
    }
}
