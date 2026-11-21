package p001hx.loopback;

/* loaded from: classes3.dex */
final class ByteRange {
    final long end;
    final long start;

    ByteRange(long j, long j2) {
        this.start = j;
        this.end = j2;
    }

    boolean isSatisfiable() {
        return this.start <= this.end;
    }

    long size() {
        return (this.end - this.start) + 1;
    }
}
