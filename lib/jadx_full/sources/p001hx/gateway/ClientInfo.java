package p001hx.gateway;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.platform.InstalledApp;
import p001hx.util.Logs;

/* loaded from: classes3.dex */
final class ClientInfo {
    private ClientInfo() {
    }

    static String forInstalledApp() {
        try {
            return create(InstalledApp.context());
        } catch (PackageManager.NameNotFoundException | ReflectiveOperationException e) {
            Log.w(Logs.TAG, "client info has no package identity", e);
            return withoutPackageIdentity();
        }
    }

    static String create(Context context) throws PackageManager.NameNotFoundException {
        JSONObject identity = identity();
        try {
            identity.put("package_name", context.getPackageName()).put("version_code", context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            return identity.toString();
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String withoutPackageIdentity() {
        return identity().toString();
    }

    private static JSONObject identity() {
        try {
            return new JSONObject().put("device_id", UUID.randomUUID().toString().replace("-", "")).put("os_version", Build.VERSION.RELEASE).put("gaid", UUID.randomUUID().toString());
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }
}
