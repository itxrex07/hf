package p001hx.loopback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes3.dex */
final class RangeHeader {
    private static final Pattern PATTERN = Pattern.compile("^bytes=(\\d{1,18})-(\\d{0,18})$");
    final long first;
    private final String last;

    private RangeHeader(long j, String str) {
        this.first = j;
        this.last = str;
    }

    static RangeHeader parse(String str) {
        Matcher matcher = str != null ? PATTERN.matcher(str) : null;
        if (matcher == null || !matcher.matches()) {
            return null;
        }
        return new RangeHeader(Long.parseLong(matcher.group(1)), matcher.group(2));
    }

    ByteRange within(long j) {
        long j2 = j - 1;
        if (!this.last.isEmpty()) {
            j2 = Math.min(j2, Long.parseLong(this.last));
        }
        return new ByteRange(this.first, j2);
    }
}
