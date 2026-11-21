package p001hx.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/* loaded from: classes3.dex */
public final class RemoteFile {
    private static final int PROBE_TIMEOUT_MS = 10000;

    private RemoteFile() {
    }

    public static long probeLength(String str) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
        httpURLConnection.setConnectTimeout(PROBE_TIMEOUT_MS);
        httpURLConnection.setReadTimeout(PROBE_TIMEOUT_MS);
        httpURLConnection.setRequestProperty("Range", "bytes=0-0");
        try {
            return lengthOf(httpURLConnection);
        } finally {
            httpURLConnection.disconnect();
        }
    }

    public static long totalFromContentRange(String str) {
        return Long.parseLong(str.substring(str.indexOf(47) + 1).trim());
    }

    private static long contentLength(HttpURLConnection httpURLConnection) {
        String headerField = httpURLConnection.getHeaderField("Content-Length");
        if (headerField != null) {
            try {
                return Long.parseLong(headerField.trim());
            } catch (NumberFormatException e) {
                return -1L;
            }
        }
        return -1L;
    }

    private static long lengthOf(HttpURLConnection httpURLConnection) throws IOException {
        int responseCode = httpURLConnection.getResponseCode();
        String headerField = httpURLConnection.getHeaderField("Content-Range");
        if (responseCode == 206 && headerField != null && headerField.indexOf(47) >= 0) {
            return totalFromContentRange(headerField);
        }
        if (responseCode == 200) {
            return contentLength(httpURLConnection);
        }
        throw new IOException("HTTP " + responseCode);
    }
}
