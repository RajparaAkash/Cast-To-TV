package screenAlike;

import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.chromecastone.Utils.MyApplication;


public final class ForegroundServiceHandler extends Handler {
    public static final int HANDLER_DETECT_ROTATION = 20;
    public static final int HANDLER_PAUSE_STREAMING = 10;
    public static final int HANDLER_RESUME_STREAMING = 11;
    public static final int HANDLER_START_STREAMING = 0;
    public static final int HANDLER_STOP_STREAMING = 1;
    public boolean mCurrentOrientation;

    public ForegroundServiceHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 0) {
            if (MyApplication.getAppData().isStreamRunning()) {
                return;
            }
            removeMessages(20);
            this.mCurrentOrientation = getOrientation();
            ImageGenerator imageGenerator = MyApplication.getImageGenerator();
            if (imageGenerator != null) {
                imageGenerator.start();
            }
            sendMessageDelayed(obtainMessage(20), 250L);
            MyApplication.getAppData().setStreamRunning(true);
        } else if (i == 1) {
            removeMessages(20);
            removeMessages(1);
            ImageGenerator imageGenerator2 = MyApplication.getImageGenerator();
            if (imageGenerator2 != null) {
                imageGenerator2.stop();
            }
            MediaProjection mediaProjection = MyApplication.getMediaProjection();
            if (mediaProjection != null) {
                mediaProjection.stop();
            }
            MyApplication.getAppData().setStreamRunning(false);
        } else if (i == 10) {
            if (MyApplication.getAppData().isStreamRunning()) {
                ImageGenerator imageGenerator3 = MyApplication.getImageGenerator();
                if (imageGenerator3 != null) {
                    imageGenerator3.stop();
                }
                sendMessageDelayed(obtainMessage(11), 250L);
            }
        } else if (i == 11) {
            if (MyApplication.getAppData().isStreamRunning()) {
                ImageGenerator imageGenerator4 = MyApplication.getImageGenerator();
                if (imageGenerator4 != null) {
                    imageGenerator4.start();
                }
                sendMessageDelayed(obtainMessage(20), 250L);
            }
        } else if (i == 20 && MyApplication.getAppData().isStreamRunning()) {
            boolean orientation = getOrientation();
            if (this.mCurrentOrientation == orientation) {
                sendMessageDelayed(obtainMessage(20), 250L);
                return;
            }
            this.mCurrentOrientation = orientation;
            obtainMessage(10).sendToTarget();
        }
    }

    private boolean getOrientation() {
        int rotation = MyApplication.getAppData().getWindowsManager().getDefaultDisplay().getRotation();
        return rotation == 0 || rotation == 2;
    }
}
