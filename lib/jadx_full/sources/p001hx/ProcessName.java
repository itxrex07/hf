package p001hx;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import p001hx.util.Logs;
import p001hx.util.Streams;

/* loaded from: classes3.dex */
final class ProcessName {
    private static final String COMMAND_LINE = "/proc/self/cmdline";

    private ProcessName() {
    }

    static String current() {
        return read(new File(COMMAND_LINE));
    }

    static String read(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                String str = new String(Streams.readAll(fileInputStream), StandardCharsets.UTF_8);
                int indexOf = str.indexOf(0);
                if (indexOf >= 0) {
                    str = str.substring(0, indexOf);
                }
                if (str.isEmpty()) {
                    str = null;
                }
                fileInputStream.close();
                return str;
            } finally {
            }
        } catch (IOException e) {
            Log.w(Logs.TAG, "process name unreadable", e);
            return null;
        }
    }

    static boolean isMain(String str) {
        return (str == null || str.contains(":")) ? false : true;
    }
}
