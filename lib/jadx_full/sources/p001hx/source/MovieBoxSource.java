package p001hx.source;

import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import p001hx.dash.CdnCookies;
import p001hx.dash.DashFile;
import p001hx.gateway.PlayInfoAcceptance;
import p001hx.gateway.PlayInfoLookup;
import p001hx.loopback.DashFileSource;
import p001hx.loopback.DashServer;
import p001hx.okhttp.ApiClient;
import p001hx.platform.Notices;
import p001hx.util.Logs;
import p001hx.util.RemoteFile;
import p001hx.util.Tasks;

/* loaded from: classes3.dex */
public final class MovieBoxSource implements DashFileSource {
    private static final int FILE_RETENTION = 8;
    private static final String NO_STREAM = "No stream for this title";
    private static final int PLAY_INFO_ATTEMPTS = 5;
    private static final long PLAY_INFO_RETRY_DELAY_MS = 2000;
    private static final int RESOURCE_RETENTION = 32;
    private final ApiClient api;
    private final DirectFiles directFiles;
    private final PlayInfoLookup replacementLookup;
    private final Map<String, SignedResource> resources = new LruMap(RESOURCE_RETENTION);
    private final Map<String, Long> originLengths = new LruMap(RESOURCE_RETENTION);
    private final Map<String, Boolean> handedOff = new LruMap(RESOURCE_RETENTION);
    private final Map<String, FutureTask<DashFile>> files = new LruMap(8);

    public MovieBoxSource(ApiClient apiClient, PlayInfoLookup playInfoLookup) {
        this.api = apiClient;
        this.directFiles = new DirectFiles(apiClient);
        this.replacementLookup = playInfoLookup;
    }

    @Override // p001hx.loopback.DashFileSource
    public DashFile open(String str, int i, int i2, int i3, String str2, long j, String str3, boolean z) throws IOException {
        String str4 = SignedResource.key(str, i, i2) + "@" + i3;
        String knownUrl = this.directFiles.knownUrl(str3);
        if (knownUrl != null) {
            throw new DashServer.RedirectException(knownUrl);
        }
        try {
            SignedResource resource = resource(str, i, i2, null);
            if (matchesOrigin(str4, str2, j)) {
                return null;
            }
            if (isHandedOff(str4, str, resource, str3, str2, z)) {
                throw new DashServer.UnavailableException(str4 + ": DASH on " + Uri.parse(resource.manifestUrl).getHost() + ", leaving the download to the app's direct file");
            }
            return getOrCreateFile(str, i, i2, i3);
        } catch (NoSignedResourceException e) {
            if (str2 == null) {
                throw e;
            }
            redirectToDirectFile(str4, str, str3, str2);
            if (matchesOrigin(str4, str2, j)) {
                return null;
            }
            Notices.showOnce(NO_STREAM);
            throw new DashServer.UnavailableException(str4 + ": no signed DASH resource and no direct file");
        }
    }

    public SignedResource observePlayInfo(String str, String str2, String str3) {
        String key = SignedResource.key(Uri.parse(str));
        SignedResource fromPlayInfo = SignedResource.fromPlayInfo(str2);
        if (fromPlayInfo == null) {
            Log.w(Logs.TAG, "play-info " + key + ": no signed DASH resource");
            return null;
        }
        Log.i(Logs.TAG, "play-info " + key + ": DASH on " + Uri.parse(fromPlayInfo.manifestUrl).getHost() + (str3 != null ? ", app API host " + str3 : ""));
        if (key == null) {
            return null;
        }
        synchronized (this.resources) {
            this.resources.put(key, fromPlayInfo);
        }
        return fromPlayInfo;
    }

    public String fetchReplacementPlayInfo(String str) {
        return this.replacementLookup.fetchPlayInfo(str, new PlayInfoAcceptance() { // from class: hx.source.MovieBoxSource.1
            @Override // p001hx.gateway.PlayInfoAcceptance
            public final boolean accepts(String str2) {
                return MovieBoxSource.isUnpacedPlayInfo(str2);
            }
        });
    }

