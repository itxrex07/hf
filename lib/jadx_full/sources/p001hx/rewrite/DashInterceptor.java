package p001hx.rewrite;

import android.net.Uri;
import android.util.Log;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import p001hx.mp4.Mp4Boxes;
import p001hx.okhttp.ChainInterceptor;
import p001hx.source.Endpoints;
import p001hx.source.MovieBoxSource;
import p001hx.source.SignedResource;
import p001hx.util.Logs;
import p001hx.util.Reflect;

/* loaded from: classes3.dex */
public final class DashInterceptor extends ChainInterceptor {
    private static final String MEDIA_TYPE = "okhttp3.MediaType";
    private static final String RESPONSE_BODY = "okhttp3.ResponseBody";
    private final Set<String> hiddenHomeTabs;
    private final ClassLoader loader;
    private final boolean routeDownloads;
    private final MovieBoxSource source;

    private enum Endpoint {
        PLAY_INFO,
        RESOURCE_LIST,
        APP_TAB,
        MEMBER_INFO,
        OPERATING_PAGE,
        SKU_PROMOTION,
        OTHER
    }

    public DashInterceptor(ClassLoader classLoader, MovieBoxSource movieBoxSource, boolean z, Set<String> set) {
        super("MovieBoxDashInterceptor", "MovieBox interceptor failed");
        this.loader = classLoader;
        this.source = movieBoxSource;
        this.routeDownloads = z;
        this.hiddenHomeTabs = set;
    }

    @Override // p001hx.okhttp.ChainInterceptor
    protected Object intercept(Object obj) throws ReflectiveOperationException {
        Object request = request(obj);
        String valueOf = String.valueOf(Reflect.call(request, "url"));
        Object proceed = proceed(obj, withV1ResourceList(request, valueOf));
        Endpoint classify = classify(valueOf);
        if (classify == Endpoint.OTHER) {
            return proceed;
        }
        Object call = Reflect.call(proceed, "body");
        if (call == null) {
            return proceed;
        }
        return withBody(proceed, call, rewriteOrKeep(classify, valueOf, proceed, (byte[]) Reflect.call(call, "bytes")));
    }

    private static Object withV1ResourceList(Object obj, String str) throws ReflectiveOperationException {
        String listingUrl = ResourceListRequest.listingUrl(str);
        if (listingUrl == null) {
            return obj;
        }
        Object call = Reflect.call(obj, "newBuilder");
        Reflect.call(call, "url", new Class[]{String.class}, listingUrl);
        return Reflect.call(call, "build");
    }

    private Endpoint classify(String str) {
        if (str.contains(Endpoints.PLAY_INFO_SEGMENT)) {
            return Endpoint.PLAY_INFO;
        }
        if (str.contains(Endpoints.RESOURCE_LIST_SEGMENT)) {
            return Endpoint.RESOURCE_LIST;
        }
        if (str.contains(Endpoints.OPERATING_PAGE)) {
            return Endpoint.OPERATING_PAGE;
        }
        if (str.contains(Endpoints.SKU_PROMOTION)) {
            return Endpoint.SKU_PROMOTION;
        }
        boolean z = true;
        boolean z2 = !this.hiddenHomeTabs.isEmpty() && str.contains(Endpoints.APP_TAB);
        if (!str.contains(Endpoints.USER_PROFILE) && !str.contains(Endpoints.MEMBER_DETAIL)) {
            z = false;
        }
        if (z2 || z) {
            return str.contains(Endpoints.APP_TAB) ? Endpoint.APP_TAB : Endpoint.MEMBER_INFO;
        }
        return Endpoint.OTHER;
    }

    private byte[] rewriteOrKeep(Endpoint endpoint, String str, Object obj, byte[] bArr) {
        try {
            return rewriteBody(endpoint, str, obj, bArr);
        } catch (Throwable th) {
            Log.e(Logs.TAG, Uri.parse(str).getPath() + " not rewritten", th);
            return bArr;
        }
    }

