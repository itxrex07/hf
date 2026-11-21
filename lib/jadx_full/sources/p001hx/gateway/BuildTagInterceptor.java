package p001hx.gateway;

import android.util.Log;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import p001hx.okhttp.ChainInterceptor;
import p001hx.util.Logs;
import p001hx.util.Reflect;

/* loaded from: classes3.dex */
public final class BuildTagInterceptor extends ChainInterceptor {
    private static final String CARRIER_FIELD = "sp_code";
    private static final String DIRECT_FILE_CARRIER = "21403";
    private static final String ESCAPED_UPGRADE_NOTICE_PATH = "aoneroom.com\\/other\\/";
    private static final String INFO_HEADER = "X-Client-Info";
    private static final long PEEKED_BYTES = 262144;
    private static final String PLAY_INFO_PATH = "/subject-api/play-info";
    private static final String RESOURCE_LIST_PATH = "/subject-api/resource";
    private static final String RESOURCE_LIST_V2_PATH = "/subject-api/resource/v2";
    private static final String RESOURCE_POSITION_PATH = "/subject-api/resource-position";
    private static final String UPGRADE_NOTICE_PATH = "aoneroom.com/other/";
    private final BuildTag buildTag;
    private final ClientIdentity clientIdentity;
    private final Map<String, String> sharedHeaders;
    private volatile String sharedTag;

    public BuildTagInterceptor(BuildTag buildTag, Map<String, String> map) {
        this(buildTag, map, ClientIdentity.forInstalledApp());
    }

    BuildTagInterceptor(BuildTag buildTag, Map<String, String> map, ClientIdentity clientIdentity) {
        super("MovieBoxBuildTagInterceptor", "MovieBox build tag failed");
        this.buildTag = buildTag;
        this.sharedHeaders = map;
        this.clientIdentity = clientIdentity;
    }

    public void tagSharedHeaders() {
        String str = this.sharedHeaders.get("X-Client-Build");
        if (str == null || !str.equals(this.sharedTag)) {
            String next = this.buildTag.next();
            this.sharedTag = next;
            this.sharedHeaders.put("X-Client-Build", next);
        }
    }

    @Override // p001hx.okhttp.ChainInterceptor
    protected Object intercept(Object obj) throws ReflectiveOperationException {
        tagSharedHeaders();
        Object request = request(obj);
        String deviceId = this.clientIdentity.deviceId();
        Object proceed = proceed(obj, withStockCertificateTag(request));
        if (!carriesUpgradeNotice(request, proceed)) {
            return proceed;
        }
        this.clientIdentity.replaceDeviceId(deviceId);
        Reflect.call(proceed, "close");
        return proceed(obj, withStockCertificateTag(request));
    }

    private Object withStockCertificateTag(Object obj) throws ReflectiveOperationException {
        if (header(obj, "X-Client-Build") == null) {
            return obj;
        }
        String next = this.buildTag.next();
        Object call = Reflect.call(obj, "newBuilder");
        setHeader(call, "X-Client-Build", next);
        String header = header(obj, INFO_HEADER);
        if (header != null) {
            String rewrite = this.clientIdentity.rewrite(header);
            if (isDirectFileLookup(obj)) {
                rewrite = withDirectFileCarrier(rewrite);
            }
            setHeader(call, INFO_HEADER, BuildTag.addTo(rewrite, next));
        }
        return Reflect.call(call, "build");
    }

    private static boolean carriesUpgradeNotice(Object obj, Object obj2) throws ReflectiveOperationException {
        if (header(obj, "X-Client-Build") == null || header(obj, INFO_HEADER) == null || !isStreamList(obj) || Reflect.call(obj2, "body") == null) {
            return false;
        }
        String str = (String) Reflect.call(Reflect.call(obj2, "peekBody", new Class[]{Long.TYPE}, Long.valueOf(PEEKED_BYTES)), "string");
        return str.contains(UPGRADE_NOTICE_PATH) || str.contains(ESCAPED_UPGRADE_NOTICE_PATH);
    }

    private static boolean isDirectFileLookup(Object obj) throws ReflectiveOperationException {
        return ((String) Reflect.call(Reflect.call(obj, "url"), "encodedPath")).contains(RESOURCE_POSITION_PATH);
    }

    private static String withDirectFileCarrier(String str) {
        try {
            return new JSONObject(str).put(CARRIER_FIELD, DIRECT_FILE_CARRIER).toString();
        } catch (JSONException e) {
            Log.w(Logs.TAG, "direct file lookup carrier not set: " + e.getMessage());
            return str;
        }
    }

    private static boolean isStreamList(Object obj) throws ReflectiveOperationException {
        String str = (String) Reflect.call(Reflect.call(obj, "url"), "encodedPath");
        return str.contains(PLAY_INFO_PATH) || str.endsWith(RESOURCE_LIST_PATH) || str.endsWith("/subject-api/resource/v2");
    }

    private static String header(Object obj, String str) throws ReflectiveOperationException {
        return (String) Reflect.call(obj, "header", new Class[]{String.class}, str);
    }

    private static void setHeader(Object obj, String str, String str2) throws ReflectiveOperationException {
        Reflect.call(obj, "header", new Class[]{String.class, String.class}, str, str2);
    }
}
