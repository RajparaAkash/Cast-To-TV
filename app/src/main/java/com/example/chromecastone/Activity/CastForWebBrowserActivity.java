package com.example.chromecastone.Activity;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chromecastone.R;
import com.example.chromecastone.Service.BackgroundService;
import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.Utils.MyApplication;
import com.example.chromecastone.databinding.ActivityCastForWebBrowserBinding;

import screenAlike.ForegroundServiceHandler;
import screenAlike.NotifyImageGenerator;


public class CastForWebBrowserActivity extends AppCompatActivity {

    private ActivityCastForWebBrowserBinding binding;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CastForWebBrowserActivity.this.updateService();
        }
    };
    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult activityResult) {
            CastForWebBrowserActivity.this.startStreaming(activityResult);
        }
    });
    private boolean isCasting;
    private ForegroundServiceHandler mForegroundServiceTaskHandler;
    private HandlerThread mHandlerThread;
    private NotifyImageGenerator mNotifyImageGenerator;
    private MediaProjectionManager projectionManager;

    
    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiver, new IntentFilter(Constant.BROADCAST_ACTION));
    }

    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActivityCastForWebBrowserBinding inflate = ActivityCastForWebBrowserBinding.inflate(getLayoutInflater());
        this.binding = inflate;
        setContentView(inflate.getRoot());
        clickStopStreamService();
        initMain();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTextButton();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initMain() {
        initView();
        addEvent();
    }


    private void initView() {
        this.binding.tb.setTitle("Mirror Web Browser");
        this.binding.tb.setNavigationIcon(R.drawable.back_img);
        this.binding.tb.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(this.binding.tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.isCasting = false;
        this.binding.tvIpAddress.setText(MyApplication.getAppData().getServerAddress());
        HandlerThread handlerThread = new HandlerThread("MyApplication", -1);
        this.mHandlerThread = handlerThread;
        handlerThread.start();
        this.mForegroundServiceTaskHandler = new ForegroundServiceHandler(this.mHandlerThread.getLooper());
    }

    
    public void startStreaming(final ActivityResult activityResult) {
        if (activityResult.getResultCode() != -1) {
            Toast.makeText(this, "Screen Cast permission denied", Toast.LENGTH_SHORT).show();
            this.isCasting = false;
            updateTextButton();
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MediaProjection mediaProjection;
                MyApplication.startServer();
                CastForWebBrowserActivity.this.projectionManager = MyApplication.getProjectionManager();
                if (CastForWebBrowserActivity.this.projectionManager == null || (mediaProjection = CastForWebBrowserActivity.this.projectionManager.getMediaProjection(activityResult.getResultCode(), activityResult.getData())) == null) {
                    return;
                }
                MyApplication.setMediaProjection(mediaProjection);
                CastForWebBrowserActivity.this.mForegroundServiceTaskHandler.obtainMessage(0).sendToTarget();
                CastForWebBrowserActivity.this.isCasting = true;
                CastForWebBrowserActivity.this.updateTextButton();
                CastForWebBrowserActivity.this.clickStartStreamService(MyApplication.getAppData().getServerAddress());
            }
        }, 1000L);
    }

    private void addEvent() {
        this.binding.ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CastForWebBrowserActivity.this.m87xa3291b05(view);
            }
        });
        this.binding.btCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CastForWebBrowserActivity.this.m89xa7be84c3(view);
            }
        });
        this.binding.btShareIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CastForWebBrowserActivity.this.m90x2a0939a2(view);
            }
        });
    }

    
    public void m87xa3291b05(View view) {
        setClipboard(this.binding.tvIpAddress.getText().toString().trim());
    }

    
    public void m89xa7be84c3(View view) {
        CastForWebBrowserActivity.this.m88x2573cfe4();
    }

    
    public void m88x2573cfe4() {
        if (!this.isCasting) {
            MediaProjectionManager projectionManager = MyApplication.getProjectionManager();
            this.projectionManager = projectionManager;
            if (projectionManager != null) {
                this.intentActivityResultLauncher.launch(projectionManager.createScreenCaptureIntent());
                return;
            }
            return;
        }
        this.mForegroundServiceTaskHandler.obtainMessage(1).sendToTarget();
        NotifyImageGenerator notifyImageGenerator = new NotifyImageGenerator(getApplicationContext());
        this.mNotifyImageGenerator = notifyImageGenerator;
        notifyImageGenerator.addDefaultScreen();
        clickStopStreamService();
        this.isCasting = false;
        updateTextButton();
    }

    
    public void m90x2a0939a2(View view) {
        shareApp(this.binding.tvIpAddress.getText().toString().trim());
    }

    private void shareApp(String str) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.text_choose_tools));
            String string = getString(R.string.text_recommend);
            intent.putExtra("android.intent.extra.TEXT", string + str + "\n");
            startActivity(Intent.createChooser(intent, getString(R.string.text_choose_tools_ip)));
        } catch (Exception unused) {
        }
    }

    
    public void updateTextButton() {
        try {
            if (this.isCasting) {
                this.binding.btCast.setText("Stop Mirroring");
                this.binding.btCast.setBackground(getResources().getDrawable(R.drawable.selector_button_screen_mirroring_red));
            } else {
                this.binding.btCast.setText("Start Mirroring");
                this.binding.btCast.setBackground(getResources().getDrawable(R.drawable.selector_button_screen_mirroring));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void updateService() {
        this.mForegroundServiceTaskHandler.obtainMessage(1).sendToTarget();
        NotifyImageGenerator notifyImageGenerator = new NotifyImageGenerator(getApplicationContext());
        this.mNotifyImageGenerator = notifyImageGenerator;
        notifyImageGenerator.addDefaultScreen();
        clickStopStreamService();
        this.isCasting = false;
        updateTextButton();
    }

    
    public void clickStartStreamService(String str) {
        Intent intent = new Intent(this, BackgroundService.class);
        intent.putExtra(Constant.NOTIFICATION_TITLE, getString(R.string.text_screen_transmisstion));
        intent.putExtra(Constant.NOTIFICATION_CONTENT, str);
        intent.putExtra(Constant.IS_STREAMING, true);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void clickStopStreamService() {
        Intent intent = new Intent(this, BackgroundService.class);
        intent.putExtra(Constant.NOTIFICATION_TITLE, getString(R.string.text_ready_to_stream));
        intent.putExtra(Constant.NOTIFICATION_CONTENT, getString(R.string.text_content_stream));
        intent.putExtra(Constant.IS_STREAMING, false);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void setClipboard(String str) {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Copied Text", str));
        Toast.makeText(this, (int) R.string.text_copy_successful, Toast.LENGTH_SHORT).show();
    }
}
