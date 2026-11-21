package p001hx.rewrite;

import android.net.Uri;
import java.util.Iterator;
import p001hx.source.Endpoints;

/* loaded from: classes3.dex */
final class ResourceListRequest {
    private static final String MEMBERSHIP_PARAMETER = "isVip";
    private static final String VERSION_SUFFIX = "/v2";

    private ResourceListRequest() {
    }

    static String listingUrl(String str) {
        Uri parse = Uri.parse(str);
        String encodedPath = parse.getEncodedPath();
        if (encodedPath == null || !encodedPath.endsWith(Endpoints.RESOURCE_LIST_V2_SEGMENT)) {
            return null;
        }
        Uri.Builder clearQuery = parse.buildUpon().encodedPath(encodedPath.substring(0, encodedPath.length() - VERSION_SUFFIX.length())).clearQuery();
        for (String str2 : parse.getQueryParameterNames()) {
            if (!MEMBERSHIP_PARAMETER.equals(str2)) {
                Iterator<String> it = parse.getQueryParameters(str2).iterator();
                while (it.hasNext()) {
                    clearQuery.appendQueryParameter(str2, it.next());
                }
            }
        }
        return clearQuery.build().toString();
    }
}
