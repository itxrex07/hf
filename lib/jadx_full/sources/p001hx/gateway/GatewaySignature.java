package p001hx.gateway;

import android.text.TextUtils;
import android.util.Base64;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes3.dex */
final class GatewaySignature {
    static final String JSON_TYPE = "application/json";
    private static final String MAC_ALGORITHM = "HmacMD5";

    private GatewaySignature() {
    }

    static String clientToken(long j) throws IOException {
        try {
            return j + "," + Digests.md5Hex(new StringBuilder(String.valueOf(j)).reverse().toString().getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
    }

    static String create(byte[] bArr, String str, String str2, String str3, long j) throws IOException {
        byte[] bytes = str3 != null ? str3.getBytes(StandardCharsets.UTF_8) : null;
        try {
            String str4 = str + "\n" + JSON_TYPE + "\n" + JSON_TYPE + "\n" + (bytes != null ? String.valueOf(bytes.length) : "") + "\n" + j + "\n" + (bytes != null ? Digests.md5Hex(bytes) : "") + "\n" + canonicalPath(str2);
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(new SecretKeySpec(bArr, MAC_ALGORITHM));
            return j + "|2|" + Base64.encodeToString(mac.doFinal(str4.getBytes(StandardCharsets.UTF_8)), 2);
        } catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    private static String canonicalPath(String str) throws IOException {
        int indexOf = str.indexOf(63);
        if (indexOf < 0) {
            return str;
        }
        ArrayList arrayList = new ArrayList();
        int i = indexOf + 1;
        for (String str2 : str.substring(i).split("&")) {
            if (!str2.isEmpty()) {
                arrayList.add(decode(str2));
            }
        }
        Collections.sort(arrayList, new Comparator() { // from class: hx.gateway.GatewaySignature.0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return GatewaySignature.lambda$canonicalPath$0((String) obj, (String) obj2);
            }
        });
        return str.substring(0, i) + TextUtils.join("&", arrayList);
    }

    static /* synthetic */ int lambda$canonicalPath$0(String str, String str2) {
        return queryKey(str).compareTo(queryKey(str2));
    }

    private static String decode(String str) throws IOException {
        int indexOf = str.indexOf(61);
        if (indexOf >= 0) {
            return URLDecoder.decode(str.substring(0, indexOf), "UTF-8") + "=" + URLDecoder.decode(str.substring(indexOf + 1), "UTF-8");
        }
        return URLDecoder.decode(str, "UTF-8");
    }

    private static String queryKey(String str) {
        int indexOf = str.indexOf(61);
        return indexOf < 0 ? str : str.substring(0, indexOf);
    }
}
