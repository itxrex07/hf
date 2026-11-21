package p001hx.rewrite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
final class JsonTree {
    private JsonTree() {
    }

    static boolean rewriteObjects(JSONObject jSONObject, JsonObjectRewrite jsonObjectRewrite) throws JSONException {
        boolean apply = jsonObjectRewrite.apply(jSONObject);
        Iterator<String> it = keysOf(jSONObject).iterator();
        while (it.hasNext()) {
            apply |= rewriteChild(jSONObject.opt(it.next()), jsonObjectRewrite);
        }
        return apply;
    }

    private static boolean rewriteArray(JSONArray jSONArray, JsonObjectRewrite jsonObjectRewrite) throws JSONException {
        boolean z = false;
        for (int i = 0; i < jSONArray.length(); i++) {
            z |= rewriteChild(jSONArray.opt(i), jsonObjectRewrite);
        }
        return z;
    }

    private static boolean rewriteChild(Object obj, JsonObjectRewrite jsonObjectRewrite) throws JSONException {
        if (obj instanceof JSONObject) {
            return rewriteObjects((JSONObject) obj, jsonObjectRewrite);
        }
        if (obj instanceof JSONArray) {
            return rewriteArray((JSONArray) obj, jsonObjectRewrite);
        }
        return false;
    }

    private static List<String> keysOf(JSONObject jSONObject) {
        ArrayList arrayList = new ArrayList();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            arrayList.add(keys.next());
        }
        return arrayList;
    }
}
