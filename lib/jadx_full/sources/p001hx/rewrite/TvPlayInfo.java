package p001hx.rewrite;

import android.net.Uri;
import android.util.Log;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.gateway.BuildTag;
import p001hx.gateway.CloudFrontPlayInfoClient;
import p001hx.gateway.PlayInfoAcceptance;
import p001hx.gateway.PlayInfoLookup;
import p001hx.source.SignedResource;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
public final class TvPlayInfo {
    private static final int MOBILE_PLAYBACK_VERSION = 50020132;
    private static final String MOBILE_PLAY_INFO_PATH = "/wefeed-mobile-bff/subject-api/play-info/v2";
    private static final String PLAY_INFO_PATH = "/wefeed-tv-bff/subject/play-info/v2";

    private TvPlayInfo() {
    }

    public static boolean isPlayInfo(String str) {
        return PLAY_INFO_PATH.equals(Uri.parse(str).getPath());
    }

    public static byte[] rewrite(String str, byte[] bArr) {
        return rewrite(str, bArr, Client.INSTANCE);
    }

    static byte[] rewrite(String str, byte[] bArr, PlayInfoLookup playInfoLookup) {
        Uri parse = Uri.parse(str);
        String queryParameter = parse.getQueryParameter("subjectId");
        String queryParameter2 = parse.getQueryParameter("se");
        String queryParameter3 = parse.getQueryParameter("ep");
        if (!PLAY_INFO_PATH.equals(parse.getPath()) || queryParameter == null || queryParameter2 == null || queryParameter3 == null) {
            return bArr;
        }
        String fetchPlayInfo = playInfoLookup.fetchPlayInfo(new Uri.Builder().path(MOBILE_PLAY_INFO_PATH).appendQueryParameter("subjectId", queryParameter).appendQueryParameter("se", queryParameter2).appendQueryParameter("ep", queryParameter3).build().toString(), new PlayInfoAcceptance() { // from class: hx.rewrite.TvPlayInfo.0
            @Override // p001hx.gateway.PlayInfoAcceptance
            public final boolean accepts(String str2) {
                return TvPlayInfo.lambda$rewrite$0(str2);
            }
        });
        if (fetchPlayInfo == null) {
            return bArr;
        }
        try {
            JSONObject jSONObject = new JSONObject(new String(bArr, StandardCharsets.UTF_8));
            JSONObject jSONObject2 = jSONObject.getJSONObject("data");
            JSONArray jSONArray = new JSONObject(fetchPlayInfo).getJSONObject("data").getJSONArray("streams");
            JSONArray jSONArray2 = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject3 = jSONArray.getJSONObject(i);
                SignedResource fromCookie = SignedResource.fromCookie(jSONObject3.optString("signCookie"));
                if (fromCookie != null) {
                    jSONObject3.put("url", fromCookie.manifestUrl).put("format", "DASH");
                    jSONArray2.put(jSONObject3);
                }
            }
            if (jSONArray2.length() == 0) {
                return bArr;
            }
            jSONObject2.put("streams", jSONArray2).put("resources", new JSONArray());
            Log.i(Logs.TAG, "TV playback uses signed DASH streams");
            return jSONObject.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            Log.w(Logs.TAG, "TV playback response is invalid", e);
            return bArr;
        }
    }

    static /* synthetic */ boolean lambda$rewrite$0(String str) {
        return SignedResource.fromPlayInfo(str) != null;
    }

    private static final class Client {
        private static final PlayInfoLookup INSTANCE = new CloudFrontPlayInfoClient(BuildTag.fromInstalledApp(), TvPlayInfo.MOBILE_PLAYBACK_VERSION);

        private Client() {
        }
    }
}