    private byte[] rewriteBody(Endpoint endpoint, String str, Object obj, byte[] bArr) throws ReflectiveOperationException {
        String str2 = new String(bArr, StandardCharsets.UTF_8);
        String rewriteText = rewriteText(endpoint, str, obj, str2);
        return rewriteText.equals(str2) ? bArr : rewriteText.getBytes(StandardCharsets.UTF_8);
    }

    private String rewriteText(Endpoint endpoint, String str, Object obj, String str2) throws ReflectiveOperationException {
        switch (endpoint.ordinal()) {
            case 0:
                return rewritePlayInfo(str, obj, str2);
            case 1:
                return this.routeDownloads ? ResourceListRewriter.rewrite(this.source.withDirectFileSizes(str2)) : str2;
            case 2:
                return HomeTabs.withoutTabs(str2, this.hiddenHomeTabs);
            case 3:
                return MemberInfoRewriter.rewrite(str2);
            case Mp4Boxes.U32_BYTES /* 4 */:
                return PromotionRewriter.withoutFeedPromotions(str2);
            case 5:
                return PromotionRewriter.withoutSkuPromotion(str2);
            default:
                throw new IllegalArgumentException("no rewrite for " + endpoint);
        }
    }

    private String rewritePlayInfo(String str, Object obj, String str2) throws ReflectiveOperationException {
        String directFilePlayInfo;
        Uri parse = Uri.parse(str);
        String withUnpacedCdn = withUnpacedCdn(parse, str2);
        String queryParameter = parse.getQueryParameter("subjectId");
        int queryInt = queryInt(parse, "se");
        int queryInt2 = queryInt(parse, "ep");
        if (SignedResource.fromPlayInfo(withUnpacedCdn) == null && (directFilePlayInfo = this.source.directFilePlayInfo(withUnpacedCdn, queryParameter, queryInt, queryInt2)) != null) {
            return directFilePlayInfo;
        }
        this.source.observePlayInfo(str, withUnpacedCdn, Uri.parse(String.valueOf(Reflect.call(Reflect.call(obj, "request"), "url"))).getHost());
        return this.routeDownloads ? PlayInfoRewriter.rewrite(withUnpacedCdn, queryParameter, queryInt, queryInt2) : PlayInfoRewriter.pointAtManifests(withUnpacedCdn);
    }

    private String withUnpacedCdn(Uri uri, String str) {
        if (!SignedResource.isEdgeCachePlayInfo(str)) {
            return str;
        }
        String fetchReplacementPlayInfo = this.source.fetchReplacementPlayInfo(uri.getEncodedPath() + "?" + uri.getEncodedQuery());
        return fetchReplacementPlayInfo != null ? fetchReplacementPlayInfo : str;
    }

    private static int queryInt(Uri uri, String str) {
        try {
            return Integer.parseInt(uri.getQueryParameter(str));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Object withBody(Object obj, Object obj2, byte[] bArr) throws ReflectiveOperationException {
        Object createResponseBody = createResponseBody(Reflect.call(obj2, "contentType"), bArr);
        Object call = Reflect.call(obj, "newBuilder");
        Reflect.call(call, "body", new Class[]{Class.forName(RESPONSE_BODY, false, this.loader)}, createResponseBody);
        Reflect.call(call, "removeHeader", new Class[]{String.class}, "Content-Length");
        return Reflect.call(call, "build");
    }

    private Object createResponseBody(Object obj, byte[] bArr) throws ReflectiveOperationException {
        Class<?> cls = Class.forName(RESPONSE_BODY, false, this.loader);
        Class<?> cls2 = Class.forName(MEDIA_TYPE, false, this.loader);
        for (Method method : cls.getMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if ("create".equals(method.getName()) && parameterTypes.length == 2) {
                if (parameterTypes[0] == cls2 && parameterTypes[1] == byte[].class) {
                    return method.invoke(null, obj, bArr);
                }
                if (parameterTypes[0] == byte[].class && parameterTypes[1] == cls2) {
                    return method.invoke(null, bArr, obj);
                }
            }
        }
        throw new IllegalStateException("no ResponseBody.create(byte[], MediaType) overload");
    }
}
