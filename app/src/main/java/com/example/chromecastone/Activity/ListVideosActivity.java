package com.example.chromecastone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.chromecastone.Adapter.ListVideosAdapter;
import com.example.chromecastone.Interface.DeviceConnectListener;
import com.example.chromecastone.Interface.ItemOnClickListener;
import com.example.chromecastone.Model.MediaFileModel;
import com.example.chromecastone.R;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.databinding.ActivityListVideosBinding;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;

import java.util.ArrayList;
import java.util.List;


public class ListVideosActivity extends AppCompatActivity implements ItemOnClickListener, DeviceConnectListener {

    private ListVideosAdapter adapter;
    private ActivityListVideosBinding binding;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManager sessionManager;
    private List<MediaFileModel> listImages = new ArrayList();
    private final SessionManagerListener<CastSession> mSessionManagerListener = new MySessionManagerListener();
    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult activityResult) {
            final int intExtra;
            if (activityResult.getResultCode() != -1 || (intExtra = activityResult.getData().getIntExtra(Constant.EXTRA_POSITION_RESULT_BACK, Constant.NOT_DATA)) == Constant.NOT_DATA) {
                return;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ListVideosActivity.this.startActivityPlay(intExtra);
                }
            }, 500L);
        }
    });

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityListVideosBinding inflate = ActivityListVideosBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        getListMediaFile();
        initMain();

        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.cast_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CastDeviceListActivity().setDeviceConnectListener(ListVideosActivity.this);
                startActivityForResult(new Intent(ListVideosActivity.this, CastDeviceListActivity.class), 100);
            }
        });
    }

    private void getListMediaFile() {
        Intent intent = getIntent();
        if (intent != null) {
            this.listImages = intent.getParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL);
        }
    }

    
    @Override
    public void onStart() {
        CastContext.getSharedInstance(this).getSessionManager().addSessionManagerListener(this.mSessionManagerListener, CastSession.class);
        super.onStart();
    }

    
    @Override
    public void onResume() {
        super.onResume();
        if (CastDeviceListActivity.upnpServiceController != null) {
            CastDeviceListActivity.upnpServiceController.resume(this);
        }
        invalidateOptionsMenu();
    }

    
    @Override
    public void onStop() {
        CastContext.getSharedInstance(this).getSessionManager().removeSessionManagerListener(this.mSessionManagerListener, CastSession.class);
        super.onStop();
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
    }

    private void initMain() {
        try {
            if (this.listImages.size() != 0) {
                ((TextView)findViewById(R.id.header_title)).setText(listImages.get(0).getFolderName());

                CastContext sharedInstance = CastContext.getSharedInstance(this);
                this.mCastContext = sharedInstance;
                SessionManager sessionManager = sharedInstance.getSessionManager();
                this.sessionManager = sessionManager;
                this.mCastSession = sessionManager.getCurrentCastSession();
                ListVideosAdapter listVideosAdapter = new ListVideosAdapter(this);
                this.adapter = listVideosAdapter;
                listVideosAdapter.setItemOnClickListener(this);
                this.binding.pbLoad.setVisibility(View.GONE);
                this.binding.rvListdataVideos.setLayoutManager(new GridLayoutManager(this, 3));
                this.adapter.setListImageData(this.listImages);
                this.binding.rvListdataVideos.setAdapter(this.adapter);
            } else {
                onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDeviceConnect(boolean z) {
        Constant.isConnected = z;
        invalidateOptionsMenu();
    }

    @Override
    public void onItemClick(int i) {
        checkConnection(i);
        if (Constant.isConnected || Constant.isChromeCastConnected || Constant.isDLNACastConnected) {
            startActivityPlay(i);
        }
    }

    private void checkConnection(int i) {
        if (!Constant.isConnected) {
            Intent intent = new Intent(this, CastDeviceListActivity.class);
            intent.putExtra(Constant.BACK_AFTER_CONNECTING, true);
            intent.putExtra(Constant.EXTRA_POSITION_RESULT, i);
            this.intentActivityResultLauncher.launch(intent);
        } else if (Constant.isChromeCastConnected) {
            if (this.mCastSession == null) {
                Intent intent2 = new Intent(this, CastDeviceListActivity.class);
                intent2.putExtra(Constant.BACK_AFTER_CONNECTING, true);
                intent2.putExtra(Constant.EXTRA_POSITION_RESULT, i);
                this.intentActivityResultLauncher.launch(intent2);
            }
        } else if (Constant.isDLNACastConnected && CastDeviceListActivity.upnpServiceController.getSelectedRenderer() == null) {
            Intent intent3 = new Intent(this, CastDeviceListActivity.class);
            intent3.putExtra(Constant.BACK_AFTER_CONNECTING, true);
            intent3.putExtra(Constant.EXTRA_POSITION_RESULT, i);
            this.intentActivityResultLauncher.launch(intent3);
        }
    }

    public void startActivityPlay(int i) {
        if (this.listImages.get(i).getType() == 1) {
            Intent intent = new Intent(this, FullImageViewActivity.class);
            intent.putParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL, (ArrayList) this.listImages);
            intent.putExtra(Constant.EXTRA_POSITION_DATA, i);
            startActivity(intent);
            return;
        }
        Intent intent2 = new Intent(this, VideoPlayActivity.class);
        intent2.putParcelableArrayListExtra(Constant.EXTRA_LIST_MEDIA_FILE_MODEL, (ArrayList) this.listImages);
        intent2.putExtra(Constant.EXTRA_POSITION_DATA, i);
        startActivity(intent2);
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
            if (castSession == ListVideosActivity.this.mCastSession) {
                ListVideosActivity.this.mCastSession = null;
            }
            ListVideosActivity.this.invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean z) {
            ListVideosActivity.this.mCastSession = castSession;
            ListVideosActivity.this.invalidateOptionsMenu();
        }

        @Override
        public void onSessionStarted(CastSession castSession, String str) {
            ListVideosActivity.this.mCastSession = castSession;
            ListVideosActivity.this.invalidateOptionsMenu();
        }
    }
}
