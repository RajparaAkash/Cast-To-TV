package com.example.chromecastone.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chromecastone.Activity.CastForWebBrowserActivity;
import com.example.chromecastone.R;
import com.example.chromecastone.Receiver.MyReceiver;
import com.example.chromecastone.Utils.Constant;


public class BackgroundService extends Service {

    private boolean isStopCast;
    private String title = "";
    private String content = "";
    private boolean isConnected = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent.getStringExtra(Constant.NOTIFICATION_TITLE) != null) {
            this.title = intent.getStringExtra(Constant.NOTIFICATION_TITLE);
        }
        if (intent.getStringExtra(Constant.NOTIFICATION_CONTENT) != null) {
            this.content = intent.getStringExtra(Constant.NOTIFICATION_CONTENT);
        }
        this.isConnected = intent.getBooleanExtra(Constant.IS_STREAMING, false);
        boolean booleanExtra = intent.getBooleanExtra(Constant.IS_STOP_STREAMING, false);
        this.isStopCast = booleanExtra;
        if (booleanExtra) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constant.BROADCAST_ACTION));
        }
        sendNotification();
        return START_NOT_STICKY;
    }

    private void sendNotification() {
        PendingIntent activity;
        Intent intent = new Intent(this, CastForWebBrowserActivity.class);
        if (Build.VERSION.SDK_INT >= 31) {
            activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), (int) R.layout.layout_custom_notification);
        remoteViews.setTextViewText(R.id.tv_title_notification, this.title);
        remoteViews.setTextViewText(R.id.tv_content_notification, this.content);
        if (this.isConnected) {
            remoteViews.setViewVisibility(R.id.iv_close_foreground, View.VISIBLE);
        } else {
            remoteViews.setViewVisibility(R.id.iv_close_foreground, View.GONE);
        }
        remoteViews.setOnClickPendingIntent(R.id.iv_close_foreground, getPendingIntent(this, true));
        startForeground(1, new NotificationCompat.Builder(this, Constant.NOTIFICATION_CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher_background).setStyle(new NotificationCompat.DecoratedCustomViewStyle()).setContentIntent(activity).setCustomContentView(remoteViews).build());
    }

    private PendingIntent getPendingIntent(Context context, boolean z) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action", z);
        if (Build.VERSION.SDK_INT >= 31) {
            return PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        return PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(Constant.NOTIFICATION_CHANNEL_ID, Constant.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
