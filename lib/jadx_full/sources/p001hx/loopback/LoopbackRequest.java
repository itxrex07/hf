package p001hx.loopback;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes3.dex */
final class LoopbackRequest {
    private static final char DEL = 127;
    private static final int MAX_HEAD_BYTES = 8192;
    static final String ORIGIN_PARAMETER = "origin";
    static final String ORIGIN_SIZE_PARAMETER = "size";
    private static final String RANGE_HEADER = "Range:";
    static final String RESOURCE_ID_PARAMETER = "resourceId";
    final int episode;
    final boolean headOnly;
    final int height;
    final String origin;
    final long originSize;
    final RangeHeader range;
    final String resourceId;
    final int season;
    final String subjectId;
    final String target;
    private static final byte[] HEAD_END = "\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    private static final Pattern PATH = Pattern.compile("^/dash/(\\d{1,19})/(\\d{1,9})/(\\d{1,9})/(\\d{1,9})\\.mp4$");

    private LoopbackRequest(boolean z, String str, Matcher matcher, Uri uri, RangeHeader rangeHeader) {
        this.headOnly = z;
        this.target = str;
        this.subjectId = matcher.group(1);
        this.season = Integer.parseInt(matcher.group(2));
        this.episode = Integer.parseInt(matcher.group(3));
        this.height = Integer.parseInt(matcher.group(4));
        this.origin = validOrigin(uri.getQueryParameter(ORIGIN_PARAMETER));
        this.originSize = parseSize(uri.getQueryParameter(ORIGIN_SIZE_PARAMETER));
        this.resourceId = uri.getQueryParameter(RESOURCE_ID_PARAMETER);
        this.range = rangeHeader;
    }

    static String readHead(InputStream inputStream) throws IOException {
        byte[] bArr = new byte[MAX_HEAD_BYTES];
        int i = 0;
        while (i < MAX_HEAD_BYTES) {
            int read = inputStream.read();
            if (read < 0) {
                break;
            }
            int i2 = i + 1;
            bArr[i] = (byte) read;
            if (endsWithHeadEnd(bArr, i2)) {
                return new String(bArr, 0, i2 - HEAD_END.length, StandardCharsets.ISO_8859_1);
            }
            i = i2;
        }
        throw new IOException("request head missing or over 8192 bytes");
    }

    static LoopbackRequest parse(String str) {
        String[] split = str.split("\r\n");
        String[] split2 = split[0].split(" ");
        if (split2.length < 2) {
            return null;
        }
        boolean equals = "HEAD".equals(split2[0]);
        Uri parse = Uri.parse("http://127.0.0.1" + split2[1]);
        Matcher matcher = parse.getPath() != null ? PATH.matcher(parse.getPath()) : null;
        if (matcher != null && matcher.matches() && (equals || "GET".equals(split2[0]))) {
            return new LoopbackRequest(equals, split2[1], matcher, parse, RangeHeader.parse(findRange(split)));
        }
        return null;
    }

    boolean startsAtBeginning() {
        return this.range == null || this.range.first == 0;
    }

    boolean isPartial() {
        return this.range != null;
    }

    ByteRange rangeWithin(long j) {
        return this.range != null ? this.range.within(j) : new ByteRange(0L, j - 1);
    }

    private static boolean endsWithHeadEnd(byte[] bArr, int i) {
        if (i < HEAD_END.length) {
            return false;
        }
        for (int i2 = 0; i2 < HEAD_END.length; i2++) {
            if (bArr[(i - HEAD_END.length) + i2] != HEAD_END[i2]) {
                return false;
            }
        }
        return true;
    }

    private static String findRange(String[] strArr) {
        for (int i = 1; i < strArr.length; i++) {
            if (strArr[i].regionMatches(true, 0, RANGE_HEADER, 0, RANGE_HEADER.length())) {
                return strArr[i].substring(RANGE_HEADER.length()).trim();
            }
        }
        return null;
    }

    private static String validOrigin(String str) {
        if (str == null || (!str.startsWith("http://") && !str.startsWith("https://"))) {
            return null;
        }
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt <= ' ' || charAt >= 127) {
                return null;
            }
        }
        return str;
    }

    private static long parseSize(String str) {
        if (str == null) {
            return 0L;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
