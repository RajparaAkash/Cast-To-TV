package fi.iki.elonen;

import android.util.Log;

import java.io.IOException;
import java.io.PrintStream;


public class ServerRunner {
    public static void run(Class cls) {
        try {
            executeInstance((NanoHTTPD) cls.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void executeInstance(NanoHTTPD nanoHTTPD) {
        try {
            nanoHTTPD.start();
        } catch (IOException e) {
            e.printStackTrace();
            PrintStream printStream = System.err;
            printStream.println("Couldn't start server:\n" + e);
            System.exit(-1);
        }
        System.out.println("Server started, Hit Enter to stop.\n");
        try {
            System.in.read();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        nanoHTTPD.stop();
        Log.i("ServerRunner", "MediaServer stop");
        System.out.println("Server stopped.\n");
    }
}
