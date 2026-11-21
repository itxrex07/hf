package p001hx.rewrite;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.loopback.DashServer;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class ResourceListRewriter {
    private ResourceListRewriter() {
    }

    static String rewrite(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject optJSONObject = jSONObject.optJSONObject("data");
            String optString = optJSONObject != null ? optJSONObject.optString("subjectId", "") : "";
            JSONArray optJSONArray = optJSONObject != null ? optJSONObject.optJSONArray("list") : null;
            if (!optString.isEmpty() && optJSONArray != null) {
                int i = 0;
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    JSONObject optJSONObject2 = optJSONArray.optJSONObject(i2);
                    if (optJSONObject2 != null && route(optString, optJSONObject2)) {
                        i++;
                    }
                }
                Log.i(Logs.TAG, "resource list " + optString + ": routed " + i + " of " + optJSONArray.length() + " items");
                return i > 0 ? jSONObject.toString() : str;
            }
            return str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "resource list not rewritten: " + e.getMessage());
            return str;
        }
    }

    private static boolean route(String str, JSONObject jSONObject) throws JSONException {
        if (!jSONObject.has("resourceLink")) {
            return false;
        }
        try {
            int i = jSONObject.getInt("se");
            int i2 = jSONObject.getInt("ep");
            int i3 = jSONObject.getInt("resolution");
            String string = jSONObject.getString("resourceLink");
            if (i < 0 || i2 < 0 || i3 <= 0) {
                Log.w(Logs.TAG, "resource item " + jSONObject.optString("resourceId") + " kept as is: se=" + i + " ep=" + i2 + " resolution=" + i3);
                return false;
            }
            String optString = jSONObject.optString("resourceId", "");
            long optLong = jSONObject.optLong("size", 0L);
            if (optString.isEmpty()) {
                optString = null;
            }
            jSONObject.put("resourceLink", DashServer.buildUrl(str, i, i2, i3, string, optLong, optString));
            return true;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "resource item " + jSONObject.optString("resourceId") + " not routed: " + e.getMessage());
            return false;
        }
    }
}
