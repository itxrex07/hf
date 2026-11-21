package p001hx.rewrite;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class PromotionRewriter {
    private static final String BANNER_SECTION = "BANNER";
    private static final String CUSTOM_SECTION = "CUSTOM";
    private static final String[] PROMOTION_TARGETS = {"/member", "/web", "/profile"};
    private static final String SINGLE_IMAGE_SECTION = "SINGLE_IMAGE";
    private static final String TARGET_PARAMETER = "type";

    private PromotionRewriter() {
    }

    static String withoutFeedPromotions(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONObject("data").getJSONArray("items");
            boolean z = false;
            for (int length = jSONArray.length() - 1; length >= 0; length--) {
                z |= removePromotions(jSONArray, length);
            }
            return z ? jSONObject.toString() : str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "home feed not rewritten: " + e.getMessage());
            return str;
        }
    }

    static String withoutSkuPromotion(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.isNull("data")) {
                jSONObject.put("data", JSONObject.NULL);
                return jSONObject.toString();
            }
            return str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "sku promotion not rewritten: " + e.getMessage());
            return str;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static boolean removePromotions(JSONArray jSONArray, int i) throws JSONException {
        char c;
        JSONObject jSONObject = jSONArray.getJSONObject(i);
        String optString = jSONObject.optString(TARGET_PARAMETER);
        switch (optString.hashCode()) {
            case 1079457412:
                if (optString.equals(SINGLE_IMAGE_SECTION)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1951953708:
                if (optString.equals(BANNER_SECTION)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1999208305:
                if (optString.equals(CUSTOM_SECTION)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return removePromotionEntries(jSONArray, i, jSONObject.getJSONObject("banner").getJSONArray("banners"));
            case 1:
                return removePromotionEntries(jSONArray, i, jSONObject.getJSONObject("customData").getJSONArray("items"));
            case 2:
                return removePromotionImage(jSONArray, i, jSONObject.getJSONObject("page"));
            default:
                return false;
        }
    }

    private static boolean removePromotionEntries(JSONArray jSONArray, int i, JSONArray jSONArray2) throws JSONException {
        boolean z = false;
        for (int length = jSONArray2.length() - 1; length >= 0; length--) {
            if (isPromotion(jSONArray2.getJSONObject(length))) {
                jSONArray2.remove(length);
                z = true;
            }
        }
        if (z && jSONArray2.length() == 0) {
            jSONArray.remove(i);
        }
        return z;
    }

    private static boolean removePromotionImage(JSONArray jSONArray, int i, JSONObject jSONObject) {
        boolean isPromotion = isPromotion(jSONObject);
        if (isPromotion) {
            jSONArray.remove(i);
        }
        return isPromotion;
    }

    private static boolean isPromotion(JSONObject jSONObject) {
        Uri parse = Uri.parse(jSONObject.optString("deepLink"));
        return isWebLink(parse) || (parse.isHierarchical() && isPromotionTarget(parse));
    }

    private static boolean isWebLink(Uri uri) {
        return "http".equals(uri.getScheme()) || "https".equals(uri.getScheme());
    }

    private static boolean isPromotionTarget(Uri uri) {
        String queryParameter = uri.getQueryParameter(TARGET_PARAMETER);
        if (queryParameter == null) {
            return false;
        }
        for (String str : PROMOTION_TARGETS) {
            if (startsAtBoundary(queryParameter, str)) {
                return true;
            }
        }
        return false;
    }

    private static boolean startsAtBoundary(String str, String str2) {
        int length = str2.length();
        return str.startsWith(str2) && (str.length() == length || str.charAt(length) == '/' || str.charAt(length) == '?');
    }
}
