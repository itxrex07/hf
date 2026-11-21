package p001hx.rewrite;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.loopback.DashServer;
import p001hx.source.SignedResource;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class PlayInfoRewriter {
    private static final String[] URL_FIELDS = {"url", "resourceLink", "downloadUrl", "playUrl"};

    private PlayInfoRewriter() {
    }

    static String pointAtManifests(String str) {
        return rewrite(str, null, -1, -1);
    }

    static String rewrite(String str, String str2, int i, int i2) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            return routeProgressiveStreams(jSONObject, str2, i, i2) | JsonTree.rewriteObjects(jSONObject, new JsonObjectRewrite() { // from class: hx.rewrite.PlayInfoRewriter.0
                @Override // p001hx.rewrite.JsonObjectRewrite
                public final boolean apply(JSONObject jSONObject2) {
                    return PlayInfoRewriter.pointAtManifest(jSONObject2);
                }
            }) ? jSONObject.toString() : str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "play-info not rewritten: " + e.getMessage());
            return str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean pointAtManifest(JSONObject jSONObject) throws JSONException {
        SignedResource fromCookie = SignedResource.fromCookie(jSONObject.optString("signCookie", ""));
        if (fromCookie == null) {
            return false;
        }
        boolean z = false;
        for (String str : URL_FIELDS) {
            if (jSONObject.has(str)) {
                jSONObject.put(str, fromCookie.manifestUrl);
                z = true;
            }
        }
        if (z) {
            jSONObject.put("format", "DASH");
        }
        return z;
    }

    private static boolean routeProgressiveStreams(JSONObject jSONObject, String str, int i, int i2) throws JSONException {
        if (str == null || i < 0 || i2 < 0) {
            return false;
        }
        JSONObject optJSONObject = jSONObject.optJSONObject("data");
        JSONArray optJSONArray = optJSONObject != null ? optJSONObject.optJSONArray("streams") : null;
        if (optJSONArray == null) {
            return false;
        }
        boolean z = false;
        for (int i3 = 0; i3 < optJSONArray.length(); i3++) {
            JSONObject optJSONObject2 = optJSONArray.optJSONObject(i3);
            if (optJSONObject2 != null && routeThroughLoopback(optJSONObject2, str, i, i2)) {
                z = true;
            }
        }
        return z;
    }

    private static boolean routeThroughLoopback(JSONObject jSONObject, String str, int i, int i2) throws JSONException {
        if (!jSONObject.optString("signCookie", "").isEmpty()) {
            return false;
        }
        String optString = jSONObject.optString("url", "");
        int highestResolution = highestResolution(jSONObject.optString("resolutions", ""));
        if (optString.isEmpty() || highestResolution <= 0) {
            return false;
        }
        jSONObject.put("url", DashServer.buildUrl(str, i, i2, highestResolution, optString, jSONObject.optLong("size", 0L), null));
        return true;
    }

    private static int highestResolution(String str) {
        int i = 0;
        for (String str2 : str.split(",")) {
            i = Math.max(i, parseResolution(str2));
        }
        return i;
    }

    private static int parseResolution(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
