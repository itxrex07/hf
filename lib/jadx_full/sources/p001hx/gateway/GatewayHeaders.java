package p001hx.gateway;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes3.dex */
final class GatewayHeaders {
    private static final String WITHHELD_CLIENT_ADDRESS = "unknown";
    private final BuildTag buildTag;
    private final String clientInfo;

    GatewayHeaders(String str, BuildTag buildTag) {
        this.clientInfo = str;
        this.buildTag = buildTag;
    }

    Map<String, String> create(byte[] bArr, String str, String str2, String str3, String str4) throws IOException {
        long currentTimeMillis = System.currentTimeMillis();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("Accept", "application/json");
        linkedHashMap.put("Content-Type", "application/json");
        linkedHashMap.put("x-client-token", GatewaySignature.clientToken(currentTimeMillis));
        linkedHashMap.put("x-tr-signature", GatewaySignature.create(bArr, str, str2, str3, currentTimeMillis));
        String next = this.buildTag != null ? this.buildTag.next() : null;
        String str5 = this.clientInfo;
        if (next != null) {
            str5 = BuildTag.addTo(str5, next);
        }
        linkedHashMap.put("x-client-info", str5);
        if (next != null) {
            linkedHashMap.put("X-Client-Build", next);
        }
        linkedHashMap.put("x-forwarded-for", WITHHELD_CLIENT_ADDRESS);
        if (str4 != null) {
            linkedHashMap.put("Authorization", "Bearer " + str4);
        }
        return linkedHashMap;
    }
}
