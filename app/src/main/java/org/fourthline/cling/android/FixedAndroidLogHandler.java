package org.fourthline.cling.android;

import android.util.Log;

import com.example.chromecastone.CastServer.CastServerService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class FixedAndroidLogHandler extends Handler {
    private static final Formatter THE_FORMATTER = new Formatter() {
        @Override
        public String format(LogRecord logRecord) {
            Throwable thrown = logRecord.getThrown();
            if (thrown != null) {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                stringWriter.write(logRecord.getMessage());
                stringWriter.write("\n");
                thrown.printStackTrace(printWriter);
                printWriter.flush();
                return stringWriter.toString();
            }
            return logRecord.getMessage();
        }
    };

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    public FixedAndroidLogHandler() {
        setFormatter(THE_FORMATTER);
    }

    @Override
    public void publish(LogRecord logRecord) {
        try {
            int androidLevel = getAndroidLevel(logRecord.getLevel());
            String loggerName = logRecord.getLoggerName();
            if (loggerName == null) {
                loggerName = "null";
            } else {
                int length = loggerName.length();
                if (length > 23) {
                    int lastIndexOf = loggerName.lastIndexOf(CastServerService.ROOT_DIR);
                    if ((length - lastIndexOf) - 1 <= 23) {
                        loggerName = loggerName.substring(lastIndexOf + 1);
                    } else {
                        loggerName = loggerName.substring(loggerName.length() - 23);
                    }
                }
            }
            Log.println(androidLevel, loggerName, getFormatter().format(logRecord));
        } catch (RuntimeException e) {
            Log.e("AndroidHandler", "Error logging message.", e);
        }
    }

    static int getAndroidLevel(Level level) {
        int intValue = level.intValue();
        if (intValue >= 1000) {
            return 6;
        }
        if (intValue >= 900) {
            return 5;
        }
        return intValue >= 800 ? 4 : 3;
    }
}
