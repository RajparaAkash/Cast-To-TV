package screenAlike;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;

import androidx.core.view.ViewCompat;

import com.example.chromecastone.R;
import com.example.chromecastone.Utils.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class NotifyImageGenerator {
    private final Context mContext;
    private byte[] mCurrentDefaultScreen;
    private int mCurrentScreenSizeX;

    public NotifyImageGenerator(Context context) {
        this.mContext = context;
    }

    public void addDefaultScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NotifyImageGenerator.this.mCurrentScreenSizeX != MyApplication.getAppData().getScreenSize().x) {
                    NotifyImageGenerator.this.mCurrentDefaultScreen = null;
                }
                if (NotifyImageGenerator.this.mCurrentDefaultScreen == null) {
                    NotifyImageGenerator notifyImageGenerator = NotifyImageGenerator.this;
                    notifyImageGenerator.mCurrentDefaultScreen = NotifyImageGenerator.generateImage(notifyImageGenerator.mContext.getString(R.string.image_generator_press), NotifyImageGenerator.this.mContext.getString(R.string.text_start_mirroring).toUpperCase(), NotifyImageGenerator.this.mContext.getString(R.string.image_generator_on_device));
                    NotifyImageGenerator.this.mCurrentScreenSizeX = MyApplication.getAppData().getScreenSize().x;
                }
                if (NotifyImageGenerator.this.mCurrentDefaultScreen != null) {
                    MyApplication.getAppData().getImageQueue().add(NotifyImageGenerator.this.mCurrentDefaultScreen);
                }
            }
        }, 500L);
    }

    
    public static byte[] generateImage(String str, String str2, String str3) {
        Bitmap createBitmap = Bitmap.createBitmap(MyApplication.getAppData().getScreenSize().x, MyApplication.getAppData().getScreenSize().y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawRGB(255, 255, 255);
        Rect rect = new Rect();
        Paint paint = new Paint(1);
        if (!"".equals(str)) {
            int displayScale = (int) (MyApplication.getAppData().getDisplayScale() * 12.0f);
            paint.setTextSize(displayScale);
            paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            paint.getTextBounds(str, 0, str.length(), rect);
            canvas.drawText(str, (createBitmap.getWidth() - rect.width()) / 2, ((createBitmap.getHeight() + rect.height()) / 2) - (displayScale * 2), paint);
        }
        if (!"".equals(str2)) {
            paint.setTextSize((int) (MyApplication.getAppData().getDisplayScale() * 16.0f));
            paint.setColor(Color.rgb((int) 153, 50, 0));
            paint.getTextBounds(str2, 0, str2.length(), rect);
            canvas.drawText(str2.toUpperCase(), (createBitmap.getWidth() - rect.width()) / 2, (createBitmap.getHeight() + rect.height()) / 2, paint);
        }
        if (!"".equals(str3)) {
            int displayScale2 = (int) (MyApplication.getAppData().getDisplayScale() * 12.0f);
            paint.setTextSize(displayScale2);
            paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            paint.getTextBounds(str3, 0, str3.length(), rect);
            canvas.drawText(str3, (createBitmap.getWidth() - rect.width()) / 2, ((createBitmap.getHeight() + rect.height()) / 2) + (displayScale2 * 2), paint);
        }
        byte[] bArr = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            createBitmap.compress(Bitmap.CompressFormat.JPEG, MyApplication.getAppData().getJpegQuality(), byteArrayOutputStream);
            bArr = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (IOException unused) {
        }
        createBitmap.recycle();
        return bArr;
    }
}
