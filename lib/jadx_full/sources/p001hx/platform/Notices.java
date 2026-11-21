package p001hx.platform;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
public final class Notices {
    private static final int CONTEXT_ATTEMPTS = 30;
    private static final long CONTEXT_RETRY_DELAY_MS = 1000;
    private static final Set<String> SHOWN = Collections.synchronizedSet(new HashSet());

    private Notices() {
    }

    public static void showOnce(String str) {
        if (SHOWN.add(str)) {
            show(str);
        }
    }

    public static void show(String str) {
        post(str, 0, 0L);
    }

    private static void post(final String str, final int i, long j) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: hx.platform.Notices.0
            @Override // java.lang.Runnable
            public final void run() {
                Notices.toast(str, i);
            }
        }, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void toast(String str, int i) {
        Context applicationOrNull = applicationOrNull();
        if (applicationOrNull != null) {
            try {
                Toast.makeText(applicationOrNull, str, 1).show();
                return;
            } catch (Throwable th) {
                Log.e(Logs.TAG, "cannot show \"" + str + "\"", th);
                return;
            }
        }
        int i2 = i + 1;
        if (i2 >= CONTEXT_ATTEMPTS) {
            Log.e(Logs.TAG, "cannot show \"" + str + "\": no application context");
        } else {
            post(str, i2, CONTEXT_RETRY_DELAY_MS);
        }
    }

    private static Context applicationOrNull() {
        try {
            return InstalledApp.context();
        } catch (ReflectiveOperationException | RuntimeException e) {
            Log.w(Logs.TAG, "application context unavailable: " + e);
            return null;
        }
    }
}
