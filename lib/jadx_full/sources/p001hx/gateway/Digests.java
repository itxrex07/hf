package p001hx.gateway;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/* loaded from: classes3.dex */
final class Digests {
    private Digests() {
    }

    static String md5Hex(byte[] bArr) throws NoSuchAlgorithmException {
        return hex(MessageDigest.getInstance("MD5").digest(bArr));
    }

    static String sha256Hex(byte[] bArr) throws NoSuchAlgorithmException {
        return hex(MessageDigest.getInstance("SHA-256").digest(bArr));
    }

    private static String hex(byte[] bArr) {
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (byte b : bArr) {
            sb.append(String.format(Locale.ROOT, "%02x", Byte.valueOf(b)));
        }
        return sb.toString();
    }
}
