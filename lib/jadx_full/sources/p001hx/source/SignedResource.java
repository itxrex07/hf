package p001hx.source;

import android.net.Uri;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public final class SignedResource {
    private static final String EDGE_KEY = "Edge-Cache-Cookie=";
    private static final String POLICY_KEY = "CloudFront-Policy=";
    final String cookie;
    public final String manifestUrl;

    private SignedResource(String str, String str2) {
        this.cookie = str;
        this.manifestUrl = str2;
    }

    public static SignedResource fromCookie(String str) {
        SignedResource fromCloudFront = fromCloudFront(str);
        return fromCloudFront != null ? fromCloudFront : fromEdgeCache(str);
    }

    public static SignedResource fromPlayInfo(String str) {
        try {
            return firstSigned(new JSONObject(str).getJSONObject("data").getJSONArray("streams"));
        } catch (JSONException e) {
            return null;
        }
    }

    public static boolean isEdgeCachePlayInfo(String str) {
        SignedResource fromPlayInfo = fromPlayInfo(str);
        return fromPlayInfo != null && fromPlayInfo.isEdgeCache();
    }

    static String key(String str, int i, int i2) {
        return str + "/" + i + "/" + i2;
    }

    static String key(Uri uri) {
        String queryParameter = uri.getQueryParameter("subjectId");
        String queryParameter2 = uri.getQueryParameter("se");
        String queryParameter3 = uri.getQueryParameter("ep");
        if (queryParameter == null || queryParameter2 == null || queryParameter3 == null) {
            return null;
        }
        try {
            return key(queryParameter, Integer.parseInt(queryParameter2), Integer.parseInt(queryParameter3));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    boolean isEdgeCache() {
        return this.cookie.contains(EDGE_KEY);
    }

    private static SignedResource firstSigned(JSONArray jSONArray) throws JSONException {
        for (int i = 0; i < jSONArray.length(); i++) {
            SignedResource fromCookie = fromCookie(jSONArray.getJSONObject(i).optString("signCookie", ""));
            if (fromCookie != null) {
                return fromCookie;
            }
        }
        return null;
    }

    private static SignedResource fromCloudFront(String str) {
        String valueAfter = valueAfter(str, POLICY_KEY, ';');
        if (valueAfter == null) {
            return null;
        }
        try {
            String string = new JSONObject(new String(Base64.decode(valueAfter.replace('-', '+').replace('_', '=').replace('~', '/'), 0), StandardCharsets.UTF_8)).getJSONArray("Statement").getJSONObject(0).getString("Resource");
            if (string.startsWith("https://") && string.endsWith("/*")) {
                return new SignedResource(str, string.substring(0, string.length() - 2) + "/index.mpd");
            }
            return null;
        } catch (IllegalArgumentException | JSONException e) {
            return null;
        }
    }

    private static SignedResource fromEdgeCache(String str) {
        String valueAfter;
        String valueAfter2 = valueAfter(str, EDGE_KEY, ';');
        if (valueAfter2 == null || (valueAfter = valueAfter(valueAfter2, "urlprefix=", ':')) == null) {
            return null;
        }
        try {
            String str2 = new String(Base64.decode(valueAfter, 8), StandardCharsets.UTF_8);
            if (!str2.startsWith("https://")) {
                return null;
            }
            if (!str2.endsWith("/")) {
                str2 = str2 + "/";
            }
            return new SignedResource(str, str2 + "index.mpd");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String valueAfter(String str, String str2, char c) {
        int indexOf = str.indexOf(str2);
        if (indexOf < 0) {
            return null;
        }
        int length = indexOf + str2.length();
        int indexOf2 = str.indexOf(c, length);
        return indexOf2 < 0 ? str.substring(length) : str.substring(length, indexOf2);
    }
}
