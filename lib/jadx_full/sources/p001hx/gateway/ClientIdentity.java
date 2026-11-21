package p001hx.gateway;

import android.content.SharedPreferences;
import android.util.Log;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.platform.InstalledApp;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class ClientIdentity {
    private static final int LOGGED_ID_LENGTH = 8;
    private static final String PREFERENCES = "hx_client_identity";
    private volatile String deviceId;
    private final JSONObject fallback;
    private final SharedPreferences preferences;
    private final Set<String> reportedFields = Collections.newSetFromMap(new ConcurrentHashMap());
    private static final String DEVICE_ID = "device_id";
    private static final String[] REQUIRED_FIELDS = {"package_name", "version_code", DEVICE_ID, "gaid"};

    ClientIdentity(String str, SharedPreferences sharedPreferences) {
        try {
            this.fallback = new JSONObject(str);
            this.preferences = sharedPreferences;
            this.deviceId = sharedPreferences != null ? sharedPreferences.getString(DEVICE_ID, null) : null;
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static ClientIdentity forInstalledApp() {
        return new ClientIdentity(ClientInfo.forInstalledApp(), appPreferences());
    }

    private static SharedPreferences appPreferences() {
        try {
            return InstalledApp.context().getSharedPreferences(PREFERENCES, 0);
        } catch (ReflectiveOperationException e) {
            Log.w(Logs.TAG, "app preferences unavailable, device id kept for this process only", e);
            return null;
        }
    }

    String deviceId() {
        return this.deviceId;
    }

    synchronized void replaceDeviceId(String str) {
        if (Objects.equals(str, this.deviceId)) {
            this.deviceId = UUID.randomUUID().toString().replace("-", "");
            if (this.preferences != null) {
                this.preferences.edit().putString(DEVICE_ID, this.deviceId).apply();
            }
            Log.w(Logs.TAG, "upgrade notice for " + (str != null ? "device id " + shown(str) : "the app's device id") + ", now sending " + shown(this.deviceId));
        }
    }

    String rewrite(String str) {
        boolean z;
        try {
            JSONObject jSONObject = new JSONObject(str);
            String str2 = this.deviceId;
            if (str2 != null && !str2.equals(jSONObject.optString(DEVICE_ID))) {
                put(jSONObject, DEVICE_ID, str2);
                z = true;
            } else {
                z = false;
            }
            for (String str3 : REQUIRED_FIELDS) {
                if (jSONObject.optString(str3).isEmpty() && this.fallback.has(str3)) {
                    put(jSONObject, str3, this.fallback.opt(str3));
                    reportMissing(str3);
                    z = true;
                }
            }
            return z ? jSONObject.toString() : str;
        } catch (JSONException e) {
            Log.w(Logs.TAG, "client info not rewritten: " + e.getMessage());
            return str;
        }
    }

    private static String shown(String str) {
        return str.substring(0, Math.min(str.length(), 8));
    }

    private static void put(JSONObject jSONObject, String str, Object obj) {
        try {
            jSONObject.put(str, obj);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    private void reportMissing(String str) {
        if (this.reportedFields.add(str)) {
            Log.w(Logs.TAG, "client info had no " + str + ", sent the hook's own");
        }
    }
}