    public String directFilePlayInfo(String str, String str2, int i, int i2) {
        return this.directFiles.playInfoFor(str, str2, i, i2);
    }

    public String withDirectFileSizes(String str) {
        return this.directFiles.withSizes(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isUnpacedPlayInfo(String str) {
        SignedResource fromPlayInfo = SignedResource.fromPlayInfo(str);
        if (fromPlayInfo != null && DashFile.isUnpacedCdn(fromPlayInfo.manifestUrl)) {
            return true;
        }
        Log.i(Logs.TAG, "alternative play-info " + (fromPlayInfo != null ? "is on " + Uri.parse(fromPlayInfo.manifestUrl).getHost() : "has no signed DASH resource"));
        return false;
    }

    private boolean matchesOrigin(String str, String str2, long j) {
        if (str2 == null || str2.contains("aoneroom.com/other/") || j <= 0 || originLength(str2) != j) {
            return false;
        }
        Log.i(Logs.TAG, str + ": redirect to origin, size " + j + " matches");
        return true;
    }

    private void redirectToDirectFile(String str, String str2, String str3, String str4) throws DashServer.RedirectException {
        String resolveUrl = this.directFiles.resolveUrl(str2, str3, str4);
        if (resolveUrl != null) {
            Log.i(Logs.TAG, str + ": redirect to direct file on " + Uri.parse(resolveUrl).getHost());
            throw new DashServer.RedirectException(resolveUrl);
        }
    }

    private boolean isHandedOff(String str, String str2, SignedResource signedResource, String str3, String str4, boolean z) {
        synchronized (this.handedOff) {
            Boolean bool = this.handedOff.get(str);
            if (bool != null) {
                return bool.booleanValue();
            }
            boolean z2 = (!z || DashFile.isUnpacedCdn(signedResource.manifestUrl) || this.directFiles.resolveUrl(str2, str3, str4) == null) ? false : true;
            synchronized (this.handedOff) {
                Boolean bool2 = this.handedOff.get(str);
                if (bool2 != null) {
                    return bool2.booleanValue();
                }
                this.handedOff.put(str, Boolean.valueOf(z2));
                return z2;
            }
        }
    }

    private long originLength(String str) {
        synchronized (this.originLengths) {
            Long l = this.originLengths.get(str);
            if (l != null) {
                return l.longValue();
            }
            try {
                long probeLength = RemoteFile.probeLength(str);
                Log.i(Logs.TAG, "origin " + str + ": " + probeLength + " bytes");
                synchronized (this.originLengths) {
                    this.originLengths.put(str, Long.valueOf(probeLength));
                }
                return probeLength;
            } catch (IOException | NumberFormatException e) {
                Log.w(Logs.TAG, "origin " + str + " not probed", e);
                return -1L;
            }
        }
    }

    private DashFile getOrCreateFile(final String str, final int i, final int i2, final int i3) throws IOException {
        FutureTask<DashFile> futureTask;
        boolean z;
        final String str2 = SignedResource.key(str, i, i2) + "/" + i3;
        synchronized (this.files) {
            futureTask = this.files.get(str2);
            if (futureTask != null) {
                z = false;
            } else {
                futureTask = new FutureTask<>(new Callable() { // from class: hx.source.MovieBoxSource.0
                    @Override // java.util.concurrent.Callable
                    public final Object call() {
                        return MovieBoxSource.this.m27lambda$getOrCreateFile$0$hxsourceMovieBoxSource(str, i, i2, i3, str2);
                    }
                });
                this.files.put(str2, futureTask);
                z = true;
            }
        }
        if (z) {
            futureTask.run();
        }
        return awaitFile(str2, futureTask);
    }

    private DashFile awaitFile(String str, FutureTask<DashFile> futureTask) throws IOException {
        try {
            return futureTask.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        } catch (ExecutionException e2) {
            synchronized (this.files) {
                if (this.files.get(str) == futureTask) {
                    this.files.remove(str);
                }
                throw Tasks.failureOf(e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: openFile, reason: merged with bridge method [inline-methods] */
    public DashFile m27lambda$getOrCreateFile$0$hxsourceMovieBoxSource(String str, int i, int i2, int i3, String str2) throws IOException {
        SignedResource resource = resource(str, i, i2, null);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        DashFile open = DashFile.open(resource.manifestUrl, i3, new PinnedCookies(resource, str, i, i2));
        Log.i(Logs.TAG, "DASH file " + str2 + ": " + open.length + " bytes from " + Uri.parse(resource.manifestUrl).getHost() + ", ready in " + (SystemClock.elapsedRealtime() - elapsedRealtime) + " ms, " + open.readPattern());
        return open;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SignedResource resource(String str, int i, int i2, String str2) throws IOException {
        SignedResource signedResource;
        String fetchReplacementPlayInfo;
        String key = SignedResource.key(str, i, i2);
        synchronized (this.resources) {
            signedResource = this.resources.get(key);
        }
        if (signedResource != null && !signedResource.cookie.equals(str2)) {
            return signedResource;
        }
        String str3 = "wefeed-mobile-bff/subject-api/play-info/v2?subjectId=" + str + "&se=" + i + "&ep=" + i2 + "&isVip=true";
        String url = this.api.url(str3);
        String playInfoWithRetry = getPlayInfoWithRetry(url, key);
        if (SignedResource.isEdgeCachePlayInfo(playInfoWithRetry) && (fetchReplacementPlayInfo = fetchReplacementPlayInfo("/" + str3)) != null) {
            playInfoWithRetry = fetchReplacementPlayInfo;
        }
        SignedResource observePlayInfo = observePlayInfo(url, playInfoWithRetry, null);
        if (observePlayInfo == null) {
            throw new NoSignedResourceException("play-info has no signed DASH resource for " + key);
        }
        return observePlayInfo;
    }

    private String getPlayInfoWithRetry(String str, String str2) throws IOException {
        int i = 1;
        while (true) {
            try {
                return this.api.get(str);
            } catch (IOException e) {
                if (i != PLAY_INFO_ATTEMPTS) {
                    Log.w(Logs.TAG, "play-info " + str2 + " attempt " + i + " of " + PLAY_INFO_ATTEMPTS + " failed", e);
                    SystemClock.sleep(PLAY_INFO_RETRY_DELAY_MS);
                    i++;
                } else {
                    throw e;
                }
            }
        }
    }

    private static final class NoSignedResourceException extends IOException {
        private static final long serialVersionUID = 1;

        NoSignedResourceException(String str) {
            super(str);
        }
    }

    private final class PinnedCookies implements CdnCookies {
        private String cookie;
        private final int episode;
        private final String manifestUrl;
        private final int season;
        private final String subjectId;

        PinnedCookies(SignedResource signedResource, String str, int i, int i2) {
            this.manifestUrl = signedResource.manifestUrl;
            this.cookie = signedResource.cookie;
            this.subjectId = str;
            this.season = i;
            this.episode = i2;
        }

        @Override // p001hx.dash.CdnCookies
        public synchronized String current() {
            return this.cookie;
        }

        @Override // p001hx.dash.CdnCookies
        public synchronized String refresh(String str) throws IOException {
            if (this.cookie.equals(str)) {
                SignedResource resource = MovieBoxSource.this.resource(this.subjectId, this.season, this.episode, str);
                if (!resource.manifestUrl.equals(this.manifestUrl)) {
                    throw new IOException("signed DASH resource moved to " + Uri.parse(resource.manifestUrl).getHost());
                }
                this.cookie = resource.cookie;
                return this.cookie;
            }
            return this.cookie;
        }
    }
}
