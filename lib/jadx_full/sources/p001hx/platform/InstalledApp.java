package p001hx.platform;

import android.content.Context;

/* loaded from: classes3.dex */
public final class InstalledApp {
    private static final String ACTIVITY_THREAD = "android.app.ActivityThread";

    private InstalledApp() {
    }

    public static Context context() throws ReflectiveOperationException {
        return (Context) Class.forName(ACTIVITY_THREAD).getMethod("currentApplication", new Class[0]).invoke(null, new Object[0]);
    }
}
