package p001hx;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import p001hx.gateway.BuildTag;
import p001hx.gateway.BuildTagInterceptor;
import p001hx.gateway.CloudFrontPlayInfoClient;
import p001hx.gateway.SharedHeaders;
import p001hx.loopback.DashServer;
import p001hx.okhttp.ApiClient;
import p001hx.platform.HotPatches;
import p001hx.platform.InstalledApp;
import p001hx.platform.Notices;
import p001hx.rewrite.DashInterceptor;
import p001hx.rewrite.HomeTabs;
import p001hx.source.MovieBoxSource;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
public final class AppPatch {
    private static final String CERTIFICATE_UNREADABLE = "Playback patch cannot read its certificate";
    private static final String INSTALL_FAILED = "Playback patch failed to load";
    private static final RetrySchedule INSTALL_RETRIES = new RetrySchedule(200, 50, 300, 1000);
    private static final String MANAGER_CLASS = "fh.c";
    private static final String SHARED_HEADERS_CLASS = "eh.b";
    private static final String SHARED_HEADERS_MISSING = "Playback patch cannot tag every request";

    private AppPatch() {
    }

    public static void install(ClassLoader classLoader) {
        new Installer(classLoader).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Class<?> resolve(String str, ClassLoader classLoader) {
        int i = 0;
        ClassLoader[] classLoaderArr = {classLoader, AppPatch.class.getClassLoader(), Thread.currentThread().getContextClassLoader()};
        while (true) {
            if (i >= 3) {
                return null;
            }
            ClassLoader classLoader2 = classLoaderArr[i];
            Class<?> loadOrNull = classLoader2 != null ? loadOrNull(str, classLoader2) : null;
            if (loadOrNull == null) {
                i++;
            } else {
                return loadOrNull;
            }
        }
    }

    private static Class<?> loadOrNull(String str, ClassLoader classLoader) {
        try {
            return Class.forName(str, false, classLoader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void installInterceptors(Class<?> cls, BuildTag buildTag, BuildTagInterceptor buildTagInterceptor) throws ReflectiveOperationException {
        AppNetwork locate = AppNetwork.locate(cls);
        Object withBuildTagInterceptor = withBuildTagInterceptor(locate.loader, locate.client, buildTagInterceptor);
        MovieBoxSource movieBoxSource = new MovieBoxSource(new ApiClient(locate.loader, withBuildTagInterceptor, locate.apiBase), new CloudFrontPlayInfoClient(buildTag));
        DashServer bindServer = bindServer(movieBoxSource);
        boolean z = (isMainProcess() && bindServer == null) ? false : true;
        if (bindServer != null) {
            try {
                bindServer.start();
            } catch (ReflectiveOperationException | RuntimeException e) {
                if (bindServer != null) {
                    bindServer.close();
                }
                throw e;
            }
        }
        locate.useClient(ClientInterceptors.addFirst(withBuildTagInterceptor, ClientInterceptors.proxy(locate.loader, new DashInterceptor(locate.loader, movieBoxSource, z, HomeTabs.readHidden()))));
        Log.i(Logs.TAG, "DASH interceptor installed");
    }

    private static Object withBuildTagInterceptor(ClassLoader classLoader, Object obj, BuildTagInterceptor buildTagInterceptor) throws ReflectiveOperationException {
        Object addBeforeTerminal = ClientInterceptors.addBeforeTerminal(obj, ClientInterceptors.proxy(classLoader, buildTagInterceptor));
        Log.i(Logs.TAG, "build tag interceptor installed");
        return addBeforeTerminal;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, String> sharedHeaders(ClassLoader classLoader) {
        try {
            Map<String, String> m0of = SharedHeaders.m0of(Class.forName(SHARED_HEADERS_CLASS, false, classLoader));
            Log.i(Logs.TAG, "shared request headers found");
            return m0of;
        } catch (ReflectiveOperationException | RuntimeException e) {
            Log.e(Logs.TAG, "shared request headers not found", e);
            Notices.showOnce(SHARED_HEADERS_MISSING);
            return new ConcurrentHashMap();
        }
    }

    private static DashServer bindServer(MovieBoxSource movieBoxSource) {
        if (!isMainProcess()) {
            return null;
        }
        try {
            return DashServer.bind(movieBoxSource);
        } catch (IOException e) {
            Log.e(Logs.TAG, "loopback server unavailable, resource lists keep their own links", e);
            return null;
        }
    }

    private static boolean isMainProcess() {
        return ProcessName.isMain(ProcessName.current());
    }

    private static final class Installer implements Runnable {
        private int attempts;
        private BuildTag buildTag;
        private Throwable failure;
        private boolean hotPatchesChecked;
        private boolean laboratoryUnlocked;
        private final Handler main = new Handler(Looper.getMainLooper());
        private final ClassLoader preferred;
        private BuildTagInterceptor tagger;

        Installer(ClassLoader classLoader) {
            this.preferred = classLoader;
        }

        void start() {
            this.main.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (tryInstall()) {
                return;
            }
            this.attempts++;
            if (AppPatch.INSTALL_RETRIES.reportsAfter(this.attempts)) {
                reportFailure();
            }
            long delayAfter = AppPatch.INSTALL_RETRIES.delayAfter(this.attempts);
            if (delayAfter == -1) {
                Log.e(Logs.TAG, "playback patch not installed after " + this.attempts + " attempts", this.failure);
            } else {
                this.main.postDelayed(this, delayAfter);
            }
        }

        private boolean tryInstall() {
            if (!this.hotPatchesChecked) {
                this.hotPatchesChecked = removeHotPatches();
            }
            Class resolve = AppPatch.resolve(AppPatch.MANAGER_CLASS, this.preferred);
            if (resolve == null) {
                return false;
            }
            if (!this.laboratoryUnlocked) {
                Laboratory.unlock(resolve.getClassLoader());
                this.laboratoryUnlocked = true;
            }
            if (this.tagger == null && !createTagger(resolve.getClassLoader())) {
                return false;
            }
            this.tagger.tagSharedHeaders();
            try {
                AppPatch.installInterceptors(resolve, this.buildTag, this.tagger);
                return true;
            } catch (Throwable th) {
                this.failure = th;
                return false;
            }
        }

        private boolean removeHotPatches() {
            try {
                Context context = InstalledApp.context();
                if (context == null) {
                    return false;
                }
                if (HotPatches.removeApplied(context)) {
                    Log.i(Logs.TAG, "applied hot patches removed");
                    return true;
                }
                return true;
            } catch (ReflectiveOperationException | RuntimeException e) {
                Log.w(Logs.TAG, "hot patches not checked: " + e);
                return false;
            }
        }

        private boolean createTagger(ClassLoader classLoader) {
            BuildTag fromInstalledApp = BuildTag.fromInstalledApp();
            if (fromInstalledApp == null) {
                return false;
            }
            this.buildTag = fromInstalledApp;
            this.tagger = new BuildTagInterceptor(fromInstalledApp, AppPatch.sharedHeaders(classLoader));
            return true;
        }

        private void reportFailure() {
            if (this.laboratoryUnlocked && this.tagger == null) {
                Log.e(Logs.TAG, "certificate unreadable, requests go out untagged");
                Notices.show(AppPatch.CERTIFICATE_UNREADABLE);
            } else {
                if (this.failure == null) {
                    Log.e(Logs.TAG, "network manager class not found");
                } else {
                    Log.e(Logs.TAG, "cannot install DASH interceptor", this.failure);
                }
                Notices.show(AppPatch.INSTALL_FAILED);
            }
        }
    }
}
