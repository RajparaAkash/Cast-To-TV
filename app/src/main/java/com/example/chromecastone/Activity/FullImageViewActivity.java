package com.example.chromecastone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chromecastone.Adapter.FullImageViewPagerAdapter;
import com.example.chromecastone.CastServer.CastServerService;
import com.example.chromecastone.Dlna.model.mediaserver.ContentDirectoryService;
import com.example.chromecastone.Dlna.model.upnp.ARendererState;
import com.example.chromecastone.Dlna.model.upnp.IRendererCommand;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Interface.RemoteMediaClientListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.Utils.QueueDataProvider;
import com.example.chromecastone.Utils.WebServerController;
import com.example.chromecastone.databinding.ActivityFullImageViewBinding;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class FullImageViewActivity extends AppCompatActivity implements ItemOnClickListener, DeviceConnectListener {

    public static Runnable runnable;
    public static Handler slideShowHandler;
    private ActivityFullImageViewBinding binding;
    private FullImageViewPagerAdapter fullImageViewPagerAdapter;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private int position;
    private QueueDataProvider provider;
    private RemoteMediaClient remoteMediaClient;
    private IRendererCommand rendererCommand;
    private ARendererState rendererState;
    private SessionManager sessionManager;
    private WebServerController webServerController;
    private static List<MediaFileModel> listImagesData = new ArrayList();
    public static boolean isQueueImageDisplay = false;
    private int currentPage = 0;
    private Handler handler = new Handler(Looper.myLooper());
    private final SessionManagerListener<CastSession> mSessionManagerListener = new MySessionManagerListener();
    private RemoteMediaClient.Listener clientListener = new RemoteMediaClientListener() {
        @Override
        public void onStatusUpdated() {
            FullImageViewActivity.this.remoteMediaClient.removeListener(this);
        }
    };


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        ActivityFullImageViewBinding inflate = ActivityFullImageViewBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        getListMediaFile();
        initMain();
    }


    private void getListMediaFile() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                listImagesData = intent.getParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL);
                int intExtra = getIntent().getIntExtra(Constant.EXTRA_POSITION_DATA, 0);
                this.position = intExtra;
                this.currentPage = intExtra;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMain() {
        if (CastDeviceListActivity.upnpServiceController != null) {
            CastDeviceListActivity.upnpServiceController.resume(this);
        }
        setSupportActionBar(this.binding.tb);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        this.webServerController = new WebServerController(this);
        CastContext sharedInstance = CastContext.getSharedInstance(this);
        this.mCastContext = sharedInstance;
        SessionManager sessionManager = sharedInstance.getSessionManager();
        this.sessionManager = sessionManager;
        this.mCastSession = sessionManager.getCurrentCastSession();
        Handler handler = new Handler(Looper.myLooper());
        slideShowHandler = handler;
        Runnable runnable2 = runnable;
        if (runnable2 != null) {
            handler.removeCallbacks(runnable2);
        }
        slideShowHandler.removeCallbacksAndMessages(null);
        isQueueImageDisplay = false;
        initView();
        castImage();
    }

    public void initView() {
        if (this.rendererState == null && Constant.isDLNACastConnected) {
            this.rendererState = CastDeviceListActivity.factory.createRendererState();
            IRendererCommand createRendererCommand = CastDeviceListActivity.factory.createRendererCommand(this.rendererState);
            this.rendererCommand = createRendererCommand;
            if (this.rendererState == null || createRendererCommand == null) {
                return;
            }
            createRendererCommand.resume();
            this.rendererCommand.updateFull();
        }
        if (listImagesData.size() >= 2 && listImagesData.get(1) == null) {
            listImagesData.remove(1);
        }
        this.fullImageViewPagerAdapter = new FullImageViewPagerAdapter(listImagesData, this);
        this.binding.vp2.setAdapter(this.fullImageViewPagerAdapter);
        this.binding.vp2.setCurrentItem(this.position);
        this.binding.tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullImageViewActivity.this.onBackPressed();
            }
        });
        this.binding.vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int i) {
                super.onPageSelected(i);
                if (FullImageViewActivity.isQueueImageDisplay) {
                    return;
                }
                FullImageViewActivity.this.currentPage = i;
                FullImageViewActivity.this.invalidateOptionsMenu();
                FullImageViewActivity.this.castImage();
            }
        });
        this.binding.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                FullImageViewActivity.this.m91xa876a31c(view);
            }
        });
        this.binding.ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                FullImageViewActivity.this.m92x44e49f7b(view);
            }
        });
        this.binding.ivPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                FullImageViewActivity.this.m93xe1529bda(view);
            }
        });
    }

    
    public void m91xa876a31c(View view) {
        if (Constant.isDLNACastConnected) {
            IRendererCommand iRendererCommand = this.rendererCommand;
            if (iRendererCommand != null) {
                iRendererCommand.commandStop();
                this.rendererCommand.pause();
            }
        } else if (Constant.isChromeCastConnected) {
            if (isQueueImageDisplay) {
                isQueueImageDisplay = false;
                slideShowHandler.removeCallbacks(runnable);
                slideShowHandler.removeCallbacksAndMessages(null);
            }
            this.remoteMediaClient.stop();
        }
        finish();
    }

    
    public void m92x44e49f7b(View view) {
        if (this.currentPage < listImagesData.size() - 1) {
            this.currentPage++;
            this.binding.vp2.setCurrentItem(this.currentPage);
        }
    }

    
    public void m93xe1529bda(View view) {
        int i = this.currentPage;
        if (i > 0) {
            this.currentPage = i - 1;
            this.binding.vp2.setCurrentItem(this.currentPage);
        }
    }

    public void castImage() {
        stopCastServer();
        this.binding.pb.setVisibility(View.VISIBLE);
        MediaFileModel mediaFileModel = listImagesData.get(this.currentPage);
        if (Constant.isChromeCastConnected) {
            QueueDataProvider queueDataProvider = QueueDataProvider.getInstance(this);
            this.provider = queueDataProvider;
            queueDataProvider.setIsImage(true);
            this.provider.clearQueue();
            MediaInfo mediaInfo = this.webServerController.getMediaInfo(mediaFileModel.getFilePath(), true);
            RemoteMediaClient remoteMediaClient = this.mCastSession.getRemoteMediaClient();
            this.remoteMediaClient = remoteMediaClient;
            if (remoteMediaClient == null) {
                return;
            }
            remoteMediaClient.addListener(this.clientListener);
            this.remoteMediaClient.load(mediaInfo, true, 0L);
            this.remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
                @Override
                public void onPreloadStatusUpdated() {
                    super.onPreloadStatusUpdated();
                    FullImageViewActivity.this.binding.pb.setVisibility(View.GONE);
                }
            });
        } else if (Constant.isDLNACastConnected) {
            this.binding.pb.setVisibility(View.GONE);
            String str = null;
            try {
                str = CastDeviceListActivity.getLocalIpAddress(this).getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            int lastIndexOf = mediaFileModel.getFilePath().lastIndexOf(46);
            String lowerCase = lastIndexOf >= 0 ? mediaFileModel.getFilePath().substring(lastIndexOf).toLowerCase() : "";
            mediaFileModel.setMediaCastUrl("http://" + str + ":8192/" + ContentDirectoryService.IMAGE_PREFIX + mediaFileModel.getId() + lowerCase);
            IRendererCommand createRendererCommand = CastDeviceListActivity.factory.createRendererCommand(CastDeviceListActivity.factory.createRendererState());
            if (createRendererCommand != null) {
                createRendererCommand.launchItem(mediaFileModel);
            }
        }
    }

    public void stopCastServer() {
        stopService(new Intent(this, CastServerService.class));
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(int i) {
        if (this.binding.tb.getVisibility() != View.VISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public final void run() {
                    FullImageViewActivity.this.m94xc5236f2();
                }
            }, 10L);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public final void run() {
                    FullImageViewActivity.this.m95xa8c03351();
                }
            }, 10L);
        }
    }

    
    public void m94xc5236f2() {
        this.binding.vp2.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

    
    public void m95xa8c03351() {
        this.binding.vp2.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
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
        super.onResume();
        invalidateOptionsMenu();
    }

    
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (Constant.isConnected) {
            menu.findItem(R.id.media_route_menu_item).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_cast_connected_white));
            return true;
        }
        menu.findItem(R.id.media_route_menu_item).setIcon(ContextCompat.getDrawable(this, R.drawable.img_cast_white));
        return true;
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
        invalidateOptionsMenu();
    }

    
    private class MySessionManagerListener implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionEnding(CastSession castSession) {
        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {
        }

        @Override
        public void onSessionResuming(CastSession castSession, String str) {
        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {
        }

        @Override
        public void onSessionStarting(CastSession castSession) {
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {
        }

        private MySessionManagerListener() {
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {
            if (castSession == FullImageViewActivity.this.mCastSession) {
                FullImageViewActivity.this.mCastSession = null;
            }
            FullImageViewActivity.this.invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean z) {
            FullImageViewActivity.this.mCastSession = castSession;
            FullImageViewActivity.this.castImage();
            FullImageViewActivity.this.invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession castSession, String str) {
            FullImageViewActivity.this.mCastSession = castSession;
            FullImageViewActivity.this.castImage();
            FullImageViewActivity.this.invalidateOptionsMenu();
        }
    }
}
