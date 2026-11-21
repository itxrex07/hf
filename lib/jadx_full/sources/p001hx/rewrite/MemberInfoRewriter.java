package p001hx.rewrite;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class MemberInfoRewriter {
    private static final int DAYS_LEFT = 9999;
    private static final String EXPIRY_DATE = "2099-12-31";

    private MemberInfoRewriter() {
    }

    static String rewrite(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            return JsonTree.rewriteObjects(jSONObject, new JsonObjectRewrite() { // from class: hx.rewrite.MemberInfoRewriter.0
                @Override // p001hx.rewrite.JsonObjectRewrite
                public final boolean apply(JSONObject jSONObject2) {
                    return MemberInfoRewriter.extendMembership(jSONObject2);
                }
            }) ? jSONObject.toString() : str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "member info not rewritten: " + e.getMessage());
            return str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean extendMembership(JSONObject jSONObject) throws JSONException {
        if (jSONObject.has("daysLeft") && jSONObject.has("memberType") && jSONObject.has("expiryDate")) {
            jSONObject.put("daysLeft", DAYS_LEFT);
            jSONObject.put("expiryDate", EXPIRY_DATE);
            return true;
        }
        return false;
    }
}
