package p001hx.dash;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import p001hx.util.Logs;
import p001hx.util.Streams;

/* loaded from: classes3.dex */
final class CdnClient {
    private static final int ATTEMPTS = 5;
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 30000;
    private static final long RETRY_DELAY_MS = 2000;

    private CdnClient() {
    }

    static <T> T withRetries(CdnRequest<T> cdnRequest) throws IOException {
        int i = 1;
        while (true) {
            try {
                return cdnRequest.send();
            } catch (IOException e) {
                if (i == ATTEMPTS || isCancelled(e)) {
                    throw e;
                }
                TransferLog.SHARED.retry();
                Log.w(Logs.TAG, "CDN read attempt " + i + " of " + ATTEMPTS + " failed", e);
                pauseBeforeRetry();
                i++;
            }
        }
        throw e;
    }

    static byte[] fetch(String str, CdnCookies cdnCookies) throws IOException {
        InputStream inputStream = connect(str, null, cdnCookies, null).getInputStream();
        try {
            byte[] readAll = Streams.readAll(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            return readAll;
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

    static HttpURLConnection connect(String str, String str2, CdnCookies cdnCookies, ReadGroup readGroup) throws IOException {
        String current = cdnCookies.current();
        HttpURLConnection request = request(str, str2, current, readGroup);
        if (request.getResponseCode() == 403) {
            request.disconnect();
            if (readGroup != null) {
                readGroup.remove(request);
            }
            request = request(str, str2, cdnCookies.refresh(current), readGroup);
        }
        int responseCode = request.getResponseCode();
        if (responseCode != 200 && responseCode != 206) {
            request.disconnect();
            throw new IOException("HTTP " + responseCode + " for " + str);
        }
        return request;
    }

    private static boolean isCancelled(IOException iOException) {
        return (iOException instanceof InterruptedIOException) && !(iOException instanceof SocketTimeoutException);
    }

    private static void pauseBeforeRetry() throws IOException {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    private static HttpURLConnection request(String str, String str2, String str3, ReadGroup readGroup) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        httpURLConnection.setReadTimeout(READ_TIMEOUT_MS);
        httpURLConnection.setRequestProperty("Cookie", str3);
        if (str2 != null) {
            httpURLConnection.setRequestProperty("Range", str2);
        }
        if (readGroup != null) {
            readGroup.add(httpURLConnection);
        }
        return httpURLConnection;
    }
}
