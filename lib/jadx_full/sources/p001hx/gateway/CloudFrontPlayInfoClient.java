package p001hx.gateway;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.platform.InstalledApp;
import p001hx.util.Logs;
import p001hx.util.Streams;

/* loaded from: classes3.dex */
public final class CloudFrontPlayInfoClient implements PlayInfoLookup {
    private static final String[] API_HOSTS = {"api6.aoneroom.com", "api4sg.aoneroom.com", "api5.aoneroom.com", "api4.aoneroom.com", "api3.aoneroom.com"};
    private static final long LOOKUP_BUDGET_MS = 5000;
    private static final int REQUEST_TIMEOUT_MS = 5000;
    private static final long RETRY_COOLDOWN_MS = 600000;
    private static final String VISITOR_LOGIN = "/wefeed-mobile-bff/user-api/visitor-login";
    private final GatewayHeaders gatewayHeaders;
    private final ConcurrentMap<String, String> tokens = new ConcurrentHashMap();
    private final Map<String, Long> hostRetryAtMs = new ConcurrentHashMap();

    public CloudFrontPlayInfoClient(BuildTag buildTag) {
        this.gatewayHeaders = new GatewayHeaders(ClientInfo.forInstalledApp(), buildTag);
    }

    public CloudFrontPlayInfoClient(BuildTag buildTag, int i) {
        try {
            this.gatewayHeaders = new GatewayHeaders(new JSONObject(ClientInfo.forInstalledApp()).put("version_code", i).toString(), buildTag);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // p001hx.gateway.PlayInfoLookup
    public String fetchPlayInfo(String str, PlayInfoAcceptance playInfoAcceptance) {
        if (SigningKey.VALUE == null) {
            return null;
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        List<String> hostsReadyAt = hostsReadyAt(elapsedRealtime);
        if (hostsReadyAt.isEmpty()) {
            Log.i(Logs.TAG, "play-info: every API host is cooling down, staying on Edge-Cache");
            return null;
        }
        BlockingQueue<Answer> startLookups = startLookups(hostsReadyAt, str, playInfoAcceptance, elapsedRealtime);
        HashSet hashSet = new HashSet(hostsReadyAt);
        String awaitAccepted = awaitAccepted(startLookups, hashSet, elapsedRealtime + LOOKUP_BUDGET_MS);
        if (awaitAccepted != null) {
            return awaitAccepted;
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            coolDown((String) it.next());
        }
        Log.i(Logs.TAG, "play-info: no unpaced CDN from " + hostsReadyAt + " within " + LOOKUP_BUDGET_MS + " ms, staying on Edge-Cache");
        return null;
    }

    private List<String> hostsReadyAt(long j) {
        ArrayList arrayList = new ArrayList();
        for (String str : API_HOSTS) {
            Long l = this.hostRetryAtMs.get(str);
            if (l == null || j >= l.longValue()) {
                arrayList.add(str);
            }
        }
        return arrayList;
    }

    private BlockingQueue<Answer> startLookups(List<String> list, String str, PlayInfoAcceptance playInfoAcceptance, final long j) {
        final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
        for (final String str2 : list) {
            final String str3 = str;
            final PlayInfoAcceptance playInfoAcceptance2 = playInfoAcceptance;
            Thread thread = new Thread(new Runnable() { // from class: hx.gateway.CloudFrontPlayInfoClient.0
                @Override // java.lang.Runnable
                public final void run() {
                    CloudFrontPlayInfoClient.this.m22lambda$startLookups$0$hxgatewayCloudFrontPlayInfoClient(linkedBlockingQueue, str2, str3, playInfoAcceptance2, j);
                }
            }, "hx-play-info-" + str2);
            thread.setDaemon(true);
            thread.start();
            str = str3;
            playInfoAcceptance = playInfoAcceptance2;
        }
        return linkedBlockingQueue;
    }

    /* renamed from: lambda$startLookups$0$hx-gateway-CloudFrontPlayInfoClient, reason: not valid java name */
    /* synthetic */ void m22lambda$startLookups$0$hxgatewayCloudFrontPlayInfoClient(BlockingQueue blockingQueue, String str, String str2, PlayInfoAcceptance playInfoAcceptance, long j) {
        blockingQueue.add(new Answer(str, fetchAccepted(str, str2, playInfoAcceptance, j)));
    }

    private static String awaitAccepted(BlockingQueue<Answer> blockingQueue, Set<String> set, long j) {
        while (!set.isEmpty()) {
            try {
                long elapsedRealtime = j - SystemClock.elapsedRealtime();
                Answer poll = elapsedRealtime > 0 ? blockingQueue.poll(elapsedRealtime, TimeUnit.MILLISECONDS) : null;
                if (poll == null) {
                    return null;
                }
                set.remove(poll.host);
                if (poll.body != null) {
                    return poll.body;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return null;
    }

    private void coolDown(String str) {
        this.hostRetryAtMs.put(str, Long.valueOf(SystemClock.elapsedRealtime() + RETRY_COOLDOWN_MS));
    }

    private String fetchAccepted(String str, String str2, PlayInfoAcceptance playInfoAcceptance, long j) {
        Throwable th;
        String str3;
        try {
            str3 = visitorToken(str);
            try {
                String sendRequest = sendRequest("GET", str, str2, null, str3);
                long elapsedRealtime = SystemClock.elapsedRealtime() - j;
                if (playInfoAcceptance.accepts(sendRequest)) {
                    if (elapsedRealtime <= LOOKUP_BUDGET_MS) {
                        this.hostRetryAtMs.remove(str);
                    }
                    Log.i(Logs.TAG, "play-info from " + str + ": unpaced CDN instead of Edge-Cache, " + elapsedRealtime + " ms");
                    return sendRequest;
                }
                Log.i(Logs.TAG, "play-info from " + str + " not used, " + elapsedRealtime + " ms");
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (str3 != null) {
                    this.tokens.remove(str, str3);
                }
                Log.w(Logs.TAG, "play-info from " + str + " failed after " + (SystemClock.elapsedRealtime() - j) + " ms", th);
                coolDown(str);
                return null;
            }
        } catch (Throwable th3) {
            th = th3;
            str3 = null;
        }
    }

    private String visitorToken(String str) throws IOException, JSONException {
        String str2 = this.tokens.get(str);
        if (str2 != null) {
            return str2;
        }
        String string = new JSONObject(sendRequest("POST", str, VISITOR_LOGIN, "{}", null)).getJSONObject("data").getString("token");
        this.tokens.put(str, string);
        return string;
    }

    private String sendRequest(String str, String str2, String str3, String str4, String str5) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL("https://" + str2 + str3).openConnection();
        try {
            httpURLConnection.setConnectTimeout(REQUEST_TIMEOUT_MS);
            httpURLConnection.setReadTimeout(REQUEST_TIMEOUT_MS);
            httpURLConnection.setRequestMethod(str);
            addHeaders(httpURLConnection, str, str3, str4, str5);
            if (str4 != null) {
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                try {
                    outputStream.write(str4.getBytes(StandardCharsets.UTF_8));
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } finally {
                }
            }
            return readSuccessful(httpURLConnection, str2);
        } finally {
            httpURLConnection.disconnect();
        }
    }

    private void addHeaders(HttpURLConnection httpURLConnection, String str, String str2, String str3, String str4) throws IOException {
        for (Map.Entry<String, String> entry : this.gatewayHeaders.create(SigningKey.VALUE, str, str2, str3, str4).entrySet()) {
            httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static String readSuccessful(HttpURLConnection httpURLConnection, String str) throws IOException {
        int responseCode = httpURLConnection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new IOException("HTTP " + responseCode + " from " + str);
        }
        InputStream inputStream = httpURLConnection.getInputStream();
        try {
            String str2 = new String(Streams.readAll(inputStream), StandardCharsets.UTF_8);
            if (inputStream != null) {
                inputStream.close();
            }
            return str2;
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    private static final class Answer {
        final String body;
        final String host;

        Answer(String str, String str2) {
            this.host = str;
            this.body = str2;
        }
    }

    private static final class SigningKey {
        private static final String META_DATA_KEY = "gateway_secret_online";
        static final byte[] VALUE = read();

        private SigningKey() {
        }

        private static byte[] read() {
            String string;
            try {
                Context context = InstalledApp.context();
                Bundle bundle = context.getPackageManager().getApplicationInfo(context.getPackageName(), 128).metaData;
                string = bundle != null ? bundle.getString(META_DATA_KEY) : null;
            } catch (Exception e) {
                Log.w(Logs.TAG, "gateway_secret_online unreadable, staying on Edge-Cache", e);
            }
            if (string == null) {
                Log.w(Logs.TAG, "gateway_secret_online missing from the manifest, staying on Edge-Cache");
                return null;
            }
            return Base64.decode(string, 0);
        }
    }
}
