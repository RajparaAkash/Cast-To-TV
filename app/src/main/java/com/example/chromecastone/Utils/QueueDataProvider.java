package com.example.chromecastone.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class QueueDataProvider {
    public static final int INVALID = -1;
    private static final String TAG = "QueueDataProvider";
    private static QueueDataProvider mInstance;
    private boolean isImage;
    private final Context mAppContext;
    private MediaQueueItem mCurrentIem;
    private boolean mDetachedQueue;
    private OnQueueDataChangedListener mListener;
    private final RemoteMediaClient.Callback mRemoteMediaClientCallback;
    private int mRepeatMode;
    private final SessionManagerListener<CastSession> mSessionManagerListener;
    private boolean mShuffle;
    private MediaQueueItem mUpcomingItem;
    private final List<MediaQueueItem> mQueue = new CopyOnWriteArrayList();
    private final Object mLock = new Object();

    
    public interface OnQueueDataChangedListener {
        void onQueueDataChanged();
    }

    private QueueDataProvider(Context context) {
        MySessionManagerListener mySessionManagerListener = new MySessionManagerListener();
        this.mSessionManagerListener = mySessionManagerListener;
        this.mRemoteMediaClientCallback = new MyRemoteMediaClientCallback();
        this.mDetachedQueue = true;
        this.isImage = false;
        Context applicationContext = context.getApplicationContext();
        this.mAppContext = applicationContext;
        this.mRepeatMode = 0;
        this.mShuffle = false;
        this.mCurrentIem = null;
        CastContext.getSharedInstance(applicationContext).getSessionManager().addSessionManagerListener(mySessionManagerListener, CastSession.class);
        syncWithRemoteQueue();
    }

    public void onUpcomingStopClicked(View view, MediaQueueItem mediaQueueItem) {
        RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        int positionByItemId = getPositionByItemId(mediaQueueItem.getItemId());
        int count = getCount() - positionByItemId;
        int[] iArr = new int[count];
        for (int i = 0; i < count; i++) {
            iArr[i] = this.mQueue.get(i + positionByItemId).getItemId();
        }
        remoteMediaClient.queueRemoveItems(iArr, null);
    }

    public void onUpcomingPlayClicked(MediaQueueItem mediaQueueItem) {
        RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.queueJumpToItem(mediaQueueItem.getItemId(), null);
    }

    public boolean isQueueDetached() {
        return this.mDetachedQueue;
    }

    public void setIsImage(boolean z) {
        this.isImage = z;
    }

    public int getPositionByItemId(int i) {
        if (this.mQueue.isEmpty()) {
            return -1;
        }
        for (int i2 = 0; i2 < this.mQueue.size(); i2++) {
            if (this.mQueue.get(i2).getItemId() == i) {
                return i2;
            }
        }
        return -1;
    }

    public static synchronized QueueDataProvider getInstance(Context context) {
        QueueDataProvider queueDataProvider;
        synchronized (QueueDataProvider.class) {
            if (mInstance == null) {
                mInstance = new QueueDataProvider(context);
            }
            queueDataProvider = mInstance;
        }
        return queueDataProvider;
    }

    public void removeFromQueue(int i) {
        synchronized (this.mLock) {
            RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
            if (remoteMediaClient == null) {
                return;
            }
            remoteMediaClient.queueRemoveItem(this.mQueue.get(i).getItemId(), null);
        }
    }

    public void removeAll() {
        synchronized (this.mLock) {
            if (this.mQueue.isEmpty()) {
                return;
            }
            RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
            if (remoteMediaClient == null) {
                return;
            }
            int[] iArr = new int[this.mQueue.size()];
            for (int i = 0; i < this.mQueue.size(); i++) {
                iArr[i] = this.mQueue.get(i).getItemId();
            }
            remoteMediaClient.queueRemoveItems(iArr, null);
            this.mQueue.clear();
        }
    }

    public void moveItem(int i, int i2) {
        RemoteMediaClient remoteMediaClient;
        if (i == i2 || (remoteMediaClient = getRemoteMediaClient()) == null) {
            return;
        }
        remoteMediaClient.queueMoveItemToNewIndex(this.mQueue.get(i).getItemId(), i2, null);
        this.mQueue.add(i2, this.mQueue.remove(i));
    }

    public int getCount() {
        return this.mQueue.size();
    }

    public MediaQueueItem getItem(int i) {
        return this.mQueue.get(i);
    }

    public void clearQueue() {
        this.mQueue.clear();
        this.mDetachedQueue = true;
        this.mCurrentIem = null;
    }

    public int getRepeatMode() {
        return this.mRepeatMode;
    }

    public boolean isShuffleOn() {
        return this.mShuffle;
    }

    public MediaQueueItem getCurrentItem() {
        return this.mCurrentIem;
    }

    public int getCurrentItemId() {
        return this.mCurrentIem.getItemId();
    }

    public MediaQueueItem getUpcomingItem() {
        Log.d(TAG, "[upcoming] getUpcomingItem() returning " + this.mUpcomingItem);
        return this.mUpcomingItem;
    }

    public void setOnQueueDataChangedListener(OnQueueDataChangedListener onQueueDataChangedListener) {
        this.mListener = onQueueDataChangedListener;
    }

    public List<MediaQueueItem> getItems() {
        return this.mQueue;
    }

    
    public void syncWithRemoteQueue() {
        List<MediaQueueItem> queueItems;
        RemoteMediaClient remoteMediaClient = getRemoteMediaClient();
        if (remoteMediaClient != null) {
            remoteMediaClient.registerCallback(this.mRemoteMediaClientCallback);
            MediaStatus mediaStatus = remoteMediaClient.getMediaStatus();
            if (mediaStatus == null || (queueItems = mediaStatus.getQueueItems()) == null || queueItems.isEmpty()) {
                return;
            }
            this.mQueue.clear();
            this.mQueue.addAll(queueItems);
            this.mRepeatMode = mediaStatus.getQueueRepeatMode();
            this.mCurrentIem = mediaStatus.getQueueItemById(mediaStatus.getCurrentItemId());
            this.mDetachedQueue = false;
            this.mUpcomingItem = mediaStatus.getQueueItemById(mediaStatus.getPreloadedItemId());
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
        public void onSessionResumed(CastSession castSession, boolean z) {
            QueueDataProvider.this.syncWithRemoteQueue();
        }

        @Override
        public void onSessionStarted(CastSession castSession, String str) {
            QueueDataProvider.this.syncWithRemoteQueue();
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {
            QueueDataProvider.this.clearQueue();
            if (QueueDataProvider.this.mListener != null) {
                QueueDataProvider.this.mListener.onQueueDataChanged();
            }
        }
    }

    
    private class MyRemoteMediaClientCallback extends RemoteMediaClient.Callback {
        private MyRemoteMediaClientCallback() {
        }

        @Override
        public void onPreloadStatusUpdated() {
            MediaStatus mediaStatus;
            RemoteMediaClient remoteMediaClient = QueueDataProvider.this.getRemoteMediaClient();
            if (remoteMediaClient == null || (mediaStatus = remoteMediaClient.getMediaStatus()) == null) {
                return;
            }
            QueueDataProvider.this.mUpcomingItem = mediaStatus.getQueueItemById(mediaStatus.getPreloadedItemId());
            Log.d(QueueDataProvider.TAG, "onRemoteMediaPreloadStatusUpdated() with item=" + QueueDataProvider.this.mUpcomingItem);
            if (QueueDataProvider.this.mListener != null) {
                QueueDataProvider.this.mListener.onQueueDataChanged();
            }
        }

        @Override
        public void onQueueStatusUpdated() {
            updateMediaQueue();
            if (QueueDataProvider.this.mListener != null) {
                QueueDataProvider.this.mListener.onQueueDataChanged();
            }
            Log.d(QueueDataProvider.TAG, "Queue was updated");
        }

        @Override
        public void onStatusUpdated() {
            updateMediaQueue();
            if (QueueDataProvider.this.mListener != null) {
                QueueDataProvider.this.mListener.onQueueDataChanged();
            }
        }

        private void updateMediaQueue() {
            MediaStatus mediaStatus;
            if (QueueDataProvider.this.isImage) {
                return;
            }
            RemoteMediaClient remoteMediaClient = QueueDataProvider.this.getRemoteMediaClient();
            List<MediaQueueItem> list = null;
            if (remoteMediaClient != null && (mediaStatus = remoteMediaClient.getMediaStatus()) != null) {
                list = mediaStatus.getQueueItems();
                QueueDataProvider.this.mRepeatMode = mediaStatus.getQueueRepeatMode();
                QueueDataProvider.this.mCurrentIem = mediaStatus.getQueueItemById(mediaStatus.getCurrentItemId());
            }
            QueueDataProvider.this.mQueue.clear();
            if (list == null) {
                Log.d(QueueDataProvider.TAG, "Queue is cleared");
                return;
            }
            Log.d(QueueDataProvider.TAG, "Queue is updated with a list of size: " + list.size());
            if (list.size() > 0) {
                QueueDataProvider.this.mQueue.addAll(list);
                QueueDataProvider.this.mDetachedQueue = false;
                return;
            }
            QueueDataProvider.this.mDetachedQueue = true;
        }
    }

    
    public RemoteMediaClient getRemoteMediaClient() {
        CastSession currentCastSession = CastContext.getSharedInstance(this.mAppContext).getSessionManager().getCurrentCastSession();
        if (currentCastSession == null || !currentCastSession.isConnected()) {
            Log.w(TAG, "Trying to get a RemoteMediaClient when no CastSession is started.");
            return null;
        }
        return currentCastSession.getRemoteMediaClient();
    }
}
