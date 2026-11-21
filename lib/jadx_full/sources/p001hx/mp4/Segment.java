package p001hx.mp4;

/* loaded from: classes3.dex */
public final class Segment {
    public long byteLength;
    public final long duration;
    public int prefixBytes;
    public final long startTime;
    public int trackIdOffset;
    public final String url;

    public Segment(String str, long j, long j2) {
        this.url = str;
        this.startTime = j;
        this.duration = j2;
    }

    public long payloadBytes() {
        return this.byteLength - this.prefixBytes;
    }
}
