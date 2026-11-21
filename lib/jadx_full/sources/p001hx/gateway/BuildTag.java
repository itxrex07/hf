package p001hx.gateway;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.platform.InstalledApp;
import p001hx.util.Logs;
import p001hx.util.Streams;

/* loaded from: classes3.dex */
public final class BuildTag {
    private static final String CERTIFICATE_ASSET = "hx.cer";
    private static final String CERTIFICATE_RESOURCE = "assets/hx.cer";
    static final String HEADER = "X-Client-Build";
    private static final long MICROS_PER_MILLI = 1000;
    private static final long MILLIS_PER_SECOND = 1000;
    private static final int RANDOM_BOUND = 1000;
    private static final String SALT = "oneroom";
    private final String certificateSha256;
    private final MillisClock clock;
    private final Random random;

    private BuildTag(String str, MillisClock millisClock, Random random) {
        this.certificateSha256 = str;
        this.clock = millisClock;
        this.random = random;
    }

    public static BuildTag fromInstalledApp() {
        byte[] readCertificate = readCertificate(BuildTag.class.getClassLoader());
        if (readCertificate != null) {
            return fromCertificate(readCertificate);
        }
        return null;
    }

    static byte[] readCertificate(ClassLoader classLoader) {
        byte[] readPackagedCertificate = readPackagedCertificate(classLoader);
        return readPackagedCertificate != null ? readPackagedCertificate : readCertificateAsset();
    }

    private static byte[] readPackagedCertificate(ClassLoader classLoader) {
        byte[] readAll;
        try {
            InputStream resourceAsStream = classLoader.getResourceAsStream(CERTIFICATE_RESOURCE);
            if (resourceAsStream != null) {
                try {
                    readAll = Streams.readAll(resourceAsStream);
                } finally {
                }
            } else {
                readAll = null;
            }
            if (resourceAsStream != null) {
                resourceAsStream.close();
            }
            return readAll;
        } catch (IOException e) {
            Log.w(Logs.TAG, "cannot read assets/hx.cer from the class path", e);
            return null;
        }
    }

    private static byte[] readCertificateAsset() {
        try {
            InputStream open = InstalledApp.context().getAssets().open(CERTIFICATE_ASSET);
            try {
                byte[] readAll = Streams.readAll(open);
                if (open != null) {
                    open.close();
                }
                return readAll;
            } finally {
            }
        } catch (Exception e) {
            Log.e(Logs.TAG, "cannot read hx.cer", e);
            return null;
        }
    }

    static BuildTag fromCertificate(byte[] bArr) {
        return fromCertificate(bArr, new MillisClock() { // from class: hx.gateway.BuildTag.0
            @Override // p001hx.gateway.MillisClock
            public final long nowMillis() {
                return System.currentTimeMillis();
            }
        }, new Random());
    }

    static BuildTag fromCertificate(byte[] bArr, MillisClock millisClock, Random random) {
        try {
            return new BuildTag(Digests.sha256Hex(bArr), millisClock, random);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    static String addTo(String str, String str2) {
        try {
            return new JSONObject(str).put(HEADER, str2).toString();
        } catch (JSONException e) {
            Log.w(Logs.TAG, "client info not tagged: " + e.getMessage());
            return str;
        }
    }

    String next() {
        long nowMillis = this.clock.nowMillis();
        String format = String.format(Locale.ROOT, "%d%06d%d", Long.valueOf(nowMillis / 1000), Long.valueOf((nowMillis % 1000) * 1000), Integer.valueOf(this.random.nextInt(RANDOM_BOUND)));
        try {
            return format + "." + Digests.md5Hex((this.certificateSha256 + SALT + format).getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
