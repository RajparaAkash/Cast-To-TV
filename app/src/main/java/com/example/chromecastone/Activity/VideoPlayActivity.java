package com.example.chromecastone.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.chromecastone.Adapter.QueueListAdapter;
import com.example.chromecastone.Dlna.model.mediaserver.ContentDirectoryService;
import com.example.chromecastone.Dlna.model.upnp.ARendererState;
import com.example.chromecastone.Dlna.model.upnp.IRendererCommand;
import com.example.chromecastone.Dlna.model.upnp.IRendererState;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Interface.RemoteMediaClientListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.Utils.QueueDataProvider;
import com.example.chromecastone.Utils.Utils;
import com.example.chromecastone.Utils.WebServerController;
import com.example.chromecastone.databinding.ActivityVideoPlayBinding;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaSeekOptions;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;


public class VideoPlayActivity extends AppCompatActivity implements ItemOnClickListener, Observer, DeviceConnectListener {
    private static final int PRELOAD_TIME_S = 20;
    static List<MediaFileModel> videoList;
    public AudioManager audioManager;
    private ActivityVideoPlayBinding binding;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private int mMaxVolume;
    private List<MediaQueueItem> mediaQueueItemList;
    private int position;
    private int positionOld;
    private QueueDataProvider provider;
    private QueueListAdapter queueListAdapter;
    private BottomSheetDialog queueListDialog;
    private RemoteMediaClient remoteMediaClient;
    private IRendererCommand rendererCommand;
    private ARendererState rendererState;
    private SessionManager sessionManager;
    private MediaFileModel video;
    private WebServerController webServerController;
    private final SessionManagerListener<CastSession> mSessionManagerListener = new MySessionManagerListener();
    private int repeatMode = 1;
    private RemoteMediaClient.Listener clientListener = new RemoteMediaClientListener() {
        @Override
        public void onStatusUpdated() {
            VideoPlayActivity.this.remoteMediaClient.removeListener(this);
        }
    };
    private BroadcastReceiver nextVideoDLNAPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (VideoPlayActivity.this.rendererState == null || VideoPlayActivity.this.rendererState.getState() != IRendererState.State.PLAY || VideoPlayActivity.this.binding == null || VideoPlayActivity.this.binding.seekBar.getProgress() <= VideoPlayActivity.this.binding.seekBar.getMax() - 20.0f) {
                return;
            }
            VideoPlayActivity.this.playNextVideo();
        }
    };


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityVideoPlayBinding inflate = ActivityVideoPlayBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        initMain();
    }


    private void initMain() {
        getListMediaFile();
        invalidateOptionsMenu();
        if (CastDeviceListActivity.upnpServiceController != null) {
            CastDeviceListActivity.upnpServiceController.resume(this);
        }
        this.audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (Constant.isChromeCastConnected) {
            this.mMaxVolume = this.audioManager.getStreamMaxVolume(3);
        } else {
            this.mMaxVolume = 100;
        }
        this.webServerController = new WebServerController(this);
        CastContext sharedInstance = CastContext.getSharedInstance(this);
        this.mCastContext = sharedInstance;
        SessionManager sessionManager = sharedInstance.getSessionManager();
        this.sessionManager = sessionManager;
        this.mCastSession = sessionManager.getCurrentCastSession();
        this.mediaQueueItemList = new ArrayList();
        this.video = videoList.get(this.position);
        QueueListAdapter queueListAdapter = new QueueListAdapter(this, false);
        this.queueListAdapter = queueListAdapter;
        queueListAdapter.setOnItemClickListener(this);
        this.queueListAdapter.setListMediaQueue(videoList);
        initView();
        startControlPoint();
        castVideo();
    }

    private void getListMediaFile() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                videoList = intent.getParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL);
                int intExtra = getIntent().getIntExtra(Constant.EXTRA_POSITION_DATA, 0);
                this.position = intExtra;
                this.positionOld = intExtra;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void updateChromecastIcon() {
        if (Constant.isConnected) {
            this.binding.actionCast.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cast_connected_white));
        } else {
            this.binding.actionCast.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_cast_white));
        }
    }

    public void initView() {
        updateChromecastIcon();
        this.binding.tvFileName.setText(this.video.getFileName());
        Glide.with((FragmentActivity) this).load(this.video.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().transform(new CenterCrop(), new RoundedCorners(18)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_thumbnail)).into(this.binding.ivThumbnail);
        this.binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m171x94d4709a(view);
            }
        });
        this.binding.ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m177x13357479(view);
            }
        });
        this.binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m178x91967858(view);
            }
        });
        this.binding.seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
                if (Constant.isChromeCastConnected) {
                    VideoPlayActivity.this.remoteMediaClient.seek(indicatorSeekBar.getProgress());
                } else if (!Constant.isDLNACastConnected || VideoPlayActivity.this.rendererState == null) {
                } else {
                    long max = (long) ((1.0d - ((indicatorSeekBar.getMax() - indicatorSeekBar.getProgress()) / indicatorSeekBar.getMax())) * VideoPlayActivity.this.rendererState.getDurationSeconds());
                    long j = max / 3600;
                    long j2 = max - (3600 * j);
                    long j3 = j2 / 60;
                    String formatTime = VideoPlayActivity.this.formatTime(j, j3, j2 - (60 * j3));
                    if (VideoPlayActivity.this.rendererCommand != null) {
                        VideoPlayActivity.this.rendererCommand.commandSeek(formatTime);
                    }
                }
            }
        });
        this.binding.ivVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m179xff77c37(view);
            }
        });
        this.binding.ivVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m180x8e588016(view);
            }
        });
        this.binding.ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m181xcb983f5(view);
            }
        });
        this.binding.ivQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m182x8b1a87d4(view);
            }
        });
        this.binding.ivMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m183x97b8bb3(view);
            }
        });
        this.binding.ivPrev15s.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m184x87dc8f92(view);
            }
        });
        this.binding.ivNext15.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m172x6ef09288(view);
            }
        });
        this.binding.actionCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m173xed519667(view);
            }
        });
        this.binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m174x6bb29a46(view);
            }
        });
        this.binding.ivMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m175xea139e25(view);
            }
        });
        this.binding.ivSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VideoPlayActivity.this.m176x6874a204(view);
            }
        });
        this.binding.sb.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar indicatorSeekBar) {
                try {
                    if (Constant.isChromeCastConnected && VideoPlayActivity.this.remoteMediaClient != null) {
                        VideoPlayActivity.this.remoteMediaClient.setPlaybackRate(indicatorSeekBar.getProgressFloat());
                    } else if (Constant.isDLNACastConnected && VideoPlayActivity.this.rendererState != null) {
                        VideoPlayActivity.this.rendererState.setPlaybackSpeed(String.valueOf(indicatorSeekBar.getProgressFloat()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
    public void m171x94d4709a(View view) {
        if (this.position < videoList.size()) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
            this.position++;
            MediaQueueItem currentItem = Constant.isChromeCastConnected ? this.provider.getCurrentItem() : null;
            if (Constant.isChromeCastConnected && currentItem != null) {
                this.remoteMediaClient.queueNext(null);
                return;
            } else if (this.position < videoList.size()) {
                this.video = videoList.get(this.position);
                Glide.with((FragmentActivity) this).load(this.video.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_video_placeholder)).into(this.binding.ivThumbnail);
                this.binding.tvFileName.setText(this.video.getFileName());
                castVideo();
                return;
            } else {
                return;
            }
        }
        Toast.makeText(this, (int) R.string.text_already_last_video, Toast.LENGTH_SHORT).show();
    }

    
    public void m177x13357479(View view) {
        if (this.position > 0) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
            this.position--;
            MediaQueueItem currentItem = Constant.isChromeCastConnected ? this.provider.getCurrentItem() : null;
            if (Constant.isChromeCastConnected && currentItem != null) {
                this.remoteMediaClient.queuePrev(null);
                return;
            }
            int i = this.position;
            if (i >= 0) {
                this.video = videoList.get(i);
                Glide.with((FragmentActivity) this).load(this.video.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_video_placeholder)).into(this.binding.ivThumbnail);
                this.binding.tvFileName.setText(this.video.getFileName());
                castVideo();
                return;
            }
            return;
        }
        Toast.makeText(this, (int) R.string.text_already_last_video, Toast.LENGTH_SHORT).show();
    }

    
    public void m178x91967858(View view) {
        if (Constant.isChromeCastConnected) {
            if (this.remoteMediaClient.isPlaying()) {
                this.binding.ivPause.setImageResource(R.drawable.ic_play_wbg);
                this.remoteMediaClient.pause();
                return;
            }
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
            this.remoteMediaClient.play();
        } else if (Constant.isDLNACastConnected) {
            if (this.rendererState.getState() == IRendererState.State.PLAY) {
                this.rendererCommand.commandPause();
                this.binding.ivPause.setImageResource(R.drawable.ic_play_wbg);
                return;
            }
            this.rendererCommand.commandPlay();
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
        }
    }

    
    public void m179xff77c37(View view) {
        setVolumeDown();
    }

    
    public void m180x8e588016(View view) {
        setVolumeUp();
    }

    
    public void m181xcb983f5(View view) {
        if (Constant.isDLNACastConnected) {
            IRendererCommand iRendererCommand = this.rendererCommand;
            if (iRendererCommand != null) {
                iRendererCommand.commandStop();
                this.rendererCommand.pause();
            }
        } else if (Constant.isChromeCastConnected) {
            this.remoteMediaClient.stop();
        }
        finish();
    }

    
    public void m182x8b1a87d4(View view) {
        showQueueListDialog();
    }

    
    public void m183x97b8bb3(View view) {
        int i = this.repeatMode;
        if (i == 3) {
            this.repeatMode = 1;
        } else {
            this.repeatMode = i + 1;
        }
        if (Constant.isChromeCastConnected) {
            this.remoteMediaClient.queueSetRepeatMode(this.repeatMode, null);
        } else {
            boolean z = Constant.isDLNACastConnected;
        }
        setRepeatModeIcon();
    }

    
    public void m184x87dc8f92(View view) {
        if (Constant.isChromeCastConnected) {
            if (this.binding.seekBar.getProgress() > 0) {
                this.remoteMediaClient.seek(this.binding.seekBar.getProgress() - 15000);
            }
        } else if (Constant.isDLNACastConnected && this.binding.seekBar.getProgress() > 0) {
            long max = ((long) ((1.0d - ((this.binding.seekBar.getMax() - this.binding.seekBar.getProgress()) / this.binding.seekBar.getMax())) * this.rendererState.getDurationSeconds())) - 15;
            long j = max / 3600;
            long j2 = max - (3600 * j);
            long j3 = j2 / 60;
            String formatTime = formatTime(j, j3, j2 - (60 * j3));
            IRendererCommand iRendererCommand = this.rendererCommand;
            if (iRendererCommand != null) {
                iRendererCommand.commandSeek(formatTime);
            }
        }
        Toast.makeText(this, "-15", Toast.LENGTH_SHORT).show();
    }

    
    public void m172x6ef09288(View view) {
        if (Constant.isChromeCastConnected) {
            this.remoteMediaClient.seek(new MediaSeekOptions.Builder().setPosition(this.binding.seekBar.getProgress() + 15000).build());
        } else if (Constant.isDLNACastConnected) {
            long max = ((long) ((1.0d - ((this.binding.seekBar.getMax() - this.binding.seekBar.getProgress()) / this.binding.seekBar.getMax())) * this.rendererState.getDurationSeconds())) + 15;
            long j = max / 3600;
            long j2 = max - (3600 * j);
            long j3 = j2 / 60;
            String formatTime = formatTime(j, j3, j2 - (60 * j3));
            IRendererCommand iRendererCommand = this.rendererCommand;
            if (iRendererCommand != null) {
                iRendererCommand.commandSeek(formatTime);
            }
        }
        Toast.makeText(this, "+15", Toast.LENGTH_SHORT).show();
    }

    
    public void m173xed519667(View view) {
        new CastDeviceListActivity().setDeviceConnectListener(this);
        startActivityForResult(new Intent(this, CastDeviceListActivity.class), 100);
    }

    
    public void m174x6bb29a46(View view) {
        onBackPressed();
    }

    
    public void m175xea139e25(View view) {
        ARendererState aRendererState;
        CastSession castSession;
        boolean z = true;
        if (Constant.isChromeCastConnected && (castSession = this.mCastSession) != null) {
            try {
                if (castSession.isMute()) {
                    this.binding.ivMute.setImageResource(R.drawable.ic_vol_unmute);
                } else {
                    this.binding.ivMute.setImageResource(R.drawable.ic_vol_mute);
                }
                CastSession castSession2 = this.mCastSession;
                if (castSession2.isMute()) {
                    z = false;
                }
                castSession2.setMute(z);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!Constant.isDLNACastConnected || (aRendererState = this.rendererState) == null) {
        } else {
            if (aRendererState.isMute()) {
                this.binding.ivMute.setImageResource(R.drawable.ic_vol_unmute);
            } else {
                this.binding.ivMute.setImageResource(R.drawable.ic_vol_mute);
            }
            ARendererState aRendererState2 = this.rendererState;
            aRendererState2.setMute(true ^ aRendererState2.isMute());
        }
    }

    
    public void m176x6874a204(View view) {
        if (Constant.isChromeCastConnected && this.remoteMediaClient != null) {
            if (this.binding.llSpeed.getVisibility() == View.VISIBLE) {
                this.binding.llSpeed.setVisibility(View.GONE);
                return;
            } else {
                this.binding.llSpeed.setVisibility(View.VISIBLE);
                return;
            }
        }
        boolean z = Constant.isDLNACastConnected;
    }

    public void setRepeatModeIcon() {
        int i = this.repeatMode;
        if (i == 0) {
            this.binding.ivMode.setImageResource(R.drawable.cast_abc_scrubber_control_off_mtrl_alpha);
        } else if (i == 1) {
            this.binding.ivMode.setImageResource(R.drawable.ic_repeat);
        } else if (i == 2) {
            this.binding.ivMode.setImageResource(R.drawable.ic_repeat_one);
        } else if (i != 3) {
        } else {
            this.binding.ivMode.setImageResource(R.drawable.ic_shuffle);
        }
    }

    public void getRandomVideoPosition() {
        this.position = new Random().nextInt(videoList.size());
    }

    public void setVolumeUp() {
        ARendererState aRendererState;
        if (Constant.isChromeCastConnected) {
            CastSession castSession = this.mCastSession;
            if (castSession == null || castSession.getVolume() >= 100.0d) {
                return;
            }
            try {
                CastSession castSession2 = this.mCastSession;
                castSession2.setVolume(castSession2.getVolume() + 0.01d);
                Toast.makeText(this, Math.round((this.mCastSession.getVolume() + 0.01d) * 100.0d) + "", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!Constant.isDLNACastConnected || this.rendererCommand == null || (aRendererState = this.rendererState) == null) {
        } else {
            int volume = aRendererState.getVolume();
            if (volume < 0) {
                volume = 0;
            }
            this.rendererCommand.setVolume(volume + 1);
            Toast.makeText(this, volume + "", Toast.LENGTH_SHORT).show();
        }
    }

    public void setVolumeDown() {
        ARendererState aRendererState;
        if (Constant.isChromeCastConnected) {
            CastSession castSession = this.mCastSession;
            if (castSession == null || castSession.getVolume() <= 0.0d) {
                return;
            }
            try {
                CastSession castSession2 = this.mCastSession;
                castSession2.setVolume(castSession2.getVolume() - 0.01d);
                Toast.makeText(this, Math.round((this.mCastSession.getVolume() - 0.01d) * 100.0d) + "", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!Constant.isDLNACastConnected || this.rendererCommand == null || (aRendererState = this.rendererState) == null) {
        } else {
            int volume = aRendererState.getVolume() - 1;
            this.rendererCommand.setVolume(volume >= 0 ? volume : 0);
        }
    }

    public void castVideo() {
        String str = null;
        if (FullImageViewActivity.slideShowHandler != null) {
            FullImageViewActivity.isQueueImageDisplay = false;
            FullImageViewActivity.slideShowHandler.removeCallbacks(FullImageViewActivity.runnable);
            FullImageViewActivity.slideShowHandler.removeCallbacksAndMessages(null);
        }
        this.binding.pb.setVisibility(View.VISIBLE);
        if (Constant.isChromeCastConnected) {
            MediaInfo mediaInfo = this.webServerController.getMediaInfo(this.video.getFilePath(), false);
            this.binding.seekBar.setMax(Integer.parseInt(this.video.getDuration()));
            this.binding.currentTimeText.setText("00:00");
            this.binding.totalTimeText.setText(Utils.formatMillis(Integer.parseInt(this.video.getDuration())));
            RemoteMediaClient remoteMediaClient = this.mCastSession.getRemoteMediaClient();
            this.remoteMediaClient = remoteMediaClient;
            if (remoteMediaClient == null) {
                return;
            }
            QueueDataProvider queueDataProvider = QueueDataProvider.getInstance(this);
            this.provider = queueDataProvider;
            queueDataProvider.setIsImage(false);
            this.provider.clearQueue();
            MediaQueueItem build = new MediaQueueItem.Builder(mediaInfo).setAutoplay(true).setPreloadTime(20.0d).build();
            this.mediaQueueItemList.add(build);
            this.remoteMediaClient.queueLoad(Utils.rebuildQueueAndAppend(this.provider.getItems(), build), this.provider.getCount(), 0, null);
            this.remoteMediaClient.addListener(this.clientListener);
            this.remoteMediaClient.addProgressListener(new RemoteMediaClient.ProgressListener() {
                @Override
                public final void onProgressUpdated(long j, long j2) {
                    VideoPlayActivity.this.m169xe2daa270(j, j2);
                }
            }, 100L);
            this.provider.setOnQueueDataChangedListener(new QueueDataProvider.OnQueueDataChangedListener() {
                @Override
                public final void onQueueDataChanged() {
                    VideoPlayActivity.this.m170x613ba64f();
                }
            });
            this.remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
                @Override
                public void onPreloadStatusUpdated() {
                    super.onPreloadStatusUpdated();
                    if (VideoPlayActivity.this.remoteMediaClient.getMediaQueue().getItemCount() == 0) {
                        VideoPlayActivity.this.addQueueVideoList();
                    }
                    VideoPlayActivity.this.binding.pb.setVisibility(View.GONE);
                }

                @Override
                public void onQueueStatusUpdated() {
                    super.onQueueStatusUpdated();
                }
            });
        } else if (Constant.isDLNACastConnected) {
            this.binding.seekBar.setMax(100.0f);
            try {
                str = CastDeviceListActivity.getLocalIpAddress(this).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            int lastIndexOf = this.video.getFilePath().lastIndexOf(46);
            String lowerCase = lastIndexOf >= 0 ? this.video.getFilePath().substring(lastIndexOf).toLowerCase() : "";
            this.video.setMediaCastUrl("http://" + str + ":8192/" + ContentDirectoryService.VIDEO_PREFIX + this.video.getId() + lowerCase);
            CastDeviceListActivity.factory.createRendererCommand(CastDeviceListActivity.factory.createRendererState()).launchItem(this.video);
        }
    }

    
    public void m169xe2daa270(long j, long j2) {
        IndicatorSeekBar indicatorSeekBar = this.binding.seekBar;
        indicatorSeekBar.setProgress(Integer.parseInt(j + ""));
        this.binding.currentTimeText.setText(Utils.formatMillis((int) j));
        this.binding.totalTimeText.setText(Utils.formatMillis((int) j2));
    }

    
    public void m170x613ba64f() {
        MediaQueueItem currentItem = this.provider.getCurrentItem();
        if (currentItem == null || currentItem.getMedia() == null || isDestroyed() || isFinishing()) {
            return;
        }
        this.binding.tvFileName.setText(currentItem.getMedia().getMetadata().getString(MediaMetadata.KEY_TITLE));
        this.binding.seekBar.setMax((int) currentItem.getMedia().getStreamDuration());
        Glide.with((FragmentActivity) this).load(currentItem.getMedia().getMetadata().getImages().get(0).getUrl()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_video_placeholder)).into(this.binding.ivThumbnail);
        this.queueListAdapter.notifyDataSetChanged();
    }

    public void addQueueVideoList() {
        if (!Constant.isChromeCastConnected || isDestroyed() || isFinishing()) {
            return;
        }
        if (this.position < videoList.size() - 1) {
            for (int i = this.position + 1; i < videoList.size(); i++) {
                MediaQueueItem build = new MediaQueueItem.Builder(this.webServerController.getMediaInfo(videoList.get(i).getFilePath(), false)).setAutoplay(true).setPreloadTime(20.0d).build();
                this.remoteMediaClient.queueAppendItem(build, null);
                if (this.mediaQueueItemList.size() < videoList.size()) {
                    this.mediaQueueItemList.add(build);
                }
            }
        }
        if (this.position > 0) {
            for (int i2 = 0; i2 < this.position; i2++) {
                MediaQueueItem build2 = new MediaQueueItem.Builder(this.webServerController.getMediaInfo(videoList.get(i2).getFilePath(), false)).setAutoplay(true).setPreloadTime(20.0d).build();
                this.remoteMediaClient.queueAppendItem(build2, null);
                if (this.mediaQueueItemList.size() < videoList.size()) {
                    this.mediaQueueItemList.add(build2);
                }
            }
        }
    }

    public void showQueueListDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.TransparentDialog);
        this.queueListDialog = bottomSheetDialog;
        bottomSheetDialog.setContentView(R.layout.dialog_queue_layout);
        this.queueListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        RecyclerView recyclerView = (RecyclerView) this.queueListDialog.findViewById(R.id.queueListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.queueListAdapter);
        this.queueListDialog.show();
    }

    @Override
    public void onStart() {
        CastContext.getSharedInstance(this).getSessionManager().addSessionManagerListener(this.mSessionManagerListener, CastSession.class);
        super.onStart();
    }

    @Override
    public void onStop() {
        CastContext.getSharedInstance(this).getSessionManager().removeSessionManagerListener(this.mSessionManagerListener, CastSession.class);
        super.onStop();
    }

    
    @Override
    public void onResume() {
        this.mCastSession = this.sessionManager.getCurrentCastSession();
        registerReceiver(this.nextVideoDLNAPlay, new IntentFilter("playNextVideo"));
        updateChromecastIcon();
        super.onResume();
    }

    
    @Override
    public void onPause() {
        this.mCastSession = null;
        super.onPause();
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.nextVideoDLNAPlay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
        updateChromecastIcon();
    }

    @Override
    public void update(Observable observable, Object obj) {
        startControlPoint();
    }

    public void startControlPoint() {
        runOnUiThread(new Runnable() {
            @Override
            public final void run() {
                VideoPlayActivity.this.m185xf3cbb427();
            }
        });
        if (this.rendererState == null && Constant.isDLNACastConnected) {
            this.rendererState = CastDeviceListActivity.factory.createRendererState();
            IRendererCommand createRendererCommand = CastDeviceListActivity.factory.createRendererCommand(this.rendererState);
            this.rendererCommand = createRendererCommand;
            if (this.rendererState == null || createRendererCommand == null) {
                return;
            }
            createRendererCommand.resume();
            this.rendererState.addObserver(this);
            this.rendererCommand.updateFull();
        }
        if (this.rendererState != null) {
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    VideoPlayActivity.this.m186x722cb806();
                }
            });
        }
    }

    
    public void m185xf3cbb427() {
        ARendererState aRendererState = this.rendererState;
        if (aRendererState == null || aRendererState.getState() == IRendererState.State.STOP) {
            this.binding.pb.setVisibility(View.VISIBLE);
        } else {
            this.binding.pb.setVisibility(View.GONE);
        }
    }

    
    public void m186x722cb806() {
        this.binding.currentTimeText.setText(this.rendererState.getPosition().replace("-", ""));
        this.binding.totalTimeText.setText(this.rendererState.getDuration());
        if (this.rendererState.getState() == IRendererState.State.PLAY) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
        } else {
            this.binding.ivPause.setImageResource(R.drawable.ic_play_wbg);
        }
        this.binding.seekBar.setProgress(this.rendererState.getElapsedPercent());
    }

    public void playNextVideo() {
        int i = this.repeatMode;
        if (i == 3) {
            getRandomVideoPosition();
        } else if (i == 1) {
            if (this.position == videoList.size() - 1) {
                this.position = 0;
            } else {
                this.position++;
            }
        }
        if (this.position < videoList.size()) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
            this.video = videoList.get(this.position);
            Glide.with((FragmentActivity) this).load(this.video.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_video_placeholder)).into(this.binding.ivThumbnail);
            castVideo();
            return;
        }
        IRendererCommand iRendererCommand = this.rendererCommand;
        if (iRendererCommand != null) {
            iRendererCommand.commandStop();
            this.rendererCommand.pause();
        }
    }

    @Override
    public void onItemClick(int i) {
        if (Constant.isChromeCastConnected) {
            if (this.mediaQueueItemList.size() < i) {
                this.queueListDialog.dismiss();
                return;
            }
            MediaQueueItem[] mediaQueueItemArr = (MediaQueueItem[]) this.mediaQueueItemList.toArray(new MediaQueueItem[this.mediaQueueItemList.size()]);
            int i2 = this.positionOld;
            if (i < i2) {
                this.position = Math.abs(((-i) - i2) + 1);
            } else {
                this.position = Math.abs(i - i2);
            }
            if (i < 0) {
                this.position = 0;
            }
            this.remoteMediaClient.queueLoad(mediaQueueItemArr, this.position, 0, null);
            this.queueListAdapter.notifyDataSetChanged();
        } else if (Constant.isDLNACastConnected) {
            this.position = i;
            this.video = videoList.get(i);
            castVideo();
        }
        this.queueListDialog.dismiss();
    }


    private class MySessionManagerListener implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionEnding(CastSession castSession) {
        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {
        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {
        }

        private MySessionManagerListener() {
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {
            if (castSession == VideoPlayActivity.this.mCastSession) {
                VideoPlayActivity.this.mCastSession = null;
            }
            VideoPlayActivity.this.updateChromecastIcon();
            Log.e("TAG", " Session Ended");
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean z) {
            VideoPlayActivity.this.mCastSession = castSession;
            VideoPlayActivity videoPlayActivity = VideoPlayActivity.this;
            videoPlayActivity.remoteMediaClient = videoPlayActivity.mCastSession.getRemoteMediaClient();
            VideoPlayActivity.this.castVideo();
            VideoPlayActivity.this.updateChromecastIcon();
            Log.e("TAG", " Session Resumed");
        }

        @Override
        public void onSessionStarted(CastSession castSession, String str) {
            VideoPlayActivity.this.mCastSession = castSession;
            VideoPlayActivity videoPlayActivity = VideoPlayActivity.this;
            videoPlayActivity.remoteMediaClient = videoPlayActivity.mCastSession.getRemoteMediaClient();
            VideoPlayActivity.this.castVideo();
            VideoPlayActivity.this.updateChromecastIcon();
            Log.e("TAG", " Session Started");
        }

        @Override
        public void onSessionStarting(CastSession castSession) {
            if (VideoPlayActivity.this.mCastSession == null) {
                VideoPlayActivity.this.mCastSession = castSession;
            }
            VideoPlayActivity videoPlayActivity = VideoPlayActivity.this;
            videoPlayActivity.remoteMediaClient = videoPlayActivity.mCastSession.getRemoteMediaClient();
        }

        @Override
        public void onSessionResuming(CastSession castSession, String str) {
            VideoPlayActivity videoPlayActivity = VideoPlayActivity.this;
            videoPlayActivity.remoteMediaClient = videoPlayActivity.mCastSession.getRemoteMediaClient();
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {
            VideoPlayActivity videoPlayActivity = VideoPlayActivity.this;
            videoPlayActivity.remoteMediaClient = videoPlayActivity.mCastSession.getRemoteMediaClient();
        }
    }

    
    public String formatTime(long j, long j2, long j3) {
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
}
