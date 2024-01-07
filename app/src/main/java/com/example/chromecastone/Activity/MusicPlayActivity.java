package com.example.chromecastone.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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


public class MusicPlayActivity extends AppCompatActivity implements DeviceConnectListener, Observer, ItemOnClickListener {

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
    private int repeateMode = 1;
    private RemoteMediaClient.Listener clientListener = new RemoteMediaClientListener() {
        @Override
        public void onStatusUpdated() {
            MusicPlayActivity.this.remoteMediaClient.removeListener(this);
        }
    };
    private BroadcastReceiver nextVideoDLNAPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicPlayActivity.this.binding == null || MusicPlayActivity.this.binding.seekBar.getProgress() <= 0) {
                return;
            }
            MusicPlayActivity.this.playNextVideo();
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


    
    public void updateChromecastICon() {
        if (Constant.isConnected) {
            this.binding.actionCast.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_cast_connected_white));
        } else {
            this.binding.actionCast.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.img_cast_white));
        }
    }

    private void initMain() {
        getListMediaFile();
        updateChromecastICon();
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
        this.binding.tvFileName.setText(this.video.getFileName());
        QueueListAdapter queueListAdapter = new QueueListAdapter(this, true);
        this.queueListAdapter = queueListAdapter;
        queueListAdapter.setOnItemClickListener(this);
        this.queueListAdapter.setListMediaQueue(videoList);
        initView();
        startControlPoint();
        castAudio();
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

    public void initView() {
        this.binding.seekBar.setProgress(0.0f);
        RequestManager with = Glide.with((FragmentActivity) this);
        with.load(Uri.parse("content://media/external/audio/albumart/" + this.video.getAlbumId())).apply((BaseRequestOptions<?>) new RequestOptions().transform(new CenterCrop(), new RoundedCorners(18)).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_thumbnail)).into(this.binding.ivThumbnail);
        this.binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m143xeee01b44(view);
            }
        });
        this.binding.ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m149x6d411f23(view);
            }
        });
        this.binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m150xeba22302(view);
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
                if (Constant.isDLNACastConnected) {
                    if (MusicPlayActivity.this.rendererState == null) {
                        return;
                    }
                    long max = (long) ((1.0d - ((indicatorSeekBar.getMax() - indicatorSeekBar.getProgress()) / indicatorSeekBar.getMax())) * MusicPlayActivity.this.rendererState.getDurationSeconds());
                    long j = max / 3600;
                    long j2 = max - (3600 * j);
                    long j3 = j2 / 60;
                    String formatTime = MusicPlayActivity.this.formatTime(j, j3, j2 - (60 * j3));
                    Context applicationContext = MusicPlayActivity.this.getApplicationContext();
                    Toast.makeText(applicationContext, MusicPlayActivity.this.getString(R.string.text_seek_to) + formatTime, Toast.LENGTH_SHORT).show();
                    if (MusicPlayActivity.this.rendererCommand != null) {
                        MusicPlayActivity.this.rendererCommand.commandSeek(formatTime);
                        return;
                    }
                    return;
                }
                MusicPlayActivity.this.remoteMediaClient.seek(indicatorSeekBar.getProgress());
            }
        });
        this.binding.ivVolumeDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m151x6a0326e1(view);
            }
        });
        this.binding.ivVolumeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m152xe8642ac0(view);
            }
        });
        this.binding.ivStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m153x66c52e9f(view);
            }
        });
        this.binding.ivQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m154xe526327e(view);
            }
        });
        this.binding.ivMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m155x6387365d(view);
            }
        });
        this.binding.ivPrev15s.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m156xe1e83a3c(view);
            }
        });
        this.binding.ivNext15.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m144xc8fc3d32(view);
            }
        });
        this.binding.actionCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m145x475d4111(view);
            }
        });
        this.binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m146xc5be44f0(view);
            }
        });
        this.binding.ivMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m147x441f48cf(view);
            }
        });
        this.binding.ivSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MusicPlayActivity.this.m148xc2804cae(view);
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
                    if (Constant.isChromeCastConnected && MusicPlayActivity.this.remoteMediaClient != null) {
                        MusicPlayActivity.this.remoteMediaClient.setPlaybackRate(indicatorSeekBar.getProgressFloat());
                    } else if (Constant.isDLNACastConnected && MusicPlayActivity.this.rendererState != null) {
                        MusicPlayActivity.this.rendererState.setPlaybackSpeed(String.valueOf(indicatorSeekBar.getProgressFloat()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    
    public void m143xeee01b44(View view) {
        if (this.position < videoList.size() - 1) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_circle_white_80dp);
            this.position++;
            MediaQueueItem currentItem = this.provider.getCurrentItem();
            if (Constant.isChromeCastConnected && currentItem != null) {
                this.remoteMediaClient.queueNext(null);
            } else if (this.position < videoList.size()) {
                this.video = videoList.get(this.position);
                Glide.with((FragmentActivity) this).load(Uri.parse("content://media/external/audio/albumart/" + this.video.getAlbumId())).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_music_placeholder)).into(this.binding.ivThumbnail);
                this.binding.tvFileName.setText(this.video.getFileName());
                castAudio();
            }
        }
    }

    
    public void m149x6d411f23(View view) {
        if (this.position > 0) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_circle_white_80dp);
            this.position--;
            MediaQueueItem currentItem = this.provider.getCurrentItem();
            if (Constant.isChromeCastConnected && currentItem != null) {
                this.remoteMediaClient.queuePrev(null);
                return;
            }
            int i = this.position;
            if (i >= 0) {
                this.video = videoList.get(i);
                Glide.with((FragmentActivity) this).load(Uri.parse("content://media/external/audio/albumart/" + this.video.getAlbumId())).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_music_placeholder)).into(this.binding.ivThumbnail);
                this.binding.tvFileName.setText(this.video.getFileName());
                castAudio();
            }
        }
    }

    
    public void m150xeba22302(View view) {
        if (Constant.isDLNACastConnected) {
            if (this.rendererState.getState() == IRendererState.State.PLAY) {
                this.rendererCommand.commandPause();
                this.binding.ivPause.setImageResource(R.drawable.ic_play_wbg);
                return;
            }
            this.rendererCommand.commandPlay();
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
        } else if (this.remoteMediaClient.isPlaying()) {
            this.binding.ivPause.setImageResource(R.drawable.ic_play_wbg);
            this.remoteMediaClient.pause();
        } else {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
            this.remoteMediaClient.play();
        }
    }

    
    public void m151x6a0326e1(View view) {
        setVolumeDown();
    }

    
    public void m152xe8642ac0(View view) {
        setVolumeUp();
    }

    
    public void m153x66c52e9f(View view) {
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

    
    public void m154xe526327e(View view) {
        showQueueListDialog();
    }

    
    public void m155x6387365d(View view) {
        int i = this.repeateMode;
        if (i == 3) {
            this.repeateMode = 1;
        } else {
            this.repeateMode = i + 1;
        }
        if (Constant.isChromeCastConnected) {
            this.remoteMediaClient.queueSetRepeatMode(this.repeateMode, null);
        } else {
            boolean z = Constant.isDLNACastConnected;
        }
        setRepeateModeIcon();
    }

    
    public void m156xe1e83a3c(View view) {
        if (Constant.isChromeCastConnected) {
            if (this.binding.seekBar.getProgress() > 0) {
                this.remoteMediaClient.seek(this.binding.seekBar.getProgress() - 15000);
            }
        } else if (Constant.isDLNACastConnected && this.binding.seekBar.getProgress() > 0) {
            long max = (long) ((1.0d - ((this.binding.seekBar.getMax() - (this.binding.seekBar.getProgress() - 15000)) / this.binding.seekBar.getMax())) * this.rendererState.getDurationSeconds());
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

    
    public void m144xc8fc3d32(View view) {
        if (Constant.isChromeCastConnected) {
            this.remoteMediaClient.seek(new MediaSeekOptions.Builder().setPosition(this.binding.seekBar.getProgress() + 15000).build());
        } else if (Constant.isDLNACastConnected) {
            long max = (long) ((1.0d - ((this.binding.seekBar.getMax() - (this.binding.seekBar.getProgress() + 15000)) / this.binding.seekBar.getMax())) * this.rendererState.getDurationSeconds());
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

    
    public void m145x475d4111(View view) {
        new CastDeviceListActivity().setDeviceConnectListener(this);
        startActivityForResult(new Intent(this, CastDeviceListActivity.class), 100);
    }

    
    public void m146xc5be44f0(View view) {
        onBackPressed();
    }

    
    public void m147x441f48cf(View view) {
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

    
    public void m148xc2804cae(View view) {
        if (!Constant.isChromeCastConnected || this.remoteMediaClient == null) {
            return;
        }
        if (this.binding.llSpeed.getVisibility() == View.VISIBLE) {
            this.binding.llSpeed.setVisibility(View.GONE);
        } else {
            this.binding.llSpeed.setVisibility(View.VISIBLE);
        }
    }

    public void setRepeateModeIcon() {
        int i = this.repeateMode;
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

    public void castAudio() {
        String str = null;
        if (FullImageViewActivity.slideShowHandler != null) {
            FullImageViewActivity.isQueueImageDisplay = false;
            FullImageViewActivity.slideShowHandler.removeCallbacks(FullImageViewActivity.runnable);
            FullImageViewActivity.slideShowHandler.removeCallbacksAndMessages(null);
        }
        this.binding.pb.setVisibility(View.VISIBLE);
        if (Constant.isChromeCastConnected) {
            MediaInfo audioMediaInfo = this.webServerController.getAudioMediaInfo(this.video.getFilePath());
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
            MediaQueueItem build = new MediaQueueItem.Builder(audioMediaInfo).setAutoplay(true).setPreloadTime(20.0d).build();
            this.mediaQueueItemList.add(build);
            this.remoteMediaClient.queueLoad(Utils.rebuildQueueAndAppend(this.provider.getItems(), build), this.provider.getCount(), 0, null);
            this.remoteMediaClient.addListener(this.clientListener);
            this.remoteMediaClient.addProgressListener(new RemoteMediaClient.ProgressListener() {
                @Override
                public final void onProgressUpdated(long j, long j2) {
                    MusicPlayActivity.this.m141xb68d11b5(j, j2);
                }
            }, 100L);
            this.provider.setOnQueueDataChangedListener(new QueueDataProvider.OnQueueDataChangedListener() {
                @Override
                public final void onQueueDataChanged() {
                    MusicPlayActivity.this.m142x34ee1594();
                }
            });
            this.remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
                @Override
                public void onPreloadStatusUpdated() {
                    super.onPreloadStatusUpdated();
                    if (MusicPlayActivity.this.remoteMediaClient.getMediaQueue().getItemCount() == 0) {
                        MusicPlayActivity.this.addQueueVideoList();
                    }
                    MusicPlayActivity.this.binding.pb.setVisibility(View.GONE);
                }

                @Override
                public void onQueueStatusUpdated() {
                    super.onQueueStatusUpdated();
                }
            });
        } else if (Constant.isDLNACastConnected) {
            this.binding.seekBar.setMax(100.0f);
            this.binding.pb.setVisibility(View.GONE);
            try {
                str = CastDeviceListActivity.getLocalIpAddress(this).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            int lastIndexOf = this.video.getFilePath().lastIndexOf(46);
            String lowerCase = lastIndexOf >= 0 ? this.video.getFilePath().substring(lastIndexOf).toLowerCase() : "";
            this.video.setMediaCastUrl("http://" + str + ":8192/" + ContentDirectoryService.AUDIO_PREFIX + this.video.getId() + lowerCase);
            CastDeviceListActivity.factory.createRendererCommand(CastDeviceListActivity.factory.createRendererState()).launchItem(this.video);
        }
    }

    
    public void m141xb68d11b5(long j, long j2) {
        IndicatorSeekBar indicatorSeekBar = this.binding.seekBar;
        indicatorSeekBar.setProgress(Integer.parseInt(j + ""));
        this.binding.currentTimeText.setText(Utils.formatMillis((int) j));
        this.binding.totalTimeText.setText(Utils.formatMillis((int) j2));
    }

    
    public void m142x34ee1594() {
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
        if (Constant.isChromeCastConnected) {
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
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
        } else if (Constant.isDLNACastConnected) {
            this.position = i;
            this.video = videoList.get(i);
            castAudio();
        }
        this.queueListDialog.dismiss();
    }

    public void startControlPoint() {
        ARendererState aRendererState = this.rendererState;
        if (aRendererState == null || aRendererState.getState() == IRendererState.State.STOP) {
            this.binding.pb.setVisibility(View.VISIBLE);
        } else {
            this.binding.pb.setVisibility(View.GONE);
        }
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
            this.binding.currentTimeText.setText(this.rendererState.getPosition().replace("-", ""));
            this.binding.totalTimeText.setText(this.rendererState.getDuration());
            runOnUiThread(new Runnable() {
                @Override
                public final void run() {
                    MusicPlayActivity.this.m157x4dd75ed1();
                }
            });
        }
    }

    
    public void m157x4dd75ed1() {
        if (this.rendererState.getState() == IRendererState.State.PLAY) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
        } else {
            this.binding.ivPause.setImageResource(R.drawable.ic_play_wbg);
        }
        this.binding.seekBar.setProgress(this.rendererState.getElapsedPercent());
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
        updateChromecastICon();
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.media_route_menu_item) {
            new CastDeviceListActivity().setDeviceConnectListener(this);
            startActivityForResult(new Intent(this, CastDeviceListActivity.class), 100);
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
        updateChromecastICon();
    }

    @Override
    public void update(Observable observable, Object obj) {
        startControlPoint();
    }

    public void getRandomVideoPosition() {
        this.position = new Random().nextInt(videoList.size());
    }

    public void playNextVideo() {
        int i = this.repeateMode;
        if (i == 3) {
            getRandomVideoPosition();
        } else if (i == 1) {
            if (this.position == videoList.size() - 1) {
                this.position = 0;
            } else {
                this.position++;
            }
        }
        if (this.position < videoList.size() - 1) {
            this.binding.ivPause.setImageResource(R.drawable.ic_pause_wbg);
            this.video = videoList.get(this.position);
            Glide.with((FragmentActivity) this).load(this.video.getFilePath()).apply((BaseRequestOptions<?>) new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.ic_video_placeholder)).into(this.binding.ivThumbnail);
            castAudio();
            return;
        }
        IRendererCommand iRendererCommand = this.rendererCommand;
        if (iRendererCommand != null) {
            iRendererCommand.commandStop();
            this.rendererCommand.pause();
        }
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
            if (castSession == MusicPlayActivity.this.mCastSession) {
                MusicPlayActivity.this.mCastSession = null;
            }
            MusicPlayActivity.this.updateChromecastICon();
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean z) {
            try {
                MusicPlayActivity.this.mCastSession = castSession;
                MusicPlayActivity musicPlayActivity = MusicPlayActivity.this;
                musicPlayActivity.remoteMediaClient = musicPlayActivity.mCastSession.getRemoteMediaClient();
                MusicPlayActivity.this.castAudio();
                MusicPlayActivity.this.updateChromecastICon();
            } catch (Exception unused) {
            }
        }

        @Override
        public void onSessionStarted(CastSession castSession, String str) {
            try {
                MusicPlayActivity.this.mCastSession = castSession;
                MusicPlayActivity musicPlayActivity = MusicPlayActivity.this;
                musicPlayActivity.remoteMediaClient = musicPlayActivity.mCastSession.getRemoteMediaClient();
                MusicPlayActivity.this.castAudio();
                MusicPlayActivity.this.updateChromecastICon();
            } catch (Exception unused) {
            }
        }

        @Override
        public void onSessionStarting(CastSession castSession) {
            if (MusicPlayActivity.this.mCastSession == null) {
                MusicPlayActivity.this.mCastSession = castSession;
            }
            MusicPlayActivity musicPlayActivity = MusicPlayActivity.this;
            musicPlayActivity.remoteMediaClient = musicPlayActivity.mCastSession.getRemoteMediaClient();
        }

        @Override
        public void onSessionResuming(CastSession castSession, String str) {
            MusicPlayActivity musicPlayActivity = MusicPlayActivity.this;
            musicPlayActivity.remoteMediaClient = musicPlayActivity.mCastSession.getRemoteMediaClient();
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {
            MusicPlayActivity musicPlayActivity = MusicPlayActivity.this;
            musicPlayActivity.remoteMediaClient = musicPlayActivity.mCastSession.getRemoteMediaClient();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
