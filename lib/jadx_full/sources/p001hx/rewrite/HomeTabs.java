package p001hx.rewrite;

import android.util.Log;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.platform.InstalledApp;
import p001hx.util.Logs;
import p001hx.util.Streams;

/* loaded from: classes3.dex */
public final class HomeTabs {
    private static final String HIDDEN_TABS_ASSET = "hx_hidden_home_tabs";

    private HomeTabs() {
    }

    public static Set<String> readHidden() {
        HashSet hashSet = new HashSet();
        try {
            InputStream open = InstalledApp.context().getAssets().open(HIDDEN_TABS_ASSET);
            try {
                for (String str : new String(Streams.readAll(open), StandardCharsets.UTF_8).split("\n")) {
                    if (!str.trim().isEmpty()) {
                        hashSet.add(str.trim());
                    }
                }
                if (open != null) {
                    open.close();
                }
            } catch (Throwable th) {
                if (open != null) {
                    try {
                        open.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException e) {
            return hashSet;
        } catch (Exception e2) {
            Log.e(Logs.TAG, "cannot read hx_hidden_home_tabs", e2);
        }
        Log.i(Logs.TAG, "hidden home tabs: " + hashSet);
        return hashSet;
    }

    static String withoutTabs(String str, Set<String> set) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONObject optJSONObject = jSONObject.optJSONObject("data");
            JSONArray optJSONArray = optJSONObject != null ? optJSONObject.optJSONArray("bottomTabs") : null;
            if (optJSONArray == null) {
                return str;
            }
            int i = 0;
            for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                i += removeSubTabs(optJSONArray.optJSONObject(i2), set);
            }
            return i > 0 ? jSONObject.toString() : str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "app tabs not rewritten: " + e.getMessage());
            return str;
        }
    }

    private static int removeSubTabs(JSONObject jSONObject, Set<String> set) {
        JSONArray optJSONArray = jSONObject != null ? jSONObject.optJSONArray("subTabs") : null;
        int i = 0;
        if (optJSONArray == null) {
            return 0;
        }
        for (int length = optJSONArray.length() - 1; length >= 0; length--) {
            JSONObject optJSONObject = optJSONArray.optJSONObject(length);
            if (optJSONObject != null && set.contains(optJSONObject.optString("tabCode"))) {
                optJSONArray.remove(length);
                i++;
            }
        }
        return i;
    }
}
