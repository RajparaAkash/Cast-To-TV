package screenAlike;

import com.example.chromecastone.Utils.MyApplication;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;



public final class ImageDispatcher {
    private volatile boolean isThreadRunning;
    private JpegStreamerThread mJpegStreamerThread;
    private final Object mLock = new Object();

    

    public class JpegStreamerThread extends Thread {
        private byte[] mCurrentJpeg;
        private byte[] mLastJpeg;
        private int mSleepCount;

        JpegStreamerThread() {
            super(JpegStreamerThread.class.getSimpleName());
        }

        @Override
        public void run() {
            while (!isInterrupted() && ImageDispatcher.this.isThreadRunning) {
                byte[] poll = MyApplication.getAppData().getImageQueue().poll();
                this.mCurrentJpeg = poll;
                if (poll == null) {
                    try {
                        sleep(24L);
                        int i = this.mSleepCount + 1;
                        this.mSleepCount = i;
                        if (i >= 20) {
                            sendLastJPEGToClients();
                        }
                    } catch (InterruptedException unused) {
                    }
                } else {
                    this.mLastJpeg = poll;
                    sendLastJPEGToClients();
                }
            }
        }

        private void sendLastJPEGToClients() {
            this.mSleepCount = 0;
            synchronized (ImageDispatcher.this.mLock) {
                if (ImageDispatcher.this.isThreadRunning) {
                    Iterator<Client> it = MyApplication.getAppData().getClientQueue().iterator();
                    while (it.hasNext()) {
                        it.next().sendClientData(2, this.mLastJpeg, false);
                    }
                }
            }
        }
    }

    
    public void addClient(Socket socket) {
        synchronized (this.mLock) {
            if (this.isThreadRunning) {
                try {
                    Client client = new Client(socket);
                    client.sendClientData(1, null, false);
                    MyApplication.getAppData().getClientQueue().add(client);
                    MyApplication.getAppData().setClients(MyApplication.getAppData().getClientQueue().size());
                } catch (IOException unused) {
                }
            }
        }
    }

    
    public void start() {
        synchronized (this.mLock) {
            if (this.isThreadRunning) {
                return;
            }
            JpegStreamerThread jpegStreamerThread = new JpegStreamerThread();
            this.mJpegStreamerThread = jpegStreamerThread;
            jpegStreamerThread.start();
            this.isThreadRunning = true;
        }
    }

    
    public void stop(byte[] bArr) {
        synchronized (this.mLock) {
            if (this.isThreadRunning) {
                this.isThreadRunning = false;
                this.mJpegStreamerThread.interrupt();
                Iterator<Client> it = MyApplication.getAppData().getClientQueue().iterator();
                while (it.hasNext()) {
                    it.next().sendClientData(2, bArr, true);
                }
                MyApplication.getAppData().getClientQueue().clear();
                MyApplication.getAppData().setClients(0);
            }
        }
    }
}
