package p001hx.mp4;

import java.util.Iterator;
import java.util.List;

/* loaded from: classes3.dex */
public final class Track {

    /* renamed from: id */
    final int f2id;
    final byte[] init;
    final List<Segment> segments;
    final long templateTimescale;

    public Track(byte[] bArr, int i, long j, List<Segment> list) {
        this.init = bArr;
        this.f2id = i;
        this.templateTimescale = j;
        this.segments = list;
    }

    long durationMillis() {
        Iterator<Segment> it = this.segments.iterator();
        long j = 0;
        while (it.hasNext()) {
            j += it.next().duration;
        }
        return (j * 1000) / this.templateTimescale;
    }
}
