package screenAlike;

import android.graphics.Bitmap;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.chromecastone.Utils.Constant;
import com.example.chromecastone.Utils.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public final class ImageGenerator {
    private volatile boolean isThreadRunning;
    private Handler mImageHandler;
    private ImageReader mImageReader;
    private HandlerThread mImageThread;
    private ByteArrayOutputStream mJpegOutputStream;
    private final Object mLock = new Object();
    private Bitmap mReusableBitmap;
    private VirtualDisplay mVirtualDisplay;


    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        private Bitmap mCleanBitmap;
        private Image mImage;
        private byte[] mJpegByteArray;
        private Image.Plane mPlane;
        private int mWidth;

        private ImageAvailableListener() {
        }

        @Override
        public void onImageAvailable(ImageReader imageReader) {
            synchronized (ImageGenerator.this.mLock) {
                if (ImageGenerator.this.isThreadRunning) {
                    try {
                        Image acquireLatestImage = ImageGenerator.this.mImageReader.acquireLatestImage();
                        this.mImage = acquireLatestImage;
                        if (acquireLatestImage == null) {
                            return;
                        }
                        Image.Plane plane = acquireLatestImage.getPlanes()[0];
                        this.mPlane = plane;
                        int rowStride = plane.getRowStride() / this.mPlane.getPixelStride();
                        this.mWidth = rowStride;
                        if (rowStride > this.mImage.getWidth()) {
                            if (ImageGenerator.this.mReusableBitmap == null) {
                                ImageGenerator.this.mReusableBitmap = Bitmap.createBitmap(this.mWidth, this.mImage.getHeight(), Bitmap.Config.ARGB_8888);
                            }
                            ImageGenerator.this.mReusableBitmap.copyPixelsFromBuffer(this.mPlane.getBuffer());
                            this.mCleanBitmap = Bitmap.createBitmap(ImageGenerator.this.mReusableBitmap, 0, 0, this.mImage.getWidth(), this.mImage.getHeight());
                        } else {
                            Bitmap createBitmap = Bitmap.createBitmap(this.mImage.getWidth(), this.mImage.getHeight(), Bitmap.Config.ARGB_8888);
                            this.mCleanBitmap = createBitmap;
                            createBitmap.copyPixelsFromBuffer(this.mPlane.getBuffer());
                        }
                        Bitmap bitmap = this.mCleanBitmap;
                        this.mImage.close();
                        ImageGenerator.this.mJpegOutputStream.reset();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ImageGenerator.this.mJpegOutputStream);
                        bitmap.recycle();
                        byte[] byteArray = ImageGenerator.this.mJpegOutputStream.toByteArray();
                        this.mJpegByteArray = byteArray;
                        if (byteArray != null) {
                            if (MyApplication.getAppData().getImageQueue().size() > 3) {
                                MyApplication.getAppData().getImageQueue().pollLast();
                            }
                            MyApplication.getAppData().getImageQueue().add(this.mJpegByteArray);
                            this.mJpegByteArray = null;
                        }
                    } catch (UnsupportedOperationException unused) {
                    }
                }
            }
        }
    }

    public void start() {
        synchronized (this.mLock) {
            if (this.isThreadRunning) {
                return;
            }
            MediaProjection mediaProjection = MyApplication.getMediaProjection();
            if (mediaProjection == null) {
                return;
            }
            HandlerThread handlerThread = new HandlerThread("ImageGenerator", -1);
            this.mImageThread = handlerThread;
            handlerThread.start();
            this.mImageReader = ImageReader.newInstance(MyApplication.getAppData().getScreenSize().x, MyApplication.getAppData().getScreenSize().y, 1, 2);
            this.mImageHandler = new Handler(this.mImageThread.getLooper());
            this.mJpegOutputStream = new ByteArrayOutputStream();
            this.mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), this.mImageHandler);
            this.mVirtualDisplay = mediaProjection.createVirtualDisplay(Constant.VIRTUAL_DISPLAY_NAME, MyApplication.getAppData().getScreenSize().x, MyApplication.getAppData().getScreenSize().y, MyApplication.getAppData().getScreenDensity(), 16, this.mImageReader.getSurface(), null, this.mImageHandler);
            this.isThreadRunning = true;
        }
    }

    public void stop() {
        synchronized (this.mLock) {
            if (this.isThreadRunning) {
                this.mImageReader.setOnImageAvailableListener(null, null);
                this.mImageReader.close();
                this.mImageReader = null;
                try {
                    this.mJpegOutputStream.close();
                } catch (IOException unused) {
                }
                this.mVirtualDisplay.release();
                this.mVirtualDisplay = null;
                this.mImageHandler.removeCallbacksAndMessages(null);
                this.mImageThread.quit();
                this.mImageThread = null;
                Bitmap bitmap = this.mReusableBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.mReusableBitmap = null;
                }
                this.isThreadRunning = false;
            }
        }
    }
}
